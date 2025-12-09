package systolic

case class PortBitWidthInfo(
                                bitWidthInputA: Int,
                                bitWidthInputB: Int,
                                bitWidthSystolicOutputC: Option[Int] = None,
                                ){

}
