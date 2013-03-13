/**
 * 分词.
 *
 * @author soulmachine@gmail.com
 * @date 2013-2-25
 * @version 0.1
 * @since Scala 2.10
 */
package com.yanjiuyanjiu.ml.kmeans

import scala.collection.mutable.ArrayBuffer

/**
 * 分词.
 *
 * @author soulmachine@gmail.com
 * @date 2013-2-25
 * @version
 * @since Scala 2.10
 */
object TextSegmentation {
  /**
   * 将英文文本分词.
   *
   * @param text
   *            文本文件
   * @return 分割好的单词数组
   */
  def tokenize(text: String): List[String] = {
    val lowerCaseText = text.toLowerCase()
    val tokens = new ArrayBuffer[String]

    val sb = new StringBuilder
    for (i <- 0 until lowerCaseText.length()) {
      val character = lowerCaseText.charAt(i)
      if (character.isLetter) {
        sb.append(character)
      } else {
        if (sb.length > 0) {
          tokens.append(sb.toString())
          sb.delete(0, sb.length())
        }
      }
    }
    if (sb.length > 0) {
      tokens.append(sb.toString())
    }

    ripStopWords(tokens)
  }
  /**
   * 去掉停止词.
   *
   * @param oldText
   *            分词后的单词数组
   * @return 去掉停止词的新数组
   */
  private def ripStopWords(tokens: ArrayBuffer[String]): List[String] = {
    val filtered = tokens.filter(!StopWordHandler.isStopWord(_))
    filtered.toList
  }
}