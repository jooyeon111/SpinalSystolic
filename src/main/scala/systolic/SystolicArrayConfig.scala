package systolic
/**
 * Configuration for systolic array dimensions and behavior.
 *
 * @param row Number of rows in the array
 * @param col Number of columns in the array
 * @param dataflow Dataflow architecture (ReuseA, ReuseB, or ReuseC)
 * @param integerConfig Integer type and bit width configuration
 *
 * @throws IllegalArgumentException if row or col is not positive
 * @throws IllegalArgumentException if ReuseC dataflow without output bitwidth
 *
 * @example {{{
 * // 4x4 array with signed 8-bit inputs
 * val config = SystolicArrayConfig(
 *   row = 4,
 *   col = 4,
 *   dataflow = Dataflow.ReuseA,
 *   integerConfig = IntegerConfig(
 *     IntegerType.SignedInteger,
 *     PortBitWidthInfo(8, 8)
 *   )
 * )
 * }}}
 */
case class SystolicArrayConfig(
  row: Int,
  col: Int,
  dataflow: Dataflow.Value,
  integerConfig: IntegerConfig,
) {

  require(row > 0, "Row must be positive")
  require(col > 0, "Col must be positive")
  require(
    dataflow != Dataflow.ReuseC || integerConfig.portBitWidthInfo.bitWidthSystolicOutputC.isDefined,
    s"ReuseC dataflow requires explicit bitWidthSystolicOutputC, but got None"
  )

}
