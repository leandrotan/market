from pyspark import SparkContext
from pyspark.sql import HiveContext,Row
from pyspark.storagelevel import StorageLevel

if __name__ == "__main__":
	sc = SparkContext(appName="TweetsConversationHierarchyBuilder")
	sqlContext = HiveContext(sc)

	toplevel = sqlContext.sql("""
		select distinct tt.id,tt.in_reply_to_status_id from 
		(select t.id,t.in_reply_to_status_id
		  from wrd10_socialmedia.fact_orc_raw_tweets t 
		  join wrd10_socialmedia.fact_orc_raw_tweets t1 on t.id = t1.in_reply_to_status_id 
		 where t.in_reply_to_status_id is null
		   and t.country='Nigeria'
		   and t1.country='Nigeria'
		 union all
		 select t2.id,t2.in_reply_to_status_id
		  from wrd10_socialmedia.fact_orc_raw_twitter_userstream t2,
		  wrd10_socialmedia.fact_orc_raw_twitter_userstream t3,
		  wrd10_socialmedia.fact_orc_raw_tweets t4
		 where t2.in_reply_to_status_id is null and ((t2.id = t3.in_reply_to_status_id) or (t2.id=t4.in_reply_to_status_id))
		   and t2.country='Nigeria'
		   and t3.country='Nigeria'
		   and t4.country='Nigeria'
		) tt
	""").persist(StorageLevel.MEMORY_AND_DISK)
	replies = sqlContext.sql("""
		select distinct tt.id,tt.in_reply_to_status_id,tt.text,tt.user_name,tt.created_at,tt.country from
		(select t.id,t.in_reply_to_status_id,t.text,t.user_name,t.created_at,t.country
		  from wrd10_socialmedia.fact_orc_raw_tweets t 
		 where t.in_reply_to_status_id is not null
		   and t.country='Nigeria'
		 union all
		 select t1.id,t1.in_reply_to_status_id,t1.text,t1.user_name,t1.created_at,t1.country
		  from wrd10_socialmedia.fact_orc_raw_twitter_userstream t1
		 where t1.in_reply_to_status_id is not null
		   and t1.country='Nigeria'
		) tt
	""").persist(StorageLevel.MEMORY_AND_DISK)

	Level = 1
	repliesReverted = replies.map(lambda row: (row.in_reply_to_status_id, row)).coalesce(30).persist(StorageLevel.MEMORY_AND_DISK)
	nLevel = repliesReverted.join(toplevel.map(lambda row: (row.id, row))).coalesce(30).persist(StorageLevel.MEMORY_AND_DISK)
	# nLevel RDD format:
	# key: parent.id
	# value: (child, (parent, top))
	nLevel = nLevel.map(lambda t: (t[0],(t[1][0],(t[1][1],t[1][1])))).coalesce(30).persist(StorageLevel.MEMORY_AND_DISK)
	def setTreeStruct(t):
		child = t[1][0]
		parent = t[1][1][0]
		top = t[1][1][1]
		return_row = Row(id=child.id,
						 in_reply_to_status_id=child.in_reply_to_status_id,
						 text = child.text,
						 user_name = child.user_name,
						 created_at = child.created_at,
						 parent=top.id,
						 level=Level,
						 country=child.country)
		return (child.id,return_row)
		
	tree = nLevel.map(setTreeStruct).cache()
	#call first() to init tree RDD before the loop
	tree.first()

	def revertLevel(t):
		# set new child.id as a key
		child = t[1][0]
		parent = t[1][1][0]
		top = t[1][1][1]
		return (child.id,(child,top))	

	level_count = nLevel.count()
	
	while(level_count>0):
		# set new child.id as a RDD key to join with replies and get new level rdd
		topReverted = nLevel.map(revertLevel)
		nLevel = repliesReverted.join(topReverted).cache()
		level_count = nLevel.count()
		if (level_count!=0):
			Level = Level+1
			tree = tree.union(nLevel.map(setTreeStruct)).cache()
	
	tree.values().map(lambda row: str(row.id)+"\t"+str(row.in_reply_to_status_id)+"\t"+row.created_at+"\t"+row.user_name+"\t"+row.text+"\t"+str(row.parent)+"\t"+str(row.level)+"\t"+row.country).coalesce(4).saveAsTextFile("hdfs://nex-hdp-14:8020/user/mykhail.martsynyuk/HierarchyResult2")
	#sc.stop()
