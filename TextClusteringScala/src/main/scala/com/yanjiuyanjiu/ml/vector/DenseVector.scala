package com.yanjiuyanjiu.ml.vector

/**
 * 向量，非稀疏时适合用这个表示, immutable.
 *
 * @author soulmachine@gmail.com
 *
 */
class DenseVector(builder: DenseVector.Builder) extends Vector(builder.dimension) {
  /** 向量. */
  private val vector = builder.vector
  val magnitude = builder.magnitude

  @Override
  def get(index: Int) = vector(index)

  @Override
  def dotProduct(that: Vector): Double = {
    val other = that.asInstanceOf[DenseVector]
    assert(this.dimension == other.dimension)
    var sum = 0.0d

    for (i <- 0 until dimension) {
      sum += vector(i) * other.vector(i)
    }
    sum
  }

  @Override
  def newBuilder(): DenseVector.Builder = {
    new DenseVector.Builder(dimension)
  }
}

/**
 * companion object.
 */
object DenseVector {
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
    val vector: Array[Double] = new Array[Double](dimension)
    /** 长度. */
    var magnitude: Double = _

    @Override
    def get(index: Int): Double = vector(index)

    @Override
    def set(index: Int, value: Double): Unit = {
      vector(index) = value
    }

    @Override
    def build(): DenseVector = {
      magnitude = Math.sqrt(length2())
      new DenseVector(this)
    }

    /**
     * 计算向量的欧拉长度的平方.
     *
     * @return 向量的长度的平方
     */
    private def length2() = (0.0d /: vector)(_ + _)
  }
}
