create table fact_fake_acquisitions
as
select          t2.*,
                case when is_user_contacted  and rand(2)<0.7  then true else false end as is_user_acquired
from
 (
   select  user_id,
          user_name,
          id,
          day_key,
          is_user_contacted,
          ts_of_contact,
          acquisition_ts,
          csp,
          country,
          row_number() over (partition by csp,category order by rand() desc) as rn from (
    select distinct user_id,
           user_name,
           csp,
           country,
           category,
           id,
           day_key,
           case when rand()>0.5 then true else false end as is_user_contacted,
           2 as ts_of_contact,
           1 as acquisition_ts
    from fact_view_raw_social_media
    where sentiment in ('Severe','Potential Churners') and user_id <> -1) t1 ) t2 
