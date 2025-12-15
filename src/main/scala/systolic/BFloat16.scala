package systolic

import spinal.core._

/**
 * BFloat16 (Brain Floating Point 16) format:
 * - 1 bit sign
 * - 8 bits exponen
 * - 7 bits mantissa
 *
 */
case class BFloat16() extends Bundle {
  val sign = Bool()
  val exponent = UInt(8 bits)
  val mantissa = UInt(7 bits)

  override def asBits: Bits = sign ## exponent ## mantissa

  def fromBits(bits: Bits): BFloat16 = {
    val bf16 = BFloat16()
    bf16.sign := bits(15)
    bf16.exponent := bits(14 downto 7).asUInt
    bf16.mantissa := bits(6 downto 0).asUInt
    bf16
  }

  def isZero: Bool = exponent === 0 && mantissa === 0
  def isInf: Bool = exponent === 0xFF && mantissa === 0
  def isNaN: Bool = exponent === 0xFF && mantissa =/= 0
  def isDenormalized: Bool = exponent === 0 && mantissa =/= 0

  def toFloat32: Float32 = {
    val fp32 = Float32()
    fp32.sign := sign
    fp32.exponent := exponent
    fp32.mantissa := mantissa @@ U(0, 16 bits)
    fp32
  }
}

object BFloat16 {
  def apply(bits: Bits): BFloat16 = {
    val bf16 = BFloat16()
    bf16.sign := bits(15)
    bf16.exponent := bits(14 downto 7).asUInt
    bf16.mantissa := bits(6 downto 0).asUInt
    bf16
  }

  def zero: BFloat16 = {
    val bf16 = BFloat16()
    bf16.sign := False
    bf16.exponent := 0
    bf16.mantissa := 0
    bf16
  }

  def fromFloat32(fp32: Float32): BFloat16 = {
    val bf16 = BFloat16()
    bf16.sign := fp32.sign
    bf16.exponent := fp32.exponent
    bf16.mantissa := fp32.mantissa(22 downto 16)
    bf16
  }

  /** Width in bits */
  val width: Int = 16
}