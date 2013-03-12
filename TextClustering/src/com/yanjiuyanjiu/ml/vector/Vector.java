/**
 *
 */
package com.yanjiuyanjiu.ml.vector;

/**
 * 向量的接口.
 *
 * @author soulmachine@gmail.com
 *
 */
public interface Vector {
	/**
	 * 向量的Builder接口.
	 *
	 * @author soulmachine@gmail.com
	 * @date 2013-3-10
	 * @version 0.1
	 * @since JDK 1.6
	 */
	interface Builder {
		/**
		 * 获取.
		 *
		 * @param index
		 *            索引
		 * @return 该位置的值
		 */
		double get(final int index);

		/**
		 * 设置.
		 *
		 * @param index
		 *            索引
		 * @param value
		 *            该位置的值
		 */
		void set(final int index, final double value);

		/**
		 * 获取对应的Builder，便于构造新向量.
		 *
		 * @return 新的响亮
		 */
		Vector build();
	}

	/**
	 * 获取向量的维度.
	 *
	 * @return 向量的维度
	 */
	int dimension();

	/**
	 * 计算向量的长度.
	 *
	 * @return 向量的长度
	 */
	double magnitude();

	/**
	 * 计算两个向量之间的距离.
	 *
	 * @param v
	 *            另一个向量
	 * @return 两个向量之间的距离
	 */
	double distance(final Vector v);

	/**
	 * 获取向量某个位置的值.
	 *
	 * @param index
	 *            位置
	 * @return 该位置的值
	 */
	double get(int index);

	/**
	 * 正规化，使得欧式长度为1.
	 *
	 * @return 是否成功。当向量为全0时，无法正规化。
	 */
	// boolean normalize();
	/**
	 * 内积.
	 *
	 * @param that
	 *            另一个向量
	 * @return 内积
	 */
	double dotProduct(final Vector that);

	/**
	 * 获取对应的Builder.
	 *
	 * @return 该类对应的Builder
	 */
	Builder newBuilder();
}
