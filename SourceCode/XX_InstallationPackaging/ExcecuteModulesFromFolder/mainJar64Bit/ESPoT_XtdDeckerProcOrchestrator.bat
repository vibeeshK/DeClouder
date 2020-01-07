@echo

REM this bat is meant for running the Xtd Decker orchestrator

java -Dlog4j.configurationFile=config\xtdDeckerProcOrchestlog4j2.xml -cp ${INSTALL_PATH}\mainJar64Bit\ESPoTXtdSrvrComp-0.0.1-SNAPSHOT-jar-with-dependencies xtdSrvrComp.XtdDeckerProcOrchestrator

pause
