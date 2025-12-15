package systolic

import spinal.core._

/**
 * BFloat16 Arithmetic with FP32 Accumulation
 *
 * Multiplication: BF16 × BF16 → FP32
 * Addition: FP32 + FP32 → FP32
 *
 */
object BFloat16Arithmetic {

  /**
   * Multiply two BFloat16 numbers and return FP32 result
   */
  def multiply(a: BFloat16, b: BFloat16): Float32 = new Composite(a, "bf16_multiply") {
    val result = Float32()

    // Special case: either input is zero
    val aIsZero = a.isZero
    val bIsZero = b.isZero
    val eitherZero = aIsZero || bIsZero

    // Special case: either input is infinity
    val aIsInf = a.isInf
    val bIsInf = b.isInf

    // Special case: either input is NaN
    val aIsNaN = a.isNaN
    val bIsNaN = b.isNaN

    // Result sign is XOR of input signs
    val resultSign = a.sign ^ b.sign

    // Mantissas with implicit leading 1 (for normalized numbers)
    // BF16 mantissa is 7 bits, so full mantissa with implicit 1 is 8 bits
    val aMantissa = Mux(a.exponent === 0, U"0" @@ a.mantissa, U"1" @@ a.mantissa)  // 8 bits
    val bMantissa = Mux(b.exponent === 0, U"0" @@ b.mantissa, U"1" @@ b.mantissa)  // 8 bits

    // Multiply mantissas: 8 × 8 = 16 bits
    val mantissaProduct = (aMantissa * bMantissa).resize(16 bits)  // 16 bits

    // Add exponents: exp_a + exp_b - bias (127)
    // Use wider type to handle overflow/underflow
    // Note: Using + instead of +^ to avoid extra bit, then resize for safety
    val expSumUnsigned = (a.exponent.resize(10 bits) + b.exponent.resize(10 bits))
    val expSum = (expSumUnsigned.asSInt - S(127, 10 bits)).resize(10 bits)

    // Normalize: mantissa product format is X.XXXXXXXXXXXXXXX (1.15 or 0.15)
    // If MSB is 1, we have 1X.XXXXXXXXXXXXXX, need to shift right and increment exponent
    val mantissaMSB = mantissaProduct(15)

    // Normalization
    val normalizedMantissa = UInt(23 bits)
    val normalizedExp = SInt(10 bits)

    when(mantissaMSB) {
      // Result is in format 1X.XXXXXXXXXXXXXX
      // Shift right by 1, increment exponent
      normalizedMantissa := (mantissaProduct(14 downto 0) @@ U(0, 8 bits)).resize(23)
      normalizedExp := (expSum + S(1, 10 bits)).resized
    } otherwise {
      // Result is in format 0X.XXXXXXXXXXXXXX or 1.XXXXXXXXXXXXXXX
      normalizedMantissa := (mantissaProduct(13 downto 0) @@ U(0, 9 bits)).resize(23)
      normalizedExp := expSum
    }

    // Handle special cases and assemble result
    when(aIsNaN || bIsNaN || (aIsInf && bIsZero) || (aIsZero && bIsInf)) {
      // NaN result
      result.sign := False
      result.exponent := U"8'hFF"
      result.mantissa := U"23'h400000"
    } elsewhen(aIsInf || bIsInf) {
      // Infinity result
      result.sign := resultSign
      result.exponent := U"8'hFF"
      result.mantissa := 0
    } elsewhen(eitherZero) {
      // Zero result
      result.sign := resultSign
      result.exponent := 0
      result.mantissa := 0
    } elsewhen(normalizedExp <= 0) {
      // Underflow - return zero (simplified, could implement denormals)
      result.sign := resultSign
      result.exponent := 0
      result.mantissa := 0
    } elsewhen(normalizedExp >= 255) {
      // Overflow - return infinity
      result.sign := resultSign
      result.exponent := U"8'hFF"
      result.mantissa := 0
    } otherwise {
      // Normal result
      result.sign := resultSign
      result.exponent := normalizedExp.resize(8).asUInt
      result.mantissa := normalizedMantissa
    }
  }.result

  /**
   * Add two FP32 numbers
   *
   * Algorithm:
   * 1. Handle special cases
   * 2. Align mantissas based on exponent difference
   * 3. Add or subtract mantissas based on signs
   * 4. Normalize result
   */
  def add(a: Float32, b: Float32): Float32 = new Composite(a, "fp32_add") {
    val result = Float32()

    // Special cases
    val aIsZero = a.isZero
    val bIsZero = b.isZero
    val aIsInf = a.isInf
    val bIsInf = b.isInf
    val aIsNaN = a.isNaN
    val bIsNaN = b.isNaN

    // Determine which operand has larger magnitude
    val aExpLarger = a.exponent > b.exponent
    val expEqual = a.exponent === b.exponent
    val aMantLarger = a.mantissa > b.mantissa
    val aLarger = aExpLarger || (expEqual && aMantLarger)

    // Order operands so larger is first
    val largerSign = Mux(aLarger, a.sign, b.sign)
    val largerExp = Mux(aLarger, a.exponent, b.exponent)
    val largerMant = Mux(aLarger, a.mantissa, b.mantissa)
    val smallerSign = Mux(aLarger, b.sign, a.sign)
    val smallerExp = Mux(aLarger, b.exponent, a.exponent)
    val smallerMant = Mux(aLarger, b.mantissa, a.mantissa)

    // Exponent difference for alignment
    val expDiff = (largerExp - smallerExp).resize(8)

    // Full mantissas with implicit leading 1 (for normalized) or 0 (for denormalized)
    // Use 27 bits: 1 (implicit) + 23 (mantissa) + 3 (guard, round, sticky)
    val largerFullMant = Mux(largerExp === 0,
      U(0, 1 bits) @@ largerMant @@ U(0, 3 bits),
      U(1, 1 bits) @@ largerMant @@ U(0, 3 bits)
    ).resize(27)

    val smallerFullMant = Mux(smallerExp === 0,
      U(0, 1 bits) @@ smallerMant @@ U(0, 3 bits),
      U(1, 1 bits) @@ smallerMant @@ U(0, 3 bits)
    ).resize(27)

    // Align smaller mantissa (shift right by exponent difference)
    // Cap shift at 27 to avoid undefined behavior
    val shiftAmount = Mux(expDiff > 27, U(27, 8 bits), expDiff)
    val alignedSmallerMant = smallerFullMant >> shiftAmount

    // Determine if we're adding or subtracting based on signs
    val sameSign = largerSign === smallerSign

    // Perform addition or subtraction
    val mantSum = UInt(28 bits)  // Extra bit for carry
    when(sameSign) {
      mantSum := (U(0, 1 bits) @@ largerFullMant) + (U(0, 1 bits) @@ alignedSmallerMant)
    } otherwise {
      mantSum := (U(0, 1 bits) @@ largerFullMant) - (U(0, 1 bits) @@ alignedSmallerMant)
    }

    // Normalize result
    val normalizedMant = UInt(23 bits)
    val normalizedExp = UInt(8 bits)
    val resultSign = Bool()

    // Count leading zeros for normalization
    val leadingZeros = UInt(5 bits)
    leadingZeros := CountLeadingZeroes(mantSum.resize(28))

    when(mantSum(27)) {
      // Overflow: shift right, increment exponent
      normalizedMant := mantSum(26 downto 4)
      normalizedExp := (largerExp + 1).resized
      resultSign := largerSign
    } elsewhen(mantSum(26)) {
      // Already normalized
      normalizedMant := mantSum(25 downto 3)
      normalizedExp := largerExp
      resultSign := largerSign
    } elsewhen(mantSum === 0) {
      // Zero result
      normalizedMant := 0
      normalizedExp := 0
      resultSign := False
    } otherwise {
      // Need to shift left to normalize
      val shiftLeft = (leadingZeros - 1).resize(5)
      val shiftedMant = mantSum << shiftLeft
      normalizedMant := shiftedMant(25 downto 3)

      when(largerExp <= shiftLeft.resize(8)) {
        // Underflow to denormal or zero
        normalizedExp := 0
      } otherwise {
        normalizedExp := (largerExp - shiftLeft.resize(8)).resized
      }
      resultSign := largerSign
    }

    // Handle special cases
    when(aIsNaN || bIsNaN) {
      result.sign := False
      result.exponent := U"8'hFF"
      result.mantissa := U"23'h400000"
    } elsewhen(aIsInf && bIsInf && (a.sign =/= b.sign)) {
      // inf - inf = NaN
      result.sign := False
      result.exponent := U"8'hFF"
      result.mantissa := U"23'h400000"
    } elsewhen(aIsInf) {
      result.sign := a.sign
      result.exponent := U"8'hFF"
      result.mantissa := 0
    } elsewhen(bIsInf) {
      result.sign := b.sign
      result.exponent := U"8'hFF"
      result.mantissa := 0
    } elsewhen(aIsZero && bIsZero) {
      result.sign := a.sign && b.sign  // -0 + -0 = -0
      result.exponent := 0
      result.mantissa := 0
    } elsewhen(aIsZero) {
      result.sign := b.sign
      result.exponent := b.exponent
      result.mantissa := b.mantissa
    } elsewhen(bIsZero) {
      result.sign := a.sign
      result.exponent := a.exponent
      result.mantissa := a.mantissa
    } otherwise {
      result.sign := resultSign
      result.exponent := normalizedExp
      result.mantissa := normalizedMant
    }
  }.result

  private def CountLeadingZeroes(value: UInt): UInt = {
    val width = value.getWidth
    val result = UInt(log2Up(width + 1) bits)

    // Simple sequential search (synthesizes to priority encoder)
    result := width
    for (i <- 0 until width) {
      when(value(width - 1 - i)) {
        result := i
      }
    }
    result
  }

  implicit val bf16Fp32Arithmetic: Arithmetic[BFloat16, Float32] = new Arithmetic[BFloat16, Float32] {

    override def multiply(inputA: BFloat16, inputB: BFloat16): Float32 =
      BFloat16Arithmetic.multiply(inputA, inputB)

    override def add(input0: Float32, input1: Float32): Float32 = BFloat16Arithmetic.add(input0, input1)

    override def addResize(input0: Float32, input1: Float32, targetWidth: Int): Float32 = {
      require(targetWidth == 32, "FP32 accumulation must have width 32")
      add(input0, input1)
    }

    override def zeroInput(width: Int): BFloat16 = BFloat16.zero
    override def zeroAccumulation(width: Int): Float32 = Float32.zero
  }






}