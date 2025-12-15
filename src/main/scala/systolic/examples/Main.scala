package systolic.examples

import spinal.core._
import systolic._

/**
 * Example: Generate Systolic Arrays with unified configuration API
 *
 * This demonstrates how to use the integrated SystolicArrayConfig
 * for integer, BFloat16, and FP16 data types.
 */
object Main extends App {

  val spinalConfig = SpinalConfig(
    targetDirectory = "output",
    defaultClockDomainFrequency = FixedFrequency(100 MHz),
    defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC, resetActiveLevel = HIGH)
  )

  // ============================================================
  // Example 1: Signed Integer Systolic Array (original behavior)
  // ============================================================
  println("=== Generating Signed Integer Systolic Array ===")

  val intConfig = SystolicArrayConfig.signedInteger(
    row = 16,
    col = 16,
    dataflow = Dataflow.ReuseB,
    bitWidthA = 8,
    bitWidthB = 8,
//    bitWidthOutputC = Some(32)
  )

  spinalConfig.generateVerilog(SystolicArray(intConfig))
  println(s"Generated: SInt_${intConfig.dataflow}_SystolicArray_${intConfig.row}x${intConfig.col}")

  // ============================================================
  // Example 2: BFloat16 Systolic Array with FP32 Accumulation
  // ============================================================
  println("\n=== Generating BFloat16 Systolic Array ===")

  val bf16Config = SystolicArrayConfig.bfloat16(
    row = 4,
    col = 4,
    dataflow = Dataflow.ReuseC
  )

  spinalConfig.generateVerilog(SystolicArray(bf16Config))
  println(s"Generated: BF16_${bf16Config.dataflow}_SystolicArray_${bf16Config.row}x${bf16Config.col}")

  // ============================================================
  // Example 3: FP16 Systolic Array with FP32 Accumulation
  // ============================================================
  println("\n=== Generating FP16 Systolic Array ===")

  val fp16Config = SystolicArrayConfig.float16(
    row = 4,
    col = 4,
    dataflow = Dataflow.ReuseC
  )

  spinalConfig.generateVerilog(SystolicArray(fp16Config))
  println(s"Generated: FP16_${fp16Config.dataflow}_SystolicArray_${fp16Config.row}x${fp16Config.col}")

  // ============================================================
  // Example 4: Typed wrappers (with structured IO)
  // ============================================================
  println("\n=== Generating Typed Systolic Arrays ===")

  spinalConfig.generateVerilog(BFloat16SystolicArrayTyped(4, 4, Dataflow.ReuseC))
  println("Generated: BF16_ReuseC_SystolicArray_4x4_Typed")

  spinalConfig.generateVerilog(Float16SystolicArrayTyped(4, 4, Dataflow.ReuseC))
  println("Generated: FP16_ReuseC_SystolicArray_4x4_Typed")

  // ============================================================
  // Example 5: All dataflow variants comparison
  // ============================================================
  println("\n=== Generating All Dataflow Variants ===")

  for (dataflow <- Seq(Dataflow.ReuseA, Dataflow.ReuseB, Dataflow.ReuseC)) {
    // BF16 variant
    val bf16 = SystolicArrayConfig.bfloat16(4, 4, dataflow)
    spinalConfig.generateVerilog(SystolicArray(bf16))
    println(s"Generated: BF16_${dataflow}_SystolicArray_4x4")

    // FP16 variant
    val fp16 = SystolicArrayConfig.float16(4, 4, dataflow)
    spinalConfig.generateVerilog(SystolicArray(fp16))
    println(s"Generated: FP16_${dataflow}_SystolicArray_4x4")
  }

  println("\n=== All Generations Complete ===")
  println("Output directory: output/")



}