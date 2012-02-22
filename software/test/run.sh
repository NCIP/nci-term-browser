#!/bin/csh -f

#----------------------------------------------------------------------------------------
set cp=.
set cp="$cp":../ncitbrowser/conf
foreach jar (../ncitbrowser/lib/*.jar lib/*.jar)
  set cp="$cp":$jar
end
set cp="$cp":bin
setenv CLASSPATH $cp

#----------------------------------------------------------------------------------------
set java=$JAVA_HOME/bin/java
set class=gov.nih.nci.evs.browser.test.MatchConceptByCode
set class=gov.nih.nci.evs.browser.test.GetConceptByCode
set class=gov.nih.nci.evs.browser.test.tree.LexEVSTreeTest
set class=gov.nih.nci.evs.browser.test.valueset.Bar
# set args=(-propertyFile /home/evsuser/apps/evs/ncit-webapp/conf/NCItBrowserProperties.xml)
# set args=(-propertyFile /local/home/jboss45c/evs/ncit-webapp/conf/NCItBrowserProperties.xml)
set args=(-propertyFile /Users/nci/apps/evs/ncit/conf/NCItBrowserProperties.xml)

#----------------------------------------------------------------------------------------
$java $class $args
