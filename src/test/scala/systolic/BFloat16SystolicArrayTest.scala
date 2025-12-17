package systolic

import spinal.core.sim._

object BFloat16SystolicArrayTest extends App {

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
  // Test BFloat16 ReuseB systolic array (input stationary)
  // ============================================================
  println("\n=== Testing BFloat16 ReuseB (Input Stationary) ===")

  val bf16ConfigReuseB = BFloat16Config(
    SystolicArraySize.defaultSystolicArraySize,
    Dataflow.ReuseB
  )

  TestConfig.createSimConfig("BF16_ReuseB_Test")
    .compile(SystolicArray(bf16ConfigReuseB))
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
