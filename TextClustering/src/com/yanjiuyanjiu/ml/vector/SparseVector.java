package com.yanjiuyanjiu.ml.vector;

import org.apache.mahout.math.list.IntArrayList;
import org.apache.mahout.math.map.OpenIntDoubleHashMap;

/**
 * 向量的稀疏表示, immutable.
 *
 * @author soulmachine@gmail.com
 *
 */
public class SparseVector extends AbstractVector {
	/** key为在向量中的位置, value为该位置的值. */
	private final OpenIntDoubleHashMap vector;

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
		/** key为在向量中的位置, value为该位置的值. */
		final OpenIntDoubleHashMap vector;
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
			vector = new OpenIntDoubleHashMap();
		}

		@Override
		public double get(final int index) {
			return vector.get(index);
		}

		@Override
		public void set(final int index, final double value) {
			vector.put(index, value);
		}

		@Override
		public Vector build() {
			magnitude = Math.sqrt(length2());
			return new SparseVector(this);
		}

		/**
		 * 计算向量的欧拉长度的平方.
		 *
		 * @return 向量的长度的平方
		 */
		private double length2() {
			double sum = 0;
			final IntArrayList keys = vector.keys();
			for (int i = 0; i < keys.size(); i++) {
				final double e = vector.get(keys.get(i));
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
	protected SparseVector(final Builder builder) {
		super(builder.dimension);
		vector = builder.vector;
		magnitude = builder.magnitude;
	}


	@Override
	public double get(final int key) {
		final Double temp = vector.get(key);
		if (temp == null) {
			return 0.0;
		}

		return temp.doubleValue();
	}

	@Override
	public double dotProduct(final Vector that) {
		final SparseVector other = (SparseVector) that;
		double sum = 0;
		final IntArrayList keys = vector.keys();
		for (int i = 0; i < keys.size(); i++) {
			final int key = keys.get(i);
			final double value = vector.get(key);

			final double value2 = other.vector.get(key);

			if (value != 0 && value2 != 0) {
				sum += value * value2;
			}
		}
		return sum;
	}

	@Override
	public SparseVector.Builder newBuilder() {
		return new Builder(dimension);
	}
}
