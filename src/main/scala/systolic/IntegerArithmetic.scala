package systolic

import spinal.core._

object IntegerArithmetic {

  implicit val sIntArithmetic: Arithmetic[SInt] = new Arithmetic[SInt] {
    override def zero(width: Int): SInt = S(0, width bits)

    override def add(input0: SInt, input1: SInt): SInt = {
      input0 +^ input1
    }

    override def addResize(input0: SInt, input1: SInt, targetWidth: Int): SInt = {
      (input0 + input1).resize(targetWidth)
    }

    override def multiply(inputA: SInt, inputB: SInt): SInt = {
      inputA * inputB
    }
  }

  implicit val uIntArithmetic: Arithmetic[UInt] = new Arithmetic[UInt] {
    override def zero(width: Int): UInt = U(0, width bits)
    override def add(input0: UInt, input1: UInt): UInt = {
      input0 +^ input1
    }

    override def addResize(input0: UInt, input1: UInt, targetWidth: Int): UInt = {
      (input0 + input1).resize(targetWidth)
    }

    override def multiply(inputA: UInt, inputB: UInt): UInt = {
      inputA * inputB
    }

  }
}
