package com.yanjiuyanjiu.ml.kmeans;

import com.yanjiuyanjiu.ml.vector.Vector;

/**
 * 文本向量.
 *
 * @author soulmachine@gmail.com
 */
public class TextVector {
	/** 文本文件的绝对路径. */
	protected final String file;
	/** 所属类别. */
	private final String catagory;
	/** 该文本转化成的向量. */
	protected Vector vector;

	/**
	 * 构造函数.
	 *
	 * @param file
	 *            文件路径
	 * @param catagory
	 *            本文件所属的类别
	 * @param vector
	 *            文本向量
	 */
	public TextVector(final String file, final String catagory,
			final Vector vector) {
		this.file = file;
		this.catagory = catagory;
		this.vector = vector;
	}

	public String getFile() {
		return file;
	}

	public String getCatagory() {
		return catagory;
	}

	public Vector getVector() {
		return vector;
	}
}
