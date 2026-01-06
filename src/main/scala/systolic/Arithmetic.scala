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

//  def multiply(inputA: InputType, inputB: InputType) : AccType
//  def add(input0: AccType, input1: AccType): AccType
//  def addResize(input0: AccType, input1: AccType, targetWidth: Int): AccType

//  implicit def castInput(t: InputType): InputAri

  implicit def castInput(t: InputType): InputArithmeticOps[InputType, AccType]
  implicit def castAcc(t: AccType): AccArithmeticOps[AccType]

  def zeroInput(width: Int): InputType
  def zeroAccumulation(width: Int): AccType

}

abstract class InputArithmeticOps[InputType <: Data, AccType <: Data](self: InputType) {
  def *(other: InputType): AccType
}

abstract class AccArithmeticOps[AccType <: Data](self: AccType) {
  def +(other: AccType): AccType
  def +^(other: AccType): AccType
//  def resize(width: Int): AccType
  def resizeTo(t: AccType): AccType
}


//object Arithmetic {
//
//  implicit val sIntArithmetic: Arithmetic[SInt, SInt] = new Arithmetic[SInt, SInt] {
//
//    override def multiply(inputA: SInt, inputB: SInt): SInt = inputA * inputB
//    override def add(input0: SInt, input1: SInt): SInt = input0 +^ input1
//    override def addResize(input0: SInt, input1: SInt, targetWidth: Int): SInt = (input0 + input1).resize(targetWidth)
//
//    override def zeroInput(width: Int): SInt = S(0, width bits)
//    override def zeroAccumulation(width: Int): SInt = S(0, width bits)
//
//  }
//
//  implicit val uIntArithmetic: Arithmetic[UInt, UInt] = new Arithmetic[UInt, UInt] {
//
//    override def multiply(inputA: UInt, inputB: UInt): UInt = inputA * inputB
//    override def add(input0: UInt, input1: UInt): UInt = input0 +^ input1
//    override def addResize(input0: UInt, input1: UInt, targetWidth: Int): UInt = (input0 + input1).resize(targetWidth)
//
//    override def zeroInput(width: Int): UInt = U(0, width bits)
//    override def zeroAccumulation(width: Int): UInt = U(0, width bits)
//
//  }
//}


object Arithmetic {

  // ============ SInt ============
  implicit val sIntArithmetic: Arithmetic[SInt, SInt] = new Arithmetic[SInt, SInt] {

    override implicit def castInput(self: SInt): InputArithmeticOps[SInt, SInt] =
      new InputArithmeticOps[SInt, SInt](self) {
        override def *(other: SInt): SInt = self * other
      }

    override implicit def castAcc(self: SInt): AccArithmeticOps[SInt] =
      new AccArithmeticOps[SInt](self) {
        override def +(other: SInt): SInt = self + other
        override def +^(other: SInt): SInt = self +^ other
//        override def resize(width: Int): SInt = self.resize(width)
        override def resizeTo(t: SInt): SInt = self.resize(t.getWidth)
      }

    override def zeroInput(width: Int): SInt = S(0, width bits)
    override def zeroAccumulation(width: Int): SInt = S(0, width bits)
  }

  // ============ UInt ============
  implicit val uIntArithmetic: Arithmetic[UInt, UInt] = new Arithmetic[UInt, UInt] {

    override implicit def castInput(self: UInt): InputArithmeticOps[UInt, UInt] =
      new InputArithmeticOps[UInt, UInt](self) {
        override def *(other: UInt): UInt = self * other
      }

    override implicit def castAcc(self: UInt): AccArithmeticOps[UInt] =
      new AccArithmeticOps[UInt](self) {
        override def +(other: UInt): UInt = self + other
        override def +^(other: UInt): UInt = self +^ other

        //        override def resize(width: Int): UInt = self.resize(width)
        override def resizeTo(t: UInt): UInt = self.resize(t.getWidth)
      }

    override def zeroInput(width: Int): UInt = U(0, width bits)
    override def zeroAccumulation(width: Int): UInt = U(0, width bits)
  }

  implicit val bf16Fp32Arithmetic: Arithmetic[BFloat16, Float32] = new Arithmetic[BFloat16, Float32] {

    override implicit def castInput(self: BFloat16): InputArithmeticOps[BFloat16, Float32] =
      new InputArithmeticOps[BFloat16, Float32](self) {
        override def *(other: BFloat16): Float32 = BFloat16Arithmetic.multiply(self, other)
      }

    override implicit def castAcc(self: Float32): AccArithmeticOps[Float32] =
      new AccArithmeticOps[Float32](self) {
        override def +(other: Float32): Float32 = BFloat16Arithmetic.add(self, other)
        override def +^(other: Float32): Float32 = BFloat16Arithmetic.add(self, other)

        //        override def resize(width: Int): Float32 = {
        //          require(width == 32)
        //          self
        //        }
        override def resizeTo(t: Float32): Float32 = self
      }

    override def zeroInput(width: Int): BFloat16 = BFloat16.zero
    override def zeroAccumulation(width: Int): Float32 = Float32.zero

  }

  implicit val fp16Fp32Arithmetic: Arithmetic[Float16, Float32] = new Arithmetic[Float16, Float32] {

    override implicit def castInput(self: Float16): InputArithmeticOps[Float16, Float32] =
      new InputArithmeticOps[Float16, Float32](self) {
        override def *(other: Float16): Float32 = Float16Arithmetic.multiply(self, other)
      }

    override implicit def castAcc(self: Float32): AccArithmeticOps[Float32] =
      new AccArithmeticOps[Float32](self) {
        override def +(other: Float32): Float32 = Float16Arithmetic.add(self, other)
        override def +^(other: Float32): Float32 = Float16Arithmetic.add(self, other)
//        override def resize(width: Int): Float32 = {
//          require(width == 32)
//          self
//        }
        override def resizeTo(t: Float32): Float32 = self
      }

    override def zeroInput(width: Int): Float16 = Float16.zero
    override def zeroAccumulation(width: Int): Float32 = Float32.zero
  }


}