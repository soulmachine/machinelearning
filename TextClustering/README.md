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