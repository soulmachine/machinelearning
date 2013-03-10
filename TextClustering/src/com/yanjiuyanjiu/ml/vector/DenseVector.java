package com.yanjiuyanjiu.ml.vector;

/**
 * 向量，非稀疏时适合用这个表示, immutable.
 *
 * @author soulmachine@gmail.com
 *
 */
public class DenseVector extends AbstractVector {
	/** 向量. */
	private final double[] vector;

	/**
	 * Builder Pattern.
	 *
	 * @author soulmachine@gmail.com
	 * @date 2013-3-8
	 * @version 0.1
	 * @since JDK 1.6
	 */
	public static class Builder implements Vector.Builder {
		/** 维度. */
		final int dimension;
		/** 向量. */
		final double[] vector;
		/** 长度. */
		protected double magnitude;

		/**
		 * 构造函数.
		 *
		 * @param dimension
		 *            向量维度，固定
		 */
		public Builder(final int dimension) {
			this.dimension = dimension;
			vector = new double[dimension];
		}

		@Override
		public double get(final int index) {
			return vector[index];
		}

		@Override
		public void set(final int index, final double value) {
			vector[index] = value;
		}

		@Override
		public Vector build() {
			magnitude = Math.sqrt(length2());
			return new DenseVector(this);
		}

		/**
		 * 计算向量的欧拉长度的平方.
		 *
		 * @return 向量的长度的平方
		 */
		private double length2() {
			double sum = 0;
			for (final double e : vector) {
				if (e != 0) {
					sum += e * e;
				}
			}
			return sum;
		}
	}

	/**
	 * 构造函数.
	 *
	 * @param builder
	 *            向量对应的Builder
	 */
	protected DenseVector(final Builder builder) {
		super(builder.dimension);
		vector = builder.vector;
		magnitude = builder.magnitude;
	}

	@Override
	public double get(final int index) {
		return vector[index];
	}

	@Override
	public double dotProduct(final Vector that) {
		final DenseVector other = (DenseVector) that;
		assert (this.dimension == other.dimension);
		double sum = 0;

		for (int i = 0; i < dimension; i++) {
			sum += vector[i] * other.vector[i];
		}
		return sum;
	}

	@Override
	public DenseVector.Builder newBuilder() {
		return new Builder(dimension);
	}
}
