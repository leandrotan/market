create table dim_operator as
select 'O2 UK' as csp, 'o2' as csp_screen_name, false as isMain, 'England' as country
union all
select  'Vodafone UK' as csp, 'vodafoneuk' as csp_screen_name, false as isMain, 'England' as country
union all
select 'Vodafone UK' as csp, 'vodafoneukhelp' as csp_screen_name, false as isMain, 'England' as country
union all
select  'Orange UK' as csp, 'orangeuk' as csp_screen_name, true as isMain, 'England' as country
union all
select  'Virgin Media UK' as csp, 'virginmedia' as csp_screen_name, false as isMain, 'England' as country
union all
select  'Hutchinson 3G' as csp, 'threeuk' as csp_screen_name, false as isMain, 'England' as country
union all
select  'Hutchinson 3G' as csp, 'threeuksupport' as csp_screen_name, false as isMain, 'England' as country
union all
select  'T-Mobile UK' as csp, 'tmobileuk' as csp_screen_name, false as isMain, 'England' as country
union all 
select  'Orange UK' as csp, 'ee' as csp_screen_name, true as isMain, 'England' as country
union all
select  'Touch Lebanon' as csp, 'touchLebanon' as csp_screen_name, false as isMain, 'Lebanon' as country 
union all 
select  'Alfa Telecom' as csp, 'AlfaTelecom' as csp_screen_name, true as isMain, 'Lebanon' as country
union all 
select  'Alfa Telecom' as csp, '76088351303' as csp_screen_name, true as isMain, 'Lebanon' as country
union all
select  'Touch Lebanon' as csp, '216944381660283' as csp_screen_name, false as isMain, 'Lebanon' as country
--- Twitter
--Egypt
union all
select  'Mobinil' as csp, 'Mobinil' as csp_screen_name, false as isMain, 'Egypt' as country
union all
select  'Vodafone' as csp, 'VodafoneEgypt' as csp_screen_name, false as isMain, 'Egypt' as country
union all
select  'Etisalat' as csp, 'EtisalatMisr' as csp_screen_name, false as isMain, 'Egypt' as country
--Jordan
union all
select  'Zain' as csp, 'ZainJo' as csp_screen_name, false as isMain, 'Jordan' as country
union all
select  'Orange' as csp, 'orangeJo' as csp_screen_name, false as isMain, 'Jordan' as country
union all
select  'Orange' as csp, 'OrangeJohelper' as csp_screen_name, false as isMain, 'Jordan' as country
union all
select  'Umniah' as csp, 'Umniahbelong' as csp_screen_name, false as isMain, 'Jordan' as country
--Nigeria 
union all
select  'Airtel' as csp, 'AirtelNigeria' as csp_screen_name, false as isMain, 'Nigeria' as country 
union all
select  'Airtel' as csp, 'airtel_care' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'Etisalat' as csp, 'etisalat_9ja' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'Etisalat' as csp, '0809ja_support' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'MTN' as csp, 'MTN180' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'Globacom' as csp, 'GlobacomLimited' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'Globacom' as csp, 'GloCare' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'MTN' as csp, 'MTNNG' as csp_screen_name, false as isMain, 'Nigeria' as country
--Qatar
union all
select  'Vodafone' as csp, 'VodafoneQatar' as csp_screen_name, false as isMain, 'Qatar' as country
union all
select  'Ooredoo' as csp, 'OoredooQatar' as csp_screen_name, false as isMain, 'Qatar' as country
--France
union all
select  'Orange' as csp, 'Orange' as csp_screen_name, false as isMain, 'France' as country
union all
select  'SFR' as csp, 'SFR' as csp_screen_name, false as isMain, 'France' as country
union all
select  'Virgin Mobile France' as csp, 'VirginMobilefr' as csp_screen_name, false as isMain, 'France' as country
union all
select  'Bouygues Telecom' as csp, 'bouyguestelecom' as csp_screen_name, false as isMain, 'France' as country
union all
select  'free' as csp, 'freemobile' as csp_screen_name, false as isMain, 'France' as country
--UAE
union all
select  'Du' as csp, 'dutweets' as csp_screen_name, false as isMain, 'UAE' as country
union all
select  'Etisalat' as csp, 'etisalat' as csp_screen_name, false as isMain, 'UAE' as country
union all
select  'Etisalat' as csp, 'etisalat_care' as csp_screen_name, false as isMain, 'UAE' as country
--KSA
union all
select  'STC' as csp, 'STCcare' as csp_screen_name, false as isMain, 'KSA' as country
union all
select  'STC' as csp, 'STC_KSA' as csp_screen_name, false as isMain, 'KSA' as country
union all
select  'Mobily' as csp, 'Mobily' as csp_screen_name, false as isMain, 'KSA' as country
union all
select  'Mobily' as csp, 'Mobily1100' as csp_screen_name, false as isMain, 'KSA' as country
union all
select  'Zain' as csp, 'ZainKSA' as csp_screen_name, false as isMain, 'KSA' as country
union all
select  'Zain' as csp, 'ZainHelpSA' as csp_screen_name, false as isMain, 'KSA' as country
union all
select  'Virgin Mobile KSA' as csp, 'Virginmobileksa' as csp_screen_name, false as isMain, 'KSA' as country
--PnG
union all
select  'Pampers' as csp, 'pampers' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Pampers' as csp, 'pampers_uk' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Pampers' as csp, 'pampers_sa' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Pampers' as csp, 'pampersarabi' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Pampers' as csp, 'pampersfrance' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Huggies' as csp, 'huggies' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Huggies' as csp, 'Huggiesmom' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Huggies' as csp, 'HuggiesCanada' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Huggies' as csp, 'huggies_sa' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Huggies' as csp, 'huggiesau' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Head & Shoulders' as csp, 'headshoulders' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Head & Shoulders' as csp, 'hsformen' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Head & Shoulders' as csp, 'headshouldersuk' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Head & Shoulders' as csp, 'hnsarabia' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Clear' as csp, 'clearhair' as csp_screen_name, false as isMain, 'PNG' as country
--- Facebook
--PnG
union all
select  'Clear' as csp, '509931205722897' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Clear' as csp, '293000887397287' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Head & Shoulders' as csp, '395549243927833' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Huggies' as csp, '106874109386' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Huggies' as csp, '109798209091133' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Pampers' as csp, '248278301951050' as csp_screen_name, false as isMain, 'PNG' as country
union all
select  'Pampers' as csp, '89121585311' as csp_screen_name, false as isMain, 'PNG' as country

--Egypt
union all
select  'Mobinil' as csp, '131619693520002' as csp_screen_name, false as isMain, 'Egypt' as country
union all
select  'Vodafone' as csp, '19973233436' as csp_screen_name, false as isMain, 'Egypt' as country
union all
select  'Etisalat' as csp, '141606109666' as csp_screen_name, false as isMain, 'Egypt' as country
--Jordan
union all
select  'Umniah' as csp, '113562225327016' as csp_screen_name, false as isMain, 'Jordan' as country 
union all
select  'Zain' as csp, '131192906913696' as csp_screen_name, false as isMain, 'Jordan' as country
union all
select  'Orange' as csp, '137718882920644' as csp_screen_name, false as isMain, 'Jordan' as country
--Nigeria 
union all
select  'Airtel' as csp, '140739362667353' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'Etisalat' as csp, '166936850008173' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'MTN' as csp, '130488246965592' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'Multilinks' as csp, '198414546933264' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'Visafone' as csp, '206741122690183' as csp_screen_name, false as isMain, 'Nigeria' as country
union all
select  'Globacom' as csp, '269492993434' as csp_screen_name, false as isMain, 'Nigeria' as country
--Qatar
union all
select  'Vodafone' as csp, '337711220169' as csp_screen_name, false as isMain, 'Qatar' as country
union all
select  'Ooredoo' as csp, '110856078925801' as csp_screen_name, false as isMain, 'Qatar' as country
--UAE
union all
select  'Du' as csp, '293843523498' as csp_screen_name, false as isMain, 'UAE' as country
union all
select  'Etisalat' as csp, '107009842653217' as csp_screen_name, false as isMain, 'UAE' as country
--KSA
union all
select  'STC' as csp, '293843523498' as csp_screen_name, false as isMain, 'KSA' as country
union all
select  'Mobily' as csp, '107009842653217' as csp_screen_name, false as isMain, 'KSA' as country
union all
select  'Zain' as csp, '194482313922290' as csp_screen_name, false as isMain, 'KSA' as country
union all
select  'Virgin Mobile KSA' as csp, '498989650179185' as csp_screen_name, false as isMain, 'KSA' as country;