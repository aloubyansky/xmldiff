
M2="/home/aloubyansky/.m2/repository"

CP="$M2/org/wildfly/core/wildfly-cli/5.0.0.Beta4-SNAPSHOT/wildfly-cli-5.0.0.Beta4-SNAPSHOT-client.jar"

java -cp "target/classes":$CP org.avoka.xmldiff.Sandbox
