package com.yanjiuyanjiu.ml.kmeans
import java.io.IOException
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import com.yanjiuyanjiu.ml.vector._

object Clustering {
  /** 日志. */
  private val LOGGER = Logger.getLogger(KMeans.getClass().getName())
  try {
    val fileHandler = new FileHandler(LOGGER.getName());
    fileHandler.setLevel(Level.INFO);
    fileHandler.setFormatter(new SimpleFormatter);
    LOGGER.addHandler(fileHandler);
  } catch {
    case e: SecurityException =>
      LOGGER.warning(e.getMessage())
    case e: IOException =>
      LOGGER.warning(e.getMessage());
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      LOGGER.severe("Usage: cluster threshold (-sparse | -dense) dataset\n")
      LOGGER.severe("Example: cluster 0.05 dataset\n")
      return
    }

    // 通过这里关闭日志，调试的时候用FINE, 运行的时候用INFO及以上
    val pkgLogger = Logger.getLogger("cn.msra.intern").setLevel(Level.INFO)

    val startTime = System.currentTimeMillis()
    val threshold = args(0).toDouble
    val sparse = args(1).equals("-sparse")

    val preprocessor = new Preprocessor(sparse)
    preprocessor.process(args(2))

    val textVectors = preprocessor.getVectors()
    val vectors =
      (if (sparse) {
        new Array[SparseVector](textVectors.length)
      } else {
        new Array[DenseVector](textVectors.length)
      }).asInstanceOf[Array[Vector]]
    for (i <- 0 until textVectors.length) {
      vectors(i) = textVectors(i).vector
    }
    val kmeans = new KMeans(threshold, vectors)
    kmeans.cluster()

    val catagories = new Array[String](textVectors.length)
    for (i <- 0 until textVectors.length) {
      catagories(i) = textVectors(i).catagory
    }
    LOGGER.info("NMI = " + kmeans.getNMI(catagories,
    preprocessor.getCatagoryFileCount()))

    val endTime = System.currentTimeMillis();
    LOGGER.info("\ttotal time: " + (endTime - startTime) / 1000 + "s.\n");
    return
  }
}