package systolic

import spinal.core._
import spinal.lib.Delay

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
