package com.yanjiuyanjiu.ml.kmeans;

import java.util.HashSet;
import java.util.Set;

/**
 * 停用词处理.
 *
 * @author soulmachine@gmail.com
 */
public final class StopWordsHandler {
	/** 禁止实例化. */
	private StopWordsHandler() {
		throw new AssertionError();
	}

	/** 常用英文停用词. */
	private static String[] stopWordsEn = {
			// 来自 c:\Windows\System32\NOISE.ENG
			"about", "1", "after", "2", "all", "also", "3", "an", "4", "and",
			"5", "another", "6", "any", "7", "are", "8", "as", "9", "at", "0",
			"be", "$", "because", "been", "before", "being", "between", "both",
			"but", "by", "came", "can", "come", "could", "did", "do", "does",
			"each", "else", "for", "from", "get", "got", "has", "had", "he",
			"have", "her", "here" };
	/** 停止词. */
	private static Set<String> stopWords = new HashSet<String>();
	static {
		for (final String word : stopWordsEn) {
			stopWords.add(word);
		}
	}

	/**
	 * 判断一个词是否是停止词.
	 *
	 * @param word
	 *            要判断的词
	 * @return 是停止词，返回true，否则返回false
	 */
	public static boolean isStopWord(final String word) {
		return stopWords.contains(word);
	}
}