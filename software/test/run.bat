@echo off
rem -------------------------------------------------------------------------------------
setlocal enabledelayedexpansion
set ocp=%CLASSPATH%

set cp=.
set cp=%cp%;..\ncitbrowser\conf
for %%x in (..\ncitbrowser\lib\*.jar lib\*.jar) do (
  set cp=!cp!;%%x
)
set cp=%cp%;bin
set CLASSPATH=%cp%

set java=%JAVA_HOME%\bin\java
set class=gov.nih.nci.evs.browser.test.MatchConceptByCode
set class=gov.nih.nci.evs.browser.test.GetConceptByCode
set class=gov.nih.nci.evs.browser.test.tree.LexEVSTreeTest
set args=-propertyFile C:/Users/yeed/apps/evs/ncit-webapp/conf/NCItBrowserProperties.xml

rem -------------------------------------------------------------------------------------
@echo on
"%java%" %class% %args%

@echo off
rem -------------------------------------------------------------------------------------
set CLASSPATH=%ocp%
@echo on
