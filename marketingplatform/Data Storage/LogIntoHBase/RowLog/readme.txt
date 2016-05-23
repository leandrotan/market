To Run use:
Add the Jar to your home directory then run
HADOOP_CLASSPATH=$(hbase classpath) hadoop jar RowLog-1-SNAPSHOT.jar log.RowLog <hbasetable> <HBase Column Family> col1:value1,col2:value2,col3:val3

If the table doesn't exist , it is created with the specified column family name then the rows are inserted.