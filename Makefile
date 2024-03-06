install:
	npm i -g yarn && yarn

jar:
	./gradlew jar

test:
	./gradlew build

run: jar
	java -jar build/libs/software-design-vu-2024-1.0-SNAPSHOT.jar

lint:
	yarn prettier:check

lint-fix:
	yarn prettier:write

ifeq ($(shell whoami), bencematajsz)
change:
	export JAVA_HOME=`/usr/libexec/java_home -v 11.0.22`
endif
