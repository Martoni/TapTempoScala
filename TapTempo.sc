// TapTempo
import jline.console.ConsoleReader /* to read keyboard */
import scala.collection._
import sys.process._ /* shell cmd execution with ! */

val VERSION = "0.1"
val PRECISION = 2
val RESET_TIME = 10
val SAMPLE_SIZE = 5
val SECOND = 1e9
val MIN = 60*SECOND

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
  }

  def printversion() {
    println("TapTempo Scala version " + VERSION)
  }

  def tempo(tfifo: mutable.Buffer[Double]):Double = {
    var sum = 0.0
    tfifo.foreach( sum += _)
    sum/tfifo.length
  }

  def main(args: Array[String]) {
    val arglist = args.toList
    type OptionMap = Map[Symbol, Int]

    /****************/
    /* parsing args */
    /****************/
    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')

      list match {
        case Nil => map
        case ("-h" | "--help") :: tail => usages(); sys.exit(0)
        case ("-v" | "--version") :: tail => printversion; sys.exit(0)
        case ("-p" | "--precision") :: value :: tail =>
                               nextOption(map ++ Map('precision -> value.toInt), tail)
        case ("-r" | "--reset-time") :: value :: tail =>
                               nextOption(map ++ Map('rtime -> value.toInt), tail)
        case ("-s" | "--sample-size") :: value :: tail =>
                               nextOption(map ++ Map('ssize -> value.toInt), tail)

        case option :: tail => println("Unknown option " + option)
                               sys.exit(0)
      }
    }
    val options = nextOption(Map(), arglist)
    println(options)

    var precision = options.getOrElse('precision, -1)
    if(precision == -1)
      precision = PRECISION
    var rtime = options.getOrElse('rtime, -1)
    if(rtime == -1)
      rtime = RESET_TIME
    var ssize = options.getOrElse('ssize, -1)
    if(ssize == -1)
      ssize = SAMPLE_SIZE

    /* Minimum caracters for completed read */
    (Seq("sh", "-c", "stty -icanon min 1 < /dev/tty") !)
    /* Do not print input caracters */
    (Seq("sh", "-c", "stty -echo < /dev/tty") !)

    var timefifo = mutable.Buffer.fill[Double](ssize)(0.0)
    var fifocount = 0

    println("Appuyer sur une touche en cadence (q pour quitter).")
    var c = 0
    var i = 0
    var current_time =  System.nanoTime()
    var old_time = System.nanoTime()
    do {
      c = Console.in.read
      current_time =  System.nanoTime()
      val tempotime = MIN/(current_time - old_time)
      println(tempotime)
      println(60/rtime)
      if(tempotime < 60/rtime){
        fifocount = 0
      }
      if(fifocount < ssize){
        fifocount += 1
      }

      timefifo(i) = tempotime
      i = (i + 1) % ssize

      if(fifocount == ssize){
        printf("Tempo : %.05f\n", tempo(timefifo))
      } else {
        printf("Tempo : %d/%d\n", fifocount, ssize)
      }

      old_time = current_time
    } while (c != 113) // While 'q' is pressed
    println("Bye Bye!")
  }

}

TapTempo.main(args)
