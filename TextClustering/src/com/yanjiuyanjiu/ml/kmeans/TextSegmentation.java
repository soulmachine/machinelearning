package com.yanjiuyanjiu.ml.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 英文分词.
 *
 * @author soulmachine@gmail.com
 *
 */
public final class TextSegmentation {
	/** 本类所有方法都是static，不需要实例化. */
	private TextSegmentation() {
		throw new AssertionError();
	}

	/**
	 * 将英文文本分词.
	 *
	 * @param text
	 *            文本文件
	 * @return 分割好的单词数组
	 */
	public static List<String> tokenizer(final String text) {
		final String lowerCaseText = text.toLowerCase(Locale.ENGLISH);
		final List<String> tokens = new ArrayList<String>();

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lowerCaseText.length(); i++) {
			final char character = lowerCaseText.charAt(i);
			if (Character.isLetter(character)) {
				sb.append(character);
			} else {
				if (sb.length() > 0) {
					tokens.add(sb.toString());
					sb.delete(0, sb.length());
				}
			}
		}
		if (sb.length() > 0) {
			tokens.add(sb.toString());
		}

		return ripStopWords(tokens);
	}

	/**
	 * 去掉停止词.
	 *
	 * @param oldText
	 *            分词后的单词数组
	 * @return 去掉停止词的新数组
	 */
	private static List<String> ripStopWords(final List<String> oldText) {
		// oldText - stopWords = temp
		final List<String> temp = new ArrayList<String>();
		for (final String word : oldText) {
			if (!StopWordsHandler.isStopWord(word)) { // 不是停用词
				temp.add(word);
			}
		}
		// String[] result = new String[temp.size()];
		// temp.toArray(result);
		// return result;
		return temp;
	}
}
