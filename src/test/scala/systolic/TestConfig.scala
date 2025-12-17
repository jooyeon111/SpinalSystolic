package systolic

import spinal.core._
import spinal.core.sim._

object TestConfig {

  val defaultSpinalConfig: SpinalConfig = SpinalConfig(
    defaultConfigForClockDomains = ClockDomainConfig(
      resetKind = SYNC,
      resetActiveLevel = HIGH
    )
  )

  def createSimConfig(workspaceName: String): SpinalSimConfig = {
    SimConfig
      .withConfig(defaultSpinalConfig)
      .withFstWave
      .workspaceName(workspaceName)
      .allOptimisation
  }

}
