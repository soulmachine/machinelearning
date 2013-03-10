package com.yanjiuyanjiu.ml.kmeans;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.mahout.math.list.IntArrayList;
import org.apache.mahout.math.set.OpenIntHashSet;

import com.yanjiuyanjiu.ml.vector.Vector;

/**
 * k-means++算法. <br/>
 * K-Means++算法步骤大概如下：<br/>
 * 1. 在已有的所有点中随机选取一个点，将其加入初始点。 2. 对于所有的点，计算出他们的D值，D值就是每个点到距离他们最近的初始点的距离的平方。 3.
 * 对所有点选取下一个点加入初始点的集合，每个点被选取的概率正比于他们的D值。 4. 如果初始点集合数目没有达到预定的数目，回到2，否则到5。 5.
 * 执行K-Means算法
 *
 * @author soulmachine@gmail.com
 *
 */
public class KMeans {
	/** 日志. */
	private static final Logger LOGGER = Logger.getLogger(KMeans.class
			.getName());
	static {
		try {
			final FileHandler fileHandler = new FileHandler(LOGGER.getName());
			fileHandler.setLevel(Level.INFO);
			fileHandler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(fileHandler);
		} catch (final SecurityException e) {
			LOGGER.warning(e.getMessage());
		} catch (final IOException e) {
			LOGGER.warning(e.getMessage());
		}
	}
	/** 随机数发生器. */
	private static final Random RANDOM = new Random();

	/** 本数据集有20个类，故K定位20. */
	public static final int K = 20;
	/** 终止迭代的阀值，当类别发生变化的点的数目小于0.05*总数时，终止迭代. */
	private final int threshold;
	/** 文本向量. */
	private final Vector[] vectors;
	/** 每个文本向量所属于的组，与vectors一一对应，联系 vectors和groups的桥梁. */
	private final int[] belongedGroup;
	// /** 每个点到最近中心点的距离. */
	// private final double[] distanceToCenter; //必须记录所有的（即用二维数组），这种是错误的
	// /** 最近的中心点. */
	// private final int[] closestCenter;
	/** 每个点到每个簇的中心点的距离. */
	private final double[][] distanceToCenter;

	/** 存放聚类的结果. */
	private final Group[] groups;

	/** 上一轮迭代完成后，每个组是否有变化，即有成员加入或离开. */

	/**
	 * 表示一个类别的组.
	 *
	 * @author 方勤
	 *
	 */
	private static final class Group {
		// 所在的数组的下标，就是该组的编号
		/** 该组的中心. */
		Vector center;
		/** 该组的成员在 vectors中的下标. */
		final OpenIntHashSet members;

		/**
		 * 构造函数.
		 *
		 * @param center
		 *            中心点
		 */
		public Group(final Vector center) {
			this.center = center;
			this.members = new OpenIntHashSet();
		}

		public Vector getCenter() {
			return center;
		}

		public OpenIntHashSet getMembers() {
			return members;
		}

		/**
		 * 添加成员.
		 *
		 * @param m
		 *            成员
		 * @return 操作是否成功
		 */
		public boolean addMember(final int m) {
			return members.add(m);
		}

		/**
		 * 添加成员.
		 *
		 * @param m
		 *            成员
		 * @return 操作是否成功
		 */
		public boolean removeMember(final int m) {
			return members.remove(m);
		}
	}

	/**
	 * 更新本组的中心点.
	 *
	 * @param g
	 *            一个组
	 */
	private void updateCenter(final Group g) {
		final int size = g.members.size();
		final int n = g.center.dimension();

		if (size == 0) {
			return; // 如果成员为空，就不更新
		}

		final Vector.Builder builder = g.center.newBuilder();
		final IntArrayList keys = g.members.keys();
		for (int i = 0; i < keys.size(); i++) {
			final int m = keys.get(i);
			for (int j = 0; j < n; j++) {
				builder.set(j, builder.get(j) + vectors[m].get(j));
			}
		}

		for (int i = 0; i < n; i++) {
			builder.set(i, builder.get(i) / size);
		}
		g.center = builder.build();
	}

	/**
	 * 构造函数.
	 *
	 * @param threshold
	 *            停止迭代的阀值
	 * @param vectors
	 *            所有的文本向量
	 */
	public KMeans(final double threshold, final Vector[] vectors) {
		this.threshold = (int) (threshold * vectors.length);
		this.vectors = vectors;
		this.belongedGroup = new int[vectors.length];
		java.util.Arrays.fill(belongedGroup, -1);

		this.distanceToCenter = new double[vectors.length][];
		for (int i = 0; i < vectors.length; i++) {
			distanceToCenter[i] = new double[K];
			java.util.Arrays.fill(distanceToCenter[i], -1);
		}
		groups = new Group[K];

		// ///////////initialize //////////////////
		// Set<Integer> seeds = getInitialSeeds1(vectors.length);
		final Set<Integer> seeds = getInitialSeedsKmeansPlusPlus(vectors, K);

		int i = 0;
		final Iterator<Integer> iter = seeds.iterator();
		while (iter.hasNext()) {
			groups[i++] = new Group(vectors[iter.next()]);
		}
		assert (i == groups.length);

		// 计算点到中心的距离
		for (i = 0; i < vectors.length; i++) {
			for (int j = 0; j < groups.length; j++) {
				distanceToCenter[i][j] = vectors[i].distance(groups[j]
						.getCenter());
			}
		}

		// 将样本随机分配到每个群
		final int[] indexes = new int[vectors.length];
		for (i = 0; i < indexes.length; i++) {
			indexes[i] = i;
		}
		for (i = 0; i < indexes.length; i++) {
			// i和j的内容交换
			final int j = RANDOM.nextInt(indexes.length);
			final int temp = indexes[i];
			indexes[i] = indexes[j];
			indexes[j] = temp;
		}
		// 每个组分到的数目
		final int eachGroupCount = vectors.length / groups.length;
		for (i = 0; i < groups.length; i++) {
			for (int j = 0; j < eachGroupCount; j++) {
				final int index = indexes[i * eachGroupCount + j];
				groups[i].addMember(index);
				belongedGroup[index] = i;
			}
		}
		for (i = eachGroupCount * groups.length; i < vectors.length; i++) {
			final int index = indexes[i];
			groups[groups.length - 1].addMember(index);
			belongedGroup[index] = groups.length - 1;
		}
		// ///////////initialize //////////////////
	}

	/**
	 * 开始聚类.
	 */
	public void cluster() {
		int changes = 0;
		int j = 1;
		do {
			final long startTime = System.currentTimeMillis();
			changes = clusterOnce();
			final long endTime = System.currentTimeMillis();
			LOGGER.info("iteration " + j + " consumed " + (endTime - startTime)
					+ "ms, " + changes + " points changed.\n");
			j++;
			// 当类别还在发生变化的点的比率小于阀值（甚至不再变化时），终止迭代
		} while (changes > threshold);
	}

	/**
	 * 一次聚类的过程.
	 *
	 * @return 本次迭代过程中，样本点的类别变动的个数
	 */
	private int clusterOnce() {
		LOGGER.fine("enter...");
		int changes = 0; // 类别发生了变动的点的个数
		// 本轮中，各组是否发生了变化
		final boolean[] currentChangedGroup = new boolean[K];
		java.util.Arrays.fill(currentChangedGroup, false);

		// 划分，把每个点划分到离它最近的中心点
		for (int i = 0; i < vectors.length; i++) {
			LOGGER.fine("vector " + i + ".\n");
			final double[] distance = distanceToCenter[i];
			// 寻找最近的中心点
			int newGroup = -1;
			double minDistance = Double.MAX_VALUE;
			for (int j = 0; j < groups.length; j++) {
				if (distance[j] < minDistance) {
					newGroup = j; // 样本i跑动到第j簇
					minDistance = distance[j];
				}
			}
			assert (newGroup != -1);
			if (newGroup != belongedGroup[i]) {
				changes++;
				currentChangedGroup[newGroup] = true;
				currentChangedGroup[belongedGroup[i]] = true;
				groups[belongedGroup[i]].removeMember(i);
				groups[newGroup].addMember(i);
				belongedGroup[i] = newGroup;
			}
		}
		// 更新，每个组的中心点
		for (int i = 0; i < groups.length; i++) {
			if (currentChangedGroup[i]) {
				updateCenter(groups[i]);
			}
		}
		// 更新所有样本点到新中心点的距离
		for (int i = 0; i < vectors.length; i++) {
			for (int j = 0; j < K; j++) {
				if (currentChangedGroup[j]) {
					distanceToCenter[i][j] = vectors[i].distance(groups[j]
							.getCenter());
				}
			}
		}
		return changes;
	}

	/**
	 * 获取初始点，k-means++算法.
	 *
	 * @param vectors
	 *            一组样本数据，每个样本由一个向量表示.
	 * @param k
	 *            要选取k个初始点
	 * @return 初始点的下标
	 */
	private static Set<Integer> getInitialSeedsKmeansPlusPlus(
			final Vector[] vectors, final int k) {
		LOGGER.fine("enter getInitialSeedsKmeansPlusPlus().\n");
		final long startTime = System.currentTimeMillis();
		final Set<Integer> seeds = new HashSet<Integer>(k);

		// 记录每个样本的D值，种子点自己则不用计算，为0
		final double[] d = new double[vectors.length];
		java.util.Arrays.fill(d, Double.MAX_VALUE);
		// 随即选取第一个点
		final int firstSeed = RANDOM.nextInt(vectors.length);
		seeds.add(firstSeed);
		d[firstSeed] = 0;

		for (int i = 1, currentSeed = firstSeed; i < k; i++) {
			// 计算每个点到中心的距离的平方，选择最小的作为D值
			for (int j = 0; j < d.length; j++) {
				// 已经在种子集合中就排除掉
				if (seeds.contains(j)) {
					continue;
				}
				final double distance = vectors[j]
						.distance(vectors[currentSeed]);
				if (d[j] > distance) {
					d[j] = distance;
				}
			}

			// 按概率，选择一个点，加入种子点集合
			currentSeed = selectSeed(d);
			d[currentSeed] = 0;
			seeds.add(currentSeed);
		}
		final long endTime = System.currentTimeMillis();
		LOGGER.fine("consummed " + (endTime - startTime) / 1000 + " s.\n");
		return seeds;
	}

	/**
	 * 随机选择 K个点，原始的kmeans算法.
	 *
	 * @param count
	 *            样本总数
	 * @return 初始的中心点
	 */
	private static Set<Integer> getInitialSeedsRandom(final int count) {
		LOGGER.fine("enter getInitialSeedsRandom().\n");
		final long startTime = System.currentTimeMillis();
		final int[] indexes = new int[count];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = i;
		}

		for (int i = 0; i < K; i++) {
			final int j = RANDOM.nextInt(count);
			final int temp = indexes[i];
			indexes[i] = indexes[j]; // 交换
			indexes[j] = temp;
		}
		final HashSet<Integer> result = new HashSet<Integer>(K);
		for (int i = 0; i < K; i++) {
			result.add(indexes[i]);
		}
		final long endTime = System.currentTimeMillis();
		LOGGER.fine("consummed " + (endTime - startTime) / 1000 + " s.\n");
		return result;
	}

	/**
	 * 选择一个点加入种子点集合.
	 *
	 * @param ds
	 *            所有点的D值，0表示已经在种子点集合中
	 * @return 一个点的下标
	 */
	private static int selectSeed(final double[] ds) {
		double min = Double.MAX_VALUE; // 最小值
		double sum = 0; // 和
		for (int i = 0; i < ds.length; i++) {
			if (ds[i] <= 0) {
				continue;
			}
			sum += ds[i];
			if (ds[i] < min) {
				min = ds[i];
			}
		}

		double r = min + (sum - min) * RANDOM.nextDouble();

		for (int i = 0; i < ds.length; i++) {
			if (ds[i] > 0) {
				r -= ds[i];
				if (r <= 0) {
					return i;
				}
			}
		}
		LOGGER.severe("wrong seed, should'nt run to here !\n");
		return -1;
	}

	/**
	 * 计算NMI，评估聚类质量.
	 *
	 * @param catagories
	 *            样本原始属于的类
	 * @param classFileCount
	 *            每个类别下的文件数目
	 * @return NMI值
	 */
	public double getNMI(final String[] catagories,
			final Map<String, Integer> classFileCount) {
		return getNMI(groups, catagories, classFileCount);
	}

	/**
	 * 计算NMI，评估聚类质量.
	 *
	 * @param groups
	 *            已经聚类好的组
	 * @param catagories
	 *            样本原始属于的类
	 * @param classFileCount
	 *            每个类别下的文件数目
	 * @return NMI值
	 */
	private static double getNMI(final Group[] groups,
			final String[] catagories,
			final Map<String, Integer> classFileCount) {
		double ikc = 0.0; // 聚类k与类别c之间的互信息
		double hk = 0.0; // 聚类k 的熵

		for (final Group g : groups) {
			final int k = g.getMembers().size(); // cluster k的文件数目
			if (k == 0) {
				continue;
			}
			// cluster k下类别c的文件总数
			final Map<String, Integer> kc = new HashMap<String, Integer>();
			final IntArrayList keys = g.getMembers().keys();
			for (int i = 0; i < keys.size(); i++) {
				final String c = catagories[keys.get(i)];

				final Integer count = kc.get(c);
				if (count == null) {
					kc.put(c, Integer.valueOf(1));
				} else {
					kc.put(c, count + 1);
				}
			}

			for (final Map.Entry<String, Integer> entry : kc.entrySet()) {
				final String c = entry.getKey();
				final Integer value = entry.getValue();

				ikc += ((double) value)
						/ catagories.length
						* Math.log(((double) catagories.length * value)
								/ (k * classFileCount.get(c)));
			}

			hk += ((double) k) / catagories.length
					* Math.log(catagories.length / ((double) k));
		}

		double hc = 0.0; // 类别c的熵
		for (final Map.Entry<String, Integer> entry
				: classFileCount.entrySet()) {
			final int c = entry.getValue();
			if (c == 0) {
				continue;
			}
			hc += ((double) c) / catagories.length
					* Math.log(catagories.length / ((double) c));
		}

		return 2 * ikc / (hc + hk);
	}
}
