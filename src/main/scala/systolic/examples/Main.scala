package systolic.examples

import spinal.core._
import systolic._

object Main extends App {

  private val systolicArrayRow: Int = 5
  private val systolicArrayCol: Int = 6
  private val dataflow = Dataflow.ReuseC
  private val integerType = IntegerType.SignedInteger
  private val bitWidthInputA = 8
  private val bitWidthInputB = 8
  private val bitWidthOutputC = 32

  val config = SystolicArrayConfig(
    row = systolicArrayRow,
    col = systolicArrayCol,
    dataflow = dataflow,
    integerConfig = IntegerConfig(
      integerType,
      PortBitWidthInfo(
        bitWidthInputA,
        bitWidthInputB,
        Some(bitWidthOutputC),
      ),
    )
  )

  SpinalConfig(
    targetDirectory = "output",
    defaultClockDomainFrequency = FixedFrequency(100 MHz),
    defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC, resetActiveLevel = HIGH)
  ).generateVerilog(
    SystolicArray(config)
  )

}
