#!/bin/csh -f

set lib = lib
set lib2 = target

set cp = .
set cp = "$cp":"$lib/acegi-security-1.0.4.jar"
set cp = "$cp":"$lib/antlr-2.7.6.jar"
set cp = "$cp":"$lib/asm.jar"
set cp = "$cp":"$lib/castor-1.0.2.jar"
set cp = "$cp":"$lib/castor-1.2-xml.jar"
set cp = "$cp":"$lib/cglib-2.1.3.jar"
set cp = "$cp":"$lib/commons-codec-1.3.jar"
set cp = "$cp":"$lib/commons-collections-3.2.jar"
set cp = "$cp":"$lib/commons-logging-1.1.jar"
set cp = "$cp":"$lib/commons-pool-1.3.jar"
set cp = "$cp":"$lib/dlbadapter.jar"
set cp = "$cp":"$lib/hibernate3.jar"
set cp = "$cp":"$lib/lbRuntime.jar"
set cp = "$cp":"$lib/lexbig.jar"
set cp = "$cp":"$lib/lexevsapi42-beans.jar"
set cp = "$cp":"$lib/lexevsapi42-framework.jar"
set cp = "$cp":"$lib/log4j-1.2.14.jar"
set cp = "$cp":"$lib/lucene-core-2.3.2.jar"
set cp = "$cp":"$lib/lucene-regex-2.3.2.jar"
set cp = "$cp":"$lib/lucene-snowball-2.3.2.jar"
set cp = "$cp":"$lib/sdk-client-framework.jar"
set cp = "$cp":"$lib/spring.jar"
set cp = "$cp":"$lib/umlslicense.jar"
set cp = "$cp":"$lib/xercesImpl.jar"
set cp = "$cp":"$lib/mm.mysql-2.0.6.jar"
set cp = "$cp":"$lib2/lexBigExamples.jar"

set java = "java"
set config_file = "-DLG_CONFIG_FILE=resources/config.props"
set class = "org.LexGrid.LexBIG.example.remote2.Main2"

set cmd = "$java -cp $cp $config_file $class"
echo $cmd
$cmd
