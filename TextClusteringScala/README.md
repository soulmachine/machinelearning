#A clone of [TextClustering](https://github.com/soulmachine/machinelearning/tree/master/TextClustering) written in Scala 
The project is a standard Eclipse project, import it using [official scala-ide](http://scala-ide.org/)

##Usage
TODO

##Performance Report
every single iteration consume 6s on average.  
total time 140s on average.  
NMI = 0.31 on average.  

Here is a log example:  
三月 13, 2013 9:54:04 下午 com.yanjiuyanjiu.ml.kmeans.Preprocessor$ com$yanjiuyanjiu$ml$kmeans$Preprocessor$$getFileInfo
INFO: scanned 7532 files.

三月 13, 2013 9:54:05 下午 com.yanjiuyanjiu.ml.kmeans.Preprocessor$ com$yanjiuyanjiu$ml$kmeans$Preprocessor$$extractVocabulary
INFO: 69474 words totally, selected 2005 words as feature.

三月 13, 2013 9:54:05 下午 com.yanjiuyanjiu.ml.kmeans.Preprocessor$ com$yanjiuyanjiu$ml$kmeans$Preprocessor$$convertToVectors
INFO: building 7532 files total.

三月 13, 2013 9:54:07 下午 com.yanjiuyanjiu.ml.kmeans.Preprocessor$ com$yanjiuyanjiu$ml$kmeans$Preprocessor$$convertToVectors
WARNING: discarded 0 files.

三月 13, 2013 9:54:26 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 1 consumed 6s, 7159 points changed.

三月 13, 2013 9:54:33 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 2 consumed 6s, 3027 points changed.

三月 13, 2013 9:54:41 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 3 consumed 7s, 1416 points changed.

三月 13, 2013 9:54:47 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 4 consumed 6s, 782 points changed.

三月 13, 2013 9:54:55 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 5 consumed 7s, 483 points changed.

三月 13, 2013 9:55:02 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 6 consumed 7s, 350 points changed.

三月 13, 2013 9:55:09 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 7 consumed 7s, 239 points changed.

三月 13, 2013 9:55:16 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 8 consumed 7s, 178 points changed.

三月 13, 2013 9:55:23 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 9 consumed 7s, 112 points changed.

三月 13, 2013 9:55:30 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 10 consumed 6s, 89 points changed.

三月 13, 2013 9:55:36 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 11 consumed 6s, 41 points changed.

三月 13, 2013 9:55:42 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 12 consumed 6s, 43 points changed.

三月 13, 2013 9:55:49 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 13 consumed 6s, 40 points changed.

三月 13, 2013 9:55:54 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 14 consumed 4s, 26 points changed.

三月 13, 2013 9:55:59 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 15 consumed 5s, 24 points changed.

三月 13, 2013 9:56:03 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 16 consumed 4s, 15 points changed.

三月 13, 2013 9:56:06 下午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 17 consumed 3s, 7 points changed.

三月 13, 2013 9:56:06 下午 com.yanjiuyanjiu.ml.kmeans.Clustering$ main
INFO: NMI = 0.3162632896002576

三月 13, 2013 9:56:06 下午 com.yanjiuyanjiu.ml.kmeans.Clustering$ main
INFO: 	total time: 136s.




