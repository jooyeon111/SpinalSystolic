package systolic

case class ProcessingElementIndex(
                                 peRowIndex: Int,
                                 peColIndex: Int,
                                 ) {

  require(peRowIndex >= 0, s"Row index must be non-negative, got $peRowIndex")
  require(peColIndex >= 0, s"Column index must be non-negative, got $peColIndex")

  def isFirstRow: Boolean = peRowIndex == 0
  def isFirstCol: Boolean = peColIndex == 0

  def isLastPeRow(peRow: Int): Boolean = peRowIndex == peRow -1
  def isLastPeCol(peCol: Int): Boolean = peColIndex == peCol -1

}

object ProcessingElementIndex {
  val defaultProcessingElementIndex: ProcessingElementIndex = ProcessingElementIndex(0,0)
}