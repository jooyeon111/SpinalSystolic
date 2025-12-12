package systolic

import spinal.core._
/**
 * Port type provider for systolic array processing elements.
 *
 * Determines appropriate hardware bit widths for all ports based on:
 * - Dataflow pattern (ReuseA, ReuseB, ReuseC)
 * - Position in the array (accumulation depth varies by position)
 * - Input bit widths
 *
 * Bit width calculation logic:
 * - '''ReuseA''' (Weight Stationary): Accumulation grows with column index
 * - '''ReuseB''' (Input Stationary): Accumulation grows with row index
 * - '''ReuseC''' (Output Stationary): Fixed accumulation width (user-specified)
 *
 * @tparam T Hardware data type (SInt or UInt)
 *
 * @example
 * {{{
 *   val provider = PortTypeProvider.signed(
 *     arrayConfig = config,
 *     bitWidthInputA = 8,
 *     bitWidthInputB = 8,
 *     bitWidthSystolicOutputC = Some(32)
 *   )
 *   val inputType = provider.createInputTypeA  // SInt(8 bits)
 * }}}
 */
trait PortTypeProvider [T <: Data] {

  def createInputTypeA: T
  def createInputTypeB: T
  def createMultOutputType: T
  def createPeInputTypeC(index: ProcessingElementIndex): T
  def createPeOutputTypeC(index: ProcessingElementIndex): T
  def createSystolicOutputTypeC: T

  final def zeroInputA(implicit arithmetic: Arithmetic[T]): T = arithmetic.zero(createInputTypeA.getBitsWidth)
  final def zeroInputB(implicit arithmetic: Arithmetic[T]): T = arithmetic.zero(createInputTypeB.getBitsWidth)
  final def zeroMultOutput(implicit arithmetic: Arithmetic[T]): T = arithmetic.zero(createMultOutputType.getBitsWidth)
  final def zeroPeInputTypeC(index: ProcessingElementIndex)(implicit arithmetic: Arithmetic[T]): T  =
    arithmetic.zero(createPeInputTypeC(index).getBitsWidth)
  final def zeroPeOutputTypeC(index: ProcessingElementIndex)(implicit arithmetic: Arithmetic[T]): T  =
    arithmetic.zero(createPeOutputTypeC(index).getBitsWidth)
    final def zeroSystolicOutputTypeC(index: ProcessingElementIndex)(implicit arithmetic: Arithmetic[T]): T =
    arithmetic.zero(createSystolicOutputTypeC.getBitsWidth)

}

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
        assert(bitWidthSystolicOutputC.isDefined,
          "Reuse C (Output Stationary) dataflow requires explict output bit width")
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
