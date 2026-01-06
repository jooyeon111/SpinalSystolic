package systolic

import spinal.core._
import systolic.Arithmetic._
/**
 * Processing Element (PE) for systolic array computation.
 *
 * Each PE performs multiply-accumulate operations with different behaviors
 * based on the dataflow type:
 *
 * '''ReuseA''': Captures input A, registers input B
 * {{{
 *   outputC = inputA_captured * inputB_flowing + inputC
 * }}}
 *
 * '''ReuseB''': Registers input A, captures input B
 * {{{
 *   outputC = inputA_flowing * inputB_captured + inputC
 * }}}
 *
 * '''ReuseC''': Accumulates partial sums
 * {{{
 *   partialSum = partialSum + (inputA * inputB)
 *   outputC = resetPartialC ? (inputA * inputB) : partialSum
 * }}}
 *
 * @param index Position in systolic array (row, column)
 * @param portEnableMask Controls which I/O ports are enabled
 * @param dataflow Dataflow architecture type
 * @param arithmetic Implicit arithmetic operations provider
 * @param portType Implicit port type provider
 * @tparam T Data type for computation
 */

object ProcessingElement {

  def apply(dataflow: Dataflow.Value): ProcessingElement[SInt, SInt] = {

    val defaultIndex = ProcessingElementIndex.defaultProcessingElementIndex
    val portEnableMask = PortEnableMask.defaultPortEnableMask
    val defaultBitWidth = PortBitWidthInfo.default8bitInputWith32bitOutput

    implicit val arithmetic: Arithmetic[SInt, SInt] = sIntArithmetic

    val arrayConfig = SignedIntConfig(
      SystolicArraySize.defaultSystolicArraySize,
      dataflow,
      defaultBitWidth.bitWidthInputA,
      defaultBitWidth.bitWidthInputB,
      defaultBitWidth.bitWidthSystolicOutputC,
    )

    implicit val portType: PortTypeProvider[SInt, SInt] = new SignedPortTypeProvider(
      arrayConfig = arrayConfig
    )

    new ProcessingElement[SInt, SInt](defaultIndex, portEnableMask, dataflow)(arithmetic, portType)

  }

}

class ProcessingElement[InputType <: Data, AccType <: Data](
                                  val index: ProcessingElementIndex,
                                  val portEnableMask: PortEnableMask,
                                  val dataflow: Dataflow.Value,
                                  )(
                                  implicit val arithmetic: Arithmetic[InputType, AccType],
                                  implicit val portType: PortTypeProvider[InputType, AccType],
) extends Component {

  val io = new Bundle {

    val inputA = in (portType.createInputTypeA)
    val inputB = in (portType.createInputTypeB)
    val inputC = portEnableMask.withInputPortC generate in {
      portType.createPeInputTypeC(index = index)
    }

    val inputCaptureEnableA = (dataflow == Dataflow.ReuseA) generate {in Bool()}
    val inputCaptureEnableB = (dataflow == Dataflow.ReuseB) generate {in Bool()}
    val outputCaptureEnableC = (dataflow == Dataflow.ReuseC) generate {in Bool()}
    val resetPartialC = (dataflow == Dataflow.ReuseC) generate in {Bool()}

    val outputA = portEnableMask.withOutputPortA generate out (portType.createInputTypeA)
    val outputB = portEnableMask.withOutputPortB generate out (portType.createInputTypeB)
    val outputC = out (portType.createPeOutputTypeC(index = index))

  }

  private def captureInput(input: InputType, inputCaptureEnable: Bool, zeroValue: InputType): InputType = {
    RegNextWhen(
      next = input,
      cond = !inputCaptureEnable,
      init = zeroValue
    )
  }

  private def registerInput(input: InputType, zeroValue: InputType): InputType = {
    RegNext(input, init = zeroValue)
  }

  private def multiply(inputA : InputType, inputB : InputType): AccType = {
    arithmetic.multiply(inputA, inputB)
  }

  dataflow match {
    case Dataflow.ReuseA =>
      buildReuseA()
    case Dataflow.ReuseB =>
      buildReuseB()
    case Dataflow.ReuseC =>
      buildReuseC()
  }

  private def buildReuseA(): Unit = {
    val capturedA = captureInput(io.inputA, io.inputCaptureEnableA, portType.zeroInputA)

    if(portEnableMask.withOutputPortA)
      io.outputA := capturedA

    if(portEnableMask.withOutputPortB)
      io.outputB := registerInput(io.inputB, portType.zeroInputB)

    val product = multiply(capturedA, io.inputB)
    io.outputC := addInputC(product)

  }

  private def buildReuseB(): Unit = {
    val capturedB = captureInput(io.inputB, io.inputCaptureEnableB, portType.zeroInputB)

    if(portEnableMask.withOutputPortA)
      io.outputA := registerInput(io.inputA, portType.zeroInputA)

    if(portEnableMask.withOutputPortB)
      io.outputB := capturedB

    val product = multiply(io.inputA, capturedB)
    io.outputC := addInputC(product)

  }

  private def buildReuseC(): Unit = {

    if (portEnableMask.withOutputPortA)
      io.outputA := registerInput(io.inputA, portType.zeroInputA)

    if (portEnableMask.withOutputPortB)
      io.outputB := registerInput(io.inputB, portType.zeroInputB)

    val product = multiply(io.inputA, io.inputB)

    val zero = portType.zeroPeOutputTypeC(index)
    val partialSum = Reg(portType.createPeOutputTypeC(index)) init zero

    val accumulatedValue = Mux(
      io.resetPartialC,
      product,
      arithmetic.addResize(product, partialSum, portType.createPeOutputTypeC(index).getBitsWidth)
    )

    partialSum := accumulatedValue

    if(portEnableMask.withInputPortC){
      io.outputC := RegNext(Mux(io.outputCaptureEnableC, partialSum, io.inputC), zero)
    } else {
      io.outputC := partialSum
    }

  }

  private def addInputC(multiplyResult: AccType): AccType = {
    val zero = portType.zeroPeOutputTypeC(index)
    val outputWidth = portType.createPeOutputTypeC(index).getBitsWidth

    if(portEnableMask.withInputPortC){
      RegNext(arithmetic.addResize(multiplyResult, io.inputC, outputWidth), init = zero)
    } else {
      RegNext(multiplyResult.resized, init = zero)
    }
  }

}
