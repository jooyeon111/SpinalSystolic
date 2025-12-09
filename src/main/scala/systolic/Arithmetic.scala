package systolic

import spinal.core._

trait Arithmetic[T <: Data] {
  def multiply(inputA: T, inputB: T) : T
  def add(input0: T, input1: T): T
  def addResize(input0: T, input1: T, targetWidth: Int): T
  def zero(width: Int) : T
}
