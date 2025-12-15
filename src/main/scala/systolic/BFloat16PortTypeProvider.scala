package systolic

import spinal.core._

/**
 * Port type provider for BFloat16 inputs with FP32 accumulation
 *
 * - Input A: BFloat16 (16 bits)
 * - Input B: BFloat16 (16 bits)
 * - Multiply output: FP32 (32 bits) - result of BF16 × BF16
 * - Accumulation/Output C: FP32 (32 bits)
 *
 * This matches the typical mixed-precision compute paradigm where:
 * - Weights and activations are stored in BF16 (memory efficient)
 * - Multiplication produces FP32 result (no precision loss)
 * - Accumulation is done in FP32 (prevents error accumulation)
 */
class BFloat16PortTypeProvider(
                                val arrayConfig: SystolicArrayConfig
                              ) extends PortTypeProvider[BFloat16, Float32] {

  // Get BF16 config (validates that we have the right config type)
  private val bf16Config: BFloat16DataTypeConfig = arrayConfig.dataTypeConfig match {
    case config: BFloat16DataTypeConfig => config
    case _ => throw new IllegalArgumentException("BFloat16PortTypeProvider requires BFloat16DataTypeConfig")
  }

  // BFloat16 input width
  private val bf16Width: Int = bf16Config.inputBitWidth

  // FP32 accumulation width
  private val fp32Width: Int = bf16Config.outputBitWidth

  // Multiply output is always FP32 (BF16 × BF16 → FP32)
  private val multOutputBitWidth: Int = fp32Width

  // Systolic output is always FP32
  private val systolicOutputBitwidth: Int = fp32Width

  override def createInputTypeA: BFloat16 = BFloat16()

  override def createInputTypeB: BFloat16 = BFloat16()

  override def createMultOutputType: Float32 = Float32()

  override def createPeInputTypeC(index: ProcessingElementIndex): Float32 = {
    // For BF16 with FP32 accumulation, all C ports are FP32
    // regardless of dataflow or position
    Float32()
  }

  override def createPeOutputTypeC(index: ProcessingElementIndex): Float32 = {
    // For BF16 with FP32 accumulation, all C ports are FP32
    Float32()
  }

  override def createSystolicOutputTypeC: Float32 = {
    Float32()
  }
}






