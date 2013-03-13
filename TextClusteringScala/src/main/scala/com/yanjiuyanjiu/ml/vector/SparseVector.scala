/**
 * 向量的稀疏表示.
 *
 * @author soulmachine@gmail.com
 * @date 2013-2-28
 * @version 0.1
 * @since scala 2.10
 */
package com.yanjiuyanjiu.ml.vector
import scala.collection.mutable.Map
/**
 * 向量的稀疏表示.
 *
 * @author soulmachine@gmail.com
 * @date 2013-2-28
 * @version
 * @since scala 2.10
 */
class SparseVector(builder: SparseVector.Builder) extends Vector(builder.dimension) {
  /** 向量. */
  private val vector = builder.vector
  val magnitude = builder.magnitude

  @Override
  def get(index: Int) = vector(index)

  @Override
  def dotProduct(that: Vector): Double = {
    val other = that.asInstanceOf[SparseVector]
    assert(this.dimension == other.dimension)
    var sum = 0.0d

    for (i <- 0 until dimension) {
      sum += vector(i) * other.vector(i)
    }
    sum
  }

  @Override
  def newBuilder(): SparseVector.Builder = {
    new SparseVector.Builder(dimension)
  }
}

/**
 * companion object.
 */
object SparseVector {
  /**
   * Builder Pattern.
   *
   * @param dimension 维度
   * @author soulmachine@gmail.com
   * @date 2013-3-8
   * @version 0.1
   * @since JDK 1.6
   */
  class Builder(val dimension: Int) extends Vector.Builder {
    /** 向量. */
    val vector = collection.mutable.Map.empty[Int, Double]
    /** 长度. */
    var magnitude: Double = _

    @Override
    def get(index: Int): Double = vector(index)

    @Override
    def set(index: Int, value: Double): Unit = {
      vector(index) = value;
    }

    @Override
    def build(): SparseVector = {
      magnitude = Math.sqrt(length2())
      new SparseVector(this)
    }

    /**
     * 计算向量的欧拉长度的平方.
     *
     * @return 向量的长度的平方
     */
    def length2() = vector.foldLeft(0.0d) { case (a, (k, v)) => a + v }
  }
}