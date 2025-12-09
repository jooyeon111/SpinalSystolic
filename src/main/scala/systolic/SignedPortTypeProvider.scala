package systolic

import spinal.core._

class SignedPortTypeProvider(
                            val arrayConfig: SystolicArrayConfig,
                            val bitWidthInputA: Int,
                            val bitWidthInputB: Int,
                            val bitWidthSystolicOutputC: Option[Int],
                            ) extends PortTypeProvider[SInt]{

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
        assert(bitWidthSystolicOutputC.isDefined, "[error]")
        bitWidthSystolicOutputC.get

    }
  }

  override def createInputTypeA: SInt = SInt(bitWidthInputA bits)
  override def createInputTypeB: SInt = SInt(bitWidthInputB bits)
  override def createMultOutputType: SInt = SInt(multOutputBitWidth bits)

  override def createPeInputTypeC(index: ProcessingElementIndex): SInt = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        SInt( (multOutputBitWidth + safeLog2Up(index.peColIndex)) bits)
      case Dataflow.ReuseB =>
        SInt( (multOutputBitWidth + safeLog2Up(index.peRowIndex)) bits)
              case Dataflow.ReuseC =>
        SInt( systolicOutputBitwidth bits )
    }
  }

  override def createPeOutputTypeC(index: ProcessingElementIndex): SInt = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        SInt( (multOutputBitWidth + safeLog2Up(index.peColIndex + 1)) bits)
      case Dataflow.ReuseB =>
        SInt( (multOutputBitWidth + safeLog2Up(index.peRowIndex + 1)) bits)
      case Dataflow.ReuseC =>
        SInt( systolicOutputBitwidth bits )
    }
  }

  override def createSystolicOutputTypeC: SInt = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        SInt( (multOutputBitWidth + safeLog2Up(arrayConfig.col)) bits)
      case Dataflow.ReuseB =>
        SInt( (multOutputBitWidth + safeLog2Up(arrayConfig.row)) bits)
      case Dataflow.ReuseC =>
        SInt( systolicOutputBitwidth bits )
    }
  }


}
