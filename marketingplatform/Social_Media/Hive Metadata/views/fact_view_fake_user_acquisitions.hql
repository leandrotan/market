create or replace view  fact_view_fake_user_acquisitions as
select distinct 
        a.user_name,
        a.user_id,
        u.day_key,
        u.social_media_source,
        u.csp,
        u.sentiment,
        u.category,
        u.subcategory,
        a.is_user_contacted,
        from_unixtime(a.ts_of_contact,'yyyy-MM-dd') as contact_date,
        a.is_user_acquired,
        u.id,
        u.country,
        from_unixtime(a.acquisition_ts,'yyyy-MM-dd') as acquisition_date
from fact_view_raw_social_media u
left join fact_fake_acquisitions a on u.user_id = a.user_id and u.day_key=a.day_key and u.country=a.country and u.csp=a.csp
where sentiment in ('Potential Churners','Severe')