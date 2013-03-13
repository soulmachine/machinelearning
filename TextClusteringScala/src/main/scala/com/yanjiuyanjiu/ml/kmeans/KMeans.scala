/**
 *
 */
package com.yanjiuyanjiu.ml.kmeans

import java.util.logging._
import java.io.IOException
import java.util.Random

import com.yanjiuyanjiu.ml.vector._

/**
 * k-means++算法. <br/>
 * K-Means++算法步骤大概如下：<br/>
 * 1. 在已有的所有点中随机选取一个点，将其加入初始点。 2. 对于所有的点，计算出他们的D值，D值就是每个点到距离他们最近的初始点的距离的平方。 3.
 * 对所有点选取下一个点加入初始点的集合，每个点被选取的概率正比于他们的D值。 4. 如果初始点集合数目没有达到预定的数目，回到2，否则到5。 5.
 * 执行K-Means算法
 * @param th 终止迭代的阀值
 * @param vectors 被聚类的文本向量
 * @author soulmachine@gmail.com
 *
 */
class KMeans(th: Double, private val vectors: Array[Vector]) {
  /** 终止迭代的阀值，当类别发生变化的点的数目小于0.05*总数时，终止迭代. */
  private val threshold = (th * vectors.length).toInt
  /** 每个文本向量所属于的组，与vectors一一对应，联系 vectors和groups的桥梁. */
  private val belongedGroup = Array.fill(vectors.length)(-1)
  /** 每个点到每个簇的中心点的距离. */
  private val distanceToCenter = Array.fill(vectors.length, KMeans.K)(-1.0d)

  /** 存放聚类的结果. */
  private val groups = new Array[KMeans.Group](KMeans.K)

  // 在 primary constructor里调用initialize()
  initialize()
  private def initialize(): Boolean = {
    //val seeds = KMeans.getInitialSeedsRandom(vectors.length)
    val seeds = KMeans.getInitialSeedsKmeansPlusPlus(vectors, KMeans.K)
    var i = 0
    for (seed <- seeds) {
      groups(i) = new KMeans.Group(vectors(seed))
      i += 1
    }
    assert(i == groups.length)

    // 计算点到中心的距离
    for (i <- 0 until vectors.length) {
      for (j <- 0 until groups.length) {
        distanceToCenter(i)(j) = vectors(i).distance(groups(j).center)
      }
    }

    // 将样本随机分配到每个群
    val indexes = new Array[Int](vectors.length)
    for (i <- 0 until indexes.length) {
      indexes(i) = i
    }

    for (i <- 0 until indexes.length) {
      // i和j的内容交换
      val j = KMeans.RANDOM.nextInt(indexes.length)
      val temp = indexes(i)
      indexes(i) = indexes(j)
      indexes(j) = temp
    }
    // 每个组分到的数目
    val eachGroupCount = vectors.length / groups.length;
    for (i <- 0 until groups.length) {
      for (j <- 0 until eachGroupCount) {
        val index = indexes(i * eachGroupCount + j)
        groups(i).addMember(index)
        belongedGroup(index) = i
      }
    }
    // 最后不够一组，残余的
    for (i <- eachGroupCount * groups.length until vectors.length) {
      val index = indexes(i)
      groups(groups.length - 1).addMember(index)
      belongedGroup(index) = groups.length - 1
    }
    for (i <- 0 until vectors.length) {
      if (belongedGroup(i) == -1) {
        KMeans.LOGGER.severe("first distance wrong!\n");
        return false
      }
    }
    true
  }

  /**
   * 开始聚类.
   */
  def cluster(): Unit = {
    var changes = 0
    var j = 1
    do {
      val startTime = System.currentTimeMillis()
      changes = clusterOnce()
      val endTime = System.currentTimeMillis()
      KMeans.LOGGER.info("iteration " + j + " consumed "
        + (endTime - startTime) / 1000 + "s, " + changes
        + " points changed.\n");
      j += 1
      // 当类别还在发生变化的点的比率小于阀值（甚至不再变化时），终止迭代
    } while (changes > threshold)
  }

  /**
   * 计算NMI，评估聚类质量.
   * @param classFileCount 每个类别下的文件数目
   * @return NMI值
   */
  def getNMI(catagories: Array[String], classFileCount: Map[String, Int]): Double = {
    KMeans.getNMI(groups, catagories, classFileCount)
  }

  /**
   * 一次聚类的过程.
   *
   * @return 本次迭代过程中，样本点的类别变动的个数
   */
  private def clusterOnce(): Int = {
    var i = 0
    var j = 0
    KMeans.LOGGER.fine("enter...")
    var changes = 0 // 类别发生了变动的点的个数
    // 本轮中，各组是否发生了变化
    val currentChangedGroup = Array.fill(KMeans.K)(false)
    // 划分，把每个点划分到离它最近的中心点
    i = 0
    while(i < vectors.length) {
    //for (i <- 0 until vectors.length) {
      KMeans.LOGGER.fine("vector " + i + ".\n");
      val distance = distanceToCenter(i)
      // 寻找最近的中心点
      var newGroup = -1
      var minDistance = Double.MaxValue
      j = 0
      while(j < groups.length) {
      //for (j <- 0 until groups.length) {
        if (distance(j) < minDistance) {
          newGroup = j; // 样本i跑动到第j簇
          minDistance = distance(j)
        }
        j += 1
      }
      assert(newGroup != -1)
      if (newGroup != belongedGroup(i)) {
        changes += 1
        currentChangedGroup(newGroup) = true
        currentChangedGroup(belongedGroup(i)) = true
        groups(belongedGroup(i)).removeMember(i)
        groups(newGroup).addMember(i)
        belongedGroup(i) = newGroup
      }
      i += 1
    }
    // 更新，每个组的中心点
    i = 0
    while(i < groups.length) {
    //for (i <- 0 until groups.length) {
      if (currentChangedGroup(i)) {
        updateCenter(groups(i))
      }
      i += 1
    }
    // 更新所有样本点到新中心点的距离
    i = 0
    while(i < vectors.length) {
    //for (i <- 0 until vectors.length) {
      for (j <- 0 until KMeans.K) {
        if (currentChangedGroup(j)) {
          distanceToCenter(i)(j) = vectors(i).distance(groups(j).center)
        }
      }
      i += 1
    }
    changes
  }

  /**
   * 更新某组的中心点.
   */
  private def updateCenter(g: KMeans.Group): Unit = {
    val size = g.members.size
    val n = g.center.dimension

    if (size == 0) {
      return ; // 如果成员为空，就不更新
    }

    val builder = g.center.newBuilder()

    for (m <- g.members) {
      for (j <- 0 until n) {
        builder.set(j, builder.get(j) + vectors(m).get(j))
      }
    }

    for (i <- 0 until n) {
      builder.set(i, builder.get(i) / size)
    }
    g.center = builder.build()
  }
}

/**
 * @author soulmachine@gmail.com
 *
 */
object KMeans {
  /** 日志. */
  private val LOGGER = Logger.getLogger(KMeans.getClass().getName())
  try {
    val fileHandler = new FileHandler(LOGGER.getName());
    fileHandler.setLevel(Level.INFO);
    fileHandler.setFormatter(new SimpleFormatter);
    LOGGER.addHandler(fileHandler);
  } catch {
    case e: SecurityException =>
      LOGGER.warning(e.getMessage())
    case e: IOException =>
      LOGGER.warning(e.getMessage());
  }

  /** 随机数发生器. */
  private val RANDOM = new Random()

  /** 本数据集有20个类，故K定位20. */
  val K: Int = 20

  /**
   * 表示一个类别的组.
   *
   * 所在的数组的下标，就是该组的编号.
   *
   * @param center 该组的中心点
   * @author 方勤
   *
   */
  private final class Group(var center: Vector) {
    /** 该组的成员在 vectors中的下标. */
    val members = scala.collection.mutable.Set.empty[Int]

    /**
     * 添加成员.
     * @param m 成员
     * @return 操作是否成功
     */
    def addMember(m: Int): Boolean = {
      members.add(m)
    }

    /**
     * 添加成员.
     * @param m 成员
     * @return 操作是否成功
     */
    def removeMember(m: Int): Boolean = {
      members.remove(m)
    }
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
  private def getInitialSeedsKmeansPlusPlus(
			vectors : Array[Vector], k : Int) : Set[Int] = {
	LOGGER.fine("enter getInitialSeedsKmeansPlusPlus().\n")
	val startTime = System.currentTimeMillis();
	val seeds = collection.mutable.Set.empty[Int]

	// 记录每个样本的D值，种子点自己则不用计算，为0
	val d = Array.fill(vectors.length)(Double.MaxValue)
	// 随即选取第一个点
	val firstSeed = RANDOM.nextInt(vectors.length)
	seeds.add(firstSeed)
	d(firstSeed) = 0

	var currentSeed = firstSeed
	for (i <- 1 until k) {
	  // 计算每个点到中心的距离的平方，选择最小的作为D值
	  for (j <- 0 until d.length) {
	    // 已经在种子集合中就排除掉
	    if (!seeds.contains(j)) {

	      val distance = vectors(j)
	        .distance(vectors(currentSeed))
	      if (d(j) > distance) {
	        d(j) = distance
	      }
	    }
	  }

	  // 按概率，选择一个点，加入种子点集合
	  currentSeed = selectSeed(d)
	  d(currentSeed) = 0
	  seeds.add(currentSeed)
	}
	val endTime = System.currentTimeMillis()
	LOGGER.fine("consummed " + (endTime - startTime) / 1000 + " s.\n")

	seeds.toSet
  }

  /**
   * 随机选择 K个点，原始的kmeans算法.
   * @param count 样本总数
   * @return 初始的中心点
   */
  private def getInitialSeedsRandom(count: Int): Set[Int] = {
    LOGGER.fine("enter getInitialSeeds1().\n");
    val startTime = System.currentTimeMillis();
    val indexes = new Array[Int](count)
    for (i <- 0 until indexes.length) {
      indexes(i) = i
    }

    for (i <- 0 until K) {
      val j = RANDOM.nextInt(count)
      val temp = indexes(i)
      indexes(i) = indexes(j) // 交换
      indexes(j) = temp
    }
    val result = scala.collection.mutable.Set.empty[Int]
    for (i <- 0 until K) {
      result.add(indexes(i))
    }
    val endTime = System.currentTimeMillis()
    LOGGER.fine("consummed " + (endTime - startTime) / 1000 + " s.\n");
    result.toSet
  }

  /**
	 * 选择一个点加入种子点集合.
	 *
	 * @param ds
	 *            所有点的D值，0表示已经在种子点集合中
	 * @return 一个点的下标
	 */
	private def selectSeed(ds : Array[Double]) : Int = {
		var min = Double.MaxValue; // 最小值
		var sum = 0.0d // 和
		for (i <- 0 until ds.length) {
		  if (ds(i) > 0) {
			sum += ds(i)
			if (ds(i) < min) {
			  min = ds(i)
			}
		  }
		}

		var r = min + (sum - min) * RANDOM.nextDouble()

		for (i <- 0 until ds.length) {
			if (ds(i) > 0) {
				r -= ds(i)
				if (r <= 0) {
					return i
				}
			}
		}
		LOGGER.severe("wrong seed, should'nt run to here !\n");
		return -1;
	}

  /**
   * 计算NMI，评估聚类质量.
   * @param classFileCount 每个类别下的文件数目
   * @return NMI值
   */
  private def getNMI(groups: Array[Group], catagories: Array[String], classFileCount: Map[String, Int]): Double = {
    var ikc = 0.0d // 聚类k与类别c之间的互信息
    var hk = 0.0d // 聚类k 的熵

    for (g <- groups) {
      val k = g.members.size // cluster k的文件数目
      if (k > 0) {
        // cluster k下类别c的文件总数
        val kc = scala.collection.mutable.Map.empty[String, Int]
        for (m <- g.members) {
          val c = catagories(m)

          kc.get(c) match {
            case Some(x) => {
              kc.put(c, x + 1)
            }
            case None => {
              kc.put(c, 1)
            }
          }
        }
        for ((c, value) <- kc) {
          ikc += value.toDouble / catagories.length * Math.log((catagories.length * value.toDouble)
            / (k * classFileCount(c)));
        }

        hk += k.toDouble / catagories.length * Math.log(catagories.length / k.toDouble)
      }
    }
    var hc = 0.0d // 类别c的熵
    for ((_, c) <- classFileCount) {
      if (c > 0) {
        hc += (c.toDouble / catagories.length
          * Math.log(catagories.length / c.toDouble))
      }
    }

    2 * ikc / (hc + hk)
  }
}