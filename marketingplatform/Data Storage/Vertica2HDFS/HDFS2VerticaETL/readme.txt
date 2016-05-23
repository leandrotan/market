How to run:
1. Export RowLog library and base directory to JYTHONPATH variable
export JYTHONPATH=/home/mykhail.martsynyuk/RowLog-1-SNAPSHOT.jar:/home/mykhail.martsynyuk/HDFS2VerticaETL
2. Export hadoop and hbase classpath
export CLASSPATH=$(hadoop classpath):$(hbase classpath)
3. Run Jython script with following command
/usr/lib/jython/jython -Dpython.cachedir=/home/mykhail.martsynyuk/PyCache Hdfs2VerticaHdfsStorage_run.py -f /user/federico.leven/WatcherModule/asset -a D -n 10 --connection "jdbc:vertica://10.104.3.26:5433/verticadst?user=wrd_user&password=xpl123" --fact_table=flat_pcmd_1w_FACT --staging_table=flat_pcmd_1w_STAGING

Notes:
To get parameters help run
/usr/lib/jython/jython Hdfs2VerticaHdfsStorage_run.py -h

Parameter "-Dpython.cachedir=/home/mykhail.martsynyuk/PyCache"
can be ommited. You will see some warning about write permissions to Cache directory, but from functionality pov everything will be fine