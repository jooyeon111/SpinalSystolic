package systolic

import spinal.core._
import spinal.core.sim._

//TODO add many edge case too!
object SystolicArrayTest extends App {

  val spinalConfig = SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(
    resetKind = SYNC,
    resetActiveLevel = HIGH,
  ))

  val defaultBitWidth = PortBitWidthInfo.default8bitInputWith32bitOutput

  val systolicConfigReuseA = SystolicArrayConfig.signedInteger(
    row = 4,
    col = 4,
    dataflow = Dataflow.ReuseA,
    defaultBitWidth.bitWidthInputA,
    defaultBitWidth.bitWidthInputB,
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("Reuse_A_Systolic_Array")
    .compile(SystolicArray(systolicConfigReuseA))
    .doSim{ dut =>

      val signedDut = dut.asInstanceOf[SystolicArray[SInt]]

      signedDut.clockDomain.forkStimulus(10, resetCycles = 2)
      // Initialize all inputs
      for (r <- 0 until 4) {
        signedDut.io.inputA(r) #= 0
        for (c <- 0 until 4) {
          signedDut.io.inputCaptureEnableA(r)(c) #= false
        }
      }
      for (c <- 0 until 4) {
        signedDut.io.inputB(c) #= 0
      }
      signedDut.clockDomain.waitSampling(2)

      println("\n=== ReuseA Test: Matrix Multiplication ===")
      println("Matrix A (4x4):")
      println("  1  2  3  4")
      println("  5  6  7  8")
      println("  9 10 11 12")
      println(" 13 14 15 16")
      println("\nMatrix B (4x4):")
      println("  1  0  0  0")
      println("  0  1  0  0")
      println("  0  0  1  0")
      println("  0  0  0  1")

      // Load weights (Matrix A) into PEs
      println("\n--- Loading weights into array ---")
      for (c <- 0 until 4) {
        for (r <- 0 until 4) {
          signedDut.io.inputA(r) #= (r * 4 + c + 1)
          signedDut.io.inputCaptureEnableA(r)(c) #= true
        }
        signedDut.clockDomain.waitSampling(1)
        for (r <- 0 until 4) {
          signedDut.io.inputCaptureEnableA(r)(c) #= false
        }
      }
      signedDut.clockDomain.waitSampling(1)

      // Feed input data (Identity matrix for simplicity)
      println("\n--- Feeding input data ---")
      val inputSequence = Array(
        Array(1, 0, 0, 0),
        Array(0, 1, 0, 0),
        Array(0, 0, 1, 0),
        Array(0, 0, 0, 1)
      )

      for (cycle <- 0 until 8) {
        for (c <- 0 until 4) {
          if (cycle < 4) {
            signedDut.io.inputB(c) #= inputSequence(cycle)(c)
          } else {
            signedDut.io.inputB(c) #= 0
          }
        }
        signedDut.clockDomain.waitSampling(1)
      }

      // Wait for results to propagate
      signedDut.clockDomain.waitSampling(5)

      // Read outputs
      println("\n--- Output Results ---")
      for (r <- 0 until 4) {
        println(s"Output Row $r: ${signedDut.io.outputC(r).toInt}")
      }

      signedDut.clockDomain.waitSampling(5)
      simSuccess()
    }

  val systolicConfigReuseB = SystolicArrayConfig.signedInteger(
    row = 4,
    col = 4,
    dataflow = Dataflow.ReuseB,
    defaultBitWidth.bitWidthInputA,
    defaultBitWidth.bitWidthInputB,
  )

  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("Reuse_B_Systolic_Array")
    .compile(SystolicArray(systolicConfigReuseB))
    .doSim{ dut =>

      val signedDut = dut.asInstanceOf[SystolicArray[SInt]]

      signedDut.clockDomain.forkStimulus(10, resetCycles = 2)

      // Initialize all inputs
      for (r <- 0 until 4) {
        signedDut.io.inputA(r) #= 0
        for (c <- 0 until 4) {
          signedDut.io.inputCaptureEnableB(r)(c) #= false
        }
      }
      for (c <- 0 until 4) {
        signedDut.io.inputB(c) #= 0
      }
      signedDut.clockDomain.waitSampling(2)

      println("\n=== ReuseB Test: Matrix Multiplication ===")
      println("Loading Matrix B (weights) into columns")

      // Load weights (Matrix B) into PEs
      println("\n--- Loading weights into array ---")
      for (r <- 0 until 4) {
        for (c <- 0 until 4) {
          signedDut.io.inputB(c) #= (c * 4 + r + 1)
          signedDut.io.inputCaptureEnableB(r)(c) #= true
        }
        signedDut.clockDomain.waitSampling(1)
        for (c <- 0 until 4) {
          signedDut.io.inputCaptureEnableB(r)(c) #= false
        }
      }
      signedDut.clockDomain.waitSampling(1)

      // Feed input data
      println("\n--- Feeding input data ---")
      val inputDataA = Array(
        Array(1, 2, 3, 4),
        Array(1, 2, 3, 4),
        Array(1, 2, 3, 4),
        Array(1, 2, 3, 4)
      )

      for (cycle <- 0 until 8) {
        for (r <- 0 until 4) {
          if (cycle < 4) {
            signedDut.io.inputA(r) #= inputDataA(r)(cycle)
          } else {
            signedDut.io.inputA(r) #= 0
          }
        }
        signedDut.clockDomain.waitSampling(1)
      }

      // Wait for results to propagate
      signedDut.clockDomain.waitSampling(5)

      // Read outputs
      println("\n--- Output Results ---")
      for (c <- 0 until 4) {
        println(s"Output Col $c: ${signedDut.io.outputC(c).toInt}")
      }

      signedDut.clockDomain.waitSampling(5)
      simSuccess()
    }

  val systolicConfigReuseC = SystolicArrayConfig.signedInteger(
    row = 4,
    col = 4,
    dataflow = Dataflow.ReuseC,
    defaultBitWidth.bitWidthInputA,
    defaultBitWidth.bitWidthInputB,
    defaultBitWidth.bitWidthSystolicOutputC,
  )

  //TODO add diagonal connection test
  SimConfig
    .withConfig(spinalConfig)
    .withFstWave
    .workspaceName("Reuse_C_Systolic_Array")
    .compile(SystolicArray(systolicConfigReuseC))
    .doSim{ dut =>

      val signedDut = dut.asInstanceOf[SystolicArray[SInt]]

      signedDut.clockDomain.forkStimulus(10, resetCycles = 2)

      // Initialize all inputs
      for (r <- 0 until 4) {
        signedDut.io.inputA(r) #= 0
        for (c <- 0 until 4) {
          signedDut.io.outputCaptureEnableC(r)(c) #= true
          signedDut.io.resetPartialC(r)(c) #= true
        }
      }
      for (c <- 0 until 4) {
        signedDut.io.inputB(c) #= 0
      }
      signedDut.clockDomain.waitSampling(2)

      println("\n=== ReuseC Test: Matrix Multiplication with Accumulation ===")
      println("Computing C = A × B where results accumulate in each PE")

      // Define test matrices
      val matrixA = Array(
        Array(1, 2, 3, 4),
        Array(2, 3, 4, 5),
        Array(3, 4, 5, 6),
        Array(4, 5, 6, 7)
      )

      val matrixB = Array(
        Array(1, 0, 0, 0),
        Array(0, 1, 0, 0),
        Array(0, 0, 1, 0),
        Array(0, 0, 0, 1)
      )

      println("\nMatrix A:")
      for (row <- matrixA) {
        println(row.mkString("  "))
      }
      println("\nMatrix B:")
      for (row <- matrixB) {
        println(row.mkString("  "))
      }

      // Reset accumulation
      println("\n--- Initializing accumulation (reset) ---")
      for (r <- 0 until 4) {
        for (c <- 0 until 4) {
          signedDut.io.resetPartialC(r)(c) #= true
        }
      }
      dut.clockDomain.waitSampling(1)

      // Perform matrix multiplication with accumulation
      println("\n--- Performing multiplication ---")
      for (k <- 0 until 4) {
        println(s"Step $k: Processing column $k of A and row $k of B")

        // Set inputs for this step
        for (r <- 0 until 4) {
          signedDut.io.inputA(r) #= matrixA(r)(k)
        }
        for (c <- 0 until 4) {
          signedDut.io.inputB(c) #= matrixB(k)(c)
        }

        // First iteration resets, subsequent iterations accumulate
        for (r <- 0 until 4) {
          for (c <- 0 until 4) {
            signedDut.io.resetPartialC(r)(c) #= (k == 0)
          }
        }

        signedDut.clockDomain.waitSampling(1)
      }

      // Wait for final results to stabilize
      signedDut.clockDomain.waitSampling(10)

      // Read and display outputs
      println("\n--- Final Output Results ---")
      val numOutputs = 4 + 4 - 1  // row + col - 1
      for (i <- 0 until numOutputs) {
        println(s"Output $i: ${signedDut.io.outputC(i).toInt}")
      }

      signedDut.clockDomain.waitSampling(5)
      simSuccess()
    }

}
