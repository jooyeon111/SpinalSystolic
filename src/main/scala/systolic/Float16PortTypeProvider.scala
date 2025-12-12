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
class Float16PortTypeProvider(
                               val arrayConfig: SystolicArrayConfig
                             ) extends PortTypeProvider[Bits] {

  // Get FP16 config (validates that we have the right config type)
  private val fp16Config: Float16DataTypeConfig = arrayConfig.dataTypeConfig match {
    case config: Float16DataTypeConfig => config
    case _ => throw new IllegalArgumentException("Float16PortTypeProvider requires Float16DataTypeConfig")
  }

  // FP16 input width
  private val fp16Width: Int = fp16Config.inputBitWidth

  // FP32 accumulation width
  private val fp32Width: Int = fp16Config.outputBitWidth

  // Multiply output is always FP32
  private val multOutputBitWidth: Int = fp32Width

  // Systolic output is always FP32
  private val systolicOutputBitwidth: Int = fp32Width

  override def createInputTypeA: Bits = Bits(fp16Width bits)

  override def createInputTypeB: Bits = Bits(fp16Width bits)

  override def createMultOutputType: Bits = Bits(multOutputBitWidth bits)

  override def createPeInputTypeC(index: ProcessingElementIndex): Bits = {
    Bits(fp32Width bits)
  }

  override def createPeOutputTypeC(index: ProcessingElementIndex): Bits = {
    Bits(fp32Width bits)
  }

  override def createSystolicOutputTypeC: Bits = {
    Bits(systolicOutputBitwidth bits)
  }
}






