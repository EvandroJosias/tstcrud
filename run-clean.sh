#!/bin/bash
cd /home/user/workspace/develop/tstcrud
mvn compile -q
java -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
     com.ejsjose.Main 2>/dev/null | \
     grep -E "^(Hello world|ID:|Produto salvo|Aplicação finalizada)"
