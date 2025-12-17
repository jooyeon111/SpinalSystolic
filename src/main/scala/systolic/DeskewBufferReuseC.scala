package systolic

import spinal.core._
import spinal.lib.Delay
/**
 * Deskew Buffer ReuseC for supporting forwarding ResueC diagonal output direction
 * Only Reuse C (Output Stationary) needs this for deskewing output
 *
 */
class DeskewBufferReuseC[T <: Data](
                        val portType: T,
                        val arrayConfig: SystolicArrayConfig,
) extends Component {

  assert(arrayConfig.dataflow == Dataflow.ReuseC, "only Resue C systolic array can use this deskewing")
  setDefinitionName(s"${TileType.TypeC}_DeskewBuffer")

  val numPort = arrayConfig.size.row + arrayConfig.size.col - 1

  val io = new Bundle {
    val input = in Vec(HardType(portType), numPort)
    val output = out Vec(HardType(portType), numPort)
  }

  for( i <- 0 until arrayConfig.size.row + arrayConfig.size.col - 1) {

    val depth = if (i < arrayConfig.size.row - 1) {
      arrayConfig.size.row - i - 1
    } else 0

    io.output(i) := Delay(io.input(i), depth)

  }

}
