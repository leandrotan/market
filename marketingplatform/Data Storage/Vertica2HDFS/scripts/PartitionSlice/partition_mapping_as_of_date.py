from com.vertica.jdbc import *
import sys, datetime
import log

logging = log.RowLog()

table_name = "flat_pcmd_1w_v2"

def log_row(hbase_row):
	logging.logRow("log", "VSQL", hbase_row)

log_row("Process_id:PartitionSlicing,Table_name:%s,Description:%s,STATUS:%s"%(table_name,"Starting partition slicing process","In progress"))


ds = DataSource()
ds.setHost("10.104.3.26")
ds.setPort(5433)
ds.setDatabase("verticadst")
ds.setUserID("wrd_user")
ds.setPassword("xpl123")
connection = ds.getConnection()

get_max_key_sql = """
SELECT nvl(max(partition_key),'20000101') as max_key, 
	   trunc(sysdate) as sysdate 
  FROM partitions 
 where location_label = 'coldstorage' 
   and projection_name like '""" + table_name + "%'"

stmt = connection.prepareStatement(get_max_key_sql)
rs = stmt.executeQuery()
max_key = 20000101
if rs.next():
	max_key = rs.getString(1)
stmt.close()
log_row("MsgType:DEBUG,Process_id:PartitionSlicing,Description:Max key found in coldstorage=%s"%(max_key))

as_of_date = datetime.datetime.strptime(sys.argv[1],"%Y%m%d").date()
log_row("MsgType:DEBUG,Process_id:PartitionSlicing,Description:As of date=%s"%str(as_of_date))
cold_date_max = (as_of_date - datetime.timedelta(days=2)).strftime("%Y%m%d")
log_row("MsgType:DEBUG,Process_id:PartitionSlicing,Description:cold_date_max=%s"%str(cold_date_max))

if max_key < cold_date_max:
	set_storage_policy_sql = """
SELECT SET_OBJECT_STORAGE_POLICY('wrd.""" + table_name + """','coldstorage',
								  ?,?
								  USING PARAMETERS ENFORCE_STORAGE_MOVE='true')
		  """
	stmt = connection.prepareStatement(set_storage_policy_sql)
	stmt.setString(1, max_key)
	stmt.setString(2, cold_date_max)
	result = -1
	try:
		rs = stmt.executeQuery()
		if rs.next():
			result = rs.getString(1)
		stmt.close()
		log_row("MsgType:DEBUG,Process_id:PartitionSlicing,Description:Set policy response is %s"%result)
	except:
		connection.close()
		raise
	if result == 0:
		print("Policy successfully set from "+max_key+" to " + cold_date_max)
else:
	log_row("MsgType:INFO,Process_id:PartitionSlicing,Description:No data to move to cold storage")
	
connection.close()