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
 * - BFloat16 with FP32 accumulation (Bits)
 * - FP16 with FP32 accumulation (Bits)
 */
//object SystolicArray {
//
//  def apply(arrayConfig: SystolicArrayConfig): SystolicArray[_ <: Data, _ <: Data] = {
//    arrayConfig.dataTypeConfig match {
//      case intConfig: IntegerDataTypeConfig =>
//        createIntegerArray(arrayConfig, intConfig)
//
//      case _: BFloat16DataTypeConfig =>
//        createBFloat16Array(arrayConfig)
//
//      case _: Float16DataTypeConfig =>
//        createFloat16Array(arrayConfig)
//    }
//  }
//
//  private def createIntegerArray(
//                                  arrayConfig: SystolicArrayConfig,
//                                  intConfig: IntegerDataTypeConfig
//                                ): SystolicArray[_ <: Data, _ <: Data] = {
//    val bitWidthPair = intConfig.portBitWidthInfo
//
//    intConfig.integerType match {
//      case IntegerType.SignedInteger =>
//        implicit val arithmetic: Arithmetic[SInt, SInt] = sIntArithmetic
//        implicit val portType: PortTypeProvider[SInt, SInt] = new SignedPortTypeProvider(
//          arrayConfig,
//          bitWidthPair.bitWidthInputA,
//          bitWidthPair.bitWidthInputB,
//          bitWidthPair.bitWidthSystolicOutputC
//        )
//        new SystolicArray[SInt, SInt](arrayConfig)(arithmetic, portType)
//
//      case IntegerType.UnsignedInteger =>
//        implicit val arithmetic: Arithmetic[UInt, UInt] = uIntArithmetic
//        implicit val portType: PortTypeProvider[UInt, UInt] = new UnsignedPortTypeProvider(
//          arrayConfig,
//          bitWidthPair.bitWidthInputA,
//          bitWidthPair.bitWidthInputB,
//          bitWidthPair.bitWidthSystolicOutputC
//        )
//        new SystolicArray[UInt, UInt](arrayConfig)(arithmetic, portType)
//    }
//  }
//
//  private def createBFloat16Array(arrayConfig: SystolicArrayConfig): SystolicArray[_ <: Data, _ <: Data] = {
//    implicit val arithmetic: Arithmetic[BFloat16, Float32] = bf16Fp32Arithmetic
//    implicit val portType: PortTypeProvider[BFloat16, Float32] = new BFloat16PortTypeProvider(arrayConfig)
//    new SystolicArray[BFloat16, Float32](arrayConfig)(arithmetic, portType)
//  }
//
//  private def createFloat16Array(arrayConfig: SystolicArrayConfig): SystolicArray[_ <: Data, _ <: Data] = {
//    implicit val arithmetic: Arithmetic[Float16, Float32] = fp16Fp32Arithmetic
//    implicit val portType: PortTypeProvider[Float16, Float32] = new Float16PortTypeProvider(arrayConfig)
//    new SystolicArray[Float16, Float32](arrayConfig)(arithmetic, portType)
//  }
//
//  object Sim {
//    def asBFloat16(arrayConfig: SystolicArrayConfig): SystolicArray[BFloat16, Float32] = {
//      require(arrayConfig.isBFloat16, "Must be BFloat16 config")
//      SystolicArray(arrayConfig).asInstanceOf[SystolicArray[BFloat16, Float32]]
//    }
//
//    def asFloat16(arrayConfig: SystolicArrayConfig): SystolicArray[Float16, Float32] = {
//      require(arrayConfig.isFloat16, "Must be Float16 config")
//      SystolicArray(arrayConfig).asInstanceOf[SystolicArray[Float16, Float32]]
//    }
//
//    def asSignedInt(arrayConfig: SystolicArrayConfig): SystolicArray[SInt, SInt] = {
//      require(arrayConfig.isInteger, "Must be integer type")
//      SystolicArray(arrayConfig).asInstanceOf[SystolicArray[SInt, SInt]]
//    }
//
//    def asUnsignedInt(arrayConfig: SystolicArrayConfig): SystolicArray[UInt, UInt] = {
//      require(arrayConfig.isInteger, "Must be integer type")
//      SystolicArray(arrayConfig).asInstanceOf[SystolicArray[UInt, UInt]]
//    }
//  }
//
//}

object SystolicArray {

  def apply(config: BFloat16Config): SystolicArray[BFloat16, Float32] = {
    implicit val arithmetic: Arithmetic[BFloat16, Float32] = bf16Fp32Arithmetic
    implicit val portType: BFloat16PortTypeProvider = new BFloat16PortTypeProvider(config)
    new SystolicArray[BFloat16, Float32](config)(arithmetic, portType)
  }

  def apply(config: Float16Config): SystolicArray[Float16, Float32] = {
    implicit val arithmetic: Arithmetic[Float16, Float32] = fp16Fp32Arithmetic
    implicit val portType: Float16PortTypeProvider = new Float16PortTypeProvider(config)
    new SystolicArray[Float16, Float32](config)(arithmetic, portType)
  }

  def apply(config: SignedIntConfig): SystolicArray[SInt, SInt] = {
    implicit val arithmetic: Arithmetic[SInt, SInt] = sIntArithmetic
    implicit val portType: SignedPortTypeProvider = new SignedPortTypeProvider(config)
    new SystolicArray[SInt, SInt](config)(arithmetic, portType)
  }

  def apply(config: UnsignedIntConfig): SystolicArray[UInt, UInt] = {  // ✅ Unsigned 처리
    implicit val arithmetic: Arithmetic[UInt, UInt] = uIntArithmetic
    implicit val portType: UnsignedPortTypeProvider = new UnsignedPortTypeProvider(config)
    new SystolicArray[UInt, UInt](config)(arithmetic, portType)
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
class SystolicArray[InputType <: Data, AccType <: Data](
                                val arrayConfig: SystolicArrayConfig
                              )(
                                implicit val arithmetic: Arithmetic[InputType, AccType],
                                implicit val portType: PortTypeProvider[InputType, AccType],
                              ) extends Component {

  // Set definition name based on data type and configuration
//  private val dataTypeName = arrayConfig.dataTypeConfig match {
//    case _: BFloat16DataTypeConfig => "BF16"
//    case _: Float16DataTypeConfig => "FP16"
//    case ic: IntegerDataTypeConfig => ic.integerType match {
//      case IntegerType.SignedInteger => "SInt"
//      case IntegerType.UnsignedInteger => "UInt"
//    }
//  }

  private val dataTypeName = arrayConfig match {
    case _: BFloat16Config => "BF16"
    case _: Float16Config => "FP16"
    case _: SignedIntConfig => "SInt"
    case _: UnsignedIntConfig => "UInt"
  }

  setDefinitionName(s"${dataTypeName}_${arrayConfig.dataflow}_SystolicArray_${arrayConfig.size.row}x${arrayConfig.size.col}")

  private def buildOutputNum: Int = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        arrayConfig.size.row
      case Dataflow.ReuseB =>
        arrayConfig.size.col
      case Dataflow.ReuseC =>
        arrayConfig.size.row + arrayConfig.size.col - 1
    }
  }

  val io = new Bundle {
    val inputA = in Vec(portType.createInputTypeA, arrayConfig.size.row)
    val inputB = in Vec(portType.createInputTypeB, arrayConfig.size.col)

    val inputCaptureEnableA = (arrayConfig.dataflow == Dataflow.ReuseA) generate in {
      Vec.fill(arrayConfig.size.row, arrayConfig.size.col){Bool()}
    }

    val inputCaptureEnableB = (arrayConfig.dataflow == Dataflow.ReuseB) generate in {
      Vec.fill(arrayConfig.size.row, arrayConfig.size.col){Bool()}
    }

    val outputCaptureEnableC = (arrayConfig.dataflow == Dataflow.ReuseC) generate in {
      Vec.fill(arrayConfig.size.row, arrayConfig.size.col){Bool()}
    }

    val resetPartialC = (arrayConfig.dataflow == Dataflow.ReuseC) generate in {
      Vec.fill(arrayConfig.size.row, arrayConfig.size.col){Bool()}
    }

    val outputC = out Vec(portType.createSystolicOutputTypeC, buildOutputNum)
  }

  val pes = Array.tabulate(arrayConfig.size.row, arrayConfig.size.col) { (r,c) =>

    val index = ProcessingElementIndex(r,c)

    val withOutputPortA = !index.isLastPeCol(arrayConfig.size.col)
    val withOutputPortB = !index.isLastPeRow(arrayConfig.size.row)
    val withInputPortC = arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        !index.isFirstCol
      case Dataflow.ReuseB =>
        !index.isFirstRow
      case Dataflow.ReuseC =>
        if (r > 0 && c < arrayConfig.size.col - 1) {
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
        for(r <- 0 until arrayConfig.size.row)
          pes(r)(0).io.inputA := RegNext(io.inputA(r))

        for{
          r <- 0 until arrayConfig.size.row
          c <- 1 until arrayConfig.size.col
        } {
          pes(r)(c).io.inputA := pes(r)(c-1).io.outputA
        }

      case Dataflow.ReuseB | Dataflow.ReuseC =>

        val tileType = TileType.TypeA

        val skewBuffer = new SkewBuffer(tileType, portType.createInputTypeA, arrayConfig.size.row)

        for(r <- 0 until arrayConfig.size.row){
          skewBuffer.io.input(r) := io.inputA(r)
          pes(r)(0).io.inputA := skewBuffer.io.output(r)
        }

        for{
          r <- 0 until arrayConfig.size.row
          c <- 1 until arrayConfig.size.col
        } {
          pes(r)(c).io.inputA := pes(r)(c-1).io.outputA
        }
    }
  }

  private def wireB(): Unit = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA | Dataflow.ReuseC =>

        val tileType = TileType.TypeB

        val skewBuffer = new SkewBuffer(tileType, portType.createInputTypeB, arrayConfig.size.col)

        for(c <- 0 until arrayConfig.size.col){
          skewBuffer.io.input(c) := io.inputB(c)
          pes(0)(c).io.inputB := skewBuffer.io.output(c)
        }

        for{
          r <- 1 until arrayConfig.size.row
          c <- 0 until arrayConfig.size.col
        } {
          pes(r)(c).io.inputB := pes(r-1)(c).io.outputB
        }

      case Dataflow.ReuseB =>
        for(c <- 0 until arrayConfig.size.col)
          pes(0)(c).io.inputB := RegNext(io.inputB(c))

        for{
          r <- 1 until arrayConfig.size.row
          c <- 0 until arrayConfig.size.col
        } {
          pes(r)(c).io.inputB := pes(r-1)(c).io.outputB
        }
    }
  }

  private def wireControl(): Unit = {
    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        for {
          r <- 0 until arrayConfig.size.row
          c <- 0 until arrayConfig.size.col
        } {
          pes(r)(c).io.inputCaptureEnableA := io.inputCaptureEnableA(r)(c)
        }

      case Dataflow.ReuseB =>
        for {
          r <- 0 until arrayConfig.size.row
          c <- 0 until arrayConfig.size.col
        } {
          pes(r)(c).io.inputCaptureEnableB := io.inputCaptureEnableB(r)(c)
        }

      case Dataflow.ReuseC =>
        for {
          r <- 0 until arrayConfig.size.row
          c <- 0 until arrayConfig.size.col
        } {
          pes(r)(c).io.outputCaptureEnableC := io.outputCaptureEnableC(r)(c)
          pes(r)(c).io.resetPartialC := io.resetPartialC(r)(c)
        }
    }
  }

  private def wireC(): Unit = {
    val tileType = TileType.TypeC

    arrayConfig.dataflow match {
      case Dataflow.ReuseA =>
        val deskewBuffer = new SkewBuffer(
          tileType = tileType,
          inputType = portType.createSystolicOutputTypeC,
          delayDepth = arrayConfig.size.row,
          isMinDepthFirst = false
        )

        for {
          r <- 0 until arrayConfig.size.row
          c <- 1 until arrayConfig.size.col
        } {
          pes(r)(c).io.inputC := pes(r)(c-1).io.outputC
        }

        for (r <- 0 until arrayConfig.size.row) {
          deskewBuffer.io.input(r) := pes(r)(arrayConfig.size.col - 1).io.outputC
          io.outputC(r) := deskewBuffer.io.output(r)
        }

      case Dataflow.ReuseB =>
        val deskewBuffer = new SkewBuffer(
          tileType = tileType,
          inputType = portType.createSystolicOutputTypeC,
          delayDepth = arrayConfig.size.col,
          isMinDepthFirst = false
        )

        for {
          r <- 1 until arrayConfig.size.row
          c <- 0 until arrayConfig.size.col
        } {
          pes(r)(c).io.inputC := pes(r-1)(c).io.outputC
        }

        for (c <- 0 until arrayConfig.size.col) {
          deskewBuffer.io.input(c) := pes(arrayConfig.size.row - 1)(c).io.outputC
          io.outputC(c) := deskewBuffer.io.output(c)
        }

      case Dataflow.ReuseC =>

        val deskewBuffer = new DeskewBufferReuseC(
          portType.createSystolicOutputTypeC,
          arrayConfig,
        )

        for {
          r <- 0 until arrayConfig.size.row
          c <- 0 until arrayConfig.size.col
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

  //TODO recode helper functions

  private def canConnectDiagonally(r: Int, c: Int): Boolean = {
//    val isRightEdgeExceptLast = (0 <= r && r < arrayConfig.size.row - 1 && c == arrayConfig.size.col - 1)
//    val isTopEdgeExceptFirst = (r == 0 && 0 < c && c < arrayConfig.size.col - 1)
//    val isMiddle = (0 < r && r < arrayConfig.size.row - 1 && 0 < c && c < arrayConfig.size.col - 1)
//
//    isRightEdgeExceptLast || isTopEdgeExceptFirst || isMiddle

    val hasNextRow = r + 1 < arrayConfig.size.row
    val hasPrevColumn = c >= 1
    hasNextRow && hasPrevColumn
  }

  private def isOutputPosition(r: Int, c: Int): Boolean = {
//    val isFirstElement = r == 0 && c == 0
//    val isLeftOrBottomEdge = (0 < r && r < arrayConfig.size.row && c == 0) ||
//      (r == arrayConfig.size.row - 1 && 0 < c && c < arrayConfig.size.col - 1)
//    val isLastElement = r == arrayConfig.size.row - 1 && c == arrayConfig.size.col - 1
//
//    isFirstElement || isLeftOrBottomEdge || isLastElement

    val onLeftEdge = c == 0
    val onBottomEdge = r == arrayConfig.size.row - 1 && c > 0
    onLeftEdge || onBottomEdge
  }

  private def getOutputIndex(r: Int, c: Int): Int = {
//    if (r == 0 && c == 0) {
//      0
//    } else if (r > 0 && r < arrayConfig.size.row && c == 0) {
//      r
//    } else if (r == arrayConfig.size.row - 1 && c > 0 && c < arrayConfig.size.col - 1) {
//      arrayConfig.size.row + c - 1
//    } else if (r == arrayConfig.size.row - 1 && c == arrayConfig.size.col - 1) {
//      arrayConfig.size.row + arrayConfig.size.col - 2
//    } else {
//      throw new Exception(s"Invalid output position: ($r, $c)")
//    }

    val onLeftEdge = c == 0

    if(onLeftEdge){
      r
    } else {
      (arrayConfig.size.row - 1) + c
    }

  }





}