 #/usr/bin/shell
 /usr/lib/flume/bin/flume-ng agent -c /usr/lib/flume/conf -f /home/sm_user/Social_Media/Facebook/conf/flumeFacebook.properties -n FacebookAgent --classpath /home/sm_user/Social_Media/Facebook/lib/*: -Dflume.root.logger=DEBUG,LOGFILE -Dflume.log.file=FlumeFacebookAgent.log
