#!/bin/bash
/etc/init.d/flume-agent $1 /home/sm_user/Social_Media/Twitter/conf/flumeTwitter_egypt.properties TwitterAgent --classpath /home/sm_user/Social_Media/Twitter/lib/*: -Dflume.root.logger=INFO,LOGFILE -Dflume.log.file=FlumeTwitterAgent_EGYPT.log