#/usr/bin/shell
/etc/init.d/flume-agent $1 /home/sm_user/Social_Media/Instagram/conf/flumeInstagramFeeds.properties -n InstagramAgent --classpath /home/sm_user/Social_Media/Instagram/lib/*: -Dflume.root.logger=INFO,LOGFILE -Dflume.log.file=InstagramFlumeAgent.log -Xmx256m
