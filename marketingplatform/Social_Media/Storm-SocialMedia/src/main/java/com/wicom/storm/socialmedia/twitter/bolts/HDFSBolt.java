package com.wicom.storm.socialmedia.twitter.bolts;

/**
 * Created by Charbel Hobeika on 5/14/2015.
 */
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.wicom.storm.socialmedia.twitter.utils.GenericConstants;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSBolt extends BaseRichBolt
{
    private static final long serialVersionUID = 42L;
    private OutputCollector collector;
    private Integer _tweetBatch;
    private String day_key;
    private String _hdfsfileName;
    private String _hdfsfileDirectory;
    private List<String> tweet_scores;

    @SuppressWarnings("rawtypes")
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector)
    {
        //System.out.print(stormConf.get(GenericConstants.HDFS_TWEETS_BATCH));
        this._tweetBatch = Integer.valueOf(stormConf.get(GenericConstants.HDFS_TWEETS_BATCH).toString());

        this.tweet_scores = new ArrayList<String>(this._tweetBatch);
        this._hdfsfileName = (String) stormConf.get(GenericConstants.HDFS_TWEETS_FILENAME);
        this._hdfsfileDirectory = (String) stormConf.get(GenericConstants.HDFS_TWEETS_DIRECTORY);
    }

    public void execute(Tuple input)
    {
        //String tweetJSON = (String) input.getValueByField("tweet");
        String COUNTRY = "country="+(String) input.getValueByField("country");

        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        this.day_key="day_key="+df.format(new Date());
        this.tweet_scores.add((String) input.getValueByField("tweet"));

        if (this.tweet_scores.size() >= _tweetBatch)
        {
            writeToHDFS(COUNTRY, this.day_key);
            this.tweet_scores = new ArrayList<String>(_tweetBatch);
        }
    }

    private void writeToHDFS(String _country, String _dayKey)
    {
        FileSystem hdfs = null;
        Path fileDirectory = null;
        OutputStream os = null;
        BufferedWriter wd = null;

        try
        {

            Configuration conf = new Configuration();
            conf.addResource(new Path("/src/main/resources/core-site.xml"));
            conf.addResource(new Path("/src/main/resources/hdfs-site.xml"));
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
            hdfs = FileSystem.get(conf);

            fileDirectory = new Path(this._hdfsfileDirectory+_country+"/"+_dayKey);
            if(!hdfs.exists(fileDirectory))
            {
                hdfs.mkdirs(fileDirectory);
            }

            Path path1 = new Path(fileDirectory+"/"+this._hdfsfileName);
            hdfs.createNewFile(path1);

            if( hdfs.exists( path1 ) )
            {
                System.out.println("Writting to HDFS: "+path1);
                Path tmpPath = new Path( fileDirectory+"/"+"tmp_"+this._hdfsfileName );

                BufferedReader br = new BufferedReader( new InputStreamReader( hdfs.open( path1 ) ) );  // Open old file for reading
                BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( hdfs.create( tmpPath , true ) ) );

                String line = br.readLine(); // Read first line of file
                while ( line != null )
                {
                    bw.write( line ); // Write line from old file to new file
                    bw.newLine();  // Writes a line separator.
                    line = br.readLine(); // Read next line of the file
                }

                for (String tweet_score : tweet_scores)
                {
                    bw.write(tweet_score);
                    bw.newLine(); // Writes a line separator
                }

                br.close(); // Closes the stream and releases any system resources associated with it.
                bw.close(); // Closes the stream and releases any system resources associated with it.
                hdfs.delete( path1, true ); // Delete old file
                hdfs.rename( tmpPath , path1 );  // Rename new file to old file name
            }
            hdfs.close(); // No more filesystem operations are needed. Will release any held locks
        }
        catch (IOException ex)
        {
            System.out.println("Failed to write tweets to HDFS under: " + ex.getMessage());

        }
        finally
        {
            try
            {
                if (os != null) os.close();
                if (wd != null) wd.close();
                if (hdfs != null) hdfs.close();
                System.out.println("Connection closed");
            }
            catch (IOException ex)
            {
                System.out.println("IO Exception thrown while closing HDFS: "+ ex.getMessage());
            }
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) { }
}