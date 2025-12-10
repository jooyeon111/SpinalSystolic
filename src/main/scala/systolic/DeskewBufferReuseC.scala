package systolic

import spinal.core._
import spinal.lib.Delay

class DeskewBufferReuseC[T <: Data](
                        val portType: T,
                        val arrayConfig: SystolicArrayConfig,
) extends Component {

  assert(arrayConfig.dataflow == Dataflow.ReuseC, "only Resue C systolic array can use this deskewing")

  val numPort = arrayConfig.row + arrayConfig.col - 1

  val io = new Bundle {
    val input = in Vec(HardType(portType), numPort)
    val output = out Vec(HardType(portType), numPort)
  }

  for( i <- 0 until arrayConfig.row + arrayConfig.col - 1) {

    val depth = if (i < arrayConfig.row - 1) {
      arrayConfig.row - i - 1
    } else 0

    io.output(i) := Delay(io.input(i), depth)

  }

}
