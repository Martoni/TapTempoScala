// TapTempo

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

  def main(args: Array[String]) {
    val arglist = args.toList

    println("TODO: parse command line")
    println(arglist)
    usages()
    //    println("Appuyer sur la touche entrée en cadence (q pour quitter).")
  }

}

TapTempo.main(args)
