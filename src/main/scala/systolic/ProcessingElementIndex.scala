package systolic

case class ProcessingElementIndex(
                                 peRowIndex: Int,
                                 peColIndex: Int,
                                 ) {

  def isFirstRow: Boolean = peRowIndex == 0
  def isFirstCol: Boolean = peColIndex == 0

  def isLastPeRow(peRow: Int): Boolean = peRowIndex == peRow -1
  def isLastPeCol(peCol: Int): Boolean = peColIndex == peCol -1

}
