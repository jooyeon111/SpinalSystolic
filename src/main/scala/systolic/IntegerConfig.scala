package systolic
/**
 * Integer Config for integer systolic arrays
 *
 * Integer type defined signed or unsigned systolic arrays
 * Port bit width info supports user defined input A (input) input B (weight) data size.
 *
 */
case class IntegerConfig(
                            integerType: IntegerType.Value,
                            portBitWidthInfo: PortBitWidthInfo,
                            )
