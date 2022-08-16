rm -rf ./bin
find ./src -name "*.java" > sources.txt
javac -d ./bin @sources.txt
rm sources.txt
