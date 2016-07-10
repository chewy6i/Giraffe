package core

import org.slf4j.LoggerFactory

object CmdLine {
  val logger = LoggerFactory.getLogger("com")

  val usage = """
    Usage:  [--appName] [--sparkMaster]
              """

  def getOpts(args: Array[String]): collection.mutable.Map[String, String] = {
    if (args.length == 0) {
      logger.warn(usage)
      System.exit(1)
    }
    val (opts, vals) = args.partition { _.startsWith("-") }

    val optsMap = collection.mutable.Map[String, String]()

    opts.map { x =>
      val pair = x.split("=")
      if (pair.length == 2) {
        optsMap += (pair(0).split("-{1,2}")(1) -> pair(1))
      } else {
        logger.warn(usage)
        System.exit(1)

      }
    }

    return optsMap
  }

}