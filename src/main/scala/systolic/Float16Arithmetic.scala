package systolic

import spinal.core._

/**
 * FP16 (IEEE 754 Half Precision) Arithmetic with FP32 Accumulation
 *
 * Multiplication: FP16 × FP16 → FP32
 * Addition: FP32 + FP32 → FP32
 *
 * FP16 has higher precision than BF16 but smaller dynamic range.
 * This is commonly used in graphics and some ML inference workloads.
 */
object Float16Arithmetic {

  /**
   * Multiply two FP16 numbers and return FP32 result
   *
   * FP16: 1 sign + 5 exponent (bias 15) + 10 mantissa
   * FP32: 1 sign + 8 exponent (bias 127) + 23 mantissa
   */
  def multiply(a: Float16, b: Float16): Float32 = new Composite(a, "fp16_multiply") {
    val result = Float32()

    // Special case flags
    val aIsZero = a.isZero
    val bIsZero = b.isZero
    val eitherZero = aIsZero || bIsZero

    val aIsInf = a.isInf
    val bIsInf = b.isInf

    val aIsNaN = a.isNaN
    val bIsNaN = b.isNaN

    // Result sign is XOR of input signs
    val resultSign = a.sign ^ b.sign

    // Mantissas with implicit leading 1 (for normalized numbers)
    // FP16 mantissa is 10 bits, so full mantissa with implicit 1 is 11 bits
    val aMantissa = Mux(a.exponent === 0, U"0" @@ a.mantissa, U"1" @@ a.mantissa)  // 11 bits
    val bMantissa = Mux(b.exponent === 0, U"0" @@ b.mantissa, U"1" @@ b.mantissa)  // 11 bits

    // Multiply mantissas: 11 × 11 = 22 bits
    val mantissaProduct = (aMantissa * bMantissa).resize(22 bits)

    // Add exponents and rebias for FP32
    // exp_a + exp_b - bias_fp16 - bias_fp16 + bias_fp32
    // = exp_a + exp_b - 15 - 15 + 127 = exp_a + exp_b + 97
    // But we need to handle the implicit 1.xxx × 1.xxx = 1x.xxx format
    // So: exp_a - 15 + exp_b - 15 + 127 = exp_a + exp_b + 97
    val expSum = (a.exponent.resize(10 bits) +^ b.exponent.resize(10 bits)).asSInt + S(97, 10 bits)

    // Normalize: mantissa product format is XX.XXXXXXXXXXXXXXXXXXXX (2.20 fixed point)
    // If bit 21 is 1, we have 1X.XXX, need to shift right and increment exponent
    val mantissaMSB = mantissaProduct(21)

    // Normalization
    val normalizedMantissa = UInt(23 bits)
    val normalizedExp = SInt(10 bits)

    when(mantissaMSB) {
      // Result is in format 1X.XXXXXXXXXXXXXXXXXXXX
      // Take bits [20:0] and pad to 23 bits, shift accounts for the 1X format
      normalizedMantissa := (mantissaProduct(20 downto 0) @@ U(0, 2 bits)).resized
      normalizedExp := (expSum + S(1, 10 bits)).resized
    } otherwise {
      // Result is in format 01.XXXXXXXXXXXXXXXXXXXX
      // Take bits [19:0] and pad to 23 bits
      normalizedMantissa := (mantissaProduct(19 downto 0) @@ U(0, 3 bits)).resized
      normalizedExp := expSum.resized
    }

    // Handle special cases and assemble result
    when(aIsNaN || bIsNaN || (aIsInf && bIsZero) || (aIsZero && bIsInf)) {
      // NaN result
      result.sign := False
      result.exponent := U"8'hFF"
      result.mantissa := U"23'h400000"  // Quiet NaN
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
      // Underflow - return zero
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
      result.exponent := normalizedExp(7 downto 0).asUInt
      result.mantissa := normalizedMantissa
    }
  }.result

  /**
   * Add two FP32 numbers (reuse from BFloat16Arithmetic)
   * FP32 addition is the same regardless of input format
   */
  def add(a: Float32, b: Float32): Float32 = {
    BFloat16Arithmetic.add(a, b)
  }
//
//  /**
//   * Implicit arithmetic instance for use with the systolic array
//   */
//  implicit val fp16Fp32Arithmetic: Arithmetic[Float16, Float32] = new Arithmetic[Float16, Float32] {
//
//    override def multiply(inputA: Float16, inputB: Float16): Float32 = Float16Arithmetic.multiply(inputA, inputB)
//    override def add(input0: Float32, input1: Float32): Float32 = Float16Arithmetic.add(input0, input1)
//    override def addResize(input0: Float32, input1: Float32, targetWidth: Int): Float32 = {
//      require(targetWidth == 32, "FP32 accumulation must have width 32")
//      Float16Arithmetic.add(input0, input1)
//    }
//
//    override def zeroInput(width: Int): Float16 = Float16.zero
//    override def zeroAccumulation(width: Int): Float32 = Float32.zero
//
//  }

}






