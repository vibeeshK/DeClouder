@echo

REM this bat is meant for running the XtdProjTrackingSrvrOrchestrator

java -Dlog4j.configurationFile=config\xtdProjTrackSrvrOrchestlog4j2.xml -cp ${INSTALL_PATH}\mainJar64Bit\ESPoTXtdSrvrComp-0.0.1-SNAPSHOT-jar-with-dependencies.jar xtdSrvrComp.XtdProjTrackingSrvrOrchestrator

pause
