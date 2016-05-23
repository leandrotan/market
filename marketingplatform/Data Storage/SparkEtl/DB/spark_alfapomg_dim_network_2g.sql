DROP TABLE IF EXISTS DROP TABLE IF EXISTS spark_alfapomg_dim_network_2g;

CREATE TABLE spark_alfapomg_dim_network_2g
  (
     cell_sid 	INTEGER NOT NULL,
	 cellname	 VARCHAR(400),
     msc_sid     INTEGER NOT NULL,
	 mscname	 VARCHAR(400),
     bsc_sid     INTEGER NOT NULL,
	 bscname	 VARCHAR(400),
	 latitude 	 NUMERIC(10,8),
	 longitude	 NUMERIC(10,8),
	 azimuth	 VARCHAR(15),
     district_sid    INTEGER NOT NULL,
     vendor     VARCHAR(400),
     technology VARCHAR(400),
     start_datetime DATETIME NOT NULL ENCODING BLOCK_DICT,
     end_datetime DATETIME ENCODING BLOCK_DICT
  )

  UNSEGMENTED ALL NODES