#/usr/bin/sh
export CLASSPATH=$(hadoop classpath):$(hbase classpath)
export JYTHONPATH=/usr/lib/pig/lib/vertica-jdbc-7.0.1-0.jar:/home/mykhail.martsynyuk/RowLog-1-SNAPSHOT.jar
jython partition_mapping_as_of_date.py 20140203