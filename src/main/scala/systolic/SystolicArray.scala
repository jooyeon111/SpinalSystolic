package systolic

import spinal.core._
import systolic.IntegerArithmetic._
/**
 * Systolic array for GEMM
 *
 *
 * Supports three dataflow types:
 *  - '''ReuseA (Input Stationary)''': Input matrix A remains in PEs
 *  - '''ReuseB (Weight Stationary)''': Weight matrix B remains in PEs
 *  - '''ReuseC (Output Stationary)''': Output matrix C remains in PEs
 *
 * @example {{{
 * val config = SystolicArrayConfig(
 *   row = 8,
 *   col = 8,
 *   dataflow = Dataflow.ReuseA,
 *   integerConfig = IntegerConfig(
 *     IntegerType.SignedInteger,
 *     PortBitWidthInfo(8, 8)
 *   )
 * )
 * val array = SystolicArray(config)
 * }}}
 *
 * @param arrayConfig Configuration for systolic array dimensions and dataflow
 * @param arithmetic Arithmetic operations for type T (multiply, add, etc.)
 * @param portType Provider for creating port types with appropriate bit widths
 * @tparam T Data type (SInt or UInt)
 *
 */
object SystolicArray {

  def apply(arrayConfig: SystolicArrayConfig): Component = {
    arrayConfig.integerConfig.integerType match {
      case IntegerType.SignedInteger =>
        val bitWidthPair = arrayConfig.integerConfig.portBitWidthInfo
        implicit val arithmetic: Arithmetic[SInt] = sIntArithmetic
        implicit val portType: PortTypeProvider[SInt] = new SignedPortTypeProvider(
          arrayConfig,
          bitWidthPair.bitWidthInputA,
          bitWidthPair.bitWidthInputB,
          bitWidthPair.bitWidthSystolicOutputC
        )
        new SystolicArray[SInt](arrayConfig)(arithmetic, portType)

      case IntegerType.UnsignedInteger =>
        val bitWidthPair = arrayConfig.integerConfig.portBitWidthInfo
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
}

class SystolicArray[T <: Data](
                   val arrayConfig: SystolicArrayConfig
                   )(
                   implicit val arithmetic: Arithmetic[T],
                   implicit val portType: PortTypeProvider[T],
) extends Component {

  setDefinitionName(s"${arrayConfig.dataflow}_SystolicArray_${arrayConfig.row}x${arrayConfig.col}")

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
        for(r <- 0 until arrayConfig.row )
          pes(r)(0).io.inputA := RegNext(io.inputA(r))

        for{
          r <- 0 until  arrayConfig.row
          c <- 1 until  arrayConfig.col
        } {
          pes(r)(c).io.inputA := pes(r)(c-1).io.outputA
        }

      case Dataflow.ReuseB | Dataflow.ReuseC=>

        val skewBuffer = new SkewBuffer(portType.createInputTypeA, arrayConfig.row)

        for( r <- 0 until arrayConfig.row){
          skewBuffer.io.input(r) := io.inputA(r)
          pes(r)(0).io.inputA := skewBuffer.io.output(r)
        }

        for{
          r <- 0 until  arrayConfig.row
          c <- 1 until  arrayConfig.col
        } {
          pes(r)(c).io.inputA := pes(r)(c-1).io.outputA
        }

    }
  }

  private def wireB(): Unit = {

    arrayConfig.dataflow match {
      case Dataflow.ReuseA | Dataflow.ReuseC =>

        val skewBuffer = new SkewBuffer(portType.createInputTypeB, arrayConfig.col)

        for( c <- 0 until arrayConfig.col){
          skewBuffer.io.input(c) := io.inputB(c)
          pes(0)(c).io.inputB := skewBuffer.io.output(c)
        }

        for{
          r <- 1 until  arrayConfig.row
          c <- 0 until  arrayConfig.col
        } {
          pes(r)(c).io.inputB := pes(r-1)(c).io.outputB
        }


      case Dataflow.ReuseB =>

        for(c <- 0 until arrayConfig.col )
          pes(0)(c).io.inputB := RegNext(io.inputB(c))

        for{
          r <- 1 until  arrayConfig.row
          c <- 0 until  arrayConfig.col
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
          deskewBuffer.io.input(r) := pes(r)(arrayConfig.col -1).io.outputC
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
//          io.outputC(c) := pes(arrayConfig.row -1)(c).io.outputC
          deskewBuffer.io.input(c) := pes(arrayConfig.row-1)(c).io.outputC
          io.outputC(c) := deskewBuffer.io.output(c)
        }

      case Dataflow.ReuseC =>

        val deskewBuffer = new DeskewBufferReuseC(portType.createSystolicOutputTypeC, arrayConfig)

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

          if (isOutputPosition(r, c)) {
            val outputIndex = getOutputIndex(r, c)
            deskewBuffer.io.input(outputIndex) := pes(r)(c).io.outputC
            io.outputC(outputIndex) := deskewBuffer.io.output(outputIndex)
          }


        }
    }
  }

  //TODO add comments
  private def getOutputIndex(r: Int, c: Int): Int = {
    if (r == 0 && c == 0) {
      // First element
      0
    } else if (r > 0 && r < arrayConfig.row && c == 0) {
      // Left edge (excluding corners)
      r
    } else if (r == arrayConfig.row - 1 && c > 0 && c < arrayConfig.col - 1) {
      // Bottom edge (excluding corners)
      arrayConfig.row + c - 1
    } else if (r == arrayConfig.row - 1 && c == arrayConfig.col - 1) {
      // Last element
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
