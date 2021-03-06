CREATE TABLE wrd.flat_pcmd_1w_STAGING (
	FCTPCMDSID	varchar(200),
	VERSION	varchar(200),
	CALL_EVENTCODE	varchar(200),
	CALL_EVENTCODEDESCRIPTION	varchar(200),
	CALL_STMSI_MMECODE	varchar(200),
	CALL_STMSI_MTMSITYPE	varchar(200),
	CALL_STMSI_MTMSI	varchar(200),
	CALL_MMEUES1APID	varchar(200),
	CALL_ENBUES1APID	varchar(200),
	COMMONITM_GUMME_PLMNID	varchar(200),
	COMMONITM_GUMME_MMEG	varchar(200),
	COMMONITM_GUMME_MEC	varchar(200),
	COMMONITM_TAI_PLMNID	varchar(200),
	COMMONITM_TAI_TAC	varchar(200),
	COMMONITM_GENB_LMNID	varchar(200),
	COMMONITM_GENB_TYPE	varchar(200),
	COMMONITM_GENB_ID	varchar(200),
	COMMONITM_CALLRELCAUSE	varchar(200),
	CALLRELCAUSEDESCRIPTION	varchar(200),
	CONN_ATTEMPTEDTIME	varchar(200),
	SETUPDURATIONMSEC	varchar(200),
	CONNECTIONDURATIONMSEC	varchar(200),
	CONN_FREQBANDINDIATOR	varchar(200),
	CONN_CGI_PLMNID	varchar(200),
	DIMRANCELLSECTORID_CONNCGI	varchar(200),
	CONNCGI_SECTORID	varchar(200),
	CONN_PCI varchar(200),
	CONN_TA	varchar(200),
	CONN_DISTUETOENB_MILES	varchar(200),
	CONN_CARDID	varchar(200),
	CONN_TRANSMITMODE	varchar(200),
	CONN_TXANTENNAMODE	varchar(200),
	CONN_CALLTYPE	varchar(200),
	TYPEOFCAL	varchar(200),
	CONN_MME_VER	varchar(200),
	CONN_MME_IP	varchar(200),
	CONN_SGW_VER	varchar(200),
	CONN_SGW_IP	varchar(200),
	RELEASE_PCI	varchar(200),
	RELEASE_CGI_PLMNID	varchar(200),
	DIMRANCELLSECTORID_RELEASECGI	varchar(200),
	RELEASECGI_SECTORID	varchar(200),
	RELEASE_FREQBANDINDICATOR	varchar(200),
	RELEASE_CQI varchar(200),
	RELEASE_TA	varchar(200),
	RELEASE_DISTUETOENB_MILES	varchar(200),
	RELEASE_CARID varchar(200),
	RELEASE_PA	varchar(200),
	DLPOWERCONTROLPARMPA	varchar(200),
	RELEASE_TRANSMITMODE	varchar(200),
	RELEASE_TXANTENNAMODE	varchar(200),
	RELEASE_MME_VER	varchar(200),
	RELEASE_MME_IP	varchar(200),
	RELEASE_SGW_VER	varchar(200),
	RELEASE_SGW_IP	varchar(200),
	HO_ENB_PLMNID	varchar(200),
	HO_ENB_TYPE	varchar(200),
	HO_ENB_ID	varchar(200),
	HO_ENB_VER	varchar(200),
	HO_ENB_IP	varchar(200),
	HO_CGI_PLMNID	varchar(200),
	DIMRANCELLSECTORID_HOCGI	varchar(200),
	HO_PCI	varchar(200),
	NUMERAB	varchar(200),
	THRUPUT00RBID	varchar(200),
	THRUPUT00EPSBEARID	varchar(200),
	THRUPUT00QCI	varchar(200),
	THRUPUT00TXDATATOSGBYTES	varchar(200),
	THRUPUT00RXDATAFROMSGBYTES	varchar(200),
	THRUPUT00TXPACKETSTOUE	varchar(200),
	THRUPUT00DLPACKETDROPRATE	varchar(200),
	THRUPUT00TXPACKETSTOSG	varchar(200),
	THRUPUT00ULPACKETDROPRATE	varchar(200),
	THRUPUT00TXDATATOUEBYTES	varchar(200),
	THRUPUT00RETXDATATOUEBYTES	varchar(200),
	THRUPUT00RXDATAFROMUEBYTES	varchar(200),
	THRUPUT01RBID	varchar(200),
	THRUPUT01EPSBEARID	varchar(200),
	THRUPUT01QCI	varchar(200),
	THRUPUT01TXDATATOSGBYTES	varchar(200),
	THRUPUT01RXDATAFROMSGBYTES	varchar(200),
	THRUPUT01TXPACKETSTOUE	varchar(200),
	THRUPUT01DLPACKETDROPRATE	varchar(200),
	THRUPUT01TXPACKETSTOSG	varchar(200),
	THRUPUT01ULPACKETDROPRATE	varchar(200),
	THRUPUT01TXDATATOUEBYTES	varchar(200),
	THRUPUT01RETXDATATOUEBYTES	varchar(200),
	THRUPUT01RXDATAFROMUEBYTES	varchar(200),
	THRUPUT02RBID	varchar(200),
	THRUPUT02EPSBEARID	varchar(200),
	THRUPUT02QCI	varchar(200),
	THRUPUT02TXDATATOSGBYTES	varchar(200),
	THRUPUT02RXDATAFROMSGBYTES	varchar(200),
	THRUPUT02TXPACKETSTOUE	varchar(200),
	THRUPUT02DLPACKETDROPRATE	varchar(200),
	THRUPUT02TXPACKETSTOSG	varchar(200),
	THRUPUT02ULPACKETDROPRATE	varchar(200),
	THRUPUT02TXDATATOUEBYTES	varchar(200),
	THRUPUT02RETXDATATOUEBYTES	varchar(200),
	THRUPUT02RXDATAFROMUEBYTES	varchar(200),
	THRUPUT03RBID	varchar(200),
	THRUPUT03EPSBEARID	varchar(200),
	THRUPUT03QCI	varchar(200),
	THRUPUT03TXDATATOSGBYTES	varchar(200),
	THRUPUT03RXDATAFROMSGBYTES	varchar(200),
	THRUPUT03TXPACKETSTOUE	varchar(200),
	THRUPUT03DLPACKETDROPRATE	varchar(200),
	THRUPUT03TXPACKETSTOSG	varchar(200),
	THRUPUT03ULPACKETDROPRATE	varchar(200),
	THRUPUT03TXDATATOUEBYTES	varchar(200),
	THRUPUT03RETXDATATOUEBYTES	varchar(200),
	THRUPUT03RXDATAFROMUEBYTES	varchar(200),
	THRUPUT04RBID	varchar(200),
	THRUPUT04EPSBEARID	varchar(200),
	THRUPUT04QCI	varchar(200),
	THRUPUT04TXDATATOSGBYTES	varchar(200),
	THRUPUT04RXDATAFROMSGBYTES	varchar(200),
	THRUPUT04TXPACKETSTOUE	varchar(200),
	THRUPUT04DLPACKETDROPRATE	varchar(200),
	THRUPUT04TXPACKETSTOSG	varchar(200),
	THRUPUT04ULPACKETDROPRATE	varchar(200),
	THRUPUT04TXDATATOUEBYTES	varchar(200),
	THRUPUT04RETXDATATOUEBYTES	varchar(200),
	THRUPUT04RXDATAFROMUEBYTES	varchar(200),
	THRUPUT05RBID	varchar(200),
	THRUPUT05EPSBEARID	varchar(200),
	THRUPUT05QCI	varchar(200),
	THRUPUT05TXDATATOSGBYTES	varchar(200),
	THRUPUT05RXDATAFROMSGBYTES	varchar(200),
	THRUPUT05XPACKETSTOUE	varchar(200),
	THRUPUT05DLPACKETDROPRATE	varchar(200),
	THRUPUT05TXPACKETSTOSG	varchar(200),
	THRUPUT05ULPACKETDROPRATE	varchar(200),
	THRUPUT05TXDATATOUEBYTES	varchar(200),
	THRUPUT05RETXDATATOUEBYTES	varchar(200),
	THRUPUT05RXDATAFROMUEBYTES	varchar(200),
	THRUPUT06RBID	varchar(200),
	THRUPUT06EPSBEARID	varchar(200),
	THRUPUT06QCI	varchar(200),
	THRUPUT06TXDATATOSGBYTES	varchar(200),
	THRUPUT06RXDATAFROMSGBYTES	varchar(200),
	THRUPUT06TXPACKETSTOUE	varchar(200),
	THRUPUT06DLPACKETDROPRATE	varchar(200),
	THRUPUT06TXPACKETSTOSG	varchar(200),
	THRUPUT06ULPACKETDROPRATE	varchar(200),
	THRUPUT06TXDATATOUEBYTES	varchar(200),
	THRUPUT06RETXDATATOUEBYTES	varchar(200),
	THRUPUT06RXDATAFROMUEBYTES	varchar(200),
	THRUPUT07RBID	varchar(200),
	THRUPUT07EPSBEARID	varchar(200),
	THRUPUT07QCI	varchar(200),
	THRUPUT07TXDATATOSGBYTES	varchar(200),
	THRUPUT07RXDATAFROMSGBYTES	varchar(200),
	THRPUT07TXPACKETSTOUE	varchar(200),
	THRUPUT07DLPACKETDROPRATE	varchar(200),
	THRUPUT07TXPACKETSTOSG	varchar(200),
	THRUPUT07ULPACKETDROPRATE	varchar(200),
	THRUPUT07TXDATATOUEBYTES	varchar(200),
	THRUPUT07RETXDATATOUEBYTES	varchar(200),
	THRUPUT07RXDATAFROMUEBYTES	varchar(200),
	RF_TXPOWER	varchar(200),
	RF_UETXPOWERDBM	varchar(200),
	RF_RSRP	varchar(200),
	RF_RSRPDBM	varchar(200),
	RF_RSRQ varchar(200),
	RF_RSRQDB	varchar(200),
	NBHD_CELLCOUNT	varchar(200),
	NBHD_CGI01PLMNID	varchar(200),
	DIMRANCELLSECTORID_NBHDCGI01	varchar(200),
	NBHD_CGI01PCI	varchar(200),
	NBHD_CGI01RSRP	varchar(200),
	NBHD_CGI01RSRPDBM	varchar(200),
	NBHD_CGI01RSRQ	varchar(200),
	NBHD_CGI01RSRQDB	varchar(200),
	NBHD_CGI01UNKNOWN	varchar(200),
	NBHD_CGI02PLMNID	varchar(200),
	DIMRANCELLSECTORID_NBHDCGI02	varchar(200),
	NBHD_CGI02PCI	varchar(200),
	NBHD_CGI02RSRP	varchar(200),
	NBHD_CGI02RSRPDBM	varchar(200),
	NBHD_CGI02RSRQ	varchar(200),
	NBHD_CGI02RSRQDB	varchar(200),
	NBHD_CGI02UNKNOWN	varchar(200),
	NBHDCGI03PLMNID	varchar(200),
	DIMRANCELLSECTORID_NBHDCGI03	varchar(200),
	NBHD_CGI03PCI	varchar(200),
	NBHD_CGI03RSRP	varchar(200),
	NBHD_CGI03RSRPDBM	varchar(200),
	NBHD_CGI03RSRQ	varchar(200),
	NBHD_CGI03RSRQDB	varchar(200),
	NBHDCGI03UNKNOWN	varchar(200),
	UEHIST_COUNT	varchar(200),
	UEHIST_CGI01PLMNID	varchar(200),
	DIMRANCELLSECTORIDUEHISTCGI01	varchar(200),
	UEHIST_CGI01CELLSIZE	varchar(200),
	UEHISTCGI01STAYTIMESEC	varchar(200),
	UEHIST_CGI02PLMNID	varchar(200),
	DIMRANCELLSECTORID_UEHISTCGI02	varchar(200),
	UEHIST_CGI02CELLSIZE	varchar(200),
	UEHIST_CGI02STAYTIMESEC	varchar(200),
	UEHIST_CGI03PLMNID	varchar(200),
	DIMRANCELLSECTORID_UEHISTCGI03	varchar(200),
	UEHIST_CGI03CELLSIZE	varchar(200),
	UEHIST_CGI03STAYTIMESEC	varchar(200),
	UEHIST_CGI04PLMNID	varchar(200),
	DIMRANCELLSECTORID_UEHISTCGI04	varchar(200),
	UEHIST_CGI04CELLSIZE	varchar(200),
	UEHIST_CGI04STAYTIMESEC	varchar(200),
	UEHIST_CGI05PLMNID	varchar(200),
	DIMRANCELLSECTORID_UEHISTCGI05	varchar(200),
	UEHIST_CGI05CELLSIZE	varchar(200),
	UEHIST_CGI05STAYTIMESEC	varchar(200),
	CALLDBG_STATECMD	varchar(200),
	CALLDBG_STATE	varchar(200),
	CALLDBG_STATECMDESCRIPTION	varchar(200),
	CALLDBG_MSGID	varchar(200),
	CALLDBG_SEQUENCE	varchar(200),
	CALLDBG_CALLID	varchar(200),
	CALLDBG_IMSI	varchar(200),
	CALLDBG_CRNTI	varchar(200),
	UNKNOWN	varchar(200),
	LATITUDE	varchar(200),
	LONGITUDE	varchar(200),
	FIXEDSPATIALGRIDID	varchar(200),
	GEOLOCATIONCD	varchar(200),
	REFERENCE	varchar(200),
	SKU_NUMBER	varchar(200),
	BAN_NBR	varchar(200),
	SRC_OWNER_CD	varchar(200),
	OS_CR_CLASS_CD	varchar(200),
	PRICE_PLAN_CD	varchar(200),
	STUS_CD	varchar(200)
);