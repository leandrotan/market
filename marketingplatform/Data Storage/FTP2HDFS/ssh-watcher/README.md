=======
ssh-spool-source
================

Prototype SshSpoolSource for Flume - think Spooling Directory Source over SSH. Caveat Emptor: It is very much **pre-alpha**.

Semantics
---------
The SshSpoolSource mirrors many semantics from the SpoolingDirectorySource. Here's what it supports at the moment:
- SSH authorization using username/password (ssh-keys not supported yet)
- Can specify a remote directory to monitor for new files added
- Any new files added in there are considered complete and ingested
- Once a file is processed, it is considered complete, changes to it will not be picked up
- The source persists the state of processed files to disk, so it will not reprocess any files; and it can pickup where it left off in the event of a restart. 


Configuring Flume
------------------

1. **Build or Download the custom Flume Source**

   The `flume-sources` directory contains a Maven project with a custom Flume source designed to connect to the specified SSH remote path and ingest the contents of the files there into HDFS.

   To build the flume-sources JAR, from the root of the git repository:
   
	   $ cd flume-sources  
	   $ mvn package
	   $ cd ..  


   This will generate a file called `flume-sources-1.0-SNAPSHOT.jar` in the `target` directory.

2. **Add the JAR to the Flume classpath**

   <pre>$ sudo cp /etc/flume-ng/conf/flume-env.sh.template /etc/flume-ng/conf/flume-env.sh</pre>
   
    Edit the `flume-env.sh` file and uncomment the `FLUME_CLASSPATH` line, and enter the path to the JAR. If adding multiple paths, separate them with a colon.

3. **Set the Flume agent name to SshAgent in /etc/default/flume-ng-agent**

    If you don't see the `/etc/default/flume-ng-agent` file, it likely means that you didn't install the `flume-ng-agent` package. In the file, you should have the following:

    <pre>FLUME_AGENT_NAME=SshAgent</pre>

4. **Modify the provided Flume configuration and copy it to /etc/flume-ng/conf**

   There is a file called `flume.conf` in the `flume-sources` directory, which needs some minor editing. There are five fields which need to be filled in with values.

   <pre>$ sudo cp flume.conf /etc/flume-ng/conf</pre>


Starting the data pipeline
------------------------

1. **Start the Flume agent**

    Create the HDFS directory hierarchy for the Flume sink.  
    
    <pre>
    $ hadoop fs -mkdir /user/flume/ssh
    $ hadoop fs -chown -R flume:flume /user/flume/ssh
    $ hadoop fs -chmod -R 770 /user/flume
    $ sudo /etc/init.d/flume-ng-agent start
    </pre>
