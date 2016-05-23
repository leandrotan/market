'''
Created on Sep 2, 2014

@author: Mykhail Martsynyuk

# Sample usage:
print (VerticaDB().get_table_size("fact"))
'''
from com.vertica.jdbc import *
import logging
logger = logging.getLogger(__name__)

class NoTableExistsException(Exception):
    """Raised in case vertica table doesnt exists"""

#TODO: wrap this as ContextManager to manage closing connection efficiently
class VerticaDB(object):
    '''
    Provides necessary utility methods
    '''
    def __init__(self):
        '''
        Constructor
        '''
        self.ds = DataSource()
        self.ds.setHost("10.104.5.29")
        self.ds.setPort(5433)
        self.ds.setDatabase("verticadst")
        self.ds.setUserID("alfxplsit")
        self.ds.setPassword("xpl123")
        logger.debug("Connecting to vertica db...")
        self.connection = self.ds.getConnection()
        logger.debug("Connected!")
        
    def __del__(self):
        self.connection.close()

    def get_table_size (self, table_name):
        '''
        Returns table size in bytes
        '''
        logger.debug("Getting table "+table_name+ " size...")
        sql = """
        SELECT SUM(USED_BYTES)
          FROM PROJECTION_STORAGE WHERE ANCHOR_TABLE_NAME = ?
           AND ANCHOR_TABLE_SCHEMA=?
           AND PROJECTION_NAME like '%b0'
        """
        logger.debug("SQL generated: "+ """
        SELECT SUM(USED_BYTES)
          FROM PROJECTION_STORAGE WHERE ANCHOR_TABLE_NAME = '""" + table_name + """'
           AND ANCHOR_TABLE_SCHEMA= '""" + self.ds.getUserID() + """'
           AND PROJECTION_NAME like '%b0'
        """)
        stmt = self.connection.prepareStatement(sql)
        stmt.setString(1, table_name)
        stmt.setString(2, self.ds.getUserID())
        rs = stmt.executeQuery()
        table_size = 0
        if rs.next():
            table_size = rs.getLong(1)
        stmt.close()
        return table_size
    
    def accert_table_exists(self, table_name):
        '''
        Throws exception in case table doesn't exists
        '''
        logger.debug("Accert table "+table_name+ " is exists...")
        sql = """
        select count(*) from tables where table_name = ? 
        """
        stmt = self.connection.prepareStatement(sql)
        stmt.setString(1, table_name)
        rs = stmt.executeQuery()
        is_table_exists = 0
        if rs.next():
            is_table_exists = rs.getInt(1)
        stmt.close()
        if is_table_exists ==0:
            raise NoTableExistsException("Table "+table_name+" doesn't exists")