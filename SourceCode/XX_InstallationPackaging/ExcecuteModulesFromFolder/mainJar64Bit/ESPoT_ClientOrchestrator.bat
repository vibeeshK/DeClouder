@echo

REM this batch process is meant for running the ESPoT Client orchestrator

java -Dlog4j.configurationFile=${INSTALL_PATH}\mainJar64Bit\config\desktopClOrchestlog4j2.xml ${INSTALL_PATH}\mainJar64Bit\ESPoT-0.0.1-SNAPSHOT-jar-with-dependencies.jar espot.ESPoTClientOrchestrator
