package systolic

import spinal.core._
import spinal.core.sim._

object Float16SystolicArrayTest extends App {

  val spinalConfig = SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(
    resetKind = SYNC,
    resetActiveLevel = HIGH
  ))

  // Helper functions for FP16 conversion
  def floatToFP16Bits(f: Float): Int = {
    val fp32Bits = java.lang.Float.floatToIntBits(f)
    val sign = (fp32Bits >> 31) & 0x1
    val exp32 = (fp32Bits >> 23) & 0xFF
    val mant32 = fp32Bits & 0x7FFFFF

    if (exp32 == 0) {
      // Zero or denormal -> zero
      sign << 15
    } else if (exp32 == 0xFF) {
      // Inf or NaN
      if (mant32 == 0) {
        (sign << 15) | (0x1F << 10)  // Infinity
      } else {
        (sign << 15) | (0x1F << 10) | 0x200  // NaN
      }
    } else {
      // Normal number: rebias exponent (exp32 - 127 + 15 = exp32 - 112)
      val exp16 = exp32 - 112
      if (exp16 <= 0) {
        // Underflow to zero
        sign << 15
      } else if (exp16 >= 31) {
        // Overflow to infinity
        (sign << 15) | (0x1F << 10)
      } else {
        val mant16 = mant32 >> 13  // Take upper 10 bits
        (sign << 15) | (exp16 << 10) | mant16
      }
    }
  }

  def fp16BitsToFloat(fp16: Int): Float = {
    val sign = (fp16 >> 15) & 0x1
    val exp16 = (fp16 >> 10) & 0x1F
    val mant16 = fp16 & 0x3FF

    if (exp16 == 0) {
      if (mant16 == 0) 0.0f else 0.0f  // Denormals treated as zero for simplicity
    } else if (exp16 == 0x1F) {
      if (mant16 == 0) {
        if (sign == 1) Float.NegativeInfinity else Float.PositiveInfinity
      } else {
        Float.NaN
      }
    } else {
      // Normal: rebias (exp16 - 15 + 127 = exp16 + 112)
      val exp32 = exp16 + 112
      val mant32 = mant16 << 13
      val fp32Bits = (sign << 31) | (exp32 << 23) | mant32
      java.lang.Float.intBitsToFloat(fp32Bits)
    }
  }

  def fp32BitsToFloat(fp32: Long): Float = {
    java.lang.Float.intBitsToFloat(fp32.toInt)
  }

  // ============================================================
  // Test FP16 ReuseC systolic array (output stationary)
  // ============================================================
  println("=== Testing FP16 Systolic Array with FP32 Accumulation ===")

  val fp16ConfigReuseC = SystolicArrayConfig.float16(
    row = 2,
    col = 2,
    dataflow = Dataflow.ReuseC
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("FP16_ReuseC_Test")
    .compile(Float16SystolicArrayTyped(fp16ConfigReuseC))
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

      // FP16 encoding:
      // 2.0 = 0 10000 0000000000 (sign=0, exp=16, mant=0) -> 1.0 * 2^(16-15) = 2.0
      // 3.0 = 0 10000 1000000000 (sign=0, exp=16, mant=512) -> 1.5 * 2^(16-15) = 3.0
      val fp16_2_0_exp = 16   // bias 15, so exponent = 1
      val fp16_2_0_mant = 0

      val fp16_3_0_exp = 16   // bias 15, so exponent = 1  
      val fp16_3_0_mant = 512 // 0.5 in mantissa -> 1.5 total

      dut.io.inputA(0).sign #= false
      dut.io.inputA(0).exponent #= fp16_2_0_exp
      dut.io.inputA(0).mantissa #= fp16_2_0_mant

      dut.io.inputA(1).sign #= false
      dut.io.inputA(1).exponent #= fp16_2_0_exp
      dut.io.inputA(1).mantissa #= fp16_2_0_mant

      dut.io.inputB(0).sign #= false
      dut.io.inputB(0).exponent #= fp16_3_0_exp
      dut.io.inputB(0).mantissa #= fp16_3_0_mant

      dut.io.inputB(1).sign #= false
      dut.io.inputB(1).exponent #= fp16_3_0_exp
      dut.io.inputB(1).mantissa #= fp16_3_0_mant

      // Reset accumulators
      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= true
      }

      dut.clockDomain.waitSampling(1)

      // Disable reset, continue accumulating
      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= false
      }

      println(s"InputA = 2.0 (exp=$fp16_2_0_exp, mant=$fp16_2_0_mant)")
      println(s"InputB = 3.0 (exp=$fp16_3_0_exp, mant=$fp16_3_0_mant)")

      dut.clockDomain.waitSampling(5)

      // Read outputs
      println("\n--- Outputs after multiplication ---")
      for (i <- 0 until 3) {
        val outSign = dut.io.outputC(i).sign.toBoolean
        val outExp = dut.io.outputC(i).exponent.toInt
        val outMant = dut.io.outputC(i).mantissa.toLong

        val fp32Bits = ((if (outSign) 1 else 0) << 31) | (outExp << 23) | outMant.toInt
        val floatValue = java.lang.Float.intBitsToFloat(fp32Bits)

        println(s"OutputC[$i]: sign=$outSign, exp=$outExp, mant=$outMant -> $floatValue")
      }

      println("\n--- Test 2: 1.0 × 1.0 accumulation ---")

      // 1.0 = 0 01111 0000000000 (sign=0, exp=15, mant=0)
      val fp16_1_0_exp = 15
      val fp16_1_0_mant = 0

      // Reset first
      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= true
      }

      for (r <- 0 until 2) {
        dut.io.inputA(r).sign #= false
        dut.io.inputA(r).exponent #= fp16_1_0_exp
        dut.io.inputA(r).mantissa #= fp16_1_0_mant
      }
      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= false
        dut.io.inputB(c).exponent #= fp16_1_0_exp
        dut.io.inputB(c).mantissa #= fp16_1_0_mant
      }

      dut.clockDomain.waitSampling(1)

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

      println("\n--- Test 3: Negative number test (-2.0 × 3.0 = -6.0) ---")

      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= true
      }

      // -2.0
      for (r <- 0 until 2) {
        dut.io.inputA(r).sign #= true  // negative
        dut.io.inputA(r).exponent #= fp16_2_0_exp
        dut.io.inputA(r).mantissa #= fp16_2_0_mant
      }
      // 3.0
      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= false
        dut.io.inputB(c).exponent #= fp16_3_0_exp
        dut.io.inputB(c).mantissa #= fp16_3_0_mant
      }

      dut.clockDomain.waitSampling(1)

      for (r <- 0 until 2; c <- 0 until 2) {
        dut.io.resetPartialC(r)(c) #= false
      }

      dut.clockDomain.waitSampling(5)

      println("\n--- Outputs (should be -6.0) ---")
      for (i <- 0 until 3) {
        val outSign = dut.io.outputC(i).sign.toBoolean
        val outExp = dut.io.outputC(i).exponent.toInt
        val outMant = dut.io.outputC(i).mantissa.toLong

        val fp32Bits = ((if (outSign) 1 else 0) << 31) | (outExp << 23) | outMant.toInt
        val floatValue = java.lang.Float.intBitsToFloat(fp32Bits)

        println(s"OutputC[$i]: sign=$outSign -> $floatValue")
      }

      dut.clockDomain.waitSampling(5)
      simSuccess()
    }

  // ============================================================
  // Test FP16 ReuseA systolic array (weight stationary)
  // ============================================================
  println("\n=== Testing FP16 ReuseA (Weight Stationary) ===")

  val fp16ConfigReuseA = SystolicArrayConfig.float16(
    row = 2,
    col = 2,
    dataflow = Dataflow.ReuseA
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("FP16_ReuseA_Test")
    .compile(Float16SystolicArrayTyped(fp16ConfigReuseA))
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

      val fp16_2_0_exp = 16
      val fp16_2_0_mant = 0

      for (c <- 0 until 2) {
        for (r <- 0 until 2) {
          dut.io.inputA(r).sign #= false
          dut.io.inputA(r).exponent #= fp16_2_0_exp
          dut.io.inputA(r).mantissa #= fp16_2_0_mant
          dut.io.inputCaptureEnableA(r)(c) #= true
        }
        dut.clockDomain.waitSampling(1)
        for (r <- 0 until 2) {
          dut.io.inputCaptureEnableA(r)(c) #= false
        }
      }

      println("--- Feeding input data (3.0) ---")

      val fp16_3_0_exp = 16
      val fp16_3_0_mant = 512

      for (c <- 0 until 2) {
        dut.io.inputB(c).sign #= false
        dut.io.inputB(c).exponent #= fp16_3_0_exp
        dut.io.inputB(c).mantissa #= fp16_3_0_mant
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

  println("\n=== All FP16 Tests Complete ===")
}
