package systolic

/**
 * Sealed trait for data type configurations.
 * This provides a unified way to configure the systolic array
 * with either integer or floating point data types.
 */
sealed trait DataTypeConfig {
  def numberType: NumberType.Value
}

/**
 * Configuration for integer data types (signed/unsigned)
 */
case class IntegerDataTypeConfig(
                                  integerType: IntegerType.Value,
                                  portBitWidthInfo: PortBitWidthInfo
                                ) extends DataTypeConfig {
  override def numberType: NumberType.Value = integerType match {
    case IntegerType.SignedInteger => NumberType.SignedInteger
    case IntegerType.UnsignedInteger => NumberType.UnsignedInteger
  }

  /** Convert to legacy IntegerConfig for backward compatibility */
  def toIntegerConfig: IntegerConfig = IntegerConfig(integerType, portBitWidthInfo)
}

/**
 * Configuration for BFloat16 with FP32 accumulation
 *
 * - Input A: BFloat16 (16 bits)
 * - Input B: BFloat16 (16 bits)
 * - Output C: FP32 (32 bits)
 */
case class BFloat16DataTypeConfig() extends DataTypeConfig {
  override def numberType: NumberType.Value = NumberType.BFloat16

  // Fixed bit widths for BF16/FP32
  val inputBitWidth: Int = 16   // BFloat16
  val outputBitWidth: Int = 32  // FP32
}
/**
 * Configuration for FP16 (IEEE 754 half-precision) with FP32 accumulation
 *
 * - Input A: FP16 (16 bits) - 1 sign + 5 exp + 10 mantissa
 * - Input B: FP16 (16 bits)
 * - Output C: FP32 (32 bits)
 */
case class Float16DataTypeConfig() extends DataTypeConfig {
  override def numberType: NumberType.Value = NumberType.Float16

  val inputBitWidth: Int = 16   // FP16
  val outputBitWidth: Int = 32  // FP32
}

object DataTypeConfig {

  /** Create signed integer configuration */
  def signedInteger(
                     bitWidthA: Int,
                     bitWidthB: Int,
                     bitWidthOutputC: Option[Int] = None
                   ): IntegerDataTypeConfig = {
    IntegerDataTypeConfig(
      IntegerType.SignedInteger,
      PortBitWidthInfo(bitWidthA, bitWidthB, bitWidthOutputC)
    )
  }

  /** Create unsigned integer configuration */
  def unsignedInteger(
                       bitWidthA: Int,
                       bitWidthB: Int,
                       bitWidthOutputC: Option[Int] = None
                     ): IntegerDataTypeConfig = {
    IntegerDataTypeConfig(
      IntegerType.UnsignedInteger,
      PortBitWidthInfo(bitWidthA, bitWidthB, bitWidthOutputC)
    )
  }

  /** Create BFloat16 configuration (FP32 accumulation is implicit) */
  def bfloat16(): BFloat16DataTypeConfig = BFloat16DataTypeConfig()

  /** Create FP16 configuration (FP32 accumulation is implicit) */
  def float16(): Float16DataTypeConfig = Float16DataTypeConfig()

  /** Create from legacy IntegerConfig */
  def fromIntegerConfig(config: IntegerConfig): IntegerDataTypeConfig = {
    IntegerDataTypeConfig(config.integerType, config.portBitWidthInfo)
  }
}