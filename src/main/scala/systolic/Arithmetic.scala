package systolic

import spinal.core._

/**
 * Arithmetic operations for systolic array processing elements.
 *
 * Supports signed (SInt) and unsigned (UInt) multiply-accumulate operations
 * with proper hardware bit width handling.
 *
 * @tparam T Hardware data type
 */

trait Arithmetic[T <: Data] {
  def multiply(inputA: T, inputB: T) : T
  def add(input0: T, input1: T): T
  def addResize(input0: T, input1: T, targetWidth: Int): T
  def zero(width: Int) : T
}


object Arithmetic {

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
