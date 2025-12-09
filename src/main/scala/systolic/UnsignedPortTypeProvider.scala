package systolic

import spinal.core._

class UnsignedPortTypeProvider(
                             val arrayConfig: SystolicArrayConfig,
                             val bitWidthInputA: Int,
                             val bitWidthInputB: Int,
                             val bitWidthSystolicOutputC: Option[Int],
                           ) extends PortTypeProvider[UInt]{

  private def safeLog2Up(value: Int): Int = {
    if (value <= 1) 0 else log2Up(value)
  }

  private val multOutputBitWidth: Int = bitWidthInputA + bitWidthInputB
  private val systolicOutputBitwidth: Int = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        multOutputBitWidth + safeLog2Up(arrayConfig.col)
      case Dataflow.ReuseB =>
        multOutputBitWidth + safeLog2Up(arrayConfig.row)
      case Dataflow.ReuseC =>
        assert(bitWidthSystolicOutputC.isDefined,
          "[error] Output stationary (Reuse C Type) needs external systolic array output bit width")
        bitWidthSystolicOutputC.get

    }
  }

  override def createInputTypeA: UInt = UInt(bitWidthInputA bits)
  override def createInputTypeB: UInt = UInt(bitWidthInputB bits)
  override def createMultOutputType: UInt = UInt(multOutputBitWidth bits)

  override def createPeInputTypeC(index: ProcessingElementIndex): UInt = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        UInt( (multOutputBitWidth + safeLog2Up(index.peColIndex)) bits)
      case Dataflow.ReuseB =>
        UInt( (multOutputBitWidth + safeLog2Up(index.peRowIndex)) bits)
              case Dataflow.ReuseC =>
        UInt( systolicOutputBitwidth bits )
    }
  }

  override def createPeOutputTypeC(index: ProcessingElementIndex): UInt = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        UInt( (multOutputBitWidth + safeLog2Up(index.peColIndex + 1)) bits)
      case Dataflow.ReuseB =>
        UInt( (multOutputBitWidth + safeLog2Up(index.peRowIndex + 1)) bits)
              case Dataflow.ReuseC =>
        UInt( systolicOutputBitwidth bits )
    }
  }

  override def createSystolicOutputTypeC: UInt = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        UInt( (multOutputBitWidth + safeLog2Up(arrayConfig.col)) bits)
      case Dataflow.ReuseB =>
        UInt( (multOutputBitWidth + safeLog2Up(arrayConfig.row)) bits)
      case Dataflow.ReuseC =>
        UInt( systolicOutputBitwidth bits )
    }
  }


}
