CREATE OR REPLACE VIEW V_STORM_NETWORK as
WITH one AS(
SELECT DISTINCT SPLIT_PART(network_id,',',1) as id,
SPLIT_PART(network_id,'=',1) AS type, 1 levels
FROM storm_stg_fact
WHERE REGEXP_COUNT(network_id, '=') = 2
UNION ALL
SELECT DISTINCT network_id as id,
SUBSTRING (SUBSTRING(network_id,
INSTR(network_id, ',', -1) +1),
1,
INSTR(SUBSTRING(network_id,
INSTR(network_id, ',', -1)+2), '=') ) AS type,
 REGEXP_COUNT(network_id, '=') levels
FROM storm_stg_fact
UNION ALL
SELECT DISTINCT SUBSTRING(network_id, 1,INSTR(network_id, ',',-1)-1) as id,
SPLIT_PART(SPLIT_PART(network_id,'=',3), ',',2) AS type,
 3 levels
FROM storm_stg_fact
WHERE REGEXP_COUNT(network_id, '=') = 4
)
SELECT id,type, levels
, CASE levels
WHEN 5 THEN CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',1), ',',1),'|')
,CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',2), ',',2),'|')
,CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',3), ',',2),'|')
,CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',4), ',',2),'|')
,SPLIT_PART(SPLIT_PART(id,'=',5), ',',2)))))
WHEN 4 THEN CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',1), ',',1),'|')
,CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',2), ',',2),'|')
,CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',3), ',',2),'|')
,SPLIT_PART(SPLIT_PART(id,'=',4), ',',2))))
 WHEN 3 THEN CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',1), ',',1),'|')
,CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',2), ',',2),'|')
,SPLIT_PART(SPLIT_PART(id,'=',3), ',',2)))
 WHEN 2 THEN CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',1), ',',1),'|')
,SPLIT_PART(SPLIT_PART(id,'=',2), ',',2))
WHEN 1 THEN SPLIT_PART(id,'=',1)
END type_path
, SPLIT_PART(SPLIT_PART(id,',',1), '=',2) AS level1_id
, SPLIT_PART(SPLIT_PART(id,',',1), '=',1) AS level1_type
, SPLIT_PART(SPLIT_PART(id,',',2), '=',2) AS level2_id
, SPLIT_PART(SPLIT_PART(id,',',2), '=',1) AS level2_type
, SPLIT_PART(SPLIT_PART(id,',',3), '=',2) AS level3_id
, SPLIT_PART(SPLIT_PART(id,',',3), '=',1) AS level3_type
, SPLIT_PART(SPLIT_PART(id,',',4), '=',2) AS level4_id
, SPLIT_PART(SPLIT_PART(id,',',4), '=',1) AS level4_type
, SPLIT_PART(SPLIT_PART(id,',',5), '=',2) AS level5_id
, SPLIT_PART(SPLIT_PART(id,',',5), '=',1) AS level5_type
FROM one
WHERE id NOT IN (SELECT DISTINCT id FROM storm_dim_network);

--create sequence storm_dim_net_seq START 1;

DROP TABLE IF EXISTS storm_dim_network_temp;
CREATE TABLE storm_dim_network_temp
AS
SELECT levels,
CAST(NULL AS INTEGER) network_sid,
s.id,
s.type,
s.type_path,
level1.level1_sid, s.level1_id, s.level1_type
,level2.level2_sid, s.level2_id, s.level2_type
,level3.level3_sid, s.level3_id, s.level3_type
,level4.level4_sid, s.level4_id, s.level4_type
 FROM V_STORM_NETWORK s
LEFT JOIN (
SELECT DISTINCT level1_sid,level1_id
FROM storm_dim_network
WHERE ISNULL(storm_dim_network.level2_id,'') = '') level1
on ISNULL(s.level1_id,'') = ISNULL(level1.level1_id,'')

LEFT JOIN (SELECT DISTINCT level2_sid,storm_dim_network.level2_id
FROM storm_dim_network
WHERE (ISNULL(storm_dim_network.level3_id,'') = ''
AND ISNULL(storm_dim_network.level2_id,'') <> '')) level2
on ISNULL(s.level2_id,'') = ISNULL(level2.level2_id,'')

LEFT JOIN (

SELECT DISTINCT level3_sid,storm_dim_network.level3_id,storm_dim_network.level2_id,storm_dim_network.level3_type
FROM storm_dim_network
WHERE ISNULL(storm_dim_network.level4_id,'') = ''
AND ISNULL(storm_dim_network.level3_id,'') <> ''
AND ISNULL(storm_dim_network.level2_id,'') <> ''

) level3
on ISNULL(s.level3_id,'') = ISNULL(level3.level3_id,'')
AND ISNULL(s.level3_type,'') = ISNULL(level3.level3_type,'')
AND ISNULL(s.level2_id,'') = ISNULL(level3.level2_id,'')

LEFT JOIN (

SELECT DISTINCT level4_sid,storm_dim_network.level4_id,storm_dim_network.level4_type,storm_dim_network.level3_id,storm_dim_network.level2_id,storm_dim_network.level3_type
FROM storm_dim_network
WHERE ISNULL(storm_dim_network.level5_id,'') = ''
AND ISNULL(storm_dim_network.level3_id,'') <> ''
AND ISNULL(storm_dim_network.level4_id,'') <> ''
AND ISNULL(storm_dim_network.level2_id,'') <> ''

) level4
on ISNULL(s.level4_id,'') = ISNULL(level4.level4_id,'')
AND ISNULL(s.level4_type,'') = ISNULL(level4.level4_type,'')
AND ISNULL(s.level3_id,'') = ISNULL(level4.level3_id,'')
AND ISNULL(s.level3_type,'') = ISNULL(level4.level3_type,'')
AND ISNULL(s.level2_id,'') = ISNULL(level4.level2_id,'');

 DROP TABLE IF EXISTS storm_dim_network_level1;
CREATE TABLE storm_dim_network_level1 AS
SELECT DISTINCT level1_id, CAST(NULL AS INTEGER) level1_sid
FROM storm_dim_network_temp
where level1_sid is null
AND network_sid is NULL;

UPDATE storm_dim_network_level1
SET level1_sid = NEXTVAL('storm_dim_net_seq');


UPDATE storm_dim_network_temp
   SET level1_sid=storm_dim_network_level1.level1_sid
   FROM storm_dim_network_level1
   WHERE storm_dim_network_temp.level1_id = storm_dim_network_level1.level1_id;

DROP TABLE IF EXISTS storm_dim_network_level2;
CREATE TABLE storm_dim_network_level2 AS
SELECT DISTINCT level2_id as id, CAST(NULL AS INTEGER) level2_sid
FROM storm_dim_network_temp
where level2_sid is null
AND network_sid is NULL;

UPDATE storm_dim_network_level2
SET level2_sid = NEXTVAL('storm_dim_net_seq');


UPDATE storm_dim_network_temp
   SET level2_sid=storm_dim_network_level2.level2_sid
   FROM storm_dim_network_level2
   WHERE storm_dim_network_temp.level2_id = storm_dim_network_level2.id
   ;

DROP TABLE IF EXISTS storm_dim_network_level3;
CREATE TABLE storm_dim_network_level3 AS
SELECT DISTINCT id, CAST(NULL AS INTEGER) level3_sid
FROM storm_dim_network_temp
where level3_sid is null
AND network_sid is NULL
AND LEVELS = 3;

UPDATE storm_dim_network_level3
SET level3_sid = NEXTVAL('storm_dim_net_seq');


UPDATE storm_dim_network_temp
   SET level3_sid=storm_dim_network_level3.level3_sid
   FROM storm_dim_network_level3
   WHERE storm_dim_network_temp.id= storm_dim_network_level3.id;


DROP TABLE IF EXISTS storm_dim_network_level4;
CREATE TABLE storm_dim_network_level4 AS
SELECT DISTINCT id, CAST(NULL AS INTEGER) level4_sid
FROM storm_dim_network_temp
where level4_sid is null
AND network_sid is NULL
AND LEVELS = 4;

UPDATE storm_dim_network_level4
SET level4_sid = NEXTVAL('storm_dim_net_seq');


UPDATE storm_dim_network_temp
   SET level4_sid=storm_dim_network_level4.level4_sid
   FROM storm_dim_network_level4
   WHERE storm_dim_network_temp.id= storm_dim_network_level4.id
   ;


   UPDATE storm_dim_network_temp
   SET network_sid =
CASE levels
WHEN 4 THEN level4_sid
WHEN 3 THEN level3_sid
WHEN 2 THEN level2_sid
WHEN 1 THEN level1_sid
END;

INSERT INTO "storm_dim_network"
("network_sid"
,"id"
,"type"
,"type_path"
,"level1_sid"
,"level1_id"
,"level1_type"
,"level2_sid"
,"level2_id"
,"level2_type"
,"level3_sid"
,"level3_id"
,"level3_type"
,"level4_sid"
,"level4_id"
,"level4_type"
,"vendor"
,"technology"
,"created_time")
SELECT "network_sid"
,"id"
,"type"
,"type_path"
,"level1_sid"
,"level1_id"
,"level1_type"
,"level2_sid"
,"level2_id"
,"level2_type"
,"level3_sid"
,"level3_id"
,"level3_type"
,"level4_sid"
,"level4_id"
,"level4_type"
,'Ericsson' "vendor"
,'Gsm' "technology"
,CURRENT_TIMESTAMP "created_time"
FROM storm_dim_network_temp;

INSERT INTO storm_alfapomg_dim_network_2g (cell_sid, cellname, msc_sid, mscname, bsc_sid, bscname, district_sid, vendor, technology, start_datetime)
SELECT DISTINCT network_sid as cell_sid,level3_id as cellname
,level1_sid as msc_sid, level1_id as mscname
,level2_sid as bsc_sid, level2_id as bscname
,0 district_sid,
vendor, technology, sysdate()
FROM storm_dim_network
WHERE vendor = 'Ericsson'
AND technology = 'Gsm'
AND type = 'CELL'
AND network_sid NOT IN (SELECT DISTINCT cell_sid FROM storm_alfapomg_dim_network_2g);