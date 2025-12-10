package systolic

import spinal.core._
import spinal.lib.Delay
/**
 * Skew buffer for aligning data streams in systolic arrays.
 * Min-depth-first mode is used for ReuseA, ReuseB and ResueC input data streams
 * Max-depth-frist mode is used for ResueA and ReuseB output data streams
 * Reuse C systolic arrays needs special deskew buffer
 *
 * ==Delay Modes==
 * - '''Min-depth-first (default)''': Ascending delays 1, 2, 3, ..., n
 * - '''Max-depth-first''': Descending delays n, n-1, n-2, ..., 1
 *
 * @example
 * {{{
 * // Skew input data for weight stationary array
 * val skewBuffer = new SkewBuffer(SInt(8 bits), delayDepth = 4)
 * for(i <- 0 until 4) {
 *   skewBuffer.io.input(i) := myInputs(i)
 *   pes(0)(i).io.inputB := skewBuffer.io.output(i)
 * }
 *
 * // Deskew output data
 * val deskew = new SkewBuffer(outputType, delayDepth = 4, isMinDepthFirst = false)
 * }}}
 *
 * @param inputType The hardware data type for each channel
 * @param delayDepth Number of parallel channels (determines max delay)
 * @param isMinDepthFirst True for ascending delays, false for descending
 * @tparam T Hardware data type extending SpinalHDL Data
 */

class SkewBuffer [T <: Data](
                            val inputType: T,
                            val delayDepth : Int,
                            val isMinDepthFirst: Boolean = true
) extends Component {

  val io = new Bundle {
    val input = in Vec(HardType(inputType), delayDepth)
    val output = out Vec(HardType(inputType), delayDepth)
  }

  if(isMinDepthFirst){
    for ( i <- 0 until delayDepth){
      io.output(i) := Delay(io.input(i), i + 1)
    }
  } else {
    for( i <- 0 until delayDepth ){
      io.output(i) := Delay(io.input(i), delayDepth -i)
    }
  }


}
