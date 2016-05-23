Create table IF NOT EXISTS CDR_Post_Rec_ORC
(
RecordType INT,
TimeStamp VARCHAR(14),
CallingPartyNumber varchar(20),
CallingRoamInfo varchar(20),
CallingCellID varchar(20),
ChargingTime VARCHAR(14),
IMEI varchar(20),
CallingPartyIMSI varchar(50),
ChargingPartyNumber varchar(20),
PayType varchar(20),
RoamState varchar(20),
HomeZoneID varchar(20),
ProductID varchar(20),
ServiceType varchar(100),
SubscriberID varchar(20),
ChargeOfFundAccounts varchar(20),
ChargeFromPrepaid varchar(20),
PrepaidBalance INT,
ChargeFromPostpaid varchar(20),
PostpaidBalance INT,
AccountID varchar(20),
CurrencyCode varchar(20),
CallingNetworkType varchar(20),
CalledPartyNumber varchar(100),
CalledPartyIMSI varchar(20),
DailedNumber varchar(100),
OriginalCalledParty varchar(20),
CalledRoamInfo varchar(100),
CalledCellID varchar(20),
ChargePartyIndicator varchar(20),
CalledNetworkType varchar(20),
GroupCallType varchar(20),
GroupPayFlag varchar(20),
NPFlag varchar(20),
NPPrefix varchar(20),
TerminationReason varchar(20),
ChargeDuration INT,
CallDuration INT,
CallForwardIndicator varchar(20),
CallReferenceNumber varchar(100),
CallType varchar(20),
HotlineIndicator varchar(20),
MSCAddress varchar(100),
RedirectingPartyID varchar(20),
OnNetIndicator varchar(20),
CamelCallLEGInformation varchar(20),
CamelDestinationNumber varchar(20),
CamelInitCFIndicator varchar(20),
CamelPhase varchar(20),
LevelOfCamelService varchar(20),
CamelSMSInformation varchar(20),
SMSID varchar(20),
SMLength varchar(20),
SpecialNumberIndicator varchar(20),
DestAddressType varchar(20),
OriginAddressType varchar(20),
ChargingID varchar(20),
DownFlux varchar(20),
UpFlux varchar(20),
SGSNAddress varchar(100),
GGSNAddress varchar(100),
ServiceID varchar(20),
RoutingArea varchar(20),
LocationAreaCode varchar(20),
OpposedNetworkType varchar(20),
AccessPrefix varchar(20),
CalledHomeAreaNumber varchar(20),
CalledHomeCountryCode varchar(4),
CalledHomeNetworkCode varchar(20),
CalledRoamAreaNumber varchar(20),
CalledRoamCountryCode varchar(4),
CalledRoamNetworkCode varchar(20),
CalledVPNGroupNumber varchar(20),
CalledVPNShortNumber varchar(20),
CalledVPNTopGroupNumber varchar(20),
CallingHomeAreaNumber varchar(20),
CallingHomeCountryCode varchar(4),
CallingHomeNetworkCode varchar(20),
CallingRoamAreaNumber varchar(20),
CallingRoamCountryCode varchar(4),
CallingRoamNetworkCode varchar(20),
CallingVPNGroupNumber varchar(20),
CallingVPNShortNumber varchar(20),
CallingVPNTopGroupNumber varchar(20),
ChargingType varchar(20),
DiameterSessionID varchar(500),
RoutingPrefix varchar(20),
SerialNO varchar(20),
Subsquence varchar(20),
WaitDuration INT,
SendResult varchar(20)
)
STORED AS ORC;