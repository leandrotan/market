In order to execute Kafka Twitter Producer, you will need to:
	- launch the kafka server using: /usr/lib/kafka/kafka_2.10-0.8.1.1/bin/kafka-server-start.sh /usr/lib/kafka/kafka_2.10-0.8.1.1/config/server.properties
	- create a topic named: Twitter.live using: /usr/lib/kafka/kafka_2.10-0.8.1.1/bin/kafka-topics.sh --create --zookeeper nex-hdp-13:2181 --replication-factor 1 --partitions 1 --topic Twitter.live
	- in case needed, modify producer.conf 
	
	For testing purposes, you can start the consumer to validate that the producer is working as expected and adding data to the topic
	- start the consumer for Twitter.live topic created previously as follows: /usr/lib/kafka/kafka_2.10-0.8.1.1/bin/kafka-console-consumer.sh --zookeeper nex-hdp-13:2181 --topic Twitter.live --from-beginning
	
	Above needs to be executed on nex-hdp-15 where kafka is installed.
	
In order to Execute storm-hive-streaming-example
	- make sure that table twitter_publicstream_storm is created under the correct DATABASE which is reflected in file TwitterHiveTopology.java
	- make sure that file TwitterHiveTopology.java points to the correct credentials (kafka Topic, Sput config, zookeeper ...)
	- clean the project and package it, and copy the jar file generated to the cluster (still the Storm topology is executed using local cluster)
	- create a directory within the same directory where the Jar is copied, as follows: src/main/resources and copy file classifier.txt there
	- run the jar file as follows: java -cp storm-hive-streaming-example-1.0-SNAPSHOT.jar com.wicom.storm.streaming.twitter.TwitterHiveTopology
	

