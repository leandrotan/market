'''
Created on Nov 11, 2014

@author: Mykhail Martsynyuk
@note: This script is main entry point to run loading from HDFS gzip files to Vertica partitioned table
'''

from logging import handlers, config, getLogger
import utils.hbase_logging
handlers.HBaseHandler = utils.hbase_logging.HBaseHandler

from optparse import OptionParser
from datetime import datetime, timedelta
import calendar

config.fileConfig("logging.ini")
from utils import vertica, hdfs
from utils.scripts import move_partitions_to_coldstorage

log = getLogger("HDFS2VerticaHDFSStorage")
# parse commandline arguments
parser = OptionParser()
parser.add_option("-f", "--folder", dest="filepath",
                  help="Hdfs file path")
parser.add_option("-r", "--regex", dest="regex_mask",
                  help="File name regular expression to catch files")
parser.add_option("-a", "--asofperiod", dest="as_of_period",
                  help="As of period for source files (M - minutes, H - hours, D - days)")
parser.add_option("-n", "--asofnumber", dest="as_of_number", type="int", 
                  help="As of period number")
parser.add_option("-v", "--connection", dest="vertica_connection_string",
                  help="""Vertica DB connection string in format 
\"jdbc:vertica://VerticaHost:portNumber/databaseName?user=username&password=password\"(with quotes)""")
parser.add_option("-t", "--fact_table", dest="fact_table_name", 
                  help="Fact table name")
parser.add_option("-s", "--staging_table", dest="staging_table_name", 
                  help="Staging table name")

(options, args) = parser.parse_args()

max_time = datetime.now()
if options.as_of_period and options.as_of_number:
    # set min_time and max_time to filter files
    if options.as_of_period == "M":
        min_time = max_time - timedelta(minutes=options.as_of_number)
    elif options.as_of_period == "H":
        min_time = max_time - timedelta(hours=options.as_of_number)
    elif options.as_of_period == "D":
        min_time = max_time - timedelta(days=options.as_of_number)
elif (options.as_of_period or options.as_of_number):
    print "Specify both asofperiod and asofnumber parameters"
    exit()
# end parse

log.info("Run parameters- File path- "+options.filepath + "; As of period- "+options.as_of_period +"; As of number- "+ str(options.as_of_number))
log.info("Run parameters- min_time- "+str(min_time) + "; max_time- "+str(max_time))

def hdfs2webhdfs_url(hdfs_url):
    '''
    Converts HDFS url to WebHDFS url
    '''
    return "http://nex-hdp-14.nexius.com:50070/webhdfs/v1"+str(hdfs_url).split("8020")[1]

vertica_db = vertica.VerticaDB(options.vertica_connection_string)
try:
    conn = vertica_db.get_connection()
    # 1. Truncate staging table
    vertica_db.exec_single_stmt("TRUNCATE TABLE wrd."+options.staging_table_name,conn)
    log.info("Staging table truncated")
    
    # 2. Run COPY stmt to load from HDFS to staging table for each file in source HDFS folder
    fs = hdfs.HadoopFS()
    files = fs.get_files_in_path(options.filepath, int(calendar.timegm(min_time.timetuple()))*1000, int(calendar.timegm(max_time.timetuple()))*1000)
    log.info(str(files))
    copy_sql = "COPY wrd."+options.staging_table_name+" FROM STDIN GZIP DELIMITER E',' DIRECT ENFORCELENGTH"
    copy = vertica.Copy(copy_sql, conn)
    for file_ in files:
        copy.add_filestream(fs.get_stream_from_file(file_))    
    r = copy.run()
    log.info("Updated rows count- "+str(copy.get_updated_rows_count()))
    log.debug("Rejected row numbers- "+str(copy.get_rejections()))
    
    # 3. Run data cleansing and transformations
    log.info("Run data cleansing and transformations")
    r = vertica_db.exec_single_stmt("delete from wrd."+options.staging_table_name+" where version <> 'v2'",conn)
    log.info("Deleted rows- "+str(r["UpdateCount"]))
    
    # 4. Run INSERT AS SELECT to fact table
    log.info("Run INSERT AS SELECT to fact table")
    r = vertica_db.exec_single_stmt("""
    INSERT INTO wrd."""+options.fact_table_name+"""
    (FCTPCMDSID,
    VERSION,
    CALL_EVENTCODE,
    CALL_EVENTCODEDESCRIPTION,
    CALL_STMSI_MMECODE,
    CALL_STMSI_MTMSITYPE,
    CALL_STMSI_MTMSI,
    CALL_MMEUES1APID,
    CALL_ENBUES1APID,
    COMMONITM_GUMME_PLMNID,
    COMMONITM_GUMME_MMEG,
    COMMONITM_GUMME_MEC,
    COMMONITM_TAI_PLMNID,
    COMMONITM_TAI_TAC,
    COMMONITM_GENB_LMNID,
    COMMONITM_GENB_TYPE,
    COMMONITM_GENB_ID,
    COMMONITM_CALLRELCAUSE,
    CALLRELCAUSEDESCRIPTION,
    CONN_ATTEMPTEDTIME,
    SETUPDURATIONMSEC,
    CONNECTIONDURATIONMSEC,
    CONN_FREQBANDINDIATOR,
    CONN_CGI_PLMNID,
    DIMRANCELLSECTORID_CONNCGI,
    CONNCGI_SECTORID,
    CONN_PCI,
    CONN_TA,
    CONN_DISTUETOENB_MILES,
    CONN_CARDID,
    CONN_TRANSMITMODE,
    CONN_TXANTENNAMODE,
    CONN_CALLTYPE,
    TYPEOFCAL,
    CONN_MME_VER,
    CONN_MME_IP,
    CONN_SGW_VER,
    CONN_SGW_IP,
    RELEASE_PCI,
    RELEASE_CGI_PLMNID,
    DIMRANCELLSECTORID_RELEASECGI,
    RELEASECGI_SECTORID,
    RELEASE_FREQBANDINDICATOR,
    RELEASE_CQI,
    RELEASE_TA,
    RELEASE_DISTUETOENB_MILES,
    RELEASE_CARID,
    RELEASE_PA,
    DLPOWERCONTROLPARMPA,
    RELEASE_TRANSMITMODE,
    RELEASE_TXANTENNAMODE,
    RELEASE_MME_VER,
    RELEASE_MME_IP,
    RELEASE_SGW_VER,
    RELEASE_SGW_IP,
    HO_ENB_PLMNID,
    HO_ENB_TYPE,
    HO_ENB_ID,
    HO_ENB_VER,
    HO_ENB_IP,
    HO_CGI_PLMNID,
    DIMRANCELLSECTORID_HOCGI,
    HO_PCI,
    NUMERAB,
    THRUPUT00RBID,
    THRUPUT00EPSBEARID,
    THRUPUT00QCI,
    THRUPUT00TXDATATOSGBYTES,
    THRUPUT00RXDATAFROMSGBYTES,
    THRUPUT00TXPACKETSTOUE,
    THRUPUT00DLPACKETDROPRATE,
    THRUPUT00TXPACKETSTOSG,
    THRUPUT00ULPACKETDROPRATE,
    THRUPUT00TXDATATOUEBYTES,
    THRUPUT00RETXDATATOUEBYTES,
    THRUPUT00RXDATAFROMUEBYTES,
    THRUPUT01RBID,
    THRUPUT01EPSBEARID,
    THRUPUT01QCI,
    THRUPUT01TXDATATOSGBYTES,
    THRUPUT01RXDATAFROMSGBYTES,
    THRUPUT01TXPACKETSTOUE,
    THRUPUT01DLPACKETDROPRATE,
    THRUPUT01TXPACKETSTOSG,
    THRUPUT01ULPACKETDROPRATE,
    THRUPUT01TXDATATOUEBYTES,
    THRUPUT01RETXDATATOUEBYTES,
    THRUPUT01RXDATAFROMUEBYTES,
    THRUPUT02RBID,
    THRUPUT02EPSBEARID,
    THRUPUT02QCI,
    THRUPUT02TXDATATOSGBYTES,
    THRUPUT02RXDATAFROMSGBYTES,
    THRUPUT02TXPACKETSTOUE,
    THRUPUT02DLPACKETDROPRATE,
    THRUPUT02TXPACKETSTOSG,
    THRUPUT02ULPACKETDROPRATE,
    THRUPUT02TXDATATOUEBYTES,
    THRUPUT02RETXDATATOUEBYTES,
    THRUPUT02RXDATAFROMUEBYTES,
    THRUPUT03RBID,
    THRUPUT03EPSBEARID,
    THRUPUT03QCI,
    THRUPUT03TXDATATOSGBYTES,
    THRUPUT03RXDATAFROMSGBYTES,
    THRUPUT03TXPACKETSTOUE,
    THRUPUT03DLPACKETDROPRATE,
    THRUPUT03TXPACKETSTOSG,
    THRUPUT03ULPACKETDROPRATE,
    THRUPUT03TXDATATOUEBYTES,
    THRUPUT03RETXDATATOUEBYTES,
    THRUPUT03RXDATAFROMUEBYTES,
    THRUPUT04RBID,
    THRUPUT04EPSBEARID,
    THRUPUT04QCI,
    THRUPUT04TXDATATOSGBYTES,
    THRUPUT04RXDATAFROMSGBYTES,
    THRUPUT04TXPACKETSTOUE,
    THRUPUT04DLPACKETDROPRATE,
    THRUPUT04TXPACKETSTOSG,
    THRUPUT04ULPACKETDROPRATE,
    THRUPUT04TXDATATOUEBYTES,
    THRUPUT04RETXDATATOUEBYTES,
    THRUPUT04RXDATAFROMUEBYTES,
    THRUPUT05RBID,
    THRUPUT05EPSBEARID,
    THRUPUT05QCI,
    THRUPUT05TXDATATOSGBYTES,
    THRUPUT05RXDATAFROMSGBYTES,
    THRUPUT05XPACKETSTOUE,
    THRUPUT05DLPACKETDROPRATE,
    THRUPUT05TXPACKETSTOSG,
    THRUPUT05ULPACKETDROPRATE,
    THRUPUT05TXDATATOUEBYTES,
    THRUPUT05RETXDATATOUEBYTES,
    THRUPUT05RXDATAFROMUEBYTES,
    THRUPUT06RBID,
    THRUPUT06EPSBEARID,
    THRUPUT06QCI,
    THRUPUT06TXDATATOSGBYTES,
    THRUPUT06RXDATAFROMSGBYTES,
    THRUPUT06TXPACKETSTOUE,
    THRUPUT06DLPACKETDROPRATE,
    THRUPUT06TXPACKETSTOSG,
    THRUPUT06ULPACKETDROPRATE,
    THRUPUT06TXDATATOUEBYTES,
    THRUPUT06RETXDATATOUEBYTES,
    THRUPUT06RXDATAFROMUEBYTES,
    THRUPUT07RBID,
    THRUPUT07EPSBEARID,
    THRUPUT07QCI,
    THRUPUT07TXDATATOSGBYTES,
    THRUPUT07RXDATAFROMSGBYTES,
    THRPUT07TXPACKETSTOUE,
    THRUPUT07DLPACKETDROPRATE,
    THRUPUT07TXPACKETSTOSG,
    THRUPUT07ULPACKETDROPRATE,
    THRUPUT07TXDATATOUEBYTES,
    THRUPUT07RETXDATATOUEBYTES,
    THRUPUT07RXDATAFROMUEBYTES,
    RF_TXPOWER,
    RF_UETXPOWERDBM,
    RF_RSRP,
    RF_RSRPDBM,
    RF_RSRQ,
    RF_RSRQDB,
    NBHD_CELLCOUNT,
    NBHD_CGI01PLMNID,
    DIMRANCELLSECTORID_NBHDCGI01,
    NBHD_CGI01PCI,
    NBHD_CGI01RSRP,
    NBHD_CGI01RSRPDBM,
    NBHD_CGI01RSRQ,
    NBHD_CGI01RSRQDB,
    NBHD_CGI01UNKNOWN,
    NBHD_CGI02PLMNID,
    DIMRANCELLSECTORID_NBHDCGI02,
    NBHD_CGI02PCI,
    NBHD_CGI02RSRP,
    NBHD_CGI02RSRPDBM,
    NBHD_CGI02RSRQ,
    NBHD_CGI02RSRQDB,
    NBHD_CGI02UNKNOWN,
    NBHDCGI03PLMNID,
    DIMRANCELLSECTORID_NBHDCGI03,
    NBHD_CGI03PCI,
    NBHD_CGI03RSRP,
    NBHD_CGI03RSRPDBM,
    NBHD_CGI03RSRQ,
    NBHD_CGI03RSRQDB,
    NBHDCGI03UNKNOWN,
    UEHIST_COUNT,
    UEHIST_CGI01PLMNID,
    DIMRANCELLSECTORIDUEHISTCGI01,
    UEHIST_CGI01CELLSIZE,
    UEHISTCGI01STAYTIMESEC,
    UEHIST_CGI02PLMNID,
    DIMRANCELLSECTORID_UEHISTCGI02,
    UEHIST_CGI02CELLSIZE,
    UEHIST_CGI02STAYTIMESEC,
    UEHIST_CGI03PLMNID,
    DIMRANCELLSECTORID_UEHISTCGI03,
    UEHIST_CGI03CELLSIZE,
    UEHIST_CGI03STAYTIMESEC,
    UEHIST_CGI04PLMNID,
    DIMRANCELLSECTORID_UEHISTCGI04,
    UEHIST_CGI04CELLSIZE,
    UEHIST_CGI04STAYTIMESEC,
    UEHIST_CGI05PLMNID,
    DIMRANCELLSECTORID_UEHISTCGI05,
    UEHIST_CGI05CELLSIZE,
    UEHIST_CGI05STAYTIMESEC,
    CALLDBG_STATECMD,
    CALLDBG_STATE,
    CALLDBG_STATECMDESCRIPTION,
    CALLDBG_MSGID,
    CALLDBG_SEQUENCE,
    CALLDBG_CALLID,
    CALLDBG_IMSI,
    CALLDBG_CRNTI,
    UNKNOWN,
    LATITUDE,
    LONGITUDE,
    FIXEDSPATIALGRIDID,
    GEOLOCATIONCD,
    REFERENCE,
    SKU_NUMBER,
    BAN_NBR,
    SRC_OWNER_CD,
    OS_CR_CLASS_CD,
    PRICE_PLAN_CD,
    STUS_CD,
    DAY_KEY)
    
    SELECT
    FCTPCMDSID,
    VERSION,
    cast(CALL_EVENTCODE as int),
    CALL_EVENTCODEDESCRIPTION,
    cast(CALL_STMSI_MMECODE as int),
    cast(CALL_STMSI_MTMSITYPE as int),
    CALL_STMSI_MTMSI,
    CALL_MMEUES1APID,
    cast(CALL_ENBUES1APID as int),
    cast(COMMONITM_GUMME_PLMNID as int),
    cast(COMMONITM_GUMME_MMEG as int),
    cast(COMMONITM_GUMME_MEC as int),
    cast(COMMONITM_TAI_PLMNID as int),
    cast(COMMONITM_TAI_TAC as int),
    cast(COMMONITM_GENB_LMNID as int),
    cast(COMMONITM_GENB_TYPE as int),
    cast(COMMONITM_GENB_ID as int),
    cast(COMMONITM_CALLRELCAUSE as int),
    CALLRELCAUSEDESCRIPTION,
    CONN_ATTEMPTEDTIME,
    cast(SETUPDURATIONMSEC as int),
    cast(CONNECTIONDURATIONMSEC as int),
    cast(CONN_FREQBANDINDIATOR as int),
    cast(CONN_CGI_PLMNID as int),
    DIMRANCELLSECTORID_CONNCGI,
    cast(CONNCGI_SECTORID as int),
    cast(CONN_PCI as int),
    cast(CONN_TA as int),
    cast(CONN_DISTUETOENB_MILES as float),
    cast(CONN_CARDID as int),
    cast(CONN_TRANSMITMODE as int),
    CONN_TXANTENNAMODE,
    cast(CONN_CALLTYPE as int),
    TYPEOFCAL,
    cast(CONN_MME_VER as int),
    CONN_MME_IP,
    cast(CONN_SGW_VER as int),
    CONN_SGW_IP,
    cast(RELEASE_PCI as int),
    cast(RELEASE_CGI_PLMNID as int),
    DIMRANCELLSECTORID_RELEASECGI,
    cast(RELEASECGI_SECTORID as int),
    cast(RELEASE_FREQBANDINDICATOR as int),
    cast(RELEASE_CQI as int),
    cast(RELEASE_TA as int),
    cast(RELEASE_DISTUETOENB_MILES as float),
    cast(RELEASE_CARID as int),
    cast(RELEASE_PA as int),
    cast(DLPOWERCONTROLPARMPA as float),
    cast(RELEASE_TRANSMITMODE as int),
    RELEASE_TXANTENNAMODE,
    cast(RELEASE_MME_VER as int),
    RELEASE_MME_IP,
    cast(RELEASE_SGW_VER as int),
    RELEASE_SGW_IP,
    cast(HO_ENB_PLMNID as int),
    cast(HO_ENB_TYPE as int),
    cast(HO_ENB_ID as int),
    cast(HO_ENB_VER as int),
    HO_ENB_IP,
    cast(HO_CGI_PLMNID as int),
    cast(DIMRANCELLSECTORID_HOCGI as int),
    cast(HO_PCI as int),
    cast(NUMERAB as int),
    cast(THRUPUT00RBID as int),
    cast(THRUPUT00EPSBEARID as int),
    cast(THRUPUT00QCI as int),
    cast(THRUPUT00TXDATATOSGBYTES as int),
    cast(THRUPUT00RXDATAFROMSGBYTES as int),
    cast(THRUPUT00TXPACKETSTOUE as int),
    cast(THRUPUT00DLPACKETDROPRATE as int),
    cast(THRUPUT00TXPACKETSTOSG as int),
    cast(THRUPUT00ULPACKETDROPRATE as int),
    cast(THRUPUT00TXDATATOUEBYTES as int),
    cast(THRUPUT00RETXDATATOUEBYTES as int),
    cast(THRUPUT00RXDATAFROMUEBYTES as int),
    cast(THRUPUT01RBID as int),
    cast(THRUPUT01EPSBEARID as int),
    cast(THRUPUT01QCI as int),
    cast(THRUPUT01TXDATATOSGBYTES as int),
    cast(THRUPUT01RXDATAFROMSGBYTES as int),
    cast(THRUPUT01TXPACKETSTOUE as int),
    cast(THRUPUT01DLPACKETDROPRATE as int),
    cast(THRUPUT01TXPACKETSTOSG as int),
    cast(THRUPUT01ULPACKETDROPRATE as int),
    cast(THRUPUT01TXDATATOUEBYTES as int),
    cast(THRUPUT01RETXDATATOUEBYTES as int),
    cast(THRUPUT01RXDATAFROMUEBYTES as int),
    cast(THRUPUT02RBID as int),
    cast(THRUPUT02EPSBEARID as int),
    cast(THRUPUT02QCI as int),
    cast(THRUPUT02TXDATATOSGBYTES as int),
    cast(THRUPUT02RXDATAFROMSGBYTES as int),
    cast(THRUPUT02TXPACKETSTOUE as int),
    cast(THRUPUT02DLPACKETDROPRATE as int),
    cast(THRUPUT02TXPACKETSTOSG as int),
    cast(THRUPUT02ULPACKETDROPRATE as int),
    cast(THRUPUT02TXDATATOUEBYTES as int),
    cast(THRUPUT02RETXDATATOUEBYTES as int),
    cast(THRUPUT02RXDATAFROMUEBYTES as int),
    cast(THRUPUT03RBID as int),
    cast(THRUPUT03EPSBEARID as int),
    cast(THRUPUT03QCI as int),
    cast(THRUPUT03TXDATATOSGBYTES as int),
    cast(THRUPUT03RXDATAFROMSGBYTES as int),
    cast(THRUPUT03TXPACKETSTOUE as int),
    cast(THRUPUT03DLPACKETDROPRATE as int),
    cast(THRUPUT03TXPACKETSTOSG as int),
    cast(THRUPUT03ULPACKETDROPRATE as int),
    cast(THRUPUT03TXDATATOUEBYTES as int),
    cast(THRUPUT03RETXDATATOUEBYTES as int),
    cast(THRUPUT03RXDATAFROMUEBYTES as int),
    cast(THRUPUT04RBID as int),
    cast(THRUPUT04EPSBEARID as int),
    cast(THRUPUT04QCI as int),
    cast(THRUPUT04TXDATATOSGBYTES as int),
    cast(THRUPUT04RXDATAFROMSGBYTES as int),
    cast(THRUPUT04TXPACKETSTOUE as int),
    cast(THRUPUT04DLPACKETDROPRATE as int),
    cast(THRUPUT04TXPACKETSTOSG as int),
    cast(THRUPUT04ULPACKETDROPRATE as int),
    cast(THRUPUT04TXDATATOUEBYTES as int),
    cast(THRUPUT04RETXDATATOUEBYTES as int),
    cast(THRUPUT04RXDATAFROMUEBYTES as int),
    cast(THRUPUT05RBID as int),
    cast(THRUPUT05EPSBEARID as int),
    cast(THRUPUT05QCI as int),
    cast(THRUPUT05TXDATATOSGBYTES as int),
    cast(THRUPUT05RXDATAFROMSGBYTES as int),
    cast(THRUPUT05XPACKETSTOUE as int),
    cast(THRUPUT05DLPACKETDROPRATE as int),
    cast(THRUPUT05TXPACKETSTOSG as int),
    cast(THRUPUT05ULPACKETDROPRATE as int),
    cast(THRUPUT05TXDATATOUEBYTES as int),
    cast(THRUPUT05RETXDATATOUEBYTES as int),
    cast(THRUPUT05RXDATAFROMUEBYTES as int),
    cast(THRUPUT06RBID as int),
    cast(THRUPUT06EPSBEARID as int),
    cast(THRUPUT06QCI as int),
    cast(THRUPUT06TXDATATOSGBYTES as int),
    cast(THRUPUT06RXDATAFROMSGBYTES as int),
    cast(THRUPUT06TXPACKETSTOUE as int),
    cast(THRUPUT06DLPACKETDROPRATE as int),
    cast(THRUPUT06TXPACKETSTOSG as int),
    cast(THRUPUT06ULPACKETDROPRATE as int),
    cast(THRUPUT06TXDATATOUEBYTES as int),
    cast(THRUPUT06RETXDATATOUEBYTES as int),
    cast(THRUPUT06RXDATAFROMUEBYTES as int),
    cast(THRUPUT07RBID as int),
    cast(THRUPUT07EPSBEARID as int),
    cast(THRUPUT07QCI as int),
    cast(THRUPUT07TXDATATOSGBYTES as int),
    cast(THRUPUT07RXDATAFROMSGBYTES as int),
    cast(THRPUT07TXPACKETSTOUE as int),
    cast(THRUPUT07DLPACKETDROPRATE as int),
    cast(THRUPUT07TXPACKETSTOSG as int),
    cast(THRUPUT07ULPACKETDROPRATE as int),
    cast(THRUPUT07TXDATATOUEBYTES as int),
    cast(THRUPUT07RETXDATATOUEBYTES as int),
    cast(THRUPUT07RXDATAFROMUEBYTES as int),
    cast(RF_TXPOWER as int),
    cast(RF_UETXPOWERDBM as int),
    cast(RF_RSRP as int),
    cast(RF_RSRPDBM as float),
    cast(RF_RSRQ as int),
    cast(RF_RSRQDB as float),
    cast(NBHD_CELLCOUNT as int),
    cast(NBHD_CGI01PLMNID as int),
    cast(DIMRANCELLSECTORID_NBHDCGI01 as float),
    cast(NBHD_CGI01PCI as int),
    cast(NBHD_CGI01RSRP as int),
    cast(NBHD_CGI01RSRPDBM as float),
    cast(NBHD_CGI01RSRQ as int),
    cast(NBHD_CGI01RSRQDB as float),
    cast(NBHD_CGI01UNKNOWN as int),
    cast(NBHD_CGI02PLMNID as int),
    cast(DIMRANCELLSECTORID_NBHDCGI02 as int),
    cast(NBHD_CGI02PCI as int),
    cast(NBHD_CGI02RSRP as int),
    cast(NBHD_CGI02RSRPDBM as float),
    cast(NBHD_CGI02RSRQ as int),
    cast(NBHD_CGI02RSRQDB as float),
    cast(NBHD_CGI02UNKNOWN as int),
    cast(NBHDCGI03PLMNID as int),
    cast(DIMRANCELLSECTORID_NBHDCGI03 as float),
    cast(NBHD_CGI03PCI as int),
    cast(NBHD_CGI03RSRP as int),
    cast(NBHD_CGI03RSRPDBM as float),
    cast(NBHD_CGI03RSRQ as int),
    cast(NBHD_CGI03RSRQDB as float),
    cast(NBHDCGI03UNKNOWN as int),
    cast(UEHIST_COUNT as int),
    cast(UEHIST_CGI01PLMNID as int),
    cast(DIMRANCELLSECTORIDUEHISTCGI01 as int),
    cast(UEHIST_CGI01CELLSIZE as int),
    cast(UEHISTCGI01STAYTIMESEC as int),
    cast(UEHIST_CGI02PLMNID as int),
    cast(DIMRANCELLSECTORID_UEHISTCGI02 as int),
    cast(UEHIST_CGI02CELLSIZE as int),
    cast(UEHIST_CGI02STAYTIMESEC as int),
    cast(UEHIST_CGI03PLMNID as int),
    cast(DIMRANCELLSECTORID_UEHISTCGI03 as int),
    cast(UEHIST_CGI03CELLSIZE as int),
    cast(UEHIST_CGI03STAYTIMESEC as int),
    cast(UEHIST_CGI04PLMNID as int),
    cast(DIMRANCELLSECTORID_UEHISTCGI04 as int),
    cast(UEHIST_CGI04CELLSIZE as int),
    cast(UEHIST_CGI04STAYTIMESEC as int),
    cast(UEHIST_CGI05PLMNID as int),
    cast(DIMRANCELLSECTORID_UEHISTCGI05 as int),
    cast(UEHIST_CGI05CELLSIZE as int),
    cast(UEHIST_CGI05STAYTIMESEC as int),
    cast(CALLDBG_STATECMD as int),
    cast(CALLDBG_STATE as int),
    CALLDBG_STATECMDESCRIPTION,
    cast(CALLDBG_MSGID as int),
    cast(CALLDBG_SEQUENCE as int),
    cast(CALLDBG_CALLID as int),
    cast(CALLDBG_IMSI as int),
    cast(CALLDBG_CRNTI as int),
    cast(UNKNOWN as int),
    cast(LATITUDE as int),
    cast(LONGITUDE as int),
    FIXEDSPATIALGRIDID,
    GEOLOCATIONCD,
    REFERENCE,
    SKU_NUMBER,
    BAN_NBR,
    SRC_OWNER_CD,
    OS_CR_CLASS_CD,
    PRICE_PLAN_CD,
    STUS_CD,
    20141104
    FROM wrd."""+options.staging_table_name
    ,conn) 
    log.info("Inserted rows- "+str(r["UpdateCount"]))
    
    # 5. Move old partitions to coldstorage
    log.info("Move old partitions to coldstorage")
    # by default it will count 3 days from sysdate as hot data and everything rest is cold
    move_partitions_to_coldstorage(vertica_db,table_name=options.fact_table_name,as_of_period="D",as_of_number=3)
    
finally:
    log.debug("Closing vertica connection..")
    conn.close()