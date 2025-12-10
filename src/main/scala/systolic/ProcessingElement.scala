package systolic

import spinal.core._
import systolic.IntegerArithmetic._
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

  def apply(dataflow: Dataflow.Value): ProcessingElement[SInt] = {

    val defaultIndex = ProcessingElementIndex(
      peRowIndex = 0,
      peColIndex = 0,
    )

    val portEnableMask = PortEnableMask(
      withOutputPortA = true,
      withOutputPortB = true,
      withInputPortC = true
    )

    implicit val arithmetic: Arithmetic[SInt] = sIntArithmetic

    val arrayConfig = SystolicArrayConfig(
      1,
      1,
      dataflow,
      IntegerConfig(
        IntegerType.SignedInteger,
        PortBitWidthInfo(8, 8)
      )
    )

    implicit val portType: PortTypeProvider[SInt] = new SignedPortTypeProvider(
      arrayConfig = arrayConfig,
      bitWidthInputA = 8,
      bitWidthInputB = 8,
      bitWidthSystolicOutputC = Some(32)
    )

    new ProcessingElement[SInt](defaultIndex, portEnableMask, dataflow)(arithmetic, portType)

  }

}

class ProcessingElement[T <: Data](
                                  val index: ProcessingElementIndex,
                                  val portEnableMask: PortEnableMask,
//                                  val arrayConfig: SystolicArrayConfig,
                                  val dataflow: Dataflow.Value,
                                  )(
                                  implicit val arithmetic: Arithmetic[T],
                                  implicit val portType: PortTypeProvider[T],
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

  private def captureInput(input: T, inputCaptureEnable: Bool, zeroValue: T): T = {

    val captureInput = RegNext(input, init = zeroValue)

    when(inputCaptureEnable){
      captureInput := input
    }
    captureInput

  }

  private def registerInput(input: T, zeroValue: T): T = {
    val registerInput = RegNext(input, init = zeroValue)
    registerInput
  }

  private def multiply(inputA : T, inputB : T): T = {
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

    val accumulatedValue = Mux(io.resetPartialC, product, arithmetic.addResize(product, partialSum, portType.createPeOutputTypeC(index).getBitsWidth))
    partialSum := accumulatedValue

    if(portEnableMask.withInputPortC){
      io.outputC := Mux(io.outputCaptureEnableC, partialSum, io.inputC)
    } else {
      io.outputC := partialSum
    }

  }

  private def addInputC(multiplyResult: T): T = {
    val zero = portType.zeroPeOutputTypeC(index)
    val outputWidth = portType.createPeOutputTypeC(index).getBitsWidth

    if(portEnableMask.withInputPortC){
      RegNext(arithmetic.addResize(multiplyResult, io.inputC, outputWidth), init = zero)
    } else {
      RegNext(multiplyResult.resized, init = zero)
    }
  }

}
