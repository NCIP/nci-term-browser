set OCP=%CLASSPATH%
set lib1=..\ncitbrowser\lib
set lib2=..\software\ncitbrowser\extlib
set lib3=.

set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;%lib1%\*
set CLASSPATH=%CLASSPATH%;%lib2%\*
set CLASSPATH=%CLASSPATH%;%lib3%\*

C:\jdk1.7.0_05\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% gov.nih.nci.evs.testUtil.ui.UITestGeneratorRunner
set CLASSPATH=%OCP%
