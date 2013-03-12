package com.yanjiuyanjiu.ml.vector;

/**
 * 向量的抽像父类.
 *
 * @author soulmachine@gmail.com
 * @date 2013-3-10
 * @version 0.1
 * @since JDK 1.6
 */
public abstract class AbstractVector implements Vector {
	/** 维度. */
	protected final int dimension;
	/** 长度，用欧式长度表示. */
	protected double magnitude;

	/**
	 * constructor.
	 *
	 * @param dimension
	 *            维度
	 */
	public AbstractVector(final int dimension) {
		this.dimension = dimension;
	}

	@Override
	public final int dimension() {
		return dimension;
	}

	@Override
	public final double magnitude() {
		return magnitude;
	}

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
	@Override
	public final double distance(final Vector vector) {
		final double cosine = this.dotProduct(vector)
				/ (this.magnitude * vector.magnitude());
		return 2 * (1 - cosine);
	}
}
