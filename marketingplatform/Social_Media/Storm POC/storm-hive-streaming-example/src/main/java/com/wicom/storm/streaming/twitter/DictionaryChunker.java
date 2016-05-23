package com.wicom.storm.streaming.twitter;
import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.MapDictionary;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

public class DictionaryChunker {

    static final double CHUNK_SCORE = 1.0;
    static ExactDictionaryChunker dictionaryChunkerTT; // AllMatches = True, CaseSensitive = True
    static ExactDictionaryChunker dictionaryChunkerTF;// AllMatches = True, CaseSensitive = False
    static ExactDictionaryChunker dictionaryChunkerFT;// AllMatches = False, CaseSensitive = True
    static ExactDictionaryChunker dictionaryChunkerFF;// AllMatches = False, CaseSensitive = False
    
    public DictionaryChunker() {
    		initDictionary();	

    	}

    public static void initDictionary() {
    	// here is where to define the dictionary

        MapDictionary<String> dictionary = new MapDictionary<String>();

        dictionary.addEntry(new DictionaryEntry<String>("Internet","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("wired","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("data","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("broadband","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("router","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("switch router","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("offline","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("download","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Upload","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("speed","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("4G","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("3G","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("LTE","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("connection","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("disconnection","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("signal","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("GPRS","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("EDGE","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("offline","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("WIFI","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("megabytes","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Mega Bytes","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("MB","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("gigabyte","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Giga Byte","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("GB","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Network","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("call","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("voice","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("drop call","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("breaking up","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("breaking","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Coverage","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("system down","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("service","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("No Service","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("voicemail","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("voice mail","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("sms","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("text","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("signal","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("signal problem","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("RBT","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Ring Back Tone","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("chat","Network / Other Network Complaints",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Customer Service","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Waiting time","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("queue","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Time","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Wait","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("business customer","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("help","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("assistance","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("waste of time","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("unresolved","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("not working","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("not operational","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("resolve","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("issue","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("subscriber","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("contract","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("friendly","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("rude","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("complaint","Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Phone","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Mobile","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("iphone","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("HTC","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("LG","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("IOS","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Android","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("handset","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("samsung","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("sony","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("shop","Business Affairs / Shops",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("mall","Business Affairs / Shops",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("ticket number","Business Affairs / Shops",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("bill","Business Affairs / Billing Complaint",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("charge","Business Affairs / Billing Complaint",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("pay","Business Affairs / Billing Complaint",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("money","Business Affairs / Billing Complaint",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("refund","Business Affairs / Billing Complaint",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("credit","Business Affairs / Billing Complaint",CHUNK_SCORE));



         dictionaryChunkerTT
            = new ExactDictionaryChunker(dictionary,
                                         IndoEuropeanTokenizerFactory.INSTANCE,
                                         true,true);

         dictionaryChunkerTF
            = new ExactDictionaryChunker(dictionary,
                                         IndoEuropeanTokenizerFactory.INSTANCE,
                                         true,false);

         dictionaryChunkerFT
            = new ExactDictionaryChunker(dictionary,
                                         IndoEuropeanTokenizerFactory.INSTANCE,
                                         false,true);

         dictionaryChunkerFF
            = new ExactDictionaryChunker(dictionary,
                                         IndoEuropeanTokenizerFactory.INSTANCE,
                                         false,false);

    }

    public static void chunk(ExactDictionaryChunker chunker, String text) {
        System.out.println("\nChunker."
                           + " All matches=" + chunker.returnAllMatches()
                           + " Case sensitive=" + chunker.caseSensitive());
        Chunking chunking = chunker.chunk(text);
        for (Chunk chunk : chunking.chunkSet()) {
            int start = chunk.start();
            int end = chunk.end();
            String type = chunk.type();
            double score = chunk.score();
            String phrase = text.substring(start,end);
            System.out.println("     phrase=|" + phrase + "|"
                               + " start=" + start
                               + " end=" + end
                               + " type=" + type
                               + " score=" + score);
        }
    }
    
    public static void getEntityAndCategory(ExactDictionaryChunker chunker, String text) {

        Chunking chunking = chunker.chunk(text);
        String entityInfo = "";
        for (Chunk chunk : chunking.chunkSet()) {
            int start = chunk.start();
            int end = chunk.end();
            String type = chunk.type();
            String phrase = text.substring(start,end);
            entityInfo = entityInfo + "     entity=|" + phrase + "|"
                    + " type=" + type + ";" ;
        }
        System.out.println(entityInfo);
    }
    
    public static String getEntity(ExactDictionaryChunker chunker, String text) {

        Chunking chunking = chunker.chunk(text);
        String entityInfo = "";
        for (Chunk chunk : chunking.chunkSet()) {
            int start = chunk.start();
            int end = chunk.end();
            String type = chunk.type();
            String phrase = text.substring(start,end);
            if (entityInfo.length() ==0)
            	entityInfo = entityInfo + phrase;
            else entityInfo = entityInfo + " | " + phrase;
        }
        //System.out.println(entityInfo);
        return entityInfo;
    }
    
    public static String getEntityCategory(ExactDictionaryChunker chunker, String text) {

        Chunking chunking = chunker.chunk(text);
        String entityInfo = "";
        for (Chunk chunk : chunking.chunkSet()) {
            int start = chunk.start();
            int end = chunk.end();
            String type = chunk.type();
            String phrase = text.substring(start,end);
            if (entityInfo.length() ==0)
            	entityInfo = entityInfo + type;
            else entityInfo = entityInfo + " | " + type;
        }
        //System.out.println(entityInfo);
        return entityInfo;
    }

}
