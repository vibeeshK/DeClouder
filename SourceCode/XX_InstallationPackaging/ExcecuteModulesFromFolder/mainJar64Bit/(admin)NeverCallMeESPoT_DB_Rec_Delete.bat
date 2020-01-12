@echo off

REM this bat is meant for running the ClearDBsAtDecktopSide class from ESPoT_CatalogDisplay.jar repository and 
REM also the class NeverInvokeMe_DeleteAllXtdDbRecs from ESPoT_XtdProjTrackingSrvrOrchestrator.jar

echo ********************************************
echo ********************************************
echo THIS PROCESS WILL BE DELETING THE OpenDeClouder DATABASE RECORDS. IF YOU DONT WANT TO PROCEED THEN CONTROL+BREAK
echo THIS PROCESS IS ONLY MEANT FOR ADMIN. IF YOU ARE NOT AN ADMIN THEN CONTROL+BREAK
echo ********************************************
echo ********************************************
@echo on

pause

java -Dlog4j.configurationFile=${INSTALL_PATH}\mainJar64Bit\config\uiAndCtlgDbDeletionslog4j2.xml -cp ${INSTALL_PATH}\mainJar64Bit\ESPoT_CatalogDisplay.jar espot.ClearDBsAtDecktopSide

pause

java -Dlog4j.configurationFile=${INSTALL_PATH}\mainJar64Bit\config\xtdDbDeletionslog4j2.xml -cp ${INSTALL_PATH}\mainJar64Bit\ESPoT_XtdProjTrackingSrvrOrchestrator.jar xtdSrvrComp.NeverInvokeMe_DeleteAllXtdDbRecs

@echo off
echo ********************************************
echo ********************************************
echo DATABASE RECORDS DELETION ATTEMPT COMPLETED
echo PLEASE REMEMBER TO ALSO DELETE THE OUTDATED OpenDeClouderUserArtifacts FROM THE USERS FOLDERS MANUALLY
echo ********************************************
echo ********************************************
@echo on

pause