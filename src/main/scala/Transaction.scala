class Transaction(val id: String, val time: String, var price: String) {
  override def toString: String = f" ID: $id | Time: $time | Price: $price";
  def csvLine: String = f"$id,$time,$price\n";
}
