/**
 * 预处理.
 */
package com.yanjiuyanjiu.ml.kmeans

import java.util.logging._
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import scalax.io._
import com.yanjiuyanjiu.ml.vector._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

/**
 * @author soulmachine@gmail.com
 * @param sparse 是否用稀疏表示
 */
final class Preprocessor(sparse: Boolean) {
  // TODO: 下面三个成员，能不能改为val，因为它只是在 process中初始化一次，以后就不变了
  /** 单词表. */
  private var vocabulary: Map[String, Preprocessor.WordGlobalInfo] = _
  /** 文本向量. */
  private var vectors: Array[TextVector] = _
  /** 每个类别的文件总数, key为类别，value为该类别下的文件数目. */
  private var catagoryFileCount: Map[String, Int] = _

  def getVectors() = vectors
  def getCatagoryFileCount() = catagoryFileCount
  /**
   * 预处理.
   * @param datasetDir 数据样本的目录
   */
  def process(datasetDir: String): Unit = {
    val files = Preprocessor.scanDirNoRecursion(datasetDir)
    val filesInfo = Preprocessor.getFileInfo(files)

    vocabulary = Preprocessor.extractVocabulary(filesInfo)
    vectors = Preprocessor.convertToVectors(filesInfo, vocabulary, sparse)
    catagoryFileCount = Preprocessor.calcClassFileCount(filesInfo);
  }
}

object Preprocessor {
  /** dataset采用UTF-8编码. */
  private val ENCODING = "ISO8859-1"; //"UTF-8";
  /** 文档频率太低的要丢弃，PERCENT < 文档频率/文档总数，一般设为0.01. */
  private val PERCENT = 0.01;
  /** 文档频率太高也要丢弃.*/
  private val PERCENT_MAX = 0.8;
  /** 日志. */
  private val LOGGER = Logger.getLogger(Preprocessor.getClass().getName())
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
  /**
   * 递归遍历目录（包括子目录），非递归实现.
   *
   * @param path
   *            目录路径，该路径下存放dataset
   * @return 所有文件的绝对路径
   */
  private def scanDirNoRecursion(path: String): List[String] = {
    val absolutePathes: ArrayBuffer[String] = new ArrayBuffer[String]

    val dirs = new ListBuffer[File]
    val dir = new File(path)
    var files = dir.listFiles()
    for (i <- 0 until files.length) {
      if (files(i).isDirectory()) {
        dirs.append(files(i))
      } else {
        //System.out.println(file[i].getAbsolutePath());
        absolutePathes.append(files(i).getAbsolutePath());
      }
    }

    while (!dirs.isEmpty) {
      val tmp = dirs.remove(0) // 首个目录
      if (tmp.isDirectory()) {
        files = tmp.listFiles()
        if (files.length > 0) {
          for (i <- 0 until files.length) {
            if (files(i).isDirectory()) {
              dirs.append(files(i)) // 目录则加入目录列表，关键
            } else {
              //System.out.println(file[i]);
              absolutePathes.append(files(i).getAbsolutePath())
            }
          }
        } else {
          LOGGER.warning(tmp.toString());
          // num++;
        }
      }
    }
    absolutePathes.toList
  }

  /**
   * 读取文本文件后，预处理得到的信息，以后依赖该信息，不再从硬盘读取文件.
   * @author 方勤
   * @param allWordsCount 该文件包含的单词总数
   * @param wordCount 每个单词的个数
   */
  private class FileInfo(val allWordsCount: Int, val wordCount: Map[String, Int]) {
  }

  /**
   * 计算每个文件的信息，统计每个单词的出现个数.
   * @param files 所有文本文件的绝对路径
   * @return 文件信息
   */
  private def getFileInfo(files: List[String]): Map[String, FileInfo] = {
    val filesInfo = scala.collection.mutable.Map.empty[String, FileInfo]
    for (file <- files) {
      val text = readTextFile(file, ENCODING)
      if (text.length() > 0) {
        val tokens = TextSegmentation.tokenize(text)

        // 本文件的单词计数器
        val wordCounter =
          scala.collection.mutable.Map.empty[String, Int]
        for (token <- tokens) {
          val count = wordCounter.get(token)
          count match {
            case Some(c) => {
              wordCounter.put(token, c + 1)
            }
            case None => {
              wordCounter.put(token, 1)
            }
          }
        }
        filesInfo.put(file, new FileInfo(tokens.size, wordCounter.toMap))
      }
    }
    LOGGER.info("scanned " + filesInfo.size + " files.\n");
    filesInfo.toMap
  }
  /**
   * 单词的全局信息.
   *
   * @param index 单词在文本向量中的位置
   * @param idf 单词的逆文档频率IDF.
   */
  private final class WordGlobalInfo(val index: Int, val idf: Double) {
  }

  /**
   * 获取单词表，并进行缩减，计算出每个单词的IDF.
   *
   * 在dataset中出现过的所有单词（去掉stopwords，标点符号，以及过滤掉一些不需要的词）<br/>
   * 如果某单词w要么在dataset中极少的文本中出现，要么在Dataset中绝大多数的文本中出现，这种词对
   * 文本的区分不会产生太大的作用，反而会影响到聚类的效果，要去掉。
   *
   * @param filesInfo 所有文本文件的预处理信息
   * @return 单词表
   */
  private def extractVocabulary(filesInfo: Map[String, FileInfo]): Map[String, WordGlobalInfo] = {
    /** 单词计数器，key为单词，value为出现过的文件数. */
    val wordCount = scala.collection.mutable.Map.empty[String, Integer]
    for ((k, v) <- filesInfo) {
      for ((kk, vv) <- v.wordCount) {
        val count = wordCount.get(kk)
        count match {
          case Some(x) => wordCount.put(kk, x + 1)
          case None => wordCount.put(kk, 1)
        }
      }
    }

    /**
     * 计算单词的优先级，用于选出一些优先级较高的词作为特征词.
     * @param word 单词
     * @param rank 排名值，用文档频率当做排名值
     * @author soulmachine@gmail.com
     *
     */
    case class WordRank(val word: String, val rank: Int) extends Ordered[WordRank] {
      def compare(that: WordRank) = rank - that.rank
    }

    val wordRanks = new Array[WordRank](wordCount.size)
    var i = 0
    for ((k, v) <- wordCount) {
      wordRanks(i) = new WordRank(k, v)
      i += 1
    }
    //TODO: quickSort not work, StackOverFlowError, why?
    //scala.util.Sorting.quickSort(wordRanks)
    scala.util.Sorting.stableSort(wordRanks)

    // 最后返回的结果，单词-IDF
    val fileCount = filesInfo.size
    val vocabulary = scala.collection.mutable.Map.empty[String, WordGlobalInfo]
    i = 0
    val minCount = (PERCENT * fileCount).toInt
    val maxCount = (PERCENT_MAX * fileCount).toInt
    for (word <- wordRanks) {
      if ((word.rank > minCount) && (word.rank < maxCount)) {
        vocabulary.put(word.word, new WordGlobalInfo(i,
          Math.log(fileCount / word.rank)))
        i += 1
      }

    }
    LOGGER.info(wordRanks.length + " words totally, selected "
      + vocabulary.size + " words as feature.\n")
    vocabulary.toMap
  }

  /**
   * 将文本文件转化为文本向量.
   * @param filesInfo 所有文本文件的信息
   * @param vocabulary 单词表，由{@link extractShrinkedVocabulary} 得到
   * @param sparse 是否用稀疏表示
   * @return 文本向量
   */
  private def convertToVectors(filesInfo: Map[String, FileInfo],
    vocabulary: Map[String, WordGlobalInfo],
    sparse: Boolean): Array[TextVector] = {
    LOGGER.info("building " + filesInfo.size + " files total.\n");
    val textVectors = new Array[TextVector](filesInfo.size)

    var i = 0
    for ((key, value) <- filesInfo) {
      val v = convertToVector(key, value, vocabulary, sparse)
      if (v.isDefined) {
        textVectors(i) = new TextVector(key, getCatagoryOfFile(key), v.get)
        i += 1
      }

    }
    // 去掉末尾的null
    val result = new Array[TextVector](i)
    for (j <- 0 until i) {
      result(j) = textVectors(j)
    }
    LOGGER.warning("discarded " + (filesInfo.size - i) + " files.\n");
    result
  }

  /**
   * 将一个文本文件转化为向量.
   * @param filePath 文件的绝对路径
   * @param fileInfo 文件的信息
   * @param vocabulary 全局的单词表
   * @param sparse 是否采用稀疏表示
   * @return 文本向量
   */
  private def convertToVector(filePath: String, fileInfo: FileInfo,
    vocabulary: Map[String, WordGlobalInfo],
    sparse: Boolean): Option[Vector] = {
    val vocabularySize = vocabulary.size
    val builder: Vector.Builder = if (sparse) {
      new SparseVector.Builder(vocabularySize)
    } else {
      new DenseVector.Builder(vocabularySize)
    }

    for ((k, v) <- fileInfo.wordCount) {
      val globalInfo = vocabulary.get(k)

      if (globalInfo.isDefined) {
        val temp = globalInfo.get
        builder.set(temp.index, v.toDouble / fileInfo.allWordsCount * temp.idf)
      }
    }
    val vector = builder.build()

    if (vector.magnitude > 0) {
      Some(vector)
    } else {
      None
    }
  }

  /**
   * 获取文件的类别
   * @param filePath 文件的绝对路径
   * @return 文件的类别，即文件的上一级目录名
   */
  private def getCatagoryOfFile(filePath: String): String = {
    val end = filePath.lastIndexOf(File.separatorChar)
    val start = filePath.substring(0, end).lastIndexOf(File.separatorChar);

    filePath.substring(start + 1, end)
  }

  /**
   * 计算每个类下的文件个数.
   * @param filesInfo 所有文件的与处理信息.
   * @return 每个类下的文件个数
   */
  private def calcClassFileCount(filesInfo: Map[String, FileInfo]): Map[String, Int] = {
    val result = scala.collection.mutable.Map.empty[String, Int]
    for ((k, v) <- filesInfo) {
      val catagory = getCatagoryOfFile(k)
      val count = result.get(catagory)
      count match {
        case Some(x) => {
          result.put(catagory, x + 1)
        }
        case None => {
          result.put(catagory, 1)
        }
      }
    }
    result.toMap
  }

  /**
   * 返回给定路径的文本文件内容.
   *
   * @param filePath
   *            给定的文本文件路径
   * @param encoding
   *            文本文件的编码，例如 "utf-8"
   * @return 文本内容，发生异常则返回空串
   */
  private def readTextFile(filePath: String, encoding: String): String = {
    val source = scala.io.Source.fromFile(filePath, encoding)
    val lines = source.mkString
    source.close()
    lines
  }

  /**
   * 写入文本文件.
   *
   * @param filePath
   *            给定的文本文件路径
   * @param encoding
   *            文本文件的编码，例如 "utf-8"
   * @return 成功返回true，失败返回false
   */
  private def writeTextFile(filePath: String,
    text: String, encoding: String): Boolean = {
    val output: Output = Resource.fromFile(filePath)
    output.write(text)(Codec.UTF8)
    true
  }
}
