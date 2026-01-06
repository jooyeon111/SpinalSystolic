package systolic

case class SystolicArraySize(row: Int, col: Int)

//TODO i think at least (5,5) systolic array size is needed
object SystolicArraySize {
  def defaultSystolicArraySize: SystolicArraySize = SystolicArraySize(4, 4)
}

trait SystolicArrayConfig {
  def size: SystolicArraySize
  def dataflow: Dataflow.Value
}

case class BFloat16Config(
                           size: SystolicArraySize,
                           dataflow: Dataflow.Value,
) extends SystolicArrayConfig

case class Float16Config(
                          size: SystolicArraySize,
                          dataflow: Dataflow.Value
) extends SystolicArrayConfig

case class SignedIntConfig(
                            size: SystolicArraySize,
                            dataflow: Dataflow.Value,
                            bitWidthA: Int,
                            bitWidthB: Int,
                            bitWidthOutputC: Option[Int] = None
) extends SystolicArrayConfig

case class UnsignedIntConfig(
                               size: SystolicArraySize,
                               dataflow: Dataflow.Value,
                               bitWidthA: Int,
                               bitWidthB: Int,
                               bitWidthOutputC: Option[Int] = None
) extends SystolicArrayConfig
