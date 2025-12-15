package systolic

import spinal.core._

/**
 * IEEE 754 Half Precision Floating Point (FP16) format:
 * - 1 bit sign
 * - 5 bits exponent (biased by 15)
 * - 10 bits mantissa (with implicit leading 1 for normalized numbers)
 *
 * FP16 has higher precision than BF16 (10 vs 7 mantissa bits) but
 * smaller dynamic range (5 vs 8 exponent bits).
 *
 * Comparison:
 *   FP16:  1 sign + 5 exponent + 10 mantissa (bias 15)
 *   BF16:  1 sign + 8 exponent + 7 mantissa  (bias 127)
 *   FP32:  1 sign + 8 exponent + 23 mantissa (bias 127)
 */
case class Float16() extends Bundle {
  val sign = Bool()
  val exponent = UInt(5 bits)
  val mantissa = UInt(10 bits)

  override def asBits: Bits = sign ## exponent ## mantissa

  def isZero: Bool = exponent === 0 && mantissa === 0
  def isInf: Bool = exponent === 0x1F && mantissa === 0
  def isNaN: Bool = exponent === 0x1F && mantissa =/= 0
  def isDenormalized: Bool = exponent === 0 && mantissa =/= 0

  /**
   * Convert FP16 to Float32
   * Requires exponent rebias: FP16 bias=15, FP32 bias=127
   */
  def toFloat32: Float32 = {
    val fp32 = Float32()
    fp32.sign := sign

    // Handle special cases
    when(isZero) {
      fp32.exponent := 0
      fp32.mantissa := 0
    } elsewhen(isInf) {
      fp32.exponent := U"8'hFF"
      fp32.mantissa := 0
    } elsewhen(isNaN) {
      fp32.exponent := U"8'hFF"
      fp32.mantissa := (mantissa << 13).resized  // Preserve NaN payload
    } elsewhen(isDenormalized) {
      // Denormalized FP16 -> normalized or denormalized FP32
      // For simplicity, convert to zero (proper handling requires normalization)
      fp32.exponent := 0
      fp32.mantissa := 0
    } otherwise {
      // Normal number: rebias exponent (exp16 - 15 + 127 = exp16 + 112)
      fp32.exponent := (exponent + U(112, 8 bits)).resized
      fp32.mantissa := (mantissa << 13).resized  // Shift 10-bit to 23-bit
    }
    fp32
  }
}

object Float16 {
  def apply(bits: Bits): Float16 = {
    val fp16 = Float16()
    fp16.sign := bits(15)
    fp16.exponent := bits(14 downto 10).asUInt
    fp16.mantissa := bits(9 downto 0).asUInt
    fp16
  }

  def zero: Float16 = {
    val fp16 = Float16()
    fp16.sign := False
    fp16.exponent := 0
    fp16.mantissa := 0
    fp16
  }

  /**
   * Convert from FP32 to FP16 (with truncation)
   */
  def fromFloat32(fp32: Float32): Float16 = {
    val fp16 = Float16()
    fp16.sign := fp32.sign

    // Handle special cases
    when(fp32.isZero) {
      fp16.exponent := 0
      fp16.mantissa := 0
    } elsewhen(fp32.isInf) {
      fp16.exponent := U"5'h1F"
      fp16.mantissa := 0
    } elsewhen(fp32.isNaN) {
      fp16.exponent := U"5'h1F"
      fp16.mantissa := (fp32.mantissa >> 13).resized
    } otherwise {
      // Rebias: exp32 - 127 + 15 = exp32 - 112
      val newExp = fp32.exponent.asSInt - S(112, 9 bits)

      when(newExp <= 0) {
        // Underflow to zero (or denormal, simplified to zero)
        fp16.exponent := 0
        fp16.mantissa := 0
      } elsewhen(newExp >= 31) {
        // Overflow to infinity
        fp16.exponent := U"5'h1F"
        fp16.mantissa := 0
      } otherwise {
        fp16.exponent := newExp(4 downto 0).asUInt
        fp16.mantissa := (fp32.mantissa >> 13).resized
      }
    }
    fp16
  }

  /** Width in bits */
  val width: Int = 16

  /** Exponent bias */
  val bias: Int = 15
}






