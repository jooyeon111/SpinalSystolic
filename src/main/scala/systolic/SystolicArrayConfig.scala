package systolic

/**
 * Unified systolic array configuration supporting both integer and floating-point types.
 *
 * This replaces the original SystolicArrayConfig with a more flexible version
 * that uses DataTypeConfig to support multiple data types.
 *
 * @param row Number of rows in the systolic array
 * @param col Number of columns in the systolic array
 * @param dataflow Dataflow pattern (ReuseA, ReuseB, ReuseC)
 * @param dataTypeConfig Configuration for the data type (integer or floating-point)
 */
case class SystolicArrayConfig(
                                row: Int,
                                col: Int,
                                dataflow: Dataflow.Value,
                                dataTypeConfig: DataTypeConfig
                              ) {

  require (row > 0, "Systolic array row size must be positive")
  require (col > 0, "Systolic array col size must be positive")

  def isInteger: Boolean = dataTypeConfig match {
    case _: IntegerDataTypeConfig => true
    case _ => false
  }

  def isBFloat16: Boolean = dataTypeConfig match {
    case _: BFloat16DataTypeConfig => true
    case _ => false
  }

  def isFloat16: Boolean = dataTypeConfig match {
    case _: Float16DataTypeConfig => true
    case _ => false
  }

  def integerConfig: IntegerConfig = dataTypeConfig match {
    case ic: IntegerDataTypeConfig => ic.toIntegerConfig
    case _ => throw new IllegalStateException("Not an integer configuration")
  }

}

//TODO make systolic array size
object SystolicArrayConfig {

  def signedInteger(
                     row: Int,
                     col: Int,
                     dataflow: Dataflow.Value,
                     bitWidthA: Int,
                     bitWidthB: Int,
                     bitWidthOutputC: Option[Int] = None
                   ): SystolicArrayConfig = {
    SystolicArrayConfig(
      row = row,
      col = col,
      dataflow = dataflow,
      dataTypeConfig = DataTypeConfig.signedInteger(bitWidthA, bitWidthB, bitWidthOutputC)
    )
  }

  def unsignedInteger(
                       row: Int,
                       col: Int,
                       dataflow: Dataflow.Value,
                       bitWidthA: Int,
                       bitWidthB: Int,
                       bitWidthOutputC: Option[Int] = None
                     ): SystolicArrayConfig = {
    SystolicArrayConfig(
      row = row,
      col = col,
      dataflow = dataflow,
      dataTypeConfig = DataTypeConfig.unsignedInteger(bitWidthA, bitWidthB, bitWidthOutputC)
    )
  }

  def bfloat16(
                row: Int,
                col: Int,
                dataflow: Dataflow.Value
              ): SystolicArrayConfig = {
    SystolicArrayConfig(
      row = row,
      col = col,
      dataflow = dataflow,
      dataTypeConfig = DataTypeConfig.bfloat16()
    )
  }

  def float16(
               row: Int,
               col: Int,
               dataflow: Dataflow.Value
             ): SystolicArrayConfig = {
    SystolicArrayConfig(
      row = row,
      col = col,
      dataflow = dataflow,
      dataTypeConfig = DataTypeConfig.float16()
    )
  }

}