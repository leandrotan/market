'''
Created on Sep 4, 2014

@author: Mykhail Martsynyuk
'''
import subprocess, re
import logging
from org.apache.hadoop.conf import Configuration
from org.apache.hadoop import fs
logger = logging.getLogger(__name__)

class HadoopShellException(Exception):
    """Raised in case some exception occurs during hadoop shell command execution"""

#TODO: Rewrite class using hadoop java lib api
class HadoopFS(object):
    '''
    Provides utility methods for hdfs
    '''

    def __init__(self):
        '''
        Init hadoop filesystem objects
        '''
        logger.debug("Initiate hadoop.fs.FileSystem object")
        self.config = Configuration()
        self.file_system = fs.FileSystem.get(self.config)
    
    def get_space_total(self):
        '''
        Returns total space in bytes
        '''
        logger.info("Run shell command: hadoop fs -df")
        
        try:
            p = subprocess.Popen(["hadoop", "fs", "-df"], stdout=subprocess.PIPE)
            out, err = p.communicate()
            logger.info("Command output: " + out)
            return int(re.split(r"\s+",out.splitlines()[1])[1])
        except:
            logger.error("Error during shell command execution")
            raise HadoopShellException(err)
    
    def get_space_free(self):
        '''
        Returns available space in bytes
        '''
        logger.info("Run shell command: hadoop fs -df")
        
        try:
            p = subprocess.Popen(["hadoop", "fs", "-df"], stdout=subprocess.PIPE)
            out, err = p.communicate()
            logger.info("Command output: " + out)
            return int(re.split(r"\s+",out.splitlines()[1])[3])
        except:
            logger.error("Error during shell command execution")
            raise HadoopShellException(err)
        
    def move_folder_to_backup(self, path):
        '''
        Moves folder content to "folder_name"+"_old"
        '''
        # remove old folder first
        logger.debug("Removing old folder")
        old_path = path+"_old"
        self.remove_folder(old_path)
        
        logger.debug("Run shell command: hadoop fs -cp "+path+" "+old_path)
        
        try:
            p = subprocess.Popen(["hadoop", "fs", "-cp", path, old_path], stdout=subprocess.PIPE)
            out, err = p.communicate()
            logger.info("Command output: " + out)
        except:
            logger.error("Error during shell command execution")
            raise HadoopShellException(err)
        
        
    def remove_folder(self, path):
        '''
        Removes folder recursively
        '''
        logger.debug("Run shell command: hadoop fs -rmr "+path)
        
        try:
            p = subprocess.Popen(["hadoop", "fs", "-rmr", path], stdout=subprocess.PIPE)
            out, err = p.communicate()
            logger.info("Command output: " + out)
        except:
            logger.error("Error during shell command execution")
            raise HadoopShellException(err)
        
    def is_path_exists(self, path):
        hPath = fs.Path(path)
        if self.file_system.exists(hPath):
            logger.debug("Path exists: " + path)
            return True
        return False