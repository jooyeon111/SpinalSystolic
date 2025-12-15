package systolic

import spinal.core._

/**
 * IEEE 754 Single Precision Floating Point (FP32) format:
 * - 1 bit sign
 * - 8 bits exponent (biased by 127)
 * - 23 bits mantissa (with implicit leading 1 for normalized numbers)
 *
 * Used for accumulation in BF16 arithmetic to maintain precision.
 */
case class Float32() extends Bundle {
  val sign = Bool()
  val exponent = UInt(8 bits)
  val mantissa = UInt(23 bits)

  override def asBits: Bits = sign ## exponent ## mantissa

  def fromBits(bits: Bits): Float32 = {
    val fp32 = Float32()
    fp32.sign := bits(31)
    fp32.exponent := bits(30 downto 23).asUInt
    fp32.mantissa := bits(22 downto 0).asUInt
    fp32
  }

  def isZero: Bool = exponent === 0 && mantissa === 0
  def isInf: Bool = exponent === 0xFF && mantissa === 0
  def isNaN: Bool = exponent === 0xFF && mantissa =/= 0
  def isDenormalized: Bool = exponent === 0 && mantissa =/= 0

  /**
   * Convert Float32 to BFloat16 (truncation - no rounding)
   */
  def toBFloat16: BFloat16 = {
    BFloat16.fromFloat32(this)
  }

  /**
   * Convert Float32 to BFloat16 with round-to-nearest-even
   */
  def toBFloat16Rounded: BFloat16 = {
    val bf16 = BFloat16()
    bf16.sign := sign

    // Round-to-nearest-even logic
    val roundBit = mantissa(15)
    val stickyBits = mantissa(14 downto 0)
    val guardBit = mantissa(16)

    val roundUp = roundBit && (stickyBits.orR || guardBit)

    val mantissaUpper = mantissa(22 downto 16)
    val roundedMantissa = mantissaUpper + roundUp.asUInt.resize(7)

    // Handle mantissa overflow (carry into exponent)
    when(roundedMantissa === 0 && roundUp) {
      bf16.exponent := exponent + 1
      bf16.mantissa := 0
    } otherwise {
      bf16.exponent := exponent
      bf16.mantissa := roundedMantissa
    }

    bf16
  }
}

object Float32 {
  def apply(bits: Bits): Float32 = {
    val fp32 = Float32()
    fp32.sign := bits(31)
    fp32.exponent := bits(30 downto 23).asUInt
    fp32.mantissa := bits(22 downto 0).asUInt
    fp32
  }

  def zero: Float32 = {
    val fp32 = Float32()
    fp32.sign := False
    fp32.exponent := 0
    fp32.mantissa := 0
    fp32
  }

  def fromBFloat16(bf16: BFloat16): Float32 = {
    bf16.toFloat32
  }

  /** Width in bits */
  val width: Int = 32

  /** Exponent bias */
  val bias: Int = 127
}








