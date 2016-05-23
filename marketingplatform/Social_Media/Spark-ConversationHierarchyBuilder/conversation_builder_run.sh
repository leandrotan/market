spark-submit \
--master=yarn-client \
--class com.wicom.spark.scala.TwitterConversationBuilder \
--num-executors=4 \
--conf spark.akka.frameSize=50 \
--conf spark.core.connection.ack.wait.timeout=600 \
--executor-memory 2g \
--jars joda-convert-1.2.jar,joda-time-2.2.jar,scalaj-time_2.10.2-0.7.jar,scopt_2.10-3.3.0.jar,hive-jdbc-0.14.0.jar \
ConversationHierarchyBuilder-0.0.1-SNAPSHOT.jar --country Lebanon hdfs://nex-hdp-14:8020/user/sm_user/SocialMedia/Conversations/country=Lebanon