@echo

REM this bat is meant for running the ESPoT Server orchestrator

java -Dlog4j.configurationFile=${INSTALL_PATH}\mainJar64Bit\config\srvrOrchestlog4j2.xml -cp ${INSTALL_PATH}\mainJar64Bit\ESPoT-0.0.1-SNAPSHOT-jar-with-dependencies.jar espot.ESPoTServerOrchestrator

pause
