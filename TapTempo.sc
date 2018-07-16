// TapTempo
import jline.console.ConsoleReader /* to read keyboard */
import scala.collection._
import sys.process._ /* shell cmd execution with ! */

object TapTempo {
  def usages() {
    println("-h, --help            affiche ce message d'aide")
    println("-p, --precision       changer le nombre de décimale du tempo à afficher")
    println("                      la valeur par défaut est 0 décimales, le max est 5 décimales")
    println("-r, --reset-time      changer le temps en seconde de remise à zéro du calcul")
    println("                      la valeur par défaut est 5 secondes")
    println("-s, --sample-size     changer le nombre d'échantillons nécessaires au calcul du tempo")
    println("                      la valeur par défaut est 5 échantillons")
    println("-v, --version         afficher la version")
    sys.exit(0)
  }

  def tempo(tfifo: mutable.Buffer[Double]):Double = {
    var sum = 0.0
    tfifo.foreach( sum += _)
    sum/tfifo.length
  }


  def main(args: Array[String]) {
    val arglist = args.toList
    val flength = 5
    val MIN = 60e9

    println("Begin")
    /* Minimum caracters for completed read */
    (Seq("sh", "-c", "stty -icanon min 1 < /dev/tty") !)
    /* Do not print input caracters */
    (Seq("sh", "-c", "stty -echo < /dev/tty") !)

    var timefifo = mutable.Buffer.fill[Double](flength)(0.0)

    println("Appuyer sur une touche en cadence (q pour quitter).")
    var c = Console.in.read
    var i = 0
    var current_time = System.nanoTime()
    var old_time = System.nanoTime()
    do {
      println("Got " + c)
      c = Console.in.read
      timefifo(i) = MIN/(current_time - old_time)
      old_time = current_time
      current_time =  System.nanoTime()
      i = (i + 1) % flength
      println(tempo(timefifo))

    } while (c != 113)

    println(arglist)
    arglist.foreach(println)
    usages()
   //    println( con.readVirtualKey() )
  }

}

TapTempo.main(args)
