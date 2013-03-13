/**
 * 向量的接口.
 *
 * @author soulmachine@gmail.com
 * @date 2013-2-27
 * @version 0.1
 * @since scala 2.10
 */
package com.yanjiuyanjiu.ml.vector

/**
 * 向量的接口.
 *
 * @author soulmachine@gmail.com
 * @date 2013-2-27
 * @version
 * @since scala 2.10
 */
abstract class Vector(val dimension: Int) {
  /**
   * 计算向量的长度.
   *
   * @return 向量的长度
   */
  val magnitude: Double

  /**
   * 内积.
   *
   * <br/>
   * 这里用cosine distance，即距离=2(1-cosA), 对于系数向量，用cosine distance比较好
   * 其实，等价于将X,Y归一化后的欧氏距离的平方 ，证明： (X-Y)^2 = X^2+Y^2-2*X*Y = 2-2*X*Y =
   * 2-2|X|*|Y|*cosA = 2-2*cosA
   *
   * @param v
   *            另一个向量
   * @return 内积
   */
  final def distance(v: Vector): Double = {
    val cosine = this.dotProduct(v) / (this.magnitude * v.magnitude)
    2 * (1 - cosine)
  }

  /**
   * 获取向量某个位置的值.
   *
   * @param index
   *            位置
   * @return 该位置的值
   */
  def get(index: Int): Double

  /**
   * 内积.
   *
   * @param that
   *            另一个向量
   * @return 内积
   */
  def dotProduct(that: Vector): Double

  /**
   * 获取对应的Builder.
   *
   * @return 该类对应的Builder
   */
  def newBuilder(): Vector.Builder
}

object Vector {
  /**
   * 向量的Builder接口.
   *
   * @author soulmachine@gmail.com
   * @date 2013-3-10
   * @version 0.1
   * @since scala 2.10
   */
  abstract class Builder {
    /**
     * 获取.
     *
     * @param index
     *            索引
     * @return 该位置的值
     */
    def get(index: Int): Double

    /**
     * 设置.
     *
     * @param index
     *            索引
     * @param value
     *            该位置的值
     */
    def set(index: Int, value: Double): Unit

    /**
     * 获取对应的Builder，便于构造新向量.
     *
     * @return 新的响亮
     */
    def build(): Vector
  }
}