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
class BFloat16PortTypeProvider(val arrayConfig: BFloat16Config) extends PortTypeProvider[BFloat16, Float32] {

  override def createInputTypeA: BFloat16 = BFloat16()
  override def createInputTypeB: BFloat16 = BFloat16()
  override def createMultOutputType: Float32 = Float32()
  override def createPeInputTypeC(index: ProcessingElementIndex): Float32 = Float32()
  override def createPeOutputTypeC(index: ProcessingElementIndex): Float32 = Float32()
  override def createSystolicOutputTypeC: Float32 = Float32()

}






