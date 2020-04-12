import java.io.{FileWriter, PrintWriter}

import scala.collection.mutable.ArrayBuffer

object PriceMatcher extends App {

  private var pricePoints = new ArrayBuffer[PricePoint]();
  private var transactions = new ArrayBuffer[Transaction]();

  println ("Reading Data");
  time { this.readPrices("data/prices-2019-02-13.csv");
         this.readTransactions("data/transactions-2019-02-13.csv")};

  println ("Matching & Printing output");
  time { this.matchPrices("data/output-2019-02-13.csv")}

  def readPrices (filename: String){
    var i = 0;
    pricePoints+= new PricePoint("NO PRICE","NO_TIME"); // corner case of first transaction happening without prior price
    val bufferedCSV = io.Source.fromFile(filename);
    for (line <- bufferedCSV.getLines().drop(1)) { // read all avoid header
      val cols = line.split(",").map(_.trim)
      pricePoints += new PricePoint(cols(0),cols(1));
      i +=1;
    }
    printf (" Read %d Price Points", i);
  }

  def readTransactions (filename: String){
    var i = 0;
    val bufferedCSV = io.Source.fromFile(filename);
    for (line <- bufferedCSV.getLines().drop(1)) { // read all avoid header
      val cols = line.split(",").map(_.trim)
      transactions += new Transaction(cols(0),cols(1),null);
      i +=1;
    }

    printf (" Read %d Transactions \n", i);
  }

  def matchPrices(filename: String) {
    var processing_index = 0; // memoized last transaction point
   // var nextPrice = lastfound+1;
    var tx_count = 0;
    val pricingbound = pricePoints.length-1;
    val outputfile= new FileWriter(filename);
    outputfile.write("TRANSACTION_ID,TRANSACTION_TIME\n"); // write header

    for (tx <- transactions ) {
      var found = false;
      while (!found && processing_index< pricingbound) {
        if (pricePoints(processing_index+1).time.compareTo(tx.time)>0) { //match found
          tx.price = pricePoints(processing_index).price;
          outputfile.write(tx.csvLine);
          found = true;
          tx_count+=1;
          //println (tx_count + ": " + tx);
        } else {
          processing_index+=1;
        }
      }

    }
    outputfile.close();

  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()

    println("Elapsed time: " + (t1-t0)/1000000+ "ms");
    result
  }
}
