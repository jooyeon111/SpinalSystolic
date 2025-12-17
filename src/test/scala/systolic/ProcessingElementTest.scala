package systolic

import spinal.core._
import spinal.core.sim._

object ProcessingElementTest extends App {

  val spinalConfig = SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(
    resetKind = SYNC,
    resetActiveLevel = HIGH
  ))

  val reuseA = Dataflow.ReuseA

//  SimConfig
//    .withConfig(spinalConfig)
//    .withFstWave
//    .workspaceName("Resue_A")
//    .allOptimisation
  TestConfig.createSimConfig("Reuse_A")
    .compile(ProcessingElement(reuseA))
    .doSim { dut =>
      dut.clockDomain.forkStimulus(10, resetCycles = 1)

      // Initialize inputs
      dut.io.inputA #= 0
      dut.io.inputB #= 0
      dut.io.inputC #= 0
      dut.io.inputCaptureEnableA #= false
      dut.clockDomain.waitSampling(1)

      // Test 1: Capture inputA and multiply with inputB
      println("\nTest 1: Capture A=5, then multiply with B=3")
      dut.io.inputA #= 5
      dut.io.inputB #= 3
      dut.io.inputCaptureEnableA #= true
      dut.clockDomain.waitSampling(1)

      dut.io.inputCaptureEnableA #= false
      dut.io.inputB #= 4
      dut.clockDomain.waitSampling(1)

      // outputC should be 5*3 + inputC (from previous cycle)
      dut.clockDomain.waitSampling(1)
      println(s"  OutputA (captured): ${dut.io.outputA.toInt}")
      println(s"  OutputC: ${dut.io.outputC.toInt}")
      assert(dut.io.outputA.toInt == 5, "OutputA should be captured value 5")

      // Test 2: Change B while A is held
      println("\nTest 2: A=5 (held), B=4")
      dut.io.inputB #= 4
      dut.clockDomain.waitSampling(2)
      println(s"  OutputC: ${dut.io.outputC.toInt}")

      dut.clockDomain.waitSampling(5)
      sleep(1000)
    }

  val reuseB = Dataflow.ReuseB

//  SimConfig
//    .withConfig(spinalConfig)
//    .withFstWave
//    .workspaceName("Resue_B")
//    .allOptimisation
  TestConfig.createSimConfig("Reuse_B")
    .compile(ProcessingElement(reuseB))
    .doSim { dut =>
      dut.clockDomain.forkStimulus(10, resetCycles = 1)

      // Initialize inputs
      dut.io.inputA #= 0
      dut.io.inputB #= 0
      dut.io.inputC #= 0
      dut.io.inputCaptureEnableB #= false
      dut.clockDomain.waitSampling(1)

      // Test 1: Capture inputB and multiply with inputA
      println("\nTest 1: Capture B=6, then multiply with A=2")
      dut.io.inputA #= 2
      dut.io.inputB #= 6
      dut.io.inputCaptureEnableB #= true
      dut.clockDomain.waitSampling(1)

      dut.io.inputCaptureEnableB #= false
      dut.io.inputA #= 3
      dut.clockDomain.waitSampling(1)

      dut.clockDomain.waitSampling(1)
      println(s"  OutputB (captured): ${dut.io.outputB.toInt}")
      println(s"  OutputC: ${dut.io.outputC.toInt}")
      assert(dut.io.outputB.toInt == 6, "OutputB should be captured value 6")

      // Test 2: Change A while B is held
      println("\nTest 2: B=6 (held), A=3")
      dut.io.inputA #= 3
      dut.clockDomain.waitSampling(2)
      println(s"  OutputC: ${dut.io.outputC.toInt}")

      dut.clockDomain.waitSampling(5)
      sleep(1000)

    }

  val reuseC = Dataflow.ReuseC

//  SimConfig
//    .withConfig(spinalConfig)
//    .withFstWave
//    .workspaceName("Resue_C")
//    .allOptimisation

  TestConfig.createSimConfig("Reuse_C")
    .compile(ProcessingElement(reuseC))
    .doSim { dut =>
      dut.clockDomain.forkStimulus(10, resetCycles = 1)

      // Initialize inputs
      dut.io.inputA #= 0
      dut.io.inputB #= 0
      dut.io.outputCaptureEnableC #= true
      dut.io.resetPartialC #= true
      dut.clockDomain.waitSampling(1)

      // Test 1: Reset and accumulate
      println("\nTest 1: Reset and accumulate (2*3)")
      dut.io.inputA #= 2
      dut.io.inputB #= 3
      dut.io.resetPartialC #= true
      dut.clockDomain.waitSampling(1)

      // Test 2: Continue accumulation (4*5)
      println("\nTest 2: Continue accumulation (4*5)")
      dut.io.inputA #= 4
      dut.io.inputB #= 5
      dut.io.resetPartialC #= false
      dut.clockDomain.waitSampling(1)

      // Test 3: Continue accumulation (3*3)
      println("\nTest 3: Continue accumulation (3*3)")
      dut.io.inputA #= 3
      dut.io.inputB #= 3
      dut.io.resetPartialC #= false
      dut.clockDomain.waitSampling(1)

      dut.clockDomain.waitSampling(1)
      println(s"  Accumulated OutputC: ${dut.io.outputC.toInt}")
      println(s"  Expected: ${2*3 + 4*5 + 3*3} (6 + 20 + 9 = 35)")

      // Test 4: Reset again
      println("\nTest 4: Reset with new values (7*2)")
      dut.io.inputA #= 7
      dut.io.inputB #= 2
      dut.io.resetPartialC #= true
      dut.clockDomain.waitSampling(2)
      println(s"  OutputC after reset: ${dut.io.outputC.toInt}")

      dut.clockDomain.waitSampling(5)
      sleep(1000)

    }
}
