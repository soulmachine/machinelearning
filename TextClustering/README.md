#text clustering using kmeans algorithm

This is a java program to perform text clustering using kmeans algorithm.  
the dataset is http://qwone.com/~jason/20Newsgroups/20news-bydate.tar.gz.  
The project is a standard Eclipse project.

##Usage
cd $PROJECT_DIR  
rm bin/* -rf  
javac -cp .:./lib/mahout-collections-1.0.jar:$CLASSPATH -d bin/ src/com/yanjiuyanjiu/ml/kmeans/*.java src/com/yanjiuyanjiu/ml/vector/*.java  
cd bin  
java -cp .:../lib/mahout-collections-1.0.jar:$CLASSPATH com/yanjiuyanjiu/ml/kmeans/Clustering 0.001 -dense ../20news-bydate-train/  
cd ..  

##Evaluation of clustering
this program uses NMI(Normalized Mutual Information) to evaluate the quality of clustering.  
NMI = 2I(K,C)/(H(K) + H(C)), see http://nlp.stanford.edu/IR-book/html/htmledition/evaluation-of-clustering-1.html

##Performance Report
every single iteration consume 2s on average.  
total time 50s on average.  
NMI = 0.3 on average.  

Here is a log example:  
三月 13, 2013 2:51:10 上午 com.yanjiuyanjiu.ml.kmeans.Preprocessor getFileInfo
INFO: scanned 7532 files.

三月 13, 2013 2:51:11 上午 com.yanjiuyanjiu.ml.kmeans.Preprocessor extractVocabulary
INFO: 69461 words totally, selected 2005 words as feature.

三月 13, 2013 2:51:11 上午 com.yanjiuyanjiu.ml.kmeans.Preprocessor convertToVectors
INFO: building 7532 files total.

三月 13, 2013 2:51:17 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 1 consumed 2263ms, 7131 points changed.

三月 13, 2013 2:51:19 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 2 consumed 2313ms, 2567 points changed.

三月 13, 2013 2:51:22 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 3 consumed 2321ms, 1362 points changed.

三月 13, 2013 2:51:24 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 4 consumed 2271ms, 794 points changed.

三月 13, 2013 2:51:26 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 5 consumed 2272ms, 550 points changed.

三月 13, 2013 2:51:29 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 6 consumed 2521ms, 404 points changed.

三月 13, 2013 2:51:31 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 7 consumed 2293ms, 295 points changed.

三月 13, 2013 2:51:34 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 8 consumed 2420ms, 247 points changed.

三月 13, 2013 2:51:36 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 9 consumed 2331ms, 196 points changed.

三月 13, 2013 2:51:38 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 10 consumed 2059ms, 138 points changed.

三月 13, 2013 2:51:40 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 11 consumed 2001ms, 103 points changed.

三月 13, 2013 2:51:42 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 12 consumed 1786ms, 81 points changed.

三月 13, 2013 2:51:44 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 13 consumed 2262ms, 81 points changed.

三月 13, 2013 2:51:46 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 14 consumed 1947ms, 55 points changed.

三月 13, 2013 2:51:47 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 15 consumed 1434ms, 54 points changed.

三月 13, 2013 2:51:49 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 16 consumed 1641ms, 48 points changed.

三月 13, 2013 2:51:52 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 17 consumed 2433ms, 44 points changed.

三月 13, 2013 2:51:54 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 18 consumed 2141ms, 33 points changed.

三月 13, 2013 2:51:55 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 19 consumed 1509ms, 29 points changed.

三月 13, 2013 2:51:56 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 20 consumed 1223ms, 24 points changed.

三月 13, 2013 2:51:57 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 21 consumed 881ms, 19 points changed.

三月 13, 2013 2:51:59 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 22 consumed 1237ms, 21 points changed.

三月 13, 2013 2:51:59 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 23 consumed 761ms, 11 points changed.

三月 13, 2013 2:52:00 上午 com.yanjiuyanjiu.ml.kmeans.KMeans cluster
INFO: iteration 24 consumed 883ms, 5 points changed.

三月 13, 2013 2:52:00 上午 com.yanjiuyanjiu.ml.kmeans.Clustering main
INFO: NMI = 0.3294934636147199
三月 13, 2013 2:52:00 上午 com.yanjiuyanjiu.ml.kmeans.Clustering main
INFO: 	total time: 55s.



