run:
	./gradlew jar && java -jar build/libs/software-design-vu-2024-1.0-SNAPSHOT.jar

change:
	export JAVA_HOME=`/usr/libexec/java_home -v 11.0.22`
	