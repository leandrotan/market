Storm Framework
	This projects aims to benefit from Apache Storm to load files into Hive Tables by leveraging the micro abtching aspect of Trident.
	The project can be split in several components such as :
		-Watcher that will Spool a directory and captures files with specific filtering,
		-Audit component that will be responsible to audit info of the file onto an Hbase dim_audit table
		-Customized hive components responsible to write tuples received from the files onto Hive
		
	The main class of this project is "MyTopology.java"; 
	it can be executed with 2 Arguments: arg[0] is a property file and arg[1] is the topology name
		If 2 arguments are passed, the topology will be submitted to the Storm Cluster.
		The topology will be executed using the following command:
		bin/storm jar jarName.jar [TopologyMainClass] [Args]
	../apache-storm-0.9.3/bin/storm jar storm-framework-1.0-SNAPSHOT.jar com.wicom.watcher.MyTopology MyTopologyCluster.properties MyTopologyAnyName
	
	If executed with only one arguments: arg[0] would be a property file, and the topology will be ran in its own container (LocalCluster) (for this option to work make sure to change the scope of storm dependency in pom.xml to "compile" instead of "provided" to include the needed libraries in the jar before packaging or point to storm libraries when calling the java)
		The topology will be executed using the following command:
		java -cp storm-framework-1.0-SNAPSHOT.jar com.wicom.watcher.MyTopology MyTopology-local.properties
	
	If executed with no arguments, then the topology will use the default values initialised in the constructor.
	
	Pre-requisites:
		- Hbase must be able to authenticate itself to HDFS, therefore make sure that file hbase-site.xml exists under the resources directory, if not it will try to search for that file
			under hadoop  classpath
		- Create a dim_audit table under Hbase as follows create 'dim_audit', 'mandatory_columns', 'optional_columns'
		- define the list of your staging table columns in class DBFields.java, and make sure to add 2 columns at the beginning: auditsid and filename
			within this class, specify if the column is a regular "column" or a "partition" column
		- in FileNeededFields, define the list of columns and their respective indexes; this will allow us to only load specific columns/fields in case we are not interested to load all fields of the file.
		- In the current implementation, we are extracting day_key from time_stamp column 
		- 2 property files are provided in case needed;The list of properties can be found in GenericConstants.java class
	
	The topology can be executed as follow: 
	
	Once executed, the Topology will be responsible to spool the incoming directory, and if a file is found, it will be offered to a queue, then polled and emitted
	for reading; once received, CDRFileReader will instantiate a bufferReader for that file, and reads the file line by line, until it reached the end of the file.
	Once the eof is reached, an additional line containing "File End" will be added, to inform HiveStateCDR that the file has been read and reached the end, thus we would manually flush hive writers, and update dim_audit accordingly.
	
	A logging mechanism is provided through logback.xml file, in case the topology is NOT executed from IDE, the tpoplogy will use the default cluster.xml file under STORM_DIR/logback/cluster.xml
	this file can be adjust as needed.
	 -Dlogback.configurationFile=/home/sm_user/apache-storm-0.9.3/logback/cluster.xml 

	BINARY files
		A new topology EricssonGsmTopology is added, that is responsible of:
			. Decoding binary files using a third party tool: asn2txt
			. An xml file is generated
			. Applying Transformation and flattening the xml file onto a csv file
			. Zipping the xml file, uploading teh file to hdfs, deleting xml and zip file from local directory
			. COPY genrated csv file onto fact_storm table on vertica