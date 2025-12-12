package systolic

import spinal.core._
import systolic.Arithmetic._
import systolic.BFloat16Arithmetic._
import systolic.Float16Arithmetic._

/**
 * Systolic Array Factory
 *
 * Creates systolic arrays for different data types:
 * - Signed Integer (SInt)
 * - Unsigned Integer (UInt)
 * TODO change bits to floating point type
 * - BFloat16 with FP32 accumulation (Bits)
 * - FP16 with FP32 accumulation (Bits)
 */
object SystolicArray {

  def apply(arrayConfig: SystolicArrayConfig): Component = {
    arrayConfig.dataTypeConfig match {
      case intConfig: IntegerDataTypeConfig =>
        createIntegerArray(arrayConfig, intConfig)

      case _: BFloat16DataTypeConfig =>
        createBFloat16Array(arrayConfig)

      case _: Float16DataTypeConfig =>
        createFloat16Array(arrayConfig)
    }
  }

  private def createIntegerArray(
                                  arrayConfig: SystolicArrayConfig,
                                  intConfig: IntegerDataTypeConfig
                                ): Component = {
    val bitWidthPair = intConfig.portBitWidthInfo

    intConfig.integerType match {
      case IntegerType.SignedInteger =>
        implicit val arithmetic: Arithmetic[SInt] = sIntArithmetic
        implicit val portType: PortTypeProvider[SInt] = new SignedPortTypeProvider(
          arrayConfig,
          bitWidthPair.bitWidthInputA,
          bitWidthPair.bitWidthInputB,
          bitWidthPair.bitWidthSystolicOutputC
        )
        new SystolicArray[SInt](arrayConfig)(arithmetic, portType)

      case IntegerType.UnsignedInteger =>
        implicit val arithmetic: Arithmetic[UInt] = uIntArithmetic
        implicit val portType: PortTypeProvider[UInt] = new UnsignedPortTypeProvider(
          arrayConfig,
          bitWidthPair.bitWidthInputA,
          bitWidthPair.bitWidthInputB,
          bitWidthPair.bitWidthSystolicOutputC
        )
        new SystolicArray[UInt](arrayConfig)(arithmetic, portType)
    }
  }

  private def createBFloat16Array(arrayConfig: SystolicArrayConfig): Component = {
    implicit val arithmetic: Arithmetic[Bits] = bf16Fp32Arithmetic
    implicit val portType: PortTypeProvider[Bits] = new BFloat16PortTypeProvider(arrayConfig)
    new SystolicArray[Bits](arrayConfig)(arithmetic, portType)
  }

  private def createFloat16Array(arrayConfig: SystolicArrayConfig): Component = {
    implicit val arithmetic: Arithmetic[Bits] = fp16Fp32Arithmetic
    implicit val portType: PortTypeProvider[Bits] = new Float16PortTypeProvider(arrayConfig)
    new SystolicArray[Bits](arrayConfig)(arithmetic, portType)
  }
}

/**
 * Parameterized Systolic Array Component
 *
 * @tparam T Data type for the array (SInt, UInt, or Bits for BFloat16)
 * @param arrayConfig Configuration for the array
 * @param arithmetic Implicit arithmetic operations for type T
 * @param portType Implicit port type provider for type T
 */
class SystolicArray[T <: Data](
                                val arrayConfig: SystolicArrayConfig
                              )(
                                implicit val arithmetic: Arithmetic[T],
                                implicit val portType: PortTypeProvider[T],
                              ) extends Component {

  // Set definition name based on data type and configuration
  private val dataTypeName = arrayConfig.dataTypeConfig match {
    case _: BFloat16DataTypeConfig => "BF16"
    case _: Float16DataTypeConfig => "FP16"
    case ic: IntegerDataTypeConfig => ic.integerType match {
      case IntegerType.SignedInteger => "SInt"
      case IntegerType.UnsignedInteger => "UInt"
    }
  }

  setDefinitionName(s"${dataTypeName}_${arrayConfig.dataflow}_SystolicArray_${arrayConfig.row}x${arrayConfig.col}")

  private def buildOutputNum: Int = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        arrayConfig.row
      case Dataflow.ReuseB =>
        arrayConfig.col
      case Dataflow.ReuseC =>
        arrayConfig.row + arrayConfig.col - 1
    }
  }

  val io = new Bundle {
    val inputA = in Vec(portType.createInputTypeA, arrayConfig.row)
    val inputB = in Vec(portType.createInputTypeB, arrayConfig.col)

    val inputCaptureEnableA = (arrayConfig.dataflow == Dataflow.ReuseA) generate in {
      Vec.fill(arrayConfig.row, arrayConfig.col){Bool()}
    }

    val inputCaptureEnableB = (arrayConfig.dataflow == Dataflow.ReuseB) generate in {
      Vec.fill(arrayConfig.row, arrayConfig.col){Bool()}
    }

    val outputCaptureEnableC = (arrayConfig.dataflow == Dataflow.ReuseC) generate in {
      Vec.fill(arrayConfig.row, arrayConfig.col){Bool()}
    }

    val resetPartialC = (arrayConfig.dataflow == Dataflow.ReuseC) generate in {
      Vec.fill(arrayConfig.row, arrayConfig.col){Bool()}
    }

    val outputC = out Vec(portType.createSystolicOutputTypeC, buildOutputNum)
  }

  val pes = Array.tabulate(arrayConfig.row, arrayConfig.col) { (r,c) =>

    val index = ProcessingElementIndex(r,c)

    val withOutputPortA = !index.isLastPeCol(arrayConfig.col)
    val withOutputPortB = !index.isLastPeRow(arrayConfig.row)
    val withInputPortC = arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        !index.isFirstCol
      case Dataflow.ReuseB =>
        !index.isFirstRow
      case Dataflow.ReuseC =>
        if (r > 0 && c < arrayConfig.col - 1) {
          canConnectDiagonally(r - 1, c + 1)
        } else {
          false
        }
    }

    val portEnableMask = PortEnableMask(withOutputPortA, withOutputPortB, withInputPortC)

    new ProcessingElement(
      index = index,
      portEnableMask = portEnableMask,
      dataflow = arrayConfig.dataflow
    )(arithmetic, portType)
  }

  wireA()
  wireB()
  wireControl()
  wireC()

  private def wireA(): Unit = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        for(r <- 0 until arrayConfig.row)
          pes(r)(0).io.inputA := RegNext(io.inputA(r))

        for{
          r <- 0 until arrayConfig.row
          c <- 1 until arrayConfig.col
        } {
          pes(r)(c).io.inputA := pes(r)(c-1).io.outputA
        }

      case Dataflow.ReuseB | Dataflow.ReuseC =>
        val skewBuffer = new SkewBuffer(portType.createInputTypeA, arrayConfig.row)

        for(r <- 0 until arrayConfig.row){
          skewBuffer.io.input(r) := io.inputA(r)
          pes(r)(0).io.inputA := skewBuffer.io.output(r)
        }

        for{
          r <- 0 until arrayConfig.row
          c <- 1 until arrayConfig.col
        } {
          pes(r)(c).io.inputA := pes(r)(c-1).io.outputA
        }
    }
  }

  private def wireB(): Unit = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA | Dataflow.ReuseC =>
        val skewBuffer = new SkewBuffer(portType.createInputTypeB, arrayConfig.col)

        for(c <- 0 until arrayConfig.col){
          skewBuffer.io.input(c) := io.inputB(c)
          pes(0)(c).io.inputB := skewBuffer.io.output(c)
        }

        for{
          r <- 1 until arrayConfig.row
          c <- 0 until arrayConfig.col
        } {
          pes(r)(c).io.inputB := pes(r-1)(c).io.outputB
        }

      case Dataflow.ReuseB =>
        for(c <- 0 until arrayConfig.col)
          pes(0)(c).io.inputB := RegNext(io.inputB(c))

        for{
          r <- 1 until arrayConfig.row
          c <- 0 until arrayConfig.col
        } {
          pes(r)(c).io.inputB := pes(r-1)(c).io.outputB
        }
    }
  }

  private def wireControl(): Unit = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        for {
          r <- 0 until arrayConfig.row
          c <- 0 until arrayConfig.col
        } {
          pes(r)(c).io.inputCaptureEnableA := io.inputCaptureEnableA(r)(c)
        }

      case Dataflow.ReuseB =>
        for {
          r <- 0 until arrayConfig.row
          c <- 0 until arrayConfig.col
        } {
          pes(r)(c).io.inputCaptureEnableB := io.inputCaptureEnableB(r)(c)
        }

      case Dataflow.ReuseC =>
        for {
          r <- 0 until arrayConfig.row
          c <- 0 until arrayConfig.col
        } {
          pes(r)(c).io.outputCaptureEnableC := io.outputCaptureEnableC(r)(c)
          pes(r)(c).io.resetPartialC := io.resetPartialC(r)(c)
        }
    }
  }

  private def wireC(): Unit = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        val deskewBuffer = new SkewBuffer(
          inputType = portType.createSystolicOutputTypeC,
          delayDepth = arrayConfig.row,
          isMinDepthFirst = false
        )

        for {
          r <- 0 until arrayConfig.row
          c <- 1 until arrayConfig.col
        } {
          pes(r)(c).io.inputC := pes(r)(c-1).io.outputC
        }

        for (r <- 0 until arrayConfig.row) {
          deskewBuffer.io.input(r) := pes(r)(arrayConfig.col - 1).io.outputC
          io.outputC(r) := deskewBuffer.io.output(r)
        }

      case Dataflow.ReuseB =>
        val deskewBuffer = new SkewBuffer(
          inputType = portType.createSystolicOutputTypeC,
          delayDepth = arrayConfig.col,
          isMinDepthFirst = false
        )

        for {
          r <- 1 until arrayConfig.row
          c <- 0 until arrayConfig.col
        } {
          pes(r)(c).io.inputC := pes(r-1)(c).io.outputC
        }

        for (c <- 0 until arrayConfig.col) {
          deskewBuffer.io.input(c) := pes(arrayConfig.row - 1)(c).io.outputC
          io.outputC(c) := deskewBuffer.io.output(c)
        }

      case Dataflow.ReuseC =>
        for {
          r <- 0 until arrayConfig.row
          c <- 0 until arrayConfig.col
        } {
          // Connect diagonal PE connections
          if (canConnectDiagonally(r, c)) {
            val targetPe = pes(r+1)(c-1)
            val currentPe = pes(r)(c)
            targetPe.io.inputC := currentPe.io.outputC
          }

          val deskewBuffer = new DeskewBufferReuseC(portType.createSystolicOutputTypeC, arrayConfig)

          if (isOutputPosition(r, c)) {
            val outputIndex = getOutputIndex(r, c)
            deskewBuffer.io.input(outputIndex) := pes(r)(c).io.outputC
            io.outputC(outputIndex) := deskewBuffer.io.output(outputIndex)
          }
        }
    }
  }

  //TODO recode helper functions
  private def getOutputIndex(r: Int, c: Int): Int = {
    if (r == 0 && c == 0) {
      0
    } else if (r > 0 && r < arrayConfig.row && c == 0) {
      r
    } else if (r == arrayConfig.row - 1 && c > 0 && c < arrayConfig.col - 1) {
      arrayConfig.row + c - 1
    } else if (r == arrayConfig.row - 1 && c == arrayConfig.col - 1) {
      arrayConfig.row + arrayConfig.col - 2
    } else {
      throw new Exception(s"Invalid output position: ($r, $c)")
    }
  }

  private def isOutputPosition(r: Int, c: Int): Boolean = {
    val isFirstElement = r == 0 && c == 0
    val isLeftOrBottomEdge = (0 < r && r < arrayConfig.row && c == 0) ||
      (r == arrayConfig.row - 1 && 0 < c && c < arrayConfig.col - 1)
    val isLastElement = r == arrayConfig.row - 1 && c == arrayConfig.col - 1

    isFirstElement || isLeftOrBottomEdge || isLastElement
  }

  private def canConnectDiagonally(r: Int, c: Int): Boolean = {
    val isRightEdgeExceptLast = (0 <= r && r < arrayConfig.row - 1 && c == arrayConfig.col - 1)
    val isTopEdgeExceptFirst = (r == 0 && 0 < c && c < arrayConfig.col - 1)
    val isMiddle = (0 < r && r < arrayConfig.row - 1 && 0 < c && c < arrayConfig.col - 1)

    isRightEdgeExceptLast || isTopEdgeExceptFirst || isMiddle
  }
}