@echo

REM this batch process is meant for running the ESPoT_CatalogDisplay

java -Dlog4j.configurationFile=${INSTALL_PATH}\mainJar64Bit\config\desktopUIlog4j2.xml -cp ${INSTALL_PATH}\mainJar64Bit\ESPoT-0.0.1-SNAPSHOT-jar-with-dependencies.jar espot.CatalogDisplay
