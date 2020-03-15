@echo

REM This batch process is meant for running the ColbTrkServerOrchestrator

java -Dlog4j.configurationFile=${INSTALL_PATH}\mainJar64Bit\config\srvrOrchestlog4j2.xml -cp ${INSTALL_PATH}\mainJar64Bit\ESPoT-0.0.1-SNAPSHOT-jar-with-dependencies.jar espot.ColbTrkServerOrchestrator
