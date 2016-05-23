'''
Created on Nov 11, 2014

@author: Mykhail Martsynyuk
'''
from log import RowLog
import logging

class HBaseHandler(logging.Handler):
    """
    A handler class which writes logging records, appropriately formatted,
    to a HBase log table.
    """
    def __init__(self, tbl_name="log", cf="VSQL"):
        """
        Initialize the handler.
        """
        logging.Handler.__init__(self)
        self.formatter = None
        self.Log = RowLog()
        self.table = tbl_name
        self.cf = cf

    def flush(self):
        """
        Nothing here
        """
        pass

    def emit(self, record):
        """
        Emit a record.

        If exception information is present, it is formatted using
        traceback.print_exception and appended to the stream.
        """
        # Generate string acceptable by LogRow module
        #
        msg = "PID:"+record.name+",MsgType:"+record.levelname+",Msg:"+record.msg
        try:
            self.Log.logRow(self.table, self.cf, msg)
        except:
            self.handleError(record)