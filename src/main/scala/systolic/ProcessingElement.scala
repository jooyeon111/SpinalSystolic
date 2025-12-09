package systolic

import spinal.core._

class ProcessingElement[T <: Data](
                                  val index: ProcessingElementIndex,
                                  val portEnableMask: PortEnableMask,
                                  val arrayConfig: SystolicArrayConfig,
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

    val inputCaptureEnableA = (arrayConfig.dataflow == Dataflow.ReuseA) generate {in Bool()}
    val inputCaptureEnableB = (arrayConfig.dataflow == Dataflow.ReuseB) generate {in Bool()}
    val outputCaptureEnableC = (arrayConfig.dataflow == Dataflow.ReuseC) generate {in Bool()}
    val resetPartialC = (arrayConfig.dataflow == Dataflow.ReuseC) generate in {Bool()}

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

  arrayConfig.dataflow match {
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

    if(portEnableMask.withOutputPortB)
      io.outputB := capturedB

    if(portEnableMask.withOutputPortA)
      io.outputA := registerInput(io.inputA, portType.zeroInputB)

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

//    io.outputC :=  Mux(io.outputCaptureEnableC, partialSum, io.inputC)
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
