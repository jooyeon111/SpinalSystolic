package systolic

import spinal.core._
import spinal.core.sim._

//TODO add many edge case too!
object SystolicArrayTest extends App {

  val defaultBitWidth = PortBitWidthInfo.default8bitInputWith32bitOutput

  val systolicConfigReuseA = SignedIntConfig(
    SystolicArraySize.defaultSystolicArraySize,
    dataflow = Dataflow.ReuseA,
    defaultBitWidth.bitWidthInputA,
    defaultBitWidth.bitWidthInputB,
  )

  TestConfig.createSimConfig("Reuse_A_Systolic_Array")
    .compile(SystolicArray(systolicConfigReuseA))
    .doSim{ dut =>

      dut.clockDomain.forkStimulus(10, resetCycles = 2)
      // Initialize all inputs
      for (r <- 0 until 4) {
        dut.io.inputA(r) #= 0
        for (c <- 0 until 4) {
          dut.io.inputCaptureEnableA(r)(c) #= false
        }
      }
      for (c <- 0 until 4) {
        dut.io.inputB(c) #= 0
      }
      dut.clockDomain.waitSampling(2)

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
          dut.io.inputA(r) #= (r * 4 + c + 1)
          dut.io.inputCaptureEnableA(r)(c) #= true
        }
        dut.clockDomain.waitSampling(1)
        for (r <- 0 until 4) {
          dut.io.inputCaptureEnableA(r)(c) #= false
        }
      }
      dut.clockDomain.waitSampling(1)

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
            dut.io.inputB(c) #= inputSequence(cycle)(c)
          } else {
            dut.io.inputB(c) #= 0
          }
        }
        dut.clockDomain.waitSampling(1)
      }

      // Wait for results to propagate
      dut.clockDomain.waitSampling(5)

      // Read outputs
      println("\n--- Output Results ---")
      for (r <- 0 until 4) {
        println(s"Output Row $r: ${dut.io.outputC(r).toInt}")
      }

      dut.clockDomain.waitSampling(5)
      simSuccess()
    }

  val systolicConfigReuseB = SignedIntConfig(
    SystolicArraySize.defaultSystolicArraySize,
    dataflow = Dataflow.ReuseB,
    defaultBitWidth.bitWidthInputA,
    defaultBitWidth.bitWidthInputB,
  )

  TestConfig.createSimConfig("Reuse_B_Systolic_Array")
    .compile(SystolicArray(systolicConfigReuseB))
    .doSim{ dut =>

      dut.clockDomain.forkStimulus(10, resetCycles = 2)

      // Initialize all inputs
      for (r <- 0 until 4) {
        dut.io.inputA(r) #= 0
        for (c <- 0 until 4) {
          dut.io.inputCaptureEnableB(r)(c) #= false
        }
      }
      for (c <- 0 until 4) {
        dut.io.inputB(c) #= 0
      }
      dut.clockDomain.waitSampling(2)

      println("\n=== ReuseB Test: Matrix Multiplication ===")
      println("Loading Matrix B (weights) into columns")

      // Load weights (Matrix B) into PEs
      println("\n--- Loading weights into array ---")
      for (r <- 0 until 4) {
        for (c <- 0 until 4) {
          dut.io.inputB(c) #= (c * 4 + r + 1)
          dut.io.inputCaptureEnableB(r)(c) #= true
        }
        dut.clockDomain.waitSampling(1)
        for (c <- 0 until 4) {
          dut.io.inputCaptureEnableB(r)(c) #= false
        }
      }
      dut.clockDomain.waitSampling(1)

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
            dut.io.inputA(r) #= inputDataA(r)(cycle)
          } else {
            dut.io.inputA(r) #= 0
          }
        }
        dut.clockDomain.waitSampling(1)
      }

      // Wait for results to propagate
      dut.clockDomain.waitSampling(5)

      // Read outputs
      println("\n--- Output Results ---")
      for (c <- 0 until 4) {
        println(s"Output Col $c: ${dut.io.outputC(c).toInt}")
      }

      dut.clockDomain.waitSampling(5)
      simSuccess()
    }

  val systolicConfigReuseC = SignedIntConfig(
    SystolicArraySize.defaultSystolicArraySize,
    dataflow = Dataflow.ReuseC,
    defaultBitWidth.bitWidthInputA,
    defaultBitWidth.bitWidthInputB,
    defaultBitWidth.bitWidthSystolicOutputC,
  )

  //TODO add diagonal connection test
  TestConfig.createSimConfig("Reuse_C_Systolic_Array")
    .compile(SystolicArray(systolicConfigReuseC))
    .doSim{ dut =>

      dut.clockDomain.forkStimulus(10, resetCycles = 2)

      // Initialize all inputs
      for (r <- 0 until 4) {
        dut.io.inputA(r) #= 0
        for (c <- 0 until 4) {
          dut.io.outputCaptureEnableC(r)(c) #= true
          dut.io.resetPartialC(r)(c) #= true
        }
      }
      for (c <- 0 until 4) {
        dut.io.inputB(c) #= 0
      }
      dut.clockDomain.waitSampling(2)

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
          dut.io.resetPartialC(r)(c) #= true
        }
      }
      dut.clockDomain.waitSampling(1)

      // Perform matrix multiplication with accumulation
      println("\n--- Performing multiplication ---")
      for (k <- 0 until 4) {
        println(s"Step $k: Processing column $k of A and row $k of B")

        // Set inputs for this step
        for (r <- 0 until 4) {
          dut.io.inputA(r) #= matrixA(r)(k)
        }
        for (c <- 0 until 4) {
          dut.io.inputB(c) #= matrixB(k)(c)
        }

        // First iteration resets, subsequent iterations accumulate
        for (r <- 0 until 4) {
          for (c <- 0 until 4) {
            dut.io.resetPartialC(r)(c) #= (k == 0)
          }
        }

        dut.clockDomain.waitSampling(1)
      }

      // Wait for final results to stabilize
      dut.clockDomain.waitSampling(10)

      // Read and display outputs
      println("\n--- Final Output Results ---")
      val numOutputs = 4 + 4 - 1  // row + col - 1
      for (i <- 0 until numOutputs) {
        println(s"Output $i: ${dut.io.outputC(i).toInt}")
      }

      dut.clockDomain.waitSampling(5)
      simSuccess()
    }

}
