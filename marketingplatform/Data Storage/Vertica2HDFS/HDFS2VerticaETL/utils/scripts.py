'''
Created on Nov 17, 2014

@author: Mykhail Martsynyuk
'''
from datetime import datetime, timedelta
import logging
log = logging.getLogger(__name__)

def move_partitions_to_coldstorage(vertica_db, table_name, as_of_period="Y", as_of_number=1, as_of_date_key=None):
    '''
    Move partitions of $table_name with keys less than "as_of_date-as_of_period*as_of_number"
    '''
    res = vertica_db.exec_single_stmt("""
SELECT nvl(max(partition_key),'20000101') as max_key, 
       to_char(sysdate,'YYYYMMDD') as sysdate 
  FROM partitions 
 where location_label = 'coldstorage' 
   and projection_name like '""" + table_name + "%'")

    if not as_of_date_key:
        as_of_date_key=res["Result"][0]["sysdate"]

    max_key = 20000101
    if res["Result"][0]["max_key"]:
        max_key = res["Result"][0]["max_key"]
    
    log.debug("Existing max partition key=%s"%str(max_key))
    as_of_date = datetime.strptime(as_of_date_key,"%Y%m%d").date()
    log.debug("As of date=%s"%str(as_of_date))
    if as_of_period=="Y":
        cold_date_max = (as_of_date - timedelta(years=as_of_number)).strftime("%Y%m%d")
    elif as_of_period=="M":
        cold_date_max = (as_of_date - timedelta(months=as_of_number)).strftime("%Y%m%d")
    elif as_of_period=="D":
        cold_date_max = (as_of_date - timedelta(days=as_of_number)).strftime("%Y%m%d")
    log.debug("Max acceptable partition key for cold data=%s"%str(cold_date_max))

    if max_key < cold_date_max:
        r = vertica_db.exec_single_stmt("""
        SELECT SET_OBJECT_STORAGE_POLICY('wrd.flat_pcmd_1w_FACT','coldstorage',
                                              '"""+str(max_key)+"""',
                                              '"""+str(cold_date_max)+"""'
                                              USING PARAMETERS ENFORCE_STORAGE_MOVE='true')
        """)
        log.info("Connection storage policy set result- " +str(r["Result"]))