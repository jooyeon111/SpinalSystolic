package systolic

case class PortBitWidthInfo(
                                bitWidthInputA: Int,
                                bitWidthInputB: Int,
                                bitWidthSystolicOutputC: Option[Int] = None,
                                ) {

  require(bitWidthInputA > 0, "Bit width for Input A must be positive")
  require(bitWidthInputB > 0, "Bit width for Input B must be positive")

  bitWidthSystolicOutputC.foreach { width =>
    require(width > 0, "Bit width for Systolic Output C must be positive")
  }

}
