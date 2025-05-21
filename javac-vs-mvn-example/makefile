## Some more javac options here:
# https://www.baeldung.com/javac-compile-classes-directory

build:
	javac -d target src/main/java/de/fhdortmund/swt2/App.java src/main/java/de/fhdortmund/swt2/Calculator.java

# Utilizing sourcepath to find files, use "user classpath" to find class and source files
build-2:
	javac -sourcepath ./src/main/java -d target ./src/main/java/de/fhdortmund/swt2/App.java 

# Utilizing classpath to find files
build-3:
	javac -cp ./src/main/java -d target ./src/main/java/de/fhdortmund/swt2/App.java

# Using wildcards to find all files in the directory and compile them (Will fail when needed packages are not in wildcard folder)
build-4:
	javac -d ./target ./src/main/java/de/fhdortmund/swt2/*.java

# Defining multiple file paths with wildcard
build-5:
	javac -d ./target ./src/main/java/de/fhdortmund/swt2/*.java ./src/main/java/de/fhdortmund/swt2/utils/*.java

build-lombok:
	javac -d ./target -cp lombok.jar src/main/java/de/fhdortmund/swt2/App.java src/main/java/de/fhdortmund/swt2/utils/Calculator.java
	javap target.de.fhdortmund.swt2.utils.Calculator

run:
	java -cp target de.fhdortmund.swt2.App