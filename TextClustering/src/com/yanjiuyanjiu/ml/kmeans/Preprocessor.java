package com.yanjiuyanjiu.ml.kmeans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.yanjiuyanjiu.ml.vector.DenseVector;
import com.yanjiuyanjiu.ml.vector.SparseVector;
import com.yanjiuyanjiu.ml.vector.Vector;

/**
 * 预处理，获取基本数据，例如单词表，文本向量等.
 *
 * @author soulmachine@gmail.com
 *
 */
public final class Preprocessor {
	/** dataset采用UTF-8编码. */
	private static final String ENCODING = "UTF-8";
	/** 文档频率太低的要丢弃，PERCENT < 文档频率/文档总数，一般设为0.01. */
	private static final double PERCENT = 0.01;
	/** 文档频率太高也要丢弃. */
	private static final double PERCENT_MAX = 0.8;

	/** logger. */
	private static final Logger LOGGER = Logger.getLogger(Preprocessor.class
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
	/** 是否用稀疏表示. */
	private final boolean sparse;
	/** 单词表. */
	private ImmutableMap<String, WordGlobalInfo> vocabulary = null;
	/** 文本向量. */
	private ImmutableList<TextVector> vectors = null;
	/** 每个类别的文件总数, key为类别，value为该类别下的文件数目. */
	private ImmutableMap<String, Integer> catagoryFileCount;

	/**
	 * 构造方法.
	 *
	 * @param sparse
	 *            使用稀疏还是普通向量表示
	 */
	public Preprocessor(final boolean sparse) {
		this.sparse = sparse;
	}

	/**
	 * 单词的全局信息.
	 */
	private static final class WordGlobalInfo {
		/** 单词在文本向量中的位置. */
		private final int index;
		/** 单词的逆文档频率IDF. */
		private final double idf;

		/**
		 * 构造函数.
		 *
		 * @param idf
		 *            逆文档频率
		 * @param index
		 *            单词在向量中的位置
		 */
		public WordGlobalInfo(final int index, final double idf) {
			this.index = index;
			this.idf = idf;
		}

		public double getIdf() {
			return idf;
		}

		public int getIndex() {
			return index;
		}
	}

	/**
	 * 预处理.
	 *
	 * @param datasetDir
	 *            数据样本的目录
	 */
	public void process(final String datasetDir) {
		final String[] files = scanDirNoRecursion(datasetDir);

		final Map<String, FileInfo> filesInfo = getFileInfo(files);

		final Map<String, WordGlobalInfo> temp1 = extractVocabulary(filesInfo);
		vocabulary = ImmutableMap.<String, WordGlobalInfo>builder().
				putAll(temp1).build();
		final List<TextVector> temp2 = convertToVectors(filesInfo,
				vocabulary, sparse);
		vectors = ImmutableList.<TextVector>builder().addAll(temp2).build();
		final Map<String, Integer> temp3 = calcClassFileCount(filesInfo);
		catagoryFileCount = ImmutableMap.<String, Integer>builder().
				putAll(temp3).build();
	}

	public ImmutableList<TextVector> getVectors() {
		return vectors;
	}

	public ImmutableMap<String, Integer> getCatagoryFileCount() {
		return catagoryFileCount;
	}

	/**
	 * 获取文件的类别.
	 *
	 * @param filePath
	 *            文件的绝对路径
	 * @return 文件的类别，即文件的上一级目录名
	 */
	private static String getCatagoryOfFile(final String filePath) {
		final int end = filePath.lastIndexOf(File.separatorChar);
		final int start = filePath.substring(0, end).lastIndexOf(
				File.separatorChar);

		return filePath.substring(start + 1, end);
	}

	/**
	 * 计算每个类下的文件个数.
	 *
	 * @param filesInfo
	 *            所有文件的与处理信息.
	 * @return 每个类下的文件个数
	 */
	private static Map<String, Integer> calcClassFileCount(
			final Map<String, FileInfo> filesInfo) {
		final Map<String, Integer> result = new HashMap<String, Integer>(
				KMeans.K);

		for (final Map.Entry<String, FileInfo> entry : filesInfo.entrySet()) {
			final String catagory = getCatagoryOfFile(entry.getKey());

			final Integer count = result.get(catagory);
			if (count == null) {
				result.put(catagory, Integer.valueOf(1));
			} else {
				result.put(catagory, count + 1);
			}
		}

		// debug
		int fileCount = 0;
		for (final Map.Entry<String, Integer> entry : result.entrySet()) {
			fileCount += entry.getValue();
		}
		if (fileCount != filesInfo.size()) {
			LOGGER.severe("file count not equal to original count.\n");
		}
		return result;
	}

	/**
	 * 递归遍历目录（包括子目录），非递归实现.
	 *
	 * @param path
	 *            目录路径，该路径下存放dataset
	 * @return 所有文件的绝对路径
	 */
	private static String[] scanDirNoRecursion(final String path) {
		final ArrayList<String> absolutePathes = new ArrayList<String>();

		final LinkedList<File> dirs = new LinkedList<File>();
		final File dir = new File(path);
		File[] file = dir.listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isDirectory()) {
				dirs.add(file[i]);
			} else {
				// System.out.println(file[i].getAbsolutePath());
				absolutePathes.add(file[i].getAbsolutePath());
			}
		}

		while (!dirs.isEmpty()) {
			final File tmp = dirs.removeFirst(); // 首个目录
			if (tmp.isDirectory()) {
				file = tmp.listFiles();
				if (file == null) {
					continue;
				}
				for (int i = 0; i < file.length; i++) {
					if (file[i].isDirectory()) {
						dirs.add(file[i]); // 目录则加入目录列表，关键
					} else {
						// System.out.println(file[i]);
						absolutePathes.add(file[i].getAbsolutePath());
					}
				}
			} else {
				LOGGER.warning(tmp.toString());
				// num++;
			}
		}

		final String[] result = new String[absolutePathes.size()];
		absolutePathes.toArray(result);
		return result;
	}

	/**
	 * 读取文本文件后，预处理得到的信息，以后依赖该信息，不再从硬盘读取文件.
	 *
	 * @author 方勤
	 *
	 */
	private static class FileInfo {
		/** 该文件所属的类别，计算NMI时需要用到. */
		// private final String catagory;
		/** 该文件包含的单词总数. */
		private final int allWordsCount;
		/** 每个单词的个数. */
		private final Map<String, Integer> wordCount;

		/**
		 * 构造函数.
		 *
		 * @param allWordsCount
		 *            文件包含的单词总数
		 * @param wordCount
		 *            每个单词的个数
		 */
		public FileInfo(final int allWordsCount,
				final Map<String, Integer> wordCount) {
			this.allWordsCount = allWordsCount;
			this.wordCount = wordCount;
		}

		public int getAllWordsCount() {
			return allWordsCount;
		}

		public Map<String, Integer> getWordCount() {
			return wordCount;
		}
	}

	/**
	 * 计算每个文件的信息，统计每个单词的出现个数.
	 *
	 * @param files
	 *            所有文本文件的绝对路径
	 * @return 文件信息
	 */
	private static Map<String, FileInfo> getFileInfo(final String[] files) {
		final Map<String, FileInfo> filesInfo = new HashMap<String, FileInfo>();
		for (final String file : files) {
			final String text = getText(file, ENCODING);
			if (text == null) {
				continue;
			}

			final List<String> tokens = TextSegmentation.tokenizer(text);

			// 本文件的单词计数器
			final Map<String, Integer> wordCounter =
					new HashMap<String, Integer>();
			for (final String token : tokens) {
				final Integer count = wordCounter.get(token);
				if (count == null) {
					wordCounter.put(token, Integer.valueOf(1));
				} else {
					wordCounter.put(token, count + 1);
				}
			}
			filesInfo.put(file, new FileInfo(tokens.size(), wordCounter));
		}
		LOGGER.info("scanned " + filesInfo.size() + " files.\n");
		return filesInfo;
	}

	/**
	 * 获取单词表，并进行缩减，计算出每个单词的IDF.
	 *
	 * 在dataset中出现过的所有单词（去掉stopwords，标点符号，以及过滤掉一些不需要的词）<br/>
	 * 如果某单词w要么在dataset中极少的文本中出现，要么在Dataset中绝大多数的文本中出现，这种词对
	 * 文本的区分不会产生太大的作用，反而会影响到聚类的效果，要去掉。
	 *
	 * @param filesInfo
	 *            所有文本文件的预处理信息
	 * @return 单词表
	 */
	private static Map<String, WordGlobalInfo> extractVocabulary(
			final Map<String, FileInfo> filesInfo) {
		/** 单词计数器，key为单词，value为出现过的文件数. */
		final Map<String, Integer> wordCount = new HashMap<String, Integer>();

		for (final Map.Entry<String, FileInfo> entryI : filesInfo.entrySet()) {
			final FileInfo fileIfo = entryI.getValue();
			for (final Map.Entry<String, Integer> entryJ : fileIfo
					.getWordCount().entrySet()) {
				final String key = entryJ.getKey();
				final Integer count = wordCount.get(key);
				if (count == null) {
					wordCount.put(key, Integer.valueOf(1));
				} else {
					wordCount.put(key, count + 1);
				}
			}
		}
		/**
		 * 计算单词的优先级，用于选出一些优先级较高的词作为特征词.
		 *
		 * @author 方勤
		 *
		 */
		class WordRank implements Comparable<WordRank> {
			final String word;
			final int rank; // 即文件频率作为排名值

			@Override
			public int compareTo(final WordRank other) {
				return rank - other.rank;
			}

			/**
			 * 构造函数
			 *
			 * @param word
			 *            单词
			 * @param rank
			 *            排名
			 */
			public WordRank(final String word, final int rank) {
				this.word = word;
				this.rank = rank;
			}
		}

		final WordRank[] wordsRank = new WordRank[wordCount.size()];
		int i = 0;
		for (final Map.Entry<String, Integer> entry : wordCount.entrySet()) {
			final String key = entry.getKey();
			final int value = entry.getValue();

			wordsRank[i++] = new WordRank(key, value);
		}
		java.util.Arrays.sort(wordsRank);
		// // debug, 生成数据文件，提供给gnuplot
		// StringBuilder sb = new StringBuilder();
		// for (i = 0; i < wordsRank.length; i++) {
		// sb.append(i + " ");
		// sb.append(wordsRank[i].rank + "\n");
		// }
		// writeText("d:\\wordrank.txt", sb.toString(), ENCODING);
		// System.exit(-1);

		// 最后返回的结果，单词-IDF
		final int fileCount = filesInfo.size();
		final HashMap<String, WordGlobalInfo> vocabulary =
				new HashMap<String, WordGlobalInfo>();
		i = 0;
		final int minCount = (int) (PERCENT * fileCount);
		final int maxCount = (int) (PERCENT_MAX * fileCount);
		for (final WordRank word : wordsRank) {
			if ((word.rank > minCount) && (word.rank < maxCount)) {
				vocabulary
						.put(word.word,
								new WordGlobalInfo(i++, Math.log(fileCount
										/ word.rank)));
			}
		}
		LOGGER.info(wordsRank.length + " words totally, selected "
				+ vocabulary.size() + " words as feature.\n");
		return vocabulary;
	}

	/**
	 * 将文本文件转化为文本向量.
	 *
	 * @param filesInfo
	 *            所有文本文件的信息
	 * @param vocabulary
	 *            单词表，由{@link extractShrinkedVocabulary} 得到
	 * @param sparse
	 *            是否用稀疏表示
	 * @return 文本向量
	 */
	private static List<TextVector> convertToVectors(
			final Map<String, FileInfo> filesInfo,
			final Map<String, WordGlobalInfo> vocabulary,
			final boolean sparse) {
		LOGGER.info("building " + filesInfo.size() + " files total.\n");
		final List<TextVector> textVectors =
				new ArrayList<TextVector>(filesInfo.size());

		for (final Map.Entry<String, FileInfo> entry : filesInfo.entrySet()) {
			final String key = entry.getKey();
			final FileInfo value = entry.getValue();

			final Vector v = convertToVector(value, vocabulary, sparse);
			if (v == null) {
				continue;
			}

			textVectors.add(new TextVector(key, getCatagoryOfFile(key), v));
		}

		return textVectors;
	}

	/**
	 * 将一个文本文件转化为向量.
	 *
	 * @param fileInfo
	 *            文件的信息
	 * @param vocabulary
	 *            全局的单词表
	 * @param sparse
	 *            是否采用稀疏表示
	 * @return 文本向量
	 */
	private static Vector convertToVector(final FileInfo fileInfo,
			final Map<String, WordGlobalInfo> vocabulary,
			final boolean sparse) {
		final int vocabularySize = vocabulary.size();

		final Vector.Builder builder;
		if (sparse) {
			builder = new SparseVector.Builder(vocabularySize);
		} else {
			builder = new DenseVector.Builder(vocabularySize);
		}

		for (final Map.Entry<String, Integer> entry : fileInfo.getWordCount()
				.entrySet()) {
			final String key = entry.getKey();
			final Integer value = entry.getValue();

			final WordGlobalInfo globalInfo = vocabulary.get(key);
			if (globalInfo != null) {
				builder.set(globalInfo.getIndex(),
						((double) value) / fileInfo.getAllWordsCount()
								* globalInfo.getIdf());
			}
		}
		final Vector vector = builder.build();

		if (vector.magnitude() > 0) {
			return vector;
		}
		return null;
	}

	/**
	 * 返回给定路径的文本文件内容.
	 *
	 * @param filePath
	 *            给定的文本文件路径
	 * @param encoding
	 *            文本文件的编码
	 * @return 文本内容
	 */
	private static String getText(final String filePath,
			final String encoding) {
		try {
			final InputStreamReader isReader = new InputStreamReader(
					new FileInputStream(filePath), encoding);

			final BufferedReader reader = new BufferedReader(isReader);
			String aLine;
			final StringBuilder sb = new StringBuilder();

			while ((aLine = reader.readLine()) != null) {
				sb.append(aLine + " ");
			}
			reader.close();
			isReader.close();
			return sb.toString();
		} catch (final UnsupportedEncodingException e) {
			LOGGER.warning(e.getMessage());
		} catch (final FileNotFoundException e) {
			LOGGER.warning(e.getMessage());
		} catch (final IOException e) {
			LOGGER.warning(e.getMessage());
		}
		return null;
	}

	/**
	 * 将字符串写入文本文件.
	 *
	 * @param filePath
	 *            给定的文本文件路径
	 * @param text
	 *            要写入文件的字符串
	 * @param encoding
	 *            文本文件的编码
	 * @return 操作是否成功
	 */
	// TODO: THIS IS JUST FOR DEBUGGING, WILL BE REMOVED
	private static boolean writeText(final String filePath, final String text,
			final String encoding) {
		try {
			final OutputStreamWriter isWriter = new OutputStreamWriter(
					new FileOutputStream(filePath), encoding);

			final BufferedWriter writer = new BufferedWriter(isWriter);
			writer.append(text);

			writer.close();
			isWriter.close();
			return true;
		} catch (final UnsupportedEncodingException e) {
			LOGGER.warning(e.getMessage());
		} catch (final FileNotFoundException e) {
			LOGGER.warning(e.getMessage());
		} catch (final IOException e) {
			LOGGER.warning(e.getMessage());
		}
		return false;
	}

	// // debug
	// // 把样本数据作为分类号的数据，计算其NMI，理论上应该为1
	// public double getOriginalNMI() {
	// final KMeans.Group[] groups = new KMeans.Group[catagoryFileCount.size()];
	// for (int i = 0; i < groups.length; i++) {
	// groups[i] = new KMeans.Group(new TextDenseVector("file", new
	// DenseVector(vocabulary.size())));
	// }
	// Map<String, Integer> catagoryNo = new HashMap<String, Integer>();
	// int no = 0;
	// for (Entry<String, Integer> entry : catagoryFileCount.entrySet()) {
	// catagoryNo.put(entry.getKey(), no++);
	// }
	// for (int i = 0; i < vectors.length; i++) {
	// final String catagory = getCatagoryOfFile(vectors[i].getFile());
	// groups[catagoryNo.get(catagory)].addMember(i);
	// }
	// final double NMI = KMeans.getNMI(groups, vectors, catagoryFileCount);
	// LOGGER.warning("standard NMI = " + NMI);
	// return NMI;
	// }
}
