This Storm Topology is responsible to Stream Twitter Data and Output the tweets to HDFS.
The Topology is consists of:
	1 Spout emitting tweets and country (created as a property) and filtering data based on Keywords and Language
	2 one Bolt receiving tuples of tweets,country; creating directory hdfs:/user/sm_user/Storm/SocialMedia/Twitter/country=<country value from tuple>/day_key=yyyymmdd
		a file will be created on hdfs under the above directory containing  tweets (JSON formatted strings )

An External HIVE Table stg_raw_twitter_storm is created pointing to /user/sm_user/Storm/SocialMedia/Twitter/

To execute the Topology:
	1- bin/storm jar jarName.jar [TopologyMainClass] [Arg0] [Arg1] (the topology will be submitted to the Storm Cluster where [Arg0] -> properties file and [Arg1] -> topology Name), make sure to set storm dependency Scope in pom.xml to "provided"
	 ../apache-storm-0.9.3/bin/storm jar ProvidedStorm-SocialMedia-0.1-jar-with-dependencies.jar com.wicom.storm.socialmedia.twitter.StormTwitterTopology StormTwitterTopology-Egypt.properties StormTwitterTopology-Egypt
	2- bin/storm jar jarName.jar [TopologyMainClass] [Arg0] (the topology will be ran in its own container (LocalCluster) [Arg0] -> properties file), make sure to set storm dependency Scope in pom.xml to "compile"
	 ../apache-storm-0.9.3/bin/storm jar Storm-SocialMedia-0.1-jar-with-dependencies.jar com.wicom.storm.socialmedia.twitter.StormTwitterTopology StormTwitterTopology-Egypt.properties
