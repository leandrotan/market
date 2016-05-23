'''
Created on Nov 11, 2014

@author: Mykhail Martsynyuk
'''
from com.vertica.jdbc import DataSource,VerticaCopyStream
import logging
log = logging.getLogger(__name__)

class VerticaDB(object):
    '''
    Represents Vertica JDBC connection
    '''
    def __init__(self, connection_string=None):
        '''
        Provides base object for vertica database connection
        '''
        self.ds = DataSource()
        if not connection_string:
            self.ds.setHost("10.104.3.26")
            self.ds.setPort(5433)
            self.ds.setDatabase("verticadst")
            self.ds.setUserID("wrd_user")
            self.ds.setPassword("xpl123")
        else:
            self.ds.setURL(connection_string)
            
    def get_connection(self):
        self.connection = self.ds.getConnection()
        return self.connection
    
    def exec_single_stmt(self, sql, connection=None):
        '''
        Returns dict of <List>result,<Long>UpdatedRowsCount
        In case some dml was executed result is boolean
        '''
        if not connection:
            connection = self.connection
        log.debug("Executing sql- "+sql)
        Result = None
        updated_rows_count = None
        stmt = connection.createStatement()
        if sql.strip()[0:6].upper()=="SELECT":
            res = stmt.executeQuery(sql)
            ResultSet = []
            meta = res.getMetaData()
            col_count = meta.getColumnCount()
            log.debug("Columns count- "+ str(col_count))
            while res.next():
                row = {}
                for i in range(1,col_count+1):
                    row[meta.getColumnName(i)]=res.getString(i)
                ResultSet.append(row)
            Result = ResultSet
        else:
            res = stmt.execute(sql)
            updated_rows_count = stmt.getUpdateCount()
            Result = res
        stmt.close()
        return {"Result":Result,"UpdateCount":updated_rows_count}

class Copy(object):
    '''
    Run vertica COPY statement and provides api to get results and rejections
    '''
    def __init__(self, stmt, connection):
        log.debug("Init Copy statement")
        self.conn = connection
        self.conn.setAutoCommit(False)
        self.updated_rows_count = None
        self.rejects = None
        self.copy_stream = VerticaCopyStream(connection, stmt)
        self.copy_stream.start()
        
    def run(self):
        log.debug("Run Copy statement. Ensure you added streams before")
        self.copy_stream.execute()
        self.rejects = self.copy_stream.getRejects()
        self.updated_rows_count = self.copy_stream.finish()
        log.debug("Copy statement finished")
        
    def get_updated_rows_count(self):
        return self.updated_rows_count
    
    def get_rejections(self):
        '''
        Return array of rejected row numbers
        '''
        return self.rejects
    
    def add_filestream(self, stream):
        '''
        Adds stream to started copy command.
        To get stream from hdfs URI use HadoopFS().get_stream_from_file method
        '''
        self.copy_stream.addStream(stream)

