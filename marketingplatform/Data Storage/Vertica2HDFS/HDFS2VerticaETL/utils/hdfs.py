'''
Created on Nov 11, 2014

@author: Mykhail Martsynyuk
'''
from org.apache.hadoop.conf import Configuration
from org.apache.hadoop import fs
import logging, re
log = logging.getLogger(__name__)

class HadoopFS(object):
    '''
    Represents hadoop fs object from org.apache.hadoop lib
    '''
    def __init__(self):
        '''
        Constructor
        '''
        self.config = Configuration()
        self.file_system = fs.FileSystem.get(self.config)
        
    def is_path_exists(self, path):
        hPath = fs.Path(path)
        if self.file_system.exists(hPath):
            return True
        return False
    
    def get_files_in_path(self, path, min_time=None, max_time=None, file_name_regex = None):
        '''
        Lists files in hdfs folder.
        Allows to use filter by modification_time and by file name
        '''
        def is_passed_mod_time_check(fStatus):
            if fStatus.getModificationTime() >= min_time and fStatus.getModificationTime() <= max_time:
                return True
            else:
                return False
            
        def is_passed_file_name_regex_check(fStatus):
            file_name = fStatus.getPath().getName()
            regex = re.compile(file_name_regex, re.IGNORECASE)
            if regex.search(file_name):
                return True
            else:
                return False
        
        log.debug("Getting files in path input parameters- "+path+"; "+str(min_time)+ "; "+ str(max_time))
        hPath = fs.Path(path)
        file_statuses = self.file_system.listStatus(hPath)
        res = []
        for f_status in file_statuses:
            if f_status.isDirectory():
                continue
            if min_time and max_time:
                if not is_passed_mod_time_check(f_status):
                    continue
            if file_name_regex:
                if not is_passed_file_name_regex_check(f_status):
                    continue
            log.debug("Found file status- "+str(f_status))
            res.append(str(f_status.path))
        return res
    
    def get_stream_from_file(self, file_path):
        '''
        Opens input stream based on file URI
        '''
        return self.file_system.open(fs.Path(file_path))