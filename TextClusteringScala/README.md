#A clone of [TextClustering](https://github.com/soulmachine/machinelearning/tree/master/TextClustering) written in Scala 
The project is a standard Eclipse project, import it using [official scala-ide](http://scala-ide.org/)

##Usage
TODO

##Performance Report
every single iteration consume 6s on average.  
total time 170s on average.  
NMI = 0.31 on average.  

Here is a log example:  
三月 13, 2013 5:31:38 下午 com.yanjiuyanjiu.ml.kmeans.Preprocessor$ com$yanjiuyanjiu$ml$kmeans$Preprocessor$$getFileInfo
INFO: scanned 7532 files.

三月 13, 2013 5:31:40 下午 com.yanjiuyanjiu.ml.kmeans.Preprocessor$ com$yanjiuyanjiu$ml$kmeans$Preprocessor$$extractVocabulary
INFO: 69474 words totally, selected 2005 words as feature.

三月 13, 2013 5:31:40 下午 com.yanjiuyanjiu.ml.kmeans.Preprocessor$ com$yanjiuyanjiu$ml$kmeans$Preprocessor$$convertToVectors
INFO: building 7532 files total.

三月 13, 2013 5:31:42 下午 com.yanjiuyanjiu.ml.kmeans.Preprocessor$ com$yanjiuyanjiu$ml$kmeans$Preprocessor$$convertToVectors
WARNING: discarded 0 files.

三月 13, 2013 5:32:01 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 1 consumed 7s, 7174 points changed.

三月 13, 2013 5:32:08 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 2 consumed 6s, 3321 points changed.

三月 13, 2013 5:32:15 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 3 consumed 6s, 1394 points changed.

三月 13, 2013 5:32:22 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 4 consumed 7s, 703 points changed.

三月 13, 2013 5:32:29 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 5 consumed 6s, 461 points changed.

三月 13, 2013 5:32:36 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 6 consumed 6s, 348 points changed.

三月 13, 2013 5:32:43 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 7 consumed 6s, 281 points changed.

三月 13, 2013 5:32:50 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 8 consumed 6s, 214 points changed.

三月 13, 2013 5:32:57 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 9 consumed 6s, 141 points changed.

三月 13, 2013 5:33:03 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 10 consumed 6s, 73 points changed.

三月 13, 2013 5:33:14 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 11 consumed 10s, 58 points changed.

三月 13, 2013 5:33:24 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 12 consumed 10s, 38 points changed.

三月 13, 2013 5:33:30 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 13 consumed 5s, 28 points changed.

三月 13, 2013 5:33:38 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 14 consumed 8s, 24 points changed.

三月 13, 2013 5:33:45 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 15 consumed 6s, 17 points changed.

三月 13, 2013 5:33:49 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 16 consumed 4s, 10 points changed.

三月 13, 2013 5:33:54 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 17 consumed 4s, 11 points changed.

三月 13, 2013 5:33:58 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 18 consumed 3s, 9 points changed.

三月 13, 2013 5:34:02 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 19 consumed 4s, 12 points changed.

三月 13, 2013 5:34:07 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 20 consumed 4s, 7 points changed.

三月 13, 2013 5:34:07 下午 com.yanjiuyanjiu.ml.kmeans.Clustering$ main
INFO: NMI = 0.3436266581787604

三月 13, 2013 5:34:07 下午 com.yanjiuyanjiu.ml.kmeans.Clustering$ main
INFO: 	total time: 162s.



