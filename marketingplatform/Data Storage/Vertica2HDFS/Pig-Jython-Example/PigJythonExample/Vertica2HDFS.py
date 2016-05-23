#!/usr/bin/python

'''
Created on Sep 4, 2014

@author: Mykhail Martsynyuk
'''
import sys, logging
import logging.config
import os
logging.config.fileConfig(os.path.join(os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(sys.argv[0])), os.pardir)),"logging.ini"))

from org.apache.pig.scripting import Pig
from utils.VerticaHelpers import VerticaDB
from utils.HadoopHelpers import HadoopFS

logger = logging.getLogger("vertica.hdfs")

logger.info("Get table names from arguments..")
tables = sys.argv[1:]
logger.info(str(len(tables))+" tables to process")

vertica = VerticaDB()
hdfs = HadoopFS()
logger.info("HDFS free space: " + str(hdfs.get_space_free()))

params = []
logger.info("Processing pig script binds for list of tables")
for table_name in tables:
    vertica.accert_table_exists(table_name)
    table_size = vertica.get_table_size(table_name)
    logger.info(table_name + " table  size is " + str(table_size) + " bytes")
    
    output_dir = "/user/mykhail.martsynyuk/vertica/export/"+table_name
    #prepare hdfs structure
    logger.info("Move folder "+output_dir+" to backup")
    hdfs.move_folder_to_backup(output_dir)
    logger.info("Remove "+output_dir)
    hdfs.remove_folder(output_dir)
    
    params.append({'out':output_dir, 'table':table_name})
    
P = Pig.compile("""
register /usr/lib/pig/lib/pig-vertica.jar
register /usr/lib/pig/lib/vertica-jdbc-7.0.1-0.jar
A = LOAD 'sql://{SELECT * FROM $table WHERE 1 = ?};{1}' USING com.vertica.pig.VerticaLoader('10.104.5.29','verticadst','5433','alfxplsit','xpl123');
STORE A INTO '$out';
""")

bound = P.bind(params)
stats_list = bound.run()

i = 0
for stats in stats_list:
    if stats.isSuccessful():
        logger.info("SUCCESS: Table: "+params[i]["table"]+"; Number jobs: "+str(stats.getNumberJobs())+ "; Time to run: "+str(stats.getDuration())+"; Files written: "+str(stats.getOutputLocations()))
    else:
        logger.info("FAIL: Table: "+params[i]["table"]+"; ERRORS: "+stats.getAllErrorMessages())
    i+=1
    
    # Next is example of how to get script output:
    #logger.debug(stats.result("A").iterator().next().get(6))
    
