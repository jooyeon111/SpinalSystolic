package systolic

import spinal.core._

trait PortTypeProvider [T <: Data] {

  def createInputTypeA: T
  def createInputTypeB: T
  def createMultOutputType: T
  def createPeInputTypeC(index: ProcessingElementIndex): T
  def createPeOutputTypeC(index: ProcessingElementIndex): T
  def createSystolicOutputTypeC: T

  final def zeroInputA(implicit arithmetic: Arithmetic[T]): T = arithmetic.zero(createInputTypeA.getBitsWidth)
  final def zeroInputB(implicit arithmetic: Arithmetic[T]): T = arithmetic.zero(createInputTypeB.getBitsWidth)
  final def zeroMultOutput(implicit arithmetic: Arithmetic[T]): T = arithmetic.zero(createMultOutputType.getBitsWidth)
  final def zeroPeInputTypeC(index: ProcessingElementIndex)(implicit arithmetic: Arithmetic[T]): T  =
    arithmetic.zero(createPeInputTypeC(index).getBitsWidth)
  final def zeroPeOutputTypeC(index: ProcessingElementIndex)(implicit arithmetic: Arithmetic[T]): T  =
    arithmetic.zero(createPeOutputTypeC(index).getBitsWidth)
    final def zeroSystolicOutputTypeC(index: ProcessingElementIndex)(implicit arithmetic: Arithmetic[T]): T =
    arithmetic.zero(createSystolicOutputTypeC.getBitsWidth)

}
