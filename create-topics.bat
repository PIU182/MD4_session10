@echo off
docker exec pharmacy-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic medicine-stock-events --partitions 3 --replication-factor 1
docker exec pharmacy-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic medicine-price-updates --partitions 1 --replication-factor 1
docker exec pharmacy-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic pharmacy-notifications --partitions 2 --replication-factor 1
docker exec pharmacy-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe
pause
