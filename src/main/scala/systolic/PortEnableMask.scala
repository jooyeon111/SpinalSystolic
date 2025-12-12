package systolic

case class PortEnableMask(
                           withOutputPortA: Boolean,
                           withOutputPortB: Boolean,
                           withInputPortC: Boolean,
                         )
object PortEnableMask {

  val defaultPortEnableMask: PortEnableMask = PortEnableMask(
    withOutputPortA = true,
    withOutputPortB = true,
    withInputPortC = true
  )

}