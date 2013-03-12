package com.yanjiuyanjiu.ml.kmeans;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.google.common.collect.ImmutableList;
import com.yanjiuyanjiu.ml.vector.Vector;

/**
 * 主程序.
 *
 * @author soulmachine@gmail.com
 *
 */
public final class Clustering {
	/** 不需要实例化. */
	private Clustering() {
		throw new AssertionError();
	}

	/** 日志. */
	private static final Logger LOGGER = Logger.getLogger(Clustering.class
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

	/**
	 * @param args
	 *            参数
	 */
	public static void main(final String[] args) {
		if (args.length != 3) {
			LOGGER.warning("Usage: cluster threshold "
					+ "(-sparse | -dense) dataset\n");
			LOGGER.warning("Example: cluster 0.05 dataset\n");
			return;
		}

		// 通过这里关闭日志，调试的时候用FINE, 运行的时候用INFO及以上
		Logger.getLogger("com.yanjiuyanjiu.ml").setLevel(Level.INFO);

		final long startTime = System.currentTimeMillis();
		final double threshold = Double.parseDouble(args[0]);
		final boolean sparse = args[1].equals("-sparse");
		final Preprocessor preprocessor = new Preprocessor(sparse);
		preprocessor.process(args[2]);

		// preprocessor.getOriginalNMI();
		// System.exit(0);

		final ImmutableList<TextVector> textVectors = preprocessor.getVectors();
		final ImmutableList.Builder<Vector> vectorBuilder =
				ImmutableList.<Vector>builder();
		for (int i = 0; i < textVectors.size(); i++) {
			vectorBuilder.add(textVectors.get(i).getVector());
		}
		final KMeans kmeans = new KMeans(threshold, vectorBuilder.build());
		kmeans.cluster();

		final ImmutableList.Builder<String> catagoryBuilder =
				ImmutableList.<String>builder();
		for (int i = 0; i < textVectors.size(); i++) {
			catagoryBuilder.add(textVectors.get(i).getCatagory());
		}
		LOGGER.info("NMI = " + kmeans.getNMI(catagoryBuilder.build(),
				preprocessor.getCatagoryFileCount()));

		final long endTime = System.currentTimeMillis();
		LOGGER.info("\ttotal time: " + (endTime - startTime) / 1000 + "s.\n");
	}
}
