package com.sentiment;
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
    static MapDictionary<String> dictionary;
    public DictionaryChunker() {
    		initDictionary();	

    	}

    public static void initDictionary() {
    	// here is where to define the dictionary
    	
        dictionary = new MapDictionary<String>();
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
        dictionary.addEntry(new DictionaryEntry<String>("Coverage","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("system down","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("service","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("No Service","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("voicemail","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("voice mail","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("sms","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("text","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("signal","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("signal problem","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("RBT","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Ring Back Tone","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("chat","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Customer Service","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Waiting time","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("queue","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Time","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Wait","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("business customer","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("help","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("assistance","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("waste of time","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("unresolved","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("not working","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("not operational","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("resolve","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("issue","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("subscriber","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("contract","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("friendly","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("rude","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("complaint","Customer Care / Customer Care",CHUNK_SCORE));
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
        dictionary.addEntry(new DictionaryEntry<String>("bill","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("charge","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("pay","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("money","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("refund","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("credit","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("ابراج ارسال","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("النت","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("لينك","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("USB","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("داتا","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الانترنت","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الداون لود","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("ميجا","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الدونلود","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("ADSL","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("h+","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("e","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("edge","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("جيجا","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("التغطية","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("المزود","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الدي اس ال","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("السيجنال","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("MB","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("جهاز التوجيه","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("تحميل","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("سرعة","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("إشارة","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("GPRS","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("4G","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>(" النت","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("بتفصل","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("H+","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الشبكه بتطلع وتنزل","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>(" موبيل انترنت","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("انترنت","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("نت","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الفلاشة","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("فلاشة","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الواتس اب","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("LTE","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("كونكت","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("internet","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("data","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("السرعه","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("سرعه","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("بايتس","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الميجا","Network / Data",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("ابراج ارسال","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الشبكات","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الاتصال","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الشبكه","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("التغطية","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("السيجنال","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("اتصل","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("المكالمه","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("لشبكة واقعة","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("مكالمه","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("بيتصل","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("بيستقبل","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("مكالمات","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("مسج","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("بتستقبل","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("بتتصل","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("غير متاح","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("دقايق","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("المكالمات","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الكول","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("المكالمة","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("calls","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("اتصال","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("يرن","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("يقطع","Network / Voice",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("ابراج ارسال","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الشبكات","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الشبكه","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("التغطية","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("السيجنال","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الشبكة","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>(" رساله","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("للتغطية","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("شريحة","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("التغطية","Network / Other Network",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("خدمه العملاء","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("خدمة العملا","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("كول سنتر","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("تواصلت","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("العملاء","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("العملا","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("استفسار","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("سؤال","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("customer","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("عميل","Customer Care / Customer Care",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الفون","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الجوال","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الموبايل","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("موبيله","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الأيفون","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("سامسونج","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("سوني","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>(" xperia","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("لوميا","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("تليفوني","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("آبل","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("فون","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("application","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("موبايل","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("التليفون","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الموبيل","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("موبيل","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("موبايلي","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("phone","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("mobile","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("ايفون","Business Affairs / Handset",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الفرع","Business Affairs / Shops",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("فرع","Business Affairs / Shops",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("كونتر","Business Affairs / Shops",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("فروع","Business Affairs / Shops",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الرصيد","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("اشحن","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("فلوس","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("رصيدي","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("تحويل الرصيد","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("فتوره","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("بتشحن","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("رصيد","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("نصب","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("السرقة","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("تجديد الباقه","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("شاحن","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("بتكلفنى كتير","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("حتدفعو الفلوس","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("بيحسبه","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("جنيه","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("خصم","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("جنية","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الفاتورة","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("قرش","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الشحن","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("ادفع","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الفاتوره","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("كاش","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("اسعار","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الاسعار","Business Affairs / Pricing or Billing",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("خدمة","Business Affairs / Other Business Affairs",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الكود","Business Affairs / Other Business Affairs",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الرقم","Business Affairs / Other Business Affairs",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("باقات","Business Affairs / Other Business Affairs",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("خدمات","Business Affairs / Other Business Affairs",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("الباقه","Business Affairs / Other Business Affairs",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("عرض","Business Affairs / Other Business Affairs",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("service","Business Affairs / Other Business Affairs",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("العرض","Business Affairs / Other Business Affairs",CHUNK_SCORE));
        
        
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
