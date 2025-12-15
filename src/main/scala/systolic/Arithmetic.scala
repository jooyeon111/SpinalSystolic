package systolic

import spinal.core._

/**
 * Arithmetic operations for systolic array processing elements.
 *
 * Supports signed (SInt) and unsigned (UInt) multiply-accumulate operations
 * with proper hardware bit width handling.
 *
 * @tparam InputType input data type (e.g, BFloat16, Float16, SInt, UInt)
 * @tparam AccType Accumulation data type (e.g, Float32, SInt, UInt)
 */

trait Arithmetic[InputType <: Data, AccType <: Data] {

  def multiply(inputA: InputType, inputB: InputType) : AccType
  def add(input0: AccType, input1: AccType): AccType
  def addResize(input0: AccType, input1: AccType, targetWidth: Int): AccType

  def zeroInput(width: Int): InputType
  def zeroAccumulation(width: Int): AccType

}


object Arithmetic {

  implicit val sIntArithmetic: Arithmetic[SInt, SInt] = new Arithmetic[SInt, SInt] {

    override def multiply(inputA: SInt, inputB: SInt): SInt = inputA * inputB
    override def add(input0: SInt, input1: SInt): SInt = input0 +^ input1
    override def addResize(input0: SInt, input1: SInt, targetWidth: Int): SInt = (input0 + input1).resize(targetWidth)

    override def zeroInput(width: Int): SInt = S(0, width bits)
    override def zeroAccumulation(width: Int): SInt = S(0, width bits)

  }

  implicit val uIntArithmetic: Arithmetic[UInt, UInt] = new Arithmetic[UInt, UInt] {

    override def multiply(inputA: UInt, inputB: UInt): UInt = inputA * inputB
    override def add(input0: UInt, input1: UInt): UInt = input0 +^ input1
    override def addResize(input0: UInt, input1: UInt, targetWidth: Int): UInt = (input0 + input1).resize(targetWidth)

    override def zeroInput(width: Int): UInt = U(0, width bits)
    override def zeroAccumulation(width: Int): UInt = U(0, width bits)

  }
}
