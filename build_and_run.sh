rm -f ./*.class

echo "Compiling"
javac KyleDB.java

java KyleDB FinalData.txt
