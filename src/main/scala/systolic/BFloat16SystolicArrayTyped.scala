package systolic

import spinal.core._
import systolic.BFloat16Arithmetic._

/**
 * Typed wrapper component that provides BFloat16/FP32 interface
 * instead of raw Bits.
 *
 * This wraps the standard SystolicArray with proper type annotations
 * for BFloat16 inputs and Floating32 outputs.
 *
 * Usage:
 *   val config = SystolicArrayConfig.bfloat16(4, 4, Dataflow.ReuseC)
 *   val array = new BFloat16SystolicArrayTyped(config)
 */
class BFloat16SystolicArrayTyped(
                                  val config: SystolicArrayConfig
                                ) extends Component {

  require(config.isBFloat16, "BFloat16SystolicArrayTyped requires a BFloat16 configuration")

  private val row = config.row
  private val col = config.col
  private val dataflow = config.dataflow

  setDefinitionName(s"BF16_${dataflow}_SystolicArray_${row}x${col}_Typed")

  private def buildOutputNum: Int = {
    dataflow match {
      case Dataflow.ReuseA => row
      case Dataflow.ReuseB => col
      case Dataflow.ReuseC => row + col - 1
    }
  }

  val io = new Bundle {
    // BFloat16 inputs
    val inputA = in Vec(BFloat16(), row)
    val inputB = in Vec(BFloat16(), col)

    // Control signals
    val inputCaptureEnableA = (dataflow == Dataflow.ReuseA) generate in {
      Vec.fill(row, col)(Bool())
    }
    val inputCaptureEnableB = (dataflow == Dataflow.ReuseB) generate in {
      Vec.fill(row, col)(Bool())
    }
    val outputCaptureEnableC = (dataflow == Dataflow.ReuseC) generate in {
      Vec.fill(row, col)(Bool())
    }
    val resetPartialC = (dataflow == Dataflow.ReuseC) generate in {
      Vec.fill(row, col)(Bool())
    }

    // FP32 outputs
    val outputC = out Vec(Floating32(), buildOutputNum)
  }

  // Create internal systolic array using the unified factory
  private implicit val arithmetic: Arithmetic[Bits] = bf16Fp32Arithmetic
  private implicit val portType: PortTypeProvider[Bits] = new BFloat16PortTypeProvider(config)

  private val internalArray = new SystolicArray[Bits](config)(arithmetic, portType)

  // Connect inputs (convert BFloat16 to Bits)
  for (r <- 0 until row) {
    internalArray.io.inputA(r) := io.inputA(r).asBits
  }
  for (c <- 0 until col) {
    internalArray.io.inputB(c) := io.inputB(c).asBits
  }

  // Connect control signals
  dataflow match {
    case Dataflow.ReuseA =>
      for (r <- 0 until row; c <- 0 until col) {
        internalArray.io.inputCaptureEnableA(r)(c) := io.inputCaptureEnableA(r)(c)
      }
    case Dataflow.ReuseB =>
      for (r <- 0 until row; c <- 0 until col) {
        internalArray.io.inputCaptureEnableB(r)(c) := io.inputCaptureEnableB(r)(c)
      }
    case Dataflow.ReuseC =>
      for (r <- 0 until row; c <- 0 until col) {
        internalArray.io.outputCaptureEnableC(r)(c) := io.outputCaptureEnableC(r)(c)
        internalArray.io.resetPartialC(r)(c) := io.resetPartialC(r)(c)
      }
  }

  // Connect outputs (convert Bits to Floating32)
  for (i <- 0 until buildOutputNum) {
    io.outputC(i) := Floating32(internalArray.io.outputC(i))
  }
}

object BFloat16SystolicArrayTyped {
  /**
   * Create typed BFloat16 systolic array from config
   */
  def apply(config: SystolicArrayConfig): BFloat16SystolicArrayTyped = {
    new BFloat16SystolicArrayTyped(config)
  }

  /**
   * Create typed BFloat16 systolic array from parameters
   */
  def apply(row: Int, col: Int, dataflow: Dataflow.Value): BFloat16SystolicArrayTyped = {
    val config = SystolicArrayConfig.bfloat16(row, col, dataflow)
    new BFloat16SystolicArrayTyped(config)
  }
}






