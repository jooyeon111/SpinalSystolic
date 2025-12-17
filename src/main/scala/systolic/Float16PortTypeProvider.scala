package systolic

import spinal.core._

/**
 * Port type provider for FP16 inputs with FP32 accumulation
 *
 * - Input A: FP16 (16 bits)
 * - Input B: FP16 (16 bits)
 * - Multiply output: FP32 (32 bits) - result of FP16 × FP16
 * - Accumulation/Output C: FP32 (32 bits)
 */
class Float16PortTypeProvider(val arrayConfig: Float16Config) extends PortTypeProvider[Float16, Float32] {

  override def createInputTypeA: Float16 = Float16()
  override def createInputTypeB: Float16 = Float16()
  override def createMultOutputType: Float32 = Float32()
  override def createPeInputTypeC(index: ProcessingElementIndex): Float32 = Float32()
  override def createPeOutputTypeC(index: ProcessingElementIndex): Float32 = Float32()
  override def createSystolicOutputTypeC: Float32 = Float32()

}






