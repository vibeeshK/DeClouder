@echo

REM this batch process is meant for running the XtdProjTrackingSrvrOrchestrator

java -Dlog4j.configurationFile=${INSTALL_PATH}\mainJar64Bit\config\xtdProjTrackSrvrOrchestlog4j2.xml -cp ${INSTALL_PATH}\mainJar64Bit\ESPoTXtdSrvrComp-0.0.1-SNAPSHOT-jar-with-dependencies.jar xtdSrvrComp.XtdProjTrackingSrvrOrchestrator
