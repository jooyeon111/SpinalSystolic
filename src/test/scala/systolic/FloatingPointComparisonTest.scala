package systolic

import spinal.core._
import spinal.core.sim._

/**
 * Combined test for comparing BFloat16 and Float16 systolic arrays
 * 
 * This test demonstrates the differences between:
 * - BFloat16: 1 sign + 8 exponent + 7 mantissa (same range as FP32, lower precision)
 * - Float16:  1 sign + 5 exponent + 10 mantissa (smaller range, higher precision)
 */
object FloatingPointComparisonTest extends App {

  val spinalConfig = SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(
    resetKind = SYNC,
    resetActiveLevel = HIGH
  ))

  // ============================================================
  // Helper functions
  // ============================================================
  
  // BFloat16 helpers
  def floatToBF16Fields(f: Float): (Boolean, Int, Int) = {
    val bits = java.lang.Float.floatToIntBits(f)
    val sign = ((bits >> 31) & 0x1) == 1
    val exp = (bits >> 23) & 0xFF
    val mant = (bits >> 16) & 0x7F
    (sign, exp, mant)
  }

  // FP16 helpers  
  def floatToFP16Fields(f: Float): (Boolean, Int, Int) = {
    val bits = java.lang.Float.floatToIntBits(f)
    val sign = ((bits >> 31) & 0x1) == 1
    val exp32 = (bits >> 23) & 0xFF
    val mant32 = bits & 0x7FFFFF
    
    if (exp32 == 0) {
      (sign, 0, 0)
    } else if (exp32 == 0xFF) {
      (sign, 0x1F, if (mant32 == 0) 0 else 0x200)
    } else {
      val exp16 = exp32 - 112  // Rebias: 127 - 15 = 112
      if (exp16 <= 0) (sign, 0, 0)
      else if (exp16 >= 31) (sign, 0x1F, 0)
      else (sign, exp16, mant32 >> 13)
    }
  }

  def fp32ToFloat(sign: Boolean, exp: Int, mant: Long): Float = {
    val bits = ((if (sign) 1 else 0) << 31) | (exp << 23) | mant.toInt
    java.lang.Float.intBitsToFloat(bits)
  }

  // ============================================================
  // Test: Compare BF16 vs FP16 precision
  // ============================================================
  println("=" * 60)
  println("Floating Point Format Comparison Test")
  println("=" * 60)
  
  val testValues = Seq(1.0f, 2.0f, 3.0f, 0.5f, 0.25f, 1.5f, 100.0f, 0.001f)
  
  println("\nFormat encoding comparison:")
  println("-" * 60)
  println(f"${"Value"}%10s | ${"BF16 (exp,mant)"}%20s | ${"FP16 (exp,mant)"}%20s")
  println("-" * 60)
  
  for (v <- testValues) {
    val (bs, be, bm) = floatToBF16Fields(v)
    val (fs, fe, fm) = floatToFP16Fields(v)
    println(f"$v%10.4f | ${if(bs)"-" else "+"}($be%3d, $bm%3d)         | ${if(fs)"-" else "+"}($fe%3d, $fm%4d)")
  }

  // ============================================================
  // Test BF16 Systolic Array
  // ============================================================
  println("\n" + "=" * 60)
  println("BFloat16 Systolic Array Test (2x2 ReuseC)")
  println("=" * 60)

  val bf16ConfigReuseB = BFloat16Config(
    SystolicArraySize.defaultSystolicArraySize,
    dataflow = Dataflow.ReuseC
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("BF16_Comparison_Test")
    .compile(SystolicArray(bf16ConfigReuseB))
    .doSim { dut =>

      dut.clockDomain.forkStimulus(10, resetCycles = 2)

      def runBF16Test(aVal: Float, bVal: Float): Unit = {
        val (as, ae, am) = floatToBF16Fields(aVal)
        val (bs, be, bm) = floatToBF16Fields(bVal)

        // Initialize and reset
        for (r <- 0 until 2; c <- 0 until 2) {
          dut.io.outputCaptureEnableC(r)(c) #= true
          dut.io.resetPartialC(r)(c) #= true
        }

        for (r <- 0 until 2) {
          dut.io.inputA(r).sign #= as
          dut.io.inputA(r).exponent #= ae
          dut.io.inputA(r).mantissa #= am
        }
        for (c <- 0 until 2) {
          dut.io.inputB(c).sign #= bs
          dut.io.inputB(c).exponent #= be
          dut.io.inputB(c).mantissa #= bm
        }

        dut.clockDomain.waitSampling(1)

        for (r <- 0 until 2; c <- 0 until 2) {
          dut.io.resetPartialC(r)(c) #= false
        }

        dut.clockDomain.waitSampling(5)

        val outSign = dut.io.outputC(0).sign.toBoolean
        val outExp = dut.io.outputC(0).exponent.toInt
        val outMant = dut.io.outputC(0).mantissa.toLong
        val result = fp32ToFloat(outSign, outExp, outMant)
        val expected = aVal * bVal

        println(f"  $aVal%8.4f × $bVal%8.4f = $result%12.6f (expected: $expected%12.6f)")
      }

      println("\nMultiplication tests:")
      runBF16Test(2.0f, 3.0f)
      runBF16Test(1.5f, 2.0f)
      runBF16Test(0.5f, 0.5f)
      runBF16Test(100.0f, 0.01f)
      runBF16Test(-2.0f, 3.0f)

      simSuccess()
    }

  // ============================================================
  // Test FP16 Systolic Array
  // ============================================================
  println("\n" + "=" * 60)
  println("Float16 Systolic Array Test (2x2 ReuseC)")
  println("=" * 60)

  val fp16ConfigReuseC = Float16Config(
    size = SystolicArraySize(2,2),
    dataflow = Dataflow.ReuseC
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("FP16_Comparison_Test")
    .compile(SystolicArray(fp16ConfigReuseC))
    .doSim { dut =>

      dut.clockDomain.forkStimulus(10, resetCycles = 2)

      def runFP16Test(aVal: Float, bVal: Float): Unit = {
        val (as, ae, am) = floatToFP16Fields(aVal)
        val (bs, be, bm) = floatToFP16Fields(bVal)

        // Initialize and reset
        for (r <- 0 until 2; c <- 0 until 2) {
          dut.io.outputCaptureEnableC(r)(c) #= true
          dut.io.resetPartialC(r)(c) #= true
        }

        for (r <- 0 until 2) {
          dut.io.inputA(r).sign #= as
          dut.io.inputA(r).exponent #= ae
          dut.io.inputA(r).mantissa #= am
        }
        for (c <- 0 until 2) {
          dut.io.inputB(c).sign #= bs
          dut.io.inputB(c).exponent #= be
          dut.io.inputB(c).mantissa #= bm
        }

        dut.clockDomain.waitSampling(1)

        for (r <- 0 until 2; c <- 0 until 2) {
          dut.io.resetPartialC(r)(c) #= false
        }

        dut.clockDomain.waitSampling(5)

        val outSign = dut.io.outputC(0).sign.toBoolean
        val outExp = dut.io.outputC(0).exponent.toInt
        val outMant = dut.io.outputC(0).mantissa.toLong
        val result = fp32ToFloat(outSign, outExp, outMant)
        val expected = aVal * bVal

        println(f"  $aVal%8.4f × $bVal%8.4f = $result%12.6f (expected: $expected%12.6f)")
      }

      println("\nMultiplication tests:")
      runFP16Test(2.0f, 3.0f)
      runFP16Test(1.5f, 2.0f)
      runFP16Test(0.5f, 0.5f)
      runFP16Test(100.0f, 0.01f)
      runFP16Test(-2.0f, 3.0f)

      simSuccess()
    }

  // ============================================================
  // Test Integer Systolic Array for comparison
  // ============================================================
  println("\n" + "=" * 60)
  println("Integer Systolic Array Test (2x2 ReuseC, 8-bit)")
  println("=" * 60)

  val intConfig = SignedIntConfig(
    size = SystolicArraySize(2,2),
    dataflow = Dataflow.ReuseC,
    bitWidthA = 8,
    bitWidthB = 8,
    bitWidthOutputC = Some(32)
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("Int_Comparison_Test")
    .compile(SystolicArray(intConfig))
    .doSim { dut =>

      dut.clockDomain.forkStimulus(10, resetCycles = 2)

      def runIntTest(aVal: Int, bVal: Int): Unit = {
        // Initialize and reset
        for (r <- 0 until 2; c <- 0 until 2) {
          dut.io.outputCaptureEnableC(r)(c) #= true
          dut.io.resetPartialC(r)(c) #= true
        }

        for (r <- 0 until 2) {
          dut.io.inputA(r) #= aVal
        }
        for (c <- 0 until 2) {
          dut.io.inputB(c) #= bVal
        }

        dut.clockDomain.waitSampling(1)

        for (r <- 0 until 2; c <- 0 until 2) {
          dut.io.resetPartialC(r)(c) #= false
        }

        dut.clockDomain.waitSampling(5)

        val result = dut.io.outputC(0).toInt
        val expected = aVal * bVal

        println(f"  $aVal%8d × $bVal%8d = $result%12d (expected: $expected%12d)")
      }

      println("\nMultiplication tests:")
      runIntTest(2, 3)
      runIntTest(10, 20)
      runIntTest(-5, 7)
      runIntTest(127, 127)
      runIntTest(-128, 1)

      simSuccess()
    }

  println("\n" + "=" * 60)
  println("All Comparison Tests Complete!")
  println("=" * 60)
}
