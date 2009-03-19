@echo off
set OCP=%CLASSPATH%
set lib=lib
set lib2=target

set CLASSPATH=.

set CLASSPATH=%CLASSPATH%;%lib%\acegi-security-1.0.4.jar
set CLASSPATH=%CLASSPATH%;%lib%\antlr-2.7.6.jar
set CLASSPATH=%CLASSPATH%;%lib%\asm.jar
set CLASSPATH=%CLASSPATH%;%lib%\castor-1.0.2.jar
set CLASSPATH=%CLASSPATH%;%lib%\castor-1.2-xml.jar
set CLASSPATH=%CLASSPATH%;%lib%\cglib-2.1.3.jar
set CLASSPATH=%CLASSPATH%;%lib%\commons-codec-1.3.jar
set CLASSPATH=%CLASSPATH%;%lib%\commons-collections-3.2.jar
set CLASSPATH=%CLASSPATH%;%lib%\commons-logging-1.1.jar
set CLASSPATH=%CLASSPATH%;%lib%\commons-pool-1.3.jar
set CLASSPATH=%CLASSPATH%;%lib%\dlbadapter.jar
set CLASSPATH=%CLASSPATH%;%lib%\hibernate3.jar
set CLASSPATH=%CLASSPATH%;%lib%\lbRuntime.jar
set CLASSPATH=%CLASSPATH%;%lib%\lexbig.jar
set CLASSPATH=%CLASSPATH%;%lib%\lexevsapi42-beans.jar
set CLASSPATH=%CLASSPATH%;%lib%\lexevsapi42-framework.jar
set CLASSPATH=%CLASSPATH%;%lib%\log4j-1.2.14.jar
set CLASSPATH=%CLASSPATH%;%lib%\lucene-core-2.3.2.jar
set CLASSPATH=%CLASSPATH%;%lib%\lucene-regex-2.3.2.jar
set CLASSPATH=%CLASSPATH%;%lib%\lucene-snowball-2.3.2.jar
set CLASSPATH=%CLASSPATH%;%lib%\sdk-client-framework.jar
set CLASSPATH=%CLASSPATH%;%lib%\spring.jar
set CLASSPATH=%CLASSPATH%;%lib%\umlslicense.jar
set CLASSPATH=%CLASSPATH%;%lib%\xercesImpl.jar
set CLASSPATH=%CLASSPATH%;%lib%\mysql-connector-java-5.1.7-bin.jar
set CLASSPATH=%CLASSPATH%;%lib2%\lexBigExamples.jar

set java=java
set config_file="-DLG_CONFIG_FILE=t:\2.3.0\resources\config\config.props"
set class="org.LexGrid.LexBIG.example.remote2.Main2"

@echo on
%java% -cp %CLASSPATH% %config_file% %class%
@echo off

set OCP=%CLASSPATH%
@echo on
