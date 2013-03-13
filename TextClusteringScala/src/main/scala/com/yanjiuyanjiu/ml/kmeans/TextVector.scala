/**
 * 文本向量.
 *
 * @author soulmachine@gmail.com
 * @date 2013-2-28
 * @version 0.1
 * @since scala 2.10
 */
package com.yanjiuyanjiu.ml.kmeans

import com.yanjiuyanjiu.ml.vector.Vector

/**
 * 文本向量.
 * @param file 文本文件的绝对路径
 * @param catagory 所属类别
 * @param vector 该文本转化成的向量
 * @author soulmachine@gmail.com
 */
class TextVector(val file: String, val catagory: String,
  val vector: Vector) {
}