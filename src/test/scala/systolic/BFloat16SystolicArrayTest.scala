package systolic

import spinal.core._
import spinal.core.sim._

object BFloat16SystolicArrayTest extends App {

  val spinalConfig = SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(
    resetKind = SYNC,
    resetActiveLevel = HIGH
  ))

  // Helper functions for BFloat16 conversion
  def floatToBF16Bits(f: Float): Int = {
    val intBits = java.lang.Float.floatToIntBits(f)
    (intBits >> 16) & 0xFFFF  // Take upper 16 bits
  }

  def bf16BitsToFloat(bf16: Int): Float = {
    val intBits = (bf16 & 0xFFFF) << 16
    java.lang.Float.intBitsToFloat(intBits)
  }

  def fp32BitsToFloat(fp32: Long): Float = {
    java.lang.Float.intBitsToFloat(fp32.toInt)
  }

  // Extract BF16 fields from float
  def floatToBF16Fields(f: Float): (Boolean, Int, Int) = {
    val bits = floatToBF16Bits(f)
    val sign = ((bits >> 15) & 0x1) == 1
    val exp = (bits >> 7) & 0xFF
    val mant = bits & 0x7F
    (sign, exp, mant)
  }

  // ============================================================
  // Test BFloat16 ReuseC systolic array (output stationary)
  // ============================================================
  println("=== Testing BFloat16 Systolic Array with FP32 Accumulation ===")

  val bf16ConfigReuseC = SystolicArrayConfig.bfloat16(
    row = 2,
    col = 2,
    dataflow = Dataflow.ReuseC
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("BF16_ReuseC_Test")
    .compile(BFloat16SystolicArrayTyped(bf16ConfigReuseC))
    .doSim { dut =>
      dut.clockDomain.forkStimulus(10, resetCycles = 2)

      // Initialize
      for (r <- 0 until 2) {
        dut.io.inputA(r).sign #= false
        dut.io.inputA(r).exponent #= 0
        dut.io.inputA(r).mantissa #= 0
        for (c <- 0 until 2) {
          dut.io.outputCaptureEnableC(r)(c) #= true
          dut.io.resetPartialC(r)(c) #= true
        }
      }
      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= false
        dut.io.inputB(c).exponent #= 0
        dut.io.inputB(c).mantissa #= 0
      }
      dut.clockDomain.waitSampling(2)

      println("\n--- Test 1: Simple multiplication (2.0 × 3.0 = 6.0) ---")

      // BF16 encoding (same exponent as FP32, truncated mantissa):
      // 2.0 = 0 10000000 0000000 (sign=0, exp=128, mant=0)
      // 3.0 = 0 10000000 1000000 (sign=0, exp=128, mant=64)
      val (sign2, exp2, mant2) = floatToBF16Fields(2.0f)
      val (sign3, exp3, mant3) = floatToBF16Fields(3.0f)

      println(s"2.0 in BF16: sign=$sign2, exp=$exp2, mant=$mant2")
      println(s"3.0 in BF16: sign=$sign3, exp=$exp3, mant=$mant3")

      dut.io.inputA(0).sign #= sign2
      dut.io.inputA(0).exponent #= exp2
      dut.io.inputA(0).mantissa #= mant2

      dut.io.inputA(1).sign #= sign2
      dut.io.inputA(1).exponent #= exp2
      dut.io.inputA(1).mantissa #= mant2

      dut.io.inputB(0).sign #= sign3
      dut.io.inputB(0).exponent #= exp3
      dut.io.inputB(0).mantissa #= mant3

      dut.io.inputB(1).sign #= sign3
      dut.io.inputB(1).exponent #= exp3
      dut.io.inputB(1).mantissa #= mant3

      // Reset accumulators
      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= true
      }

      dut.clockDomain.waitSampling(1)

      // Disable reset, continue accumulating
      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= false
      }

      dut.clockDomain.waitSampling(5)

      // Read outputs
      println("\n--- Outputs after multiplication (expected: 6.0) ---")
      for (i <- 0 until 3) {
        val outSign = dut.io.outputC(i).sign.toBoolean
        val outExp = dut.io.outputC(i).exponent.toInt
        val outMant = dut.io.outputC(i).mantissa.toLong

        val fp32Bits = ((if (outSign) 1 else 0) << 31) | (outExp << 23) | outMant.toInt
        val floatValue = java.lang.Float.intBitsToFloat(fp32Bits)

        println(s"OutputC[$i]: sign=$outSign, exp=$outExp, mant=$outMant -> $floatValue")
      }

      println("\n--- Test 2: Accumulation (1.0 + 1.0 + 1.0 + ...) ---")

      val (sign1, exp1, mant1) = floatToBF16Fields(1.0f)
      println(s"1.0 in BF16: sign=$sign1, exp=$exp1, mant=$mant1")

      // Reset first
      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= true
      }

      // Set all inputs to 1.0
      for (r <- 0 until 2) {
        dut.io.inputA(r).sign #= sign1
        dut.io.inputA(r).exponent #= exp1
        dut.io.inputA(r).mantissa #= mant1
      }
      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= sign1
        dut.io.inputB(c).exponent #= exp1
        dut.io.inputB(c).mantissa #= mant1
      }

      dut.clockDomain.waitSampling(1)

      // Now accumulate more 1.0 × 1.0 results
      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= false
      }

      dut.clockDomain.waitSampling(3)

      println("\n--- Accumulated outputs ---")
      for (i <- 0 until 3) {
        val outSign = dut.io.outputC(i).sign.toBoolean
        val outExp = dut.io.outputC(i).exponent.toInt
        val outMant = dut.io.outputC(i).mantissa.toLong

        val fp32Bits = ((if (outSign) 1 else 0) << 31) | (outExp << 23) | outMant.toInt
        val floatValue = java.lang.Float.intBitsToFloat(fp32Bits)

        println(s"OutputC[$i]: $floatValue")
      }

      println("\n--- Test 3: Negative numbers (-2.0 × 3.0 = -6.0) ---")

      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= true
      }

      // -2.0 (flip sign bit)
      for (r <- 0 until 2) {
        dut.io.inputA(r).sign #= true
        dut.io.inputA(r).exponent #= exp2
        dut.io.inputA(r).mantissa #= mant2
      }
      // 3.0
      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= sign3
        dut.io.inputB(c).exponent #= exp3
        dut.io.inputB(c).mantissa #= mant3
      }

      dut.clockDomain.waitSampling(1)

      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= false
      }

      dut.clockDomain.waitSampling(5)

      println("\n--- Outputs (expected: -6.0) ---")
      for (i <- 0 until 3) {
        val outSign = dut.io.outputC(i).sign.toBoolean
        val outExp = dut.io.outputC(i).exponent.toInt
        val outMant = dut.io.outputC(i).mantissa.toLong

        val fp32Bits = ((if (outSign) 1 else 0) << 31) | (outExp << 23) | outMant.toInt
        val floatValue = java.lang.Float.intBitsToFloat(fp32Bits)

        println(s"OutputC[$i]: sign=$outSign -> $floatValue")
      }

      println("\n--- Test 4: Small numbers (0.5 × 0.5 = 0.25) ---")

      val (sign05, exp05, mant05) = floatToBF16Fields(0.5f)
      println(s"0.5 in BF16: sign=$sign05, exp=$exp05, mant=$mant05")

      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= true
      }

      for (r <- 0 until 2) {
        dut.io.inputA(r).sign #= sign05
        dut.io.inputA(r).exponent #= exp05
        dut.io.inputA(r).mantissa #= mant05
      }
      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= sign05
        dut.io.inputB(c).exponent #= exp05
        dut.io.inputB(c).mantissa #= mant05
      }

      dut.clockDomain.waitSampling(1)

      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= false
      }

      dut.clockDomain.waitSampling(5)

      println("\n--- Outputs (expected: 0.25) ---")
      for (i <- 0 until 3) {
        val outSign = dut.io.outputC(i).sign.toBoolean
        val outExp = dut.io.outputC(i).exponent.toInt
        val outMant = dut.io.outputC(i).mantissa.toLong

        val fp32Bits = ((if (outSign) 1 else 0) << 31) | (outExp << 23) | outMant.toInt
        val floatValue = java.lang.Float.intBitsToFloat(fp32Bits)

        println(s"OutputC[$i]: $floatValue")
      }

      dut.clockDomain.waitSampling(5)
      simSuccess()
    }

  // ============================================================
  // Test BFloat16 ReuseA systolic array (weight stationary)
  // ============================================================
  println("\n=== Testing BFloat16 ReuseA (Weight Stationary) ===")

  val bf16ConfigReuseA = SystolicArrayConfig.bfloat16(
    row = 2,
    col = 2,
    dataflow = Dataflow.ReuseA
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("BF16_ReuseA_Test")
    .compile(BFloat16SystolicArrayTyped(bf16ConfigReuseA))
    .doSim { dut =>
      dut.clockDomain.forkStimulus(10, resetCycles = 2)

      // Initialize
      for (r <- 0 until 2) {
        dut.io.inputA(r).sign #= false
        dut.io.inputA(r).exponent #= 0
        dut.io.inputA(r).mantissa #= 0
        for (c <- 0 until 2) {
          dut.io.inputCaptureEnableA(r)(c) #= false
        }
      }
      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= false
        dut.io.inputB(c).exponent #= 0
        dut.io.inputB(c).mantissa #= 0
      }
      dut.clockDomain.waitSampling(2)

      println("\n--- Loading weights (2.0) into PEs ---")

      val (sign2, exp2, mant2) = floatToBF16Fields(2.0f)

      for (c <- 0 until 2) {
        for (r <- 0 until 2) {
          dut.io.inputA(r).sign #= sign2
          dut.io.inputA(r).exponent #= exp2
          dut.io.inputA(r).mantissa #= mant2
          dut.io.inputCaptureEnableA(r)(c) #= true
        }
        dut.clockDomain.waitSampling(1)
        for (r <- 0 until 2) {
          dut.io.inputCaptureEnableA(r)(c) #= false
        }
      }

      println("--- Feeding input data (3.0) ---")

      val (sign3, exp3, mant3) = floatToBF16Fields(3.0f)

      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= sign3
        dut.io.inputB(c).exponent #= exp3
        dut.io.inputB(c).mantissa #= mant3
      }

      dut.clockDomain.waitSampling(8)

      println("\n--- Outputs (expected: 6.0 or accumulated) ---")
      for (i <- 0 until 2) {
        val outSign = dut.io.outputC(i).sign.toBoolean
        val outExp = dut.io.outputC(i).exponent.toInt
        val outMant = dut.io.outputC(i).mantissa.toLong

        val fp32Bits = ((if (outSign) 1 else 0) << 31) | (outExp << 23) | outMant.toInt
        val floatValue = java.lang.Float.intBitsToFloat(fp32Bits)

        println(s"OutputC[$i]: $floatValue")
      }

      dut.clockDomain.waitSampling(5)
      simSuccess()
    }

  // ============================================================
  // Test BFloat16 ReuseB systolic array (input stationary)
  // ============================================================
  println("\n=== Testing BFloat16 ReuseB (Input Stationary) ===")

  val bf16ConfigReuseB = SystolicArrayConfig.bfloat16(
    row = 2,
    col = 2,
    dataflow = Dataflow.ReuseB
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("BF16_ReuseB_Test")
    .compile(BFloat16SystolicArrayTyped(bf16ConfigReuseB))
    .doSim { dut =>
      dut.clockDomain.forkStimulus(10, resetCycles = 2)

      // Initialize
      for (r <- 0 until 2) {
        dut.io.inputA(r).sign #= false
        dut.io.inputA(r).exponent #= 0
        dut.io.inputA(r).mantissa #= 0
      }
      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= false
        dut.io.inputB(c).exponent #= 0
        dut.io.inputB(c).mantissa #= 0
        for (r <- 0 until 2) {
          dut.io.inputCaptureEnableB(r)(c) #= false
        }
      }
      dut.clockDomain.waitSampling(2)

      println("\n--- Loading inputs (3.0) into PEs ---")

      val (sign3, exp3, mant3) = floatToBF16Fields(3.0f)

      for (r <- 0 until 2) {
        for (c <- 0 until 2) {
          dut.io.inputB(c).sign #= sign3
          dut.io.inputB(c).exponent #= exp3
          dut.io.inputB(c).mantissa #= mant3
          dut.io.inputCaptureEnableB(r)(c) #= true
        }
        dut.clockDomain.waitSampling(1)
        for (c <- 0 until 2) {
          dut.io.inputCaptureEnableB(r)(c) #= false
        }
      }

      println("--- Feeding weight data (2.0) ---")

      val (sign2, exp2, mant2) = floatToBF16Fields(2.0f)

      for (r <- 0 until 2) {
        dut.io.inputA(r).sign #= sign2
        dut.io.inputA(r).exponent #= exp2
        dut.io.inputA(r).mantissa #= mant2
      }

      dut.clockDomain.waitSampling(8)

      println("\n--- Outputs ---")
      for (i <- 0 until 2) {
        val outSign = dut.io.outputC(i).sign.toBoolean
        val outExp = dut.io.outputC(i).exponent.toInt
        val outMant = dut.io.outputC(i).mantissa.toLong

        val fp32Bits = ((if (outSign) 1 else 0) << 31) | (outExp << 23) | outMant.toInt
        val floatValue = java.lang.Float.intBitsToFloat(fp32Bits)

        println(s"OutputC[$i]: $floatValue")
      }

      dut.clockDomain.waitSampling(5)
      simSuccess()
    }

  println("\n=== All BFloat16 Tests Complete ===")
}
