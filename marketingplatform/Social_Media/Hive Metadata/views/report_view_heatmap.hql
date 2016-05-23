create or replace
 view report_view_heatmap as
select 
network_count*100/total as Bad_Sentiment,
network_count*100/total as Network_Bad_Sentiment,
CC_count*100/total  as CustomerCare_Bad_Sentiment,
BA_count*100/total as BusinessAffairs_Bad_Sentiment,
tweet_count,csp, Category, q.day_key,isMain,region,sentiment,social_media_source
from
(select 
count(distinct case when category like '%Network%' then o.id end )+count(distinct case when category like '%Customer Care%' then o.id end )+count(distinct case when category like '%Business Affairs%' then o.id end ) as total,
count(distinct o.id) as tweet_count,csp,'Network' as Category, o.day_key,isMain,region,sentiment,social_media_source,
count(distinct case when category like '%Network%' then o.id end ) as network_count,
count(distinct case when category like '%Customer Care%' then o.id end ) as CC_count,
count(distinct case when category like '%Business Affairs%' then o.id end ) as BA_count
from 
fact_view_raw_social_media o 
group by csp,o.day_key,isMain,region,sentiment,social_media_source
) as q

UNION ALL

select 
BA_count*100/total as Bad_Sentiment,
network_count*100/total as Network_Bad_Sentiment,
CC_count*100/total  as CustomerCare_Bad_Sentiment,
BA_count*100/total as BusinessAffairs_Bad_Sentiment,
tweet_count,csp, Category, q.day_key,isMain,region,sentiment,social_media_source
from
(select 
count(distinct case when category like '%Network%' then o.id end )+count(distinct case when category like '%Customer Care%' then o.id end )+count(distinct case when category like '%Business Affairs%' then o.id end ) as total,
count(distinct o.id) as tweet_count,csp,'Business Affairs' as Category, o.day_key,isMain,region,sentiment,social_media_source,
count(distinct case when category like '%Network%' then o.id end ) as network_count,
count(distinct case when category like '%Customer Care%' then o.id end ) as CC_count,
count(distinct case when category like '%Business Affairs%' then o.id end ) as BA_count
from 
fact_view_raw_social_media o 
group by csp,o.day_key,isMain,region,sentiment,social_media_source
) as q

UNION ALL

select 
CC_count*100/total as Bad_Sentiment,
network_count*100/total as Network_Bad_Sentiment,
CC_count*100/total  as CustomerCare_Bad_Sentiment,
BA_count*100/total as BusinessAffairs_Bad_Sentiment,
tweet_count,csp, Category, q.day_key,isMain,region,sentiment,social_media_source
from
(select 
count(distinct case when category like '%Network%' then o.id end )+count(distinct case when category like '%Customer Care%' then o.id end )+count(distinct case when category like '%Business Affairs%' then o.id end ) as total,
count(distinct o.id) as tweet_count,csp,'Customer Care' as Category, o.day_key,isMain,region,sentiment,social_media_source,
count(distinct case when category like '%Network%' then o.id end ) as network_count,
count(distinct case when category like '%Customer Care%' then o.id end ) as CC_count,
count(distinct case when category like '%Business Affairs%' then o.id end ) as BA_count
from 
fact_view_raw_social_media o 
group by csp,o.day_key,isMain,region,sentiment,social_media_source
) as q