BLOCK DATA MON
      REAL NTCUPR,NATRO
      CHARACTER*1 KODE,NOH,NOLIST,SKIP,WYTYP
      CHARACTER*2 ACR(20),KODE2,NTYP
      CHARACTER*3 MONTHS
      CHARACTER*6 CROP,DATE
      CHARACTER*10 AREAID,IDET,IDLU,IDSM,PNM
      COMMON/CC/ AREAID(7),CROP(2,20),DATE(2),MONTHS(12),
     1   NOLIST,NOH,KODE(32),KODE2(2),NTYP(9),WYTYP(200)
     2   ,IDET(7),IDLU(7),IDSM(7),PNM(14)
ccc      DATA MONTHS# months of the water year /'OCT','NOV','DEC','JAN','FEB','MAR',
ccc     1            'APR','MAY','JUN','JUL','AUG','SEP'/
      DATA MONTHS/'OCT','NOV','DEC','JAN','FEB','MAR',
     1            'APR','MAY','JUN','JUL','AUG','SEP'/
ccc      DATA NTYP# ? /'BP','ET','CP','SM','ST','AW','TC','HD','HN'/
      DATA NTYP/'BP','ET','CP','SM','ST','AW','TC','HD','HN'/

ccc	KODE(12)# 'A'
ccc	KODE(13)# 'B'
ccc	KODE(14)# 'C'
ccc	KODE(17)# 'F'
ccc	KODE(27)# 'T'

ccc	?/' ',' ',' ',' ',' ',' ',
ccc     1          ' ',' ',' ',' ',' ','A',
ccc     2          'B','C','D','E','F','G',
ccc     3          'H','K','L','M','N','P',
ccc     4          'R','S','T','U','V','W',
ccc     5          'X','Y'/
      DATA KODE/' ',' ',' ',' ',' ',' ',
     1          ' ',' ',' ',' ',' ','A',
     2          'B','C','D','E','F','G',
     3          'H','K','L','M','N','P',
     4          'R','S','T','U','V','W',
     5          'X','Y'/
      DATA KODE2/'PC','PN'/
      END
C********************************************************************
       PROGRAM DICU5
C      WRITE(*,'(45H DELTA ISLAND CONSUMPTIVE USE PROGRAM - DICU5)')
C      WRITE(*,'(32H FOR CONSECUTIVE WATER YEAR RUNS)')
C      WRITE(*,'(18H VERSION: 04/14/88,//)')                          
C*****************************************************************
C   THIS IS THE DELTA ISLAND CONSUMPTIVE USE PROGRAM
C   USED TO ESTIMATE DELTA ISLAND CU FOR SELECTED
C   CONSECUTIVE WATER YEARS:  NFYR THRU LYR (PERIOD = NOCONYR)
C*****************************************************************
C  *  PROGRAM PGCU5 WAS THE ORIGINAL VERSION (DOP)
C  *  APOLLO PGCU5 VERSION                                 REV. 3/86
C
C  * DELTA ISLAND BY ISLAND VERSION - KG                   JAN. 87
C  * DELTA CONSUMPTIVE USE REPORTED IN ACRE-FT (AF)        MAR. 87
C  * REPORT SUMMARY HISTORIC APPLIED WATER (THDPAW)        MAY  87
C  * SPLIT OUT CU OF SEEPAGE FROM CU OF PRECIP             JUN. 87
C  * VERSION TO RUN CONSECUTIVE WATER YEARS:
C     -NACRES DIMENSIONED BY WATER YEAR,
C     -WATER YEAR DIMENSION INCREASED TO 70
C     -STATEMENTS PERTAINING TO THE CENTRAL VALLEY DELETED
C     -PARAMETER EFF SET TO 1.0 (NOT REQUIRED FOR ISL. RUNS)
C     -PARAMETER EVAP READ ONCE FROM DICU5.5 FOR ALL MONTHS
C            & WATER YEARS                                 MAR. 88
C  * RUN TIME SCREEN PRINT COMMENTS ADDED                  APR. 88
C
C$
C$-----Water year dimension increased from 70 to 200.      NM (5/21/95)      
C$
C*********************************************************************
C..TAPE1 = CU INPUT DATA, BY DELTA ISLAND AREAS (OUTPUT FROM DICU5IN).
C..
C..TAPE2 = TOTAL ISLAND AREAS IN ACRES BY WATER YEAR TYPE SPECIFICATIONS
C..        NON-CRITICAL AND CRITICAL - FOR CONSECUTIVE WATER YEARS OF
C..        INTEREST.
C..
C..TAPE3 = WATER YEAR TYPE SPECIFICATIONS FOR CONSECUTIVE WATER YEARS
C..        OF INTEREST (DETERMINED BY NOCONYR, NFYR, LYR AND NOYR)
C..
C..TAPE4 = CROP IRRIGATION SCHEDULES - NON-CRITICAL AND CRITICAL WYEARS.
C..
C..TAPE5 = PAN EVAPORATION FACTORS FOR ADJUSTING AVERAGE DOP ET VALUES
C..        FOR CONSECUTIVE WATER YEARS OF INTEREST.
C..
C..TAPE10 = COLUMN LISTING CONSUMPTIVE USE, PRECIPITATION, AND SOIL MOIS
C..       STORAGE, IN INCHES PLUS HISTORIC DEPLETION, PROJECTED CONSUMPT
C..        AND DIVERSION REQUIREMENT WITH DOMESTIC URBAN WATER USE, IN 1
C..
C..TAPE12 = TOTAL BASIN PRECIPITATION
C..           COMP MODEL TABLE
C..           INPUT TO DELTA WATER REQUIREMENTS MODEL.
C..
C..TAPE13 (DELTA ISLANDS) = TOTAL HISTORIC CU OF PRECIP, ALL CATEGORIES.
C..
C..TAPE14 (DELTA ISLANDS) = TOTAL HISTORIC CU OF SEEPAGE, ALL CATEGORIES
C..
C..TAPE17 = HISTORIC DEPLETION OF APPLIED WATER BY IRR. AND URBAN
C..             COMP MODEL TABLE
C..
C..TAPE27 = INPUT TO THE DEPLETION ANALYSIS =
C..         HISTORIC DEPLETION BY IRR. AND URBAN AREAS
C..
C..         INPUT TO DELTA WATER REQUIREMENTS MODEL =
C..         IRR. + URBAN + DRY GRAIN + RIP. VEG + WATER SURFACE + NATIVE
C..
C..TAPE41 (DELTA ISLANDS) = HISTORIC CU OF SEEPAGE AND CU OF PRECIP
C..
C********************************************************************
      REAL NTCUPR,NATRO
      CHARACTER*1 KODE,NOH,NOLIST,SKIP,WYTYP
      CHARACTER*2 ACR(20),KODE2,NTYP
      CHARACTER*3 MONTHS
      CHARACTER*6 CROP,DATE
      CHARACTER*10 AREAID,IDET,IDLU,IDSM,PNM
      CHARACTER*40 DICU5_1, DICU5_2, DICU5_3, DICU5_4

C..
      COMMON/A/ AVEVAP(12),CHSMCR(12),CHSMGR(12),
     1   CHSMVG(12),CRDEPL(12),CROPCU(12,20),CROPET(12,20),
     2   CRSMST(12),CUA(12),CUINRV(13),CUPPTC(12),CUPPTV(12),
     3   CUPRV(13),CUPTGR(12),CUPTWS(12),DGAC(200),DGET(13)
      COMMON/B/ EFF(12,200),EVAP(13,200),FEETCR(12),FEETGR(12),
     1   FEETVG(12),FTCUA(12),
     2   GRANCU(12),GRDEPL(12),GRSMST(12)
      COMMON/C/ HCAP(200),HCRCUA(13),HCRCUP(13),HCRPAC(20,200),
     1   HGRWTR(12),HL(12),HLWNCU(12),HPL(200),HRSCUW(12),HRUNOF(13),
     2   HNGWTR(13),HUNVCU(12),HURBAC(200),HURCUP(13),HURCUW(13),
     3   HW(12),HWATER(12),HWSCU(12)
      COMMON/D/ KDEF1(10),KDEF2(10),
     1   KR(20),LEV(5),MFI(12,20),MFINC(12,20),MFIC(12,20),MONDAY(12),
     2   NTCUPR(13),NATRO(13),CROOT(20),NYR(200)
      COMMON/E/ P(12,200),PCAP(4),PCRAC(20,5),
     1   PCRCUA(12),PCRPAC(20,200),PCUA(12),PDGAC(4),PDOMRB(12),
     2   PDVIRR(12),PDVTOT(12),PGRWTR(12),PIRRF(12),PLWNCU(12),
     3   PMOIST(12,20),PPL(4),PRECIP(12),
     4   PMRV1I(13),PMRV2P(13),PRSCUW(12),PRUNOF(13),
     5   PNGWTR(13),PRVAC(4),PUNVCU(12),PURAC(4),PURBRF(12),
     6   PURCUW(12),PW(12),PWATER(12),PWSAC(4),PWSCU(12)
      COMMON/F/ RMRVAC(200),RMRV1I(13),RMRV2P(13),RMRV3T(13),
     1   RESDCU(12),RFF(12),RFFURB(12),RPLRAC(200),RPLR1I(13),
     2   RPLR2P(13),RPLR3T(13),RVCU(13),RVET(13)
      COMMON/G/ SEEPCR(13,20),SEEPDG(13),SEEPNV(13),SEEPRW(13),
     1   STARTC(20)
      COMMON/H/ THDPAW(13),THW(12),TOTCUW(12),TOTHCP(13),
     1   TOTHDP(13),TOTHUB(12),TOTHW(12),TOTPDP(13),TOTPUB(12),
     2   TOTPCP(13),TPDPAW(13),
     3   TPR(13),UMOIST(12,20),VEGCU(12),VEGET(12),VGDEPL(12),
     4   VGSMST(12),WSAC(200),WSET(13),WSURCU(12)
      COMMON/AA/ DGMOIS,DGROOT,FACTOR,PCAPTA,PCTLNH,PCTNVP,
     1   PDGA,PMRVAC,PPRVAC,POP,PURBAC,PWSA,STRTDG,
     2   STARTV,VMOIST,VROOT,PCTLNP,PCTNVH,PERCLS
      COMMON/BB/ LEVEL,NACRES(200),NAREA,NOCRP,NOYR,NY
      COMMON/CC/ AREAID(7),CROP(2,20),DATE(2),MONTHS(12),
     1   NOLIST,NOH,KODE(32),KODE2(2),NTYP(9),WYTYP(200)
     2   ,IDET(7),IDLU(7),IDSM(7),PNM(14)
C..*
      COMMON/DD/ CUP(12),CUS(12),CUPLN(12),CUSLN(12),CUPVC(12),
     1   CUSVC(12),HCUP(12),HCUSEEP(12),TCUPLN(12)
      real diffmoist(12,20)
C..
      WRITE(*,'(45H DELTA ISLAND CONSUMPTIVE USE PROGRAM - DICU5)')  
      WRITE(*,'(32H FOR CONSECUTIVE WATER YEAR RUNS)')               
      WRITE(*,'(18H VERSION: 04/14/88,//)')                          
ccc      CALL INIZ# call to zero out the variables
      CALL INIZ
C..
	CALL GETENV('DICU5.1',DICU5_1)
	CALL GETENV('DICU5.2',DICU5_2)
	CALL GETENV('DICU5.3',DICU5_3)
	CALL GETENV('DICU5.4',DICU5_4)
      OPEN(1,FILE=DICU5_1,FORM='FORMATTED',STATUS='OLD')
      OPEN(2,FILE=DICU5_2,FORM='FORMATTED',STATUS='OLD')
      OPEN(3,FILE=DICU5_3,FORM='FORMATTED',STATUS='OLD')
      OPEN(4,FILE=DICU5_4,FORM='FORMATTED',STATUS='OLD')
      OPEN(5,FILE='DICU5.5',FORM='FORMATTED',STATUS='OLD')
      OPEN(10,FILE='DICU5.10',FORM='FORMATTED',STATUS='NEW')
      OPEN(12,FILE='DICU5.12',FORM='FORMATTED',STATUS='NEW')
      OPEN(13,FILE='DICU5.13',FORM='FORMATTED',STATUS='NEW')
      OPEN(14,FILE='DICU5.14',FORM='FORMATTED',STATUS='NEW')
      OPEN(17,FILE='DICU5.17',FORM='FORMATTED',STATUS='NEW')
      OPEN(27,FILE='DICU5.27',FORM='FORMATTED',STATUS='NEW')
      OPEN(41,FILE='DICU5.41',FORM='FORMATTED',STATUS='NEW')
C...
ccc	NSTUDY# subareas ,NOH# ?blank ,
ccc	NOCONYR# years (85 for 1922-2006) ,
ccc	NOCRP# crops(15)
      READ(1,3)NSTUDY,NOH,NOCONYR,NOCRP
ccc      READ(2,2008) SKIP,SKIP# just a read to get over the next two lines
      READ(2,2008) SKIP,SKIP

ccc	WYTYP(J)# water year type ,J=1,NOCONYR)
      READ(3,2008) (WYTYP(J),J=1,NOCONYR)
C...        NON-CRITICAL / CRITICAL WY IRRIG SCEDULES      - KG 2/26/88
      DO K=1,NOCRP
ccc	MFINC# irrigation scedule for noncritical water years (month,crop)
ccc	0=No Irrigation, 1=Irrigation	see p.24 "irrigation season" of Feb. '95 report
         READ(4,91)(MFINC(M,K),M=1,12)
      enddo
      DO K=1,NOCRP
ccc	MFIC# irrigation scedule for critical water years (month, crop)
ccc	0=No Irrigation, 1=Irrigation
         READ(4,91)(MFIC(M,K),M=1,12)
      enddo
C...        PAN EVAPORATION / ET ADJUSTMENT FACTORS        - KG 3/3/88
      DO N=1,NOCONYR

ccc	EVAP# ET adj. factor {pan evap *100/ avg evap} (month, num years from 1921) 
         read(5,'(9x,12f6.0)')(EVAP(M,N),M=1,12) 

ccc	 convert the ET adj. factor to a fraction
         do k=1,12
            evap(k,n)=evap(k,n)/100.
         enddo

C         !read(5,1997)(EVAP(M,N),M=1,12)
C         !read(5,'(9x,12I6)')(IEVAP(k),k=1,12)
C         !do k=1,12
C         !   evap(k,n)=float(ievap(k))/100.
C         !enddo
      enddo
      
C..
C   ISLAND (STUDY) DO LOOP
C..

cccccccc	This is the big loop	ccccccc

ccc	NSTUDY# num subareas
      DO 510 KSTUDY=1,NSTUDY

ccc	NAREA# num areas ('1') ,
ccc	NFYR# the first water year('1922') ,
ccc	LYR# the last water year ('2006') ,
ccc	NOCRP# num crops ('15') ,
ccc	DATE(1&2)# The study name ('DICU5-','1MAR96') ,
ccc	LEVEL# ?('1996') ,
ccc	NOLIST# minus sign is the option for printing out DICU output ('-')
         READ(1,10)NAREA,NFYR,LYR,NOCRP,DATE,LEVEL,NOLIST

ccc	AREAID# heading statement from dicu5.1 broken into 7 lines (K),K=1,7)
         READ(1,12)(AREAID(K),K=1,7)

C         WRITE(*,'(49H STARTING ISLAND: ID#, NAME, CODE AND PRECIP CODE)')
C         WRITE(*,'(I4,4X,7A10/)') NAREA,(AREAID(K),K=1,7)
         WRITE(12,3000)NAREA,KODE(12),NAREA,NAREA,KODE(12),(DATE(K),K=1,2)

ccc	NOH is equal to " " so this block writes out the heading for each subarea        
         IF(NOH.NE.'-') then 
            WRITE(13,4020)NAREA,KODE(13),NAREA,KODE(13),(AREAID(K),K=1,7)
            WRITE(14,4021)NAREA,KODE(14),NAREA,KODE(14),(AREAID(K),K=1,7)
            WRITE(17,3006)NAREA,KODE(17),NAREA,NAREA,KODE(17),(DATE(K),K=1,2)
            WRITE(27,2001)NAREA,KODE(27),NAREA,NAREA,KODE(27),(DATE(K),K=1,2)
         endif

C--------Old STyle
C         IF(NOH.EQ.'-')GO TO 15
C         WRITE(13,4020)NAREA,KODE(13),NAREA,KODE(13),(AREAID(K),K=1,7)
C         WRITE(14,4021)NAREA,KODE(14),NAREA,KODE(14),(AREAID(K),K=1,7)
C         WRITE(17,3006)NAREA,KODE(17),NAREA,NAREA,KODE(17),(DATE(K),K=1,2)
C         WRITE(27,2001)NAREA,KODE(27),NAREA,NAREA,KODE(27),(DATE(K),K=1,2)
C 15      READ(1,11)(IDET(K),K=1,7)

ccc	IDET# reads the 4th line of the subarea data - descriptive info
         READ(1,11)(IDET(K),K=1,7)

ccc	IDSM# reads the 5th line of the subarea data - descriptive info
         READ(1,11)(IDSM(K),K=1,7)

ccc	IDLU# reads the 6th line of the subarea data - descriptive info
         READ(1,11)(IDLU(K),K=1,7)

ccc	NOYR# number of years in the run
         NOYR=LYR-NFYR+1

         DO K=1,NOCRP

ccc	ACR# abbreviated crop names, see p.19 of Feb '95 report (Hari?)
ccc	CROP# crop name broken into 2 strings
            READ(1,71)ACR(K),(CROP(J,K),J=1,2)
         enddo
          
         DO K=1,NOCRP
ccc	KR() a switch to tell if the crop is RICE
            IF(ACR(K).EQ.'RI')KR(K)=1
            IF(ACR(K).NE.'RI')KR(K)=0
         enddo

         DO K=1,NOCRP
ccc	CROPET# crop ET in inches (month,crop), p.24 of Feb '95 report (Hari?)
            READ(1,84)(CROPET(M,K),M=1,12)
         enddo

ccc	DGET# ET for dry grain native (month)
ccc	VEGET# ET for native vegetation (month)
ccc	RVET# ET for riparian vegetation (month)
ccc	WSET# ET for water surface (month)
         READ(1,84)(DGET(M),M=1,12)
         READ(1,84)(VEGET(M),M=1,12)
         READ(1,84)(RVET(M),M=1,12)
         READ(1,84)(WSET(M),M=1,12)

ccc	PERCLS# ? percent loss (0.0)
         READ(1,85)PERCLS
      
         DO K=1,NOCRP
ccc	CROOT# root depth (crop)
ccc	STARTC# Initial Soil Moisture in Oct (crop)
            READ(1,100)CROOT(K),STARTC(K)
         enddo

ccc	DGROOT# dry grain root depth
ccc	STRTDG# Initial Soil Moisture for dry grain in Oct 
ccc	DGMOIS# Max dry grain soil moisture
         READ(1,100)DGROOT,STRTDG,DGMOIS

ccc	VROOT# native vegetation root depth
ccc	STARTV# Initial Soil Moisture for native vegetation in Oct
ccc	VMOIST# Max dry native vegetation soil moisture
         READ(1,100)VROOT,STARTV,VMOIST

         DO K=1,NOCRP
ccc	UMOIST# max. soil moisture, i.e. upper limit (month,crop)
ccc	PMOIST# min. soil moisture, i.e. lower limit (month, crop)
            READ(1,102)(UMOIST(M,K),M=1,12),(PMOIST(M,K),M=1,12)
         enddo 

         DO  K=1,NOCRP
ccc	SEEPCR# crop seepage (month, crop) in lowlands only, p20 Feb. '95 report
            READ(1,102)(SEEPCR(M,K),M=1,12)
         enddo
      
ccc	SEEPDG# dry grain seepage (month) in lowlands only
ccc	SEEPNV# native veg.seepage (month) in lowlands only
ccc	SEEPRW# riaprian veg. and ?water surface seepage (month)
         READ(1,102)(SEEPDG(M),M=1,12)
         READ(1,102)(SEEPNV(M),M=1,12)
         READ(1,102)(SEEPRW(M),M=1,12)

ccc     PNM# read two lines of descriptive info.
         READ(1,110)(PNM(K),K=1,14)
      
ccc	FACTOR# precipitation index factor (1.0)
         READ(1,84)FACTOR

ccc	usually skipped, I have only seen a "-" for NOLIST
         IF(NOLIST.NE.'-') then
            WRITE(10,112) LEVEL,NAREA,(AREAID(J),J=1,7)
            WRITE(10,114) DATE(1),DATE(2)
            WRITE(10,115)(PNM(J),J=1,14),FACTOR,PERCLS,(IDET(K),K=1,7),
     &           (IDSM(K),K=1,7),(IDLU(K),K=1,7)
            WRITE(41,4000)(AREAID(K),K=1,7)
         endif
C--------Original logic
C         IF(NOLIST.EQ.'-')GO TO 116
C         WRITE(10,112) LEVEL,NAREA,(AREAID(J),J=1,7)
C         WRITE(10,114) DATE(1),DATE(2)
C         WRITE(10,115)(PNM(J),J=1,14),FACTOR,PERCLS,(IDET(K),K=1,7),
C     &        (IDSM(K),K=1,7),(IDLU(K),K=1,7)
C         WRITE(41,4000)(AREAID(K),K=1,7)
C 116     CONTINUE

ccc	num of days in each wateryear month, e.g. month 1 is Oct.
         MONDAY(1)=31
         MONDAY(3)=31
         MONDAY(4)=31
         MONDAY(6)=31
         MONDAY(8)=31
         MONDAY(10)=31
         MONDAY(11)=31
         MONDAY(2)=30
         MONDAY(7)=30
         MONDAY(9)=30
         MONDAY(12)=30
         MONDAY(5)=28

         DO N=1,NOYR
ccc	NYR# water year (num years after 1921)
ccc	P# Precipitation (month, num years after 1921)
            READ(1,'(7x,i4,12f6.2)')NYR(N),(P(M,N),M=1,12)
         enddo

ccc	PCTLNH# total percent of lawn
ccc	PCTLNP# percent of lawn precipitation
ccc	PCTNVH# total percent of native veg.
ccc	PCTNVP# percent of native veg. precip.
ccc	first reading at line 192 in DICU5.1 =>   1URPCT     25    34    37    19    38    47                                     
         READ(1,2000)PCTLNH,PCTLNP,PCTNVH,PCTNVP

ccc	NOLEV# number of levels or decades
ccc	LEV# decade (level)
ccc	first reading at line 193 =>     1PLV        4  1980  1990  2000  2010                                     
         READ(1,2007)NOLEV,(LEV(LV),LV=1,NOLEV)

C...     NON-CRITICAL / CRITICAL WY TOTAL ISLAND ACRERAGES - KG 2/25/88

         DO N=1,NOYR

ccc	NACRES# acreage of current subarea for each year (years from 1921), It can vary with the water 
ccc	 year type, e.g. For the first subarea critical wyt has 11920, all others (A,B,D,W) have 12170 
            READ(2,2002)NACRES(N)
         enddo
C...

         DO N=1,NOYR
ccc	HURBAC# total urban acreage on the subarea (years from 1921)
ccc	HCRPAC# total acres of a crop on the subarea (crop, num years after 1921)
            READ(1,'(11x,19F6.0)')HURBAC(N),(HCRPAC(K,N),K=1,NOCRP)
         enddo

         READ(1,2008)SKIP

         DO LV=1,NOLEV
ccc	Perhaps different levels of landuse development for each decade
ccc	PURAC# ? percent? urban acreage "0s" (level)
ccc	PCRAC# ? percent? crop acreage "all 0s" (crop, level)
            READ(1,2003)PURAC(LV),(PCRAC(K,LV),K=1,NOCRP)
         enddo

ccc	RFFURB# ? urban - nothing in the data
         READ(1,2003)(RFFURB(M),M=1,12)

         ! Variable N in the following line not clearly defined.
         ! N comes from the do loop above. 
         ! It is NOYR+1!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!WARNING
ccc	These do not seem to be used elsewhere
ccc	HPL# ? "01" (N),
ccc	HCAP# ? "all 0s" (N)
         READ(1,2009)HPL(N),HCAP(N)

         DO LV=1,NOLEV
ccc	PPL# ? "nothing in data"(LV),
ccc	PCAP# ? "nothing in data" (LV)
            READ(1,2009)PPL(LV),PCAP(LV)
         enddo 

ccc	if LEVEL {=1996} < 1980 or > 2010 then skip the calcs. and go to 508 
         IF(LEVEL.LT.LEV(1).OR.LEVEL.GT.LEV(NOLEV))GO TO 508
ccc	if LEVEL {=1996} < LEV(4) {=2010} then LV=3
         IF(LEVEL.LE.LEV(NOLEV))LV=NOLEV-1
ccc	if LEVEL {=1996} < LEV(3) {=2000} then LV=2
         IF(LEVEL.LE.LEV(NOLEV-1))LV=NOLEV-2
ccc	if LEVEL {=1996} < LEV(2) {=1990} then LV=1
         IF(LEVEL.LE.LEV(NOLEV-2))LV=NOLEV-3
ccc	if LEVEL {=1996} < LEV(1) {=1980} then LV=0
         IF(LEVEL.LE.LEV(NOLEV-3))LV=NOLEV-4

ccc	calculate the fraction into the decade the level (of development?) the data is
ccc	RL is (1996- LEV(2) {=1990})/(2000-1990) = 0.6
         RL=(LEVEL-LEV(LV))/(LEV(LV+1)-LEV(LV))

ccc	zero out the following variables
ccc	 switches for if a crop is 'RI' and 'FI'
         KZR=0
         KZF=0

ccc	zero out the following variables
         DO K=1,10
            KDEF1(K)=0
            KDEF2(K)=0
         enddo

ccc	initialize the following variables for the current subarea
ccc	these are critical(KDEF1()) and consecutive critical water years(KDEF2())
cccccccc 1939 was left out, why?
         KDEF1(1)=24
         KDEF1(2)=29
         KDEF1(3)=31
         KDEF1(4)=33
         KDEF1(5)=76
         KDEF2(1)=34
         KDEF2(2)=77

         DO K=1,NOCRP
ccc	linear interpolate the percent of each crop acreage at the current level 
ccc	from the between the prior beginning decade to the next decade.
            PCRPAC(K,1)=PCRAC(K,LV)+(PCRAC(K,LV+1)-PCRAC(K,LV))*RL
            
ccc	truncate PCRPAC to the tenths place
            NPCRP=IFIX(PCRPAC(K,1)*10)
            PCRPAC(K,1)=0.0
            PCRPAC(K,1)=NPCRP/10
            
ccc	switches set to the crop array index if a crop is RICE or FIELD
            IF(ACR(K).EQ.'RI')KZR=K
            IF(ACR(K).EQ.'FI')KZF=K
         enddo

ccc	set the percent of each crop acreage the same for every year
         DO N=1,NOYR
            DO K=1,NOCRP
               PCRPAC(K,N)=PCRPAC(K,1)
            enddo
         enddo

         n=1
         do while (N.le.10)

ccc	if the current critical year in KDEF1 is < first WY, go to next KDEF1 
c******	NOT SURE IF THIS WILL WORK, I THINK NFYR=1922 AND KDEF1() USES 0-77 *********************
Ccc            IF(KDEF1(N).LT.NFYR) then !GO TO 180
            IF((1900+KDEF1(N)).LT.NFYR) then
               n=n+1
ccc	if the current critical year in KDEF1 is > last WY, get out of the loop
Ccc            else IF(KDEF1(N).EQ.0.OR.KDEF1(N).GT.LYR) then !GO TO 181
            else IF(KDEF1(N).EQ.0.OR.(1900+KDEF1(N)).GT.LYR) then
               n=11
            else
ccc		NDEF# =number of years KDEF1(n) is from 1921
c****** this eqtn may be flawed if KDEF(N) is between 0-77 and NFYR is 1922
Ccc               NDEF=KDEF1(N)-NFYR+1
               NDEF=(1900+KDEF1(N))-NFYR+1
ccc		the percent of acreage for crop 'RICE' is lowered by 15% for NDEF years from 1921
               PCRPAC(KZR,NDEF)=PCRPAC(KZR,NDEF)-(0.15*PCRPAC(KZR,1))
ccc		the percent of acreage for crop 'FIELD' is raised by ~ 5% for NDEF years from 1921
               PCRPAC(KZF,NDEF)=PCRPAC(KZF,NDEF)+0.33*(0.15*PCRPAC(KZR,1))
               n=n+1
            endif
         enddo
C--------Original Program
C         DO 180 N=1,10
C            IF(KDEF1(N).LT.NFYR)GO TO 180
C            IF(KDEF1(N).EQ.0.OR.KDEF1(N).GT.LYR)GO TO 181
C            NDEF=KDEF1(N)-NFYR+1
C            PCRPAC(KZR,NDEF)=PCRPAC(KZR,NDEF)-(0.15*PCRPAC(KZR,1))
C            PCRPAC(KZF,NDEF)=PCRPAC(KZF,NDEF)+0.33*(0.15*PCRPAC(KZR,1))
C 180     CONTINUE

         n=1
         do while(n.le.10)

ccc	if the current critical year in KDEF2 is < first WY, go to next KDEF2 
c******	NOT SURE IF THIS WILL WORK, I THINK NFYR=1922 AND KDEF1() USES 0-77 *********************
Ccc            IF(KDEF2(N).LT.NFYR) then  !GO TO 182
            IF((1900+KDEF2(N)).LT.NFYR) then  !GO TO 182
               n=n+1
ccc	if the current critical year in KDEF2 is 0 or > last WY, get out of the loop
Ccc            else IF(KDEF2(N).EQ.0.OR.KDEF2(N).GT.LYR)  then !GO TO 183
            else IF(KDEF2(N).EQ.0.OR.(1900+KDEF2(N)).GT.LYR)  then !GO TO 183
               n=11
            else
ccc		NDEF# =number of years critical water year, KDEF2(n), is from 1921
c****** this eqtn may be flawed if KDEF(N) is between 0-77 and NFYR is 1922
Ccc               NDEF=KDEF2(N)-NFYR+1
               NDEF=(1900+KDEF2(N))-NFYR+1
ccc		the percent of acreage for crop 'RICE' is lowered by 35% for NDEF years from 1921
               PCRPAC(KZR,NDEF)=PCRPAC(KZR,NDEF)-(0.35*PCRPAC(KZR,1))
ccc		the percent of acreage for crop 'FIELD' is raised by ~ 11.6% for NDEF years from 1921
               PCRPAC(KZF,NDEF)=PCRPAC(KZF,NDEF)+0.33*(0.35*PCRPAC(KZR,1))
               n=n+1
            endif
         enddo

C--------Original Program
C 181     DO 182 N=1,10
C            IF(KDEF2(N).LT.NFYR)GO TO 182
C            IF(KDEF2(N).EQ.0.OR.KDEF2(N).GT.LYR)GO TO 183
C            NDEF=KDEF2(N)-NFYR+1
C            PCRPAC(KZR,NDEF)=PCRPAC(KZR,NDEF)-(0.35*PCRPAC(KZR,1))
C            PCRPAC(KZF,NDEF)=PCRPAC(KZF,NDEF)+0.33*(0.35*PCRPAC(KZR,1))
C 182     CONTINUE

ccc	percent urban acreage is interpolated for LEVEL time between beg. of decade and end of decade
 183     PURBAC=PURAC(LV)+(PURAC(LV+1)-PURAC(LV))*RL
ccc	percent ? OP is interpolated for LEVEL time between beg. of decade and end of decade
         POP=PPL(LV)+(PPL(LV+1)-PPL(LV))*RL
ccc	percent ? CAPTA is interpolated for LEVEL time between beg. of decade and end of decade
         PCAPTA=PCAP(LV)+(PCAP(LV+1)-PCAP(LV))*RL

C..       SET EFF TO 1.0 (NOT USED IN ISLAND STUDY)  -KG 3/3/88
         DO NY=1,NOYR
            DO M=1,12
               EFF(M,NY)=1.0
            enddo
         enddo
C..
ccc	RMRVBA# ? "0"
	 READ(1,2003)RMRVBA
      
         DO NY=1,NOYR
ccc	RMRVAC# Riparian veg. acreage of historic area (NY),
ccc	WSAC# water surface acreage (NY),
ccc	DGAC# dry grain acreage ="0" in critical years (NY)
            READ(1,'(11X,19F6.0)')RMRVAC(NY),WSAC(NY),DGAC(NY)
            RPLRAC(NY)=RMRVBA-RMRVAC(NY)
         enddo 
         
ccc	NV veg. = total acreage for subarea - total acreage for the other 19 land use categories         
         
         DO 211 L=1,NOLEV
ccc	PRVAC# ? P riparian veg. acreage "0"(L),e(12
ccc	PWSAC# ? P water surface acreage"0"(L),
ccc	PDGAC# ? P Dry Grain acreage "0"(L)
            READ(1,2003)PRVAC(L),PWSAC(L),PDGAC(L)
 211     CONTINUE

         PMRVAC=PRVAC(LV)+(PRVAC(LV+1)-PRVAC(LV))*RL
         NPRV=IFIX(PMRVAC*10)
         PMRVAC=NPRV/10
         PPRVAC=RMRVBA-PMRVAC
         PWSA=PWSAC(LV)+(PWSAC(LV+1)-PWSAC(LV))*RL
         NPRV=IFIX(PWSA*10)
         PWSA=NPRV/10
         PDGA=PDGAC(LV)+(PDGAC(LV+1)-PDGAC(LV))*RL
         NPRV=IFIX(PDGA*10)
         PDGA=NPRV/10
      
ccc	AVEVAP# ? average evap. "all 100"
         READ(1,'(9x,12f6.0)')(AVEVAP(M),M=1,12)
         do m=1,12
             Avevap(m)=Avevap(m)/100.
         enddo

C         !READ(1,1997)(AVEVAP(M),M=1,12)
C         !read(1,'(9x,12i6)') (ievap(m),m=1,12)  
C         !do m=1,12
C             !Avevap(m)=float(ievap(m))/100.
C         !enddo
C         write(*,1997)(AVEVAP(M),M=1,12)
                      
C...
C...SUBROUTINE FOR YEARLY CALCULATIONS*********************
C...
ccc	call to calculate for each wateryear the CU for the current subarea
         CALL CUYEAR
C...
         GO TO 510
 508     WRITE(10,509)KSTUDY,LEVEL
 510  CONTINUE
ccc	In the files I have seen NOLIST is always "-" 
      IF(NOLIST.EQ.'-')GO TO 900
      WRITE(10,998)
C...
    3 FORMAT(I3,A1,2I4)                                                 
   10 FORMAT(I3,2I5,I3,8X,2A6,I5,1X,A1)                                    
   11 FORMAT(8X,7A10)                                                   
   12 FORMAT(8X,7A10)                                                   
   71 FORMAT(3X,A2,4X,2A6)                                              
   84 FORMAT(9X,12F6.2)                                                 
   85 FORMAT(9X,F6.0)                                                   
   91 FORMAT(6X,12I1)                                                   
  100 FORMAT(9X,3F6.2)                                                  
  102 FORMAT(9X,12F6.2)                                                 
  110 FORMAT(9X,7A10/9X,7A10)                                           
  112 FORMAT(1H1,////////////60X,I5,' LEVEL'/45X,                       
     1               38HCONSUMPTIVE USE DETERMINATION FOR AREA,I4,      
     2   /35X,7A10,20X,F4.2)                                            
  114 FORMAT(1H0,60X,2A6)                                               
  115 FORMAT(//5X,24HPRECIPITATION STATION...,7A10,                     
     1      /29X,7A10,10X,14HBASIN INDEX = ,F4.2/114X,'LOSSES = '       
     2      ,F4.2/5X,7A10//5X,7A10//,5X,7A10/1H1)                       
  509 FORMAT(5X,'THE LEVEL INDICATED IS OUTSIDE RANGE OF LAND USE PROJEC',
     1'.  KSTUDY = ',I3,'  LEVEL = ',I5)                                 
  998 FORMAT(1H1/)                                                      
 1997 FORMAT(9X,12F6.2)                                                 
 1999 FORMAT(7X,I4,12F6.2)                                              
 2000 FORMAT(9X,6F6.2)                                                  
 2001 FORMAT( I3,A1,'T1 4   HISTORIC DEPLETION, AREA ',I3/              
     1   I3,A1,'T2            FROM ',2A6,' CU STUDY')                   
 2002 FORMAT(18X,I7)                                                    
 2003 FORMAT(9X,19F6.0)                                                 
 2007 FORMAT(9X,6I6)                                                    
 2008 FORMAT(A1)                                                        
 2009 FORMAT(9X,F6.1,F6.0)                                              
 3000 FORMAT( I3,A1,'T1 4 TOTAL BASIN PRECIPITATION, AREA ',I3/         
     1   I3,A1,'T2            FROM ',2A6,' CU STUDY')                   
 3006 FORMAT( I3,A1,'T1 4 HISTORIC DEPLETION OF APPLIED WATER BY IRR. AND',
     1 ' URBAN, AREA ',I3/
     2   I3,A1,'T2            FROM ',2A6,' CU STUDY')                   
 4000 FORMAT(////12X,'CU OF PRECIP AND SEEPAGE ANALYSIS'/               
     1   17X,7A10/)                                                     
 4020 FORMAT(I3,A1,'T1 4  TOTAL HISTORIC CU OF PRECIPITATION'/          
     1   I3,A1,'T2     ',7A10)                                          
 4021 FORMAT(I3,A1,'T1 4  TOTAL HISTORIC CU OF SEEPAGE'/                
     1   I3,A1,'T2     ',7A10)                                          
  900 STOP 'SUCCESS'
      END
C********************************************************************
      SUBROUTINE INIZ
      REAL NTCUPR,NATRO
      CHARACTER*1 KODE,NOH,NOLIST,SKIP,WYTYP
      CHARACTER*2 ACR(20),KODE2,NTYP
      CHARACTER*3 MONTHS
      CHARACTER*6 CROP,DATE
      CHARACTER*10 AREAID,IDET,IDLU,IDSM,PNM
C..
      COMMON/A/ AVEVAP(12),CHSMCR(12),CHSMGR(12),
     1   CHSMVG(12),CRDEPL(12),CROPCU(12,20),CROPET(12,20),
     2   CRSMST(12),CUA(12),CUINRV(13),CUPPTC(12),CUPPTV(12),
     3   CUPRV(13),CUPTGR(12),CUPTWS(12),DGAC(200),DGET(13)
      COMMON/B/ EFF(12,200),EVAP(13,200),FEETCR(12),FEETGR(12),
     1   FEETVG(12),FTCUA(12),
     2   GRANCU(12),GRDEPL(12),GRSMST(12)
      COMMON/C/ HCAP(200),HCRCUA(13),HCRCUP(13),HCRPAC(20,200),
     1   HGRWTR(12),HL(12),HLWNCU(12),HPL(200),HRSCUW(12),HRUNOF(13),
     2   HNGWTR(13),HUNVCU(12),HURBAC(200),HURCUP(13),HURCUW(13),
     3   HW(12),HWATER(12),HWSCU(12)
      COMMON/D/ KDEF1(10),KDEF2(10),
     1   KR(20),LEV(5),MFI(12,20),MFINC(12,20),MFIC(12,20),MONDAY(12),
     2   NTCUPR(13),NATRO(13),CROOT(20),NYR(200)
      COMMON/E/ P(12,200),PCAP(4),PCRAC(20,5),
     1   PCRCUA(12),PCRPAC(20,200),PCUA(12),PDGAC(4),PDOMRB(12),
     2   PDVIRR(12),PDVTOT(12),PGRWTR(12),PIRRF(12),PLWNCU(12),
     3   PMOIST(12,20),PPL(4),PRECIP(12),
     4   PMRV1I(13),PMRV2P(13),PRSCUW(12),PRUNOF(13),
     5   PNGWTR(13),PRVAC(4),PUNVCU(12),PURAC(4),PURBRF(12),
     6   PURCUW(12),PW(12),PWATER(12),PWSAC(4),PWSCU(12)
      COMMON/F/ RMRVAC(200),RMRV1I(13),RMRV2P(13),RMRV3T(13),
     1   RESDCU(12),RFF(12),RFFURB(12),RPLRAC(200),RPLR1I(13),
     2   RPLR2P(13),RPLR3T(13),RVCU(13),RVET(13)
      COMMON/G/ SEEPCR(13,20),SEEPDG(13),SEEPNV(13),SEEPRW(13),
     1   STARTC(20)
      COMMON/H/ THDPAW(13),THW(12),TOTCUW(12),TOTHCP(13),
     1   TOTHDP(13),TOTHUB(12),TOTHW(12),TOTPDP(13),TOTPUB(12),
     2   TOTPCP(13),TPDPAW(13),
     3   TPR(13),UMOIST(12,20),VEGCU(12),VEGET(12),VGDEPL(12),
     4   VGSMST(12),WSAC(200),WSET(13),WSURCU(12)
      COMMON/AA/ DGMOIS,DGROOT,FACTOR,PCAPTA,PCTLNH,PCTNVP,
     1   PDGA,PMRVAC,PPRVAC,POP,PURBAC,PWSA,STRTDG,
     2   STARTV,VMOIST,VROOT,PCTLNP,PCTNVH,PERCLS
      COMMON/BB/ LEVEL,NACRES(200),NAREA,NOCRP,NOYR,NY
      COMMON/CC/ AREAID(7),CROP(2,20),DATE(2),MONTHS(12),
     1   NOLIST,NOH,KODE(32),KODE2(2),NTYP(9),WYTYP(200)
     2   ,IDET(7),IDLU(7),IDSM(7),PNM(14)
C..*
      COMMON/DD/ CUP(12),CUS(12),CUPLN(12),CUSLN(12),CUPVC(12),
     1   CUSVC(12),HCUP(12),HCUSEEP(12),TCUPLN(12)
C..
      DO 10 I=1,12
      AVEVAP(I)=0.
      CHSMCR(I)=0.
      CHSMGR(I)=0.
      CHSMVG(I)=0.
      CRDEPL(I)=0.
C...
      DO 20 J=1,20
      CROPCU(I,J)=0.
      CROPET(I,J)=0.
      MFI(I,J)=0
      MFINC(I,J)=0
      MFIC(I,J)=0
      PMOIST(I,J)=0.
      UMOIST(I,J)=0.
   20 CONTINUE
      CRSMST(I)=0.
      CUA(I)=0.
      CUPPTC(I)=0.
      CUPPTV(I)=0.
      CUPTGR(I)=0.
      CUPTWS(I)=0.
C...
      DO 30 J=1,100
      EFF(I,J)=0.
      P(I,J)=0.
      EVAP(I,J)=0.
      NACRES(J)=0
   30 CONTINUE
      FEETCR(I)=0.
      FEETGR(I)=0.
      FEETVG(I)=0.
      FTCUA(I)=0.
      GRANCU(I)=0.
      GRDEPL(I)=0.
      GRSMST(I)=0.
      HGRWTR(I)=0.
      HL(I)=0.
      HLWNCU(I)=0.
      HRSCUW(I)=0.
      HUNVCU(I)=0.
      HW(I)=0.
      HWATER(I)=0.
      HWSCU(I)=0.
      MONDAY(I)=0
      PCRCUA(I)=0.
      PCUA(I)=0.
      PDOMRB(I)=0.
      PDVIRR(I)=0.
      PDVTOT(I)=0.
      PGRWTR(I)=0.
      PIRRF(I)=0.
      PLWNCU(I)=0.
      PRECIP(I)=0.
      PRSCUW(I)=0.
      PUNVCU(I)=0.
      PURBRF(I)=0.
      PURCUW(I)=0.
      PW(I)=0.
      PWATER(I)=0.
      PWSCU(I)=0.
      RESDCU(I)=0.
      RFF(I)=0.
      RFFURB(I)=0.
      THW(I)=0.
      TOTCUW(I)=0.
      TOTHDP(I)=0.
      TOTHUB(I)=0.
      TOTHW(I)=0.
      TOTPUB(I)=0.
      VEGCU(I)=0.
      VEGET(I)=0.
      VGDEPL(I)=0.
      VGSMST(I)=0.
      WSURCU(I)=0.
   10 CONTINUE
      RETURN
      END
C********************************************************************
      SUBROUTINE CUYEAR
      REAL NTCUPR,NATRO
      CHARACTER*1 KODE,NOH,NOLIST,SKIP,WYTYP
      CHARACTER*2 ACR(20),KODE2,NTYP
      CHARACTER*3 MONTHS
      CHARACTER*6 CROP,DATE
      CHARACTER*10 AREAID,IDET,IDLU,IDSM,PNM
C..
      COMMON/A/ AVEVAP(12),CHSMCR(12),CHSMGR(12),
     1   CHSMVG(12),CRDEPL(12),CROPCU(12,20),CROPET(12,20),
     2   CRSMST(12),CUA(12),CUINRV(13),CUPPTC(12),CUPPTV(12),
     3   CUPRV(13),CUPTGR(12),CUPTWS(12),DGAC(200),DGET(13)
      COMMON/B/ EFF(12,200),EVAP(13,200),FEETCR(12),FEETGR(12),
     1   FEETVG(12),FTCUA(12),
     2   GRANCU(12),GRDEPL(12),GRSMST(12)
      COMMON/C/ HCAP(200),HCRCUA(13),HCRCUP(13),HCRPAC(20,200),
     1   HGRWTR(12),HL(12),HLWNCU(12),HPL(200),HRSCUW(12),HRUNOF(13),
     2   HNGWTR(13),HUNVCU(12),HURBAC(200),HURCUP(13),HURCUW(13),
     3   HW(12),HWATER(12),HWSCU(12)
      COMMON/D/ KDEF1(10),KDEF2(10),
     1   KR(20),LEV(5),MFI(12,20),MFINC(12,20),MFIC(12,20),MONDAY(12),
     2   NTCUPR(13),NATRO(13),CROOT(20),NYR(200)
      COMMON/E/ P(12,200),PCAP(4),PCRAC(20,5),
     1   PCRCUA(12),PCRPAC(20,200),PCUA(12),PDGAC(4),PDOMRB(12),
     2   PDVIRR(12),PDVTOT(12),PGRWTR(12),PIRRF(12),PLWNCU(12),
     3   PMOIST(12,20),PPL(4),PRECIP(12),
     4   PMRV1I(13),PMRV2P(13),PRSCUW(12),PRUNOF(13),
     5   PNGWTR(13),PRVAC(4),PUNVCU(12),PURAC(4),PURBRF(12),
     6   PURCUW(12),PW(12),PWATER(12),PWSAC(4),PWSCU(12)
      COMMON/F/ RMRVAC(200),RMRV1I(13),RMRV2P(13),RMRV3T(13),
     1   RESDCU(12),RFF(12),RFFURB(12),RPLRAC(200),RPLR1I(13),
     2   RPLR2P(13),RPLR3T(13),RVCU(13),RVET(13)
      COMMON/G/ SEEPCR(13,20),SEEPDG(13),SEEPNV(13),SEEPRW(13),
     1   STARTC(20)
      COMMON/H/ THDPAW(13),THW(12),TOTCUW(12),TOTHCP(13),
     1   TOTHDP(13),TOTHUB(12),TOTHW(12),TOTPDP(13),TOTPUB(12),
     2   TOTPCP(13),TPDPAW(13),
     3   TPR(13),UMOIST(12,20),VEGCU(12),VEGET(12),VGDEPL(12),
     4   VGSMST(12),WSAC(200),WSET(13),WSURCU(12)
      COMMON/AA/ DGMOIS,DGROOT,FACTOR,PCAPTA,PCTLNH,PCTNVP,
     1   PDGA,PMRVAC,PPRVAC,POP,PURBAC,PWSA,STRTDG,
     2   STARTV,VMOIST,VROOT,PCTLNP,PCTNVH,PERCLS
      COMMON/BB/ LEVEL,NACRES(200),NAREA,NOCRP,NOYR,NY
      COMMON/CC/ AREAID(7),CROP(2,20),DATE(2),MONTHS(12),
     1   NOLIST,NOH,KODE(32),KODE2(2),NTYP(9),WYTYP(200)
     2   ,IDET(7),IDLU(7),IDSM(7),PNM(14)
C..*
      COMMON/DD/ CUP(12),CUS(12),CUPLN(12),CUSLN(12),CUPVC(12),
     1   CUSVC(12),HCUP(12),HCUSEEP(12),TCUPLN(12)
C..*
C*****************  YEARLY  DO LOOP  *********************
C...
      DO 500 NY=1,NOYR
C..*                                 KG 2/26/88
ccc	if the water year type is critical
      IF(WYTYP(NY).EQ.'C') THEN
ccc	apply the critical year irrigation schedule to current year (NY)
        DO 219 M=1,12
        DO 218 K=1,NOCRP
ccc	MFI# irrigation scedule (month, crop)
        MFI(M,K)=MFIC#(M,K)
  218   CONTINUE
  219   CONTINUE

ccc	if the water year type is not critical
      ELSE
ccc	apply the non-critical year irrigation schedule to current year (NY)
        DO 222 M=1,12
        DO 221 K=1,NOCRP
        MFI(M,K)=MFINC(M,K)
  221   CONTINUE
  222   CONTINUE
      ENDIF
C...
ccc	In the files I have seen NOLIST is always "-" 
      IF(NOLIST.EQ.'-')GO TO 216
      WRITE(41,4001) NYR(NY),(MONTHS(M),M=1,12)
C..*
  216 TPR(13)=0.0
      DO 217 M=1,12
C..*
      HCUP(M)=0.0
      HCUSEEP(M)=0.0
C..*
ccc	TPR# total precipitation in TAF (month)
      TPR(M)=NACRES(NY)*P(M,NY)*FACTOR/12000.
      TPR(M)=IFIX((TPR(M)+0.05)*10.)/10.
ccc	TPR(13)# total precipitation in TAF (year)
      TPR(13)=TPR(13)+TPR(M)

  217 CONTINUE

  223 HURCUW(13)=0.
      HURCUP(13)=0.
      TLHURB=0.
      TLPURB=0.
      GRTHDP=0.
      TOTHCP(13)=0.
      THDPAW(13)=0.
      GRTPDP=0.
      GRTHW=0.
      HDRAIN=0.
      PDRAIN=0.
      DO 225 J=1,12
      PCUA(J)=0.
      TOTCUW(J)=0.
      TOTHDP(J)=0.
      TOTHCP(J)=0.
      THDPAW(J)=0.
      TOTPDP(J)=0.
      TOTPCP(J)=0.
      TPDPAW(J)=0.
      TOTHW(J)=0.
  225 THW(J)=0.
  228 HRNVAC=NACRES(NY)-HURBAC(NY)-RMRVAC(NY)-WSAC(NY)-DGAC(NY)
      PRNVAC=NACRES(NY)-PURBAC-PMRVAC-PWSA-PDGA
      DO 230 K=1,NOCRP
      HRNVAC=HRNVAC-HCRPAC(K,NY)
      PRNVAC=PRNVAC-PCRPAC(K,NY)
      DO 229 M=1,12
      CROPCU(M,K)=CROPET(M,K)*EVAP(M,NY)/AVEVAP(M)
  229 CONTINUE
  230 CONTINUE
C...
C...CROP AND URBAN CONSUMPTIVE USE  ***********************
C...
  234 KRP=1
      KCR=1
  235 CONTINUE
      TOTPRC=0.
      DO 220 J=1,12
      PRECIP(J)=P(J,NY)*FACTOR+SEEPCR(J,KRP)
      TOTPRC=TOTPRC+PRECIP(J)
  220 CONTINUE
  236 TCUPTC=0.
      TCHSMC=0.
      TCUA=0.
      TCDEPL=0.
      TFTCR=0.
      HCRCUP(13)=0.
      HCRCUA(13)=0.
      THWATR=0.
      TPWATR=0.
      TPCUW=0.
      TPDVIR=0.
      TPIRF=0.
      TPDMRB=0.
      TPRBRF=0.
      TPDVTO=0.
      TCUCR=0.
      DO 280 J=1,12
      TCUCR=TCUCR + CROPCU(J,KRP)
      CUPPTC(J)= AMIN1(PRECIP(J),CROPCU(J,KRP))
      IF(J-1)240,251,240
  240 IF(CRSMST(J-1).GE.PMOIST(J,KRP))GO TO 242
      CHSMCR(J)=AMIN1(AMAX1(PRECIP(J)-CROPCU(J,KRP),PMOIST(J,KRP)
     1    -CRSMST(J-1)),UMOIST(J,KRP)-CRSMST(J-1))
      CRSMST(J)=CRSMST(J-1)+CHSMCR(J)
      GO TO 267
  242 IF(PRECIP(J)-CROPCU(J,KRP))243,245,245
  243 CHSMCR(J)=-AMIN1(CRSMST(J-1)-PMOIST(J,KRP),AMAX1(
     1CROPCU(J,KRP)-PRECIP(J),CRSMST(J-1)-UMOIST(J,KRP)))
      GO TO 246
  245 CHSMCR(J)=AMIN1(PRECIP(J)-CROPCU(J,KRP),UMOIST(J,KRP)-CRSMST(J-1))
  246 CRSMST(J)= CRSMST(J-1) + CHSMCR(J)
      IF(J.EQ.12.AND.KR(KRP).EQ.1)GO TO 250
      GO TO 273
  250 DRAIN=AMAX1(CRSMST(J-1)-PMOIST(J,KRP)-CROPCU(J,KRP),0.0)
      HDRAIN=DRAIN*HCRPAC(KRP,NY)/12000.
      PDRAIN=DRAIN*PCRPAC(KRP,NY)/12000.
      CUPPTC(J)=0.0
      GO TO 274
  251 IF(STARTC(KRP)-PMOIST(J,KRP))252,260,260
  252 CHSMCR(J)=AMIN1(AMAX1(PRECIP(J)-CROPCU(J,KRP),PMOIST(J,KRP)
     1   -STARTC(KRP)),UMOIST(J,KRP)-STARTC(KRP))
      CRSMST(J)=STARTC(KRP)+CHSMCR(J)
      GO TO 267
  260 IF(PRECIP(J)-CROPCU(J,KRP))261,265,265
  261 CHSMCR(J)=-AMIN1(STARTC(KRP)-PMOIST(J,KRP),AMAX1(
     1CROPCU(J,KRP)-PRECIP(J),STARTC(KRP)-UMOIST(J,KRP)))
      GO TO 266
  265 CHSMCR(J)=AMIN1(PRECIP(J)-CROPCU(J,KRP),UMOIST(J,KRP)-STARTC(KRP))
  266 CRSMST(J)= STARTC(KRP)+ CHSMCR(J)
      GO TO 273
  267 CUA(J)=AMAX1(CHSMCR(J)+CROPCU(J,KRP)-PRECIP(J),0.00)
      IF(MFI(J,KRP)-1)270,275,275
  270 IF(J-1)271,272,271
  271 CHSMCR(J)=AMIN1(UMOIST(J,KRP)-CRSMST(J-1),AMAX1(PRECIP(J)
     1    -CROPCU(J,KRP),0.00))
      CRSMST(J)=CRSMST(J-1)+CHSMCR(J)
      GO TO 274
  272 CHSMCR(J)=AMIN1(UMOIST(J,KRP)-STARTC(KRP),AMAX1(PRECIP(J)
     1   -CROPCU(J,KRP),0.00))
      CRSMST(J)=STARTC(KRP)+CHSMCR(J)
      GO TO 274
  273 CUA(J)=AMAX1(CROPCU(J,KRP)- CUPPTC(J)-ABS(CHSMCR(J)),0.00)
      IF(MFI(J,KRP)-1)274,275,275
  274 CUA(J)=0.
  275 CRDEPL(J)= CUPPTC(J)+ CUA(J)+AMAX1(CHSMCR(J),0.00)
     1               -AMAX1(0.,CHSMCR(J)+CUPPTC(J)-PRECIP(J))
      FEETCR(J)= CRDEPL(J)/12.
      FTCUA(J)= CUA(J)/12.
      HW(J)=HCRPAC(KRP,NY)*FEETCR(J)/1000.
      PW(J)=PCRPAC(KRP,NY)*FEETCR(J)/1000.
      HCRCUP(J)=(CRDEPL(J)-CUA(J))*HCRPAC(KRP,NY)/12000.
C..*
      CUP(J)=AMIN1(P(J,NY)*FACTOR*HCRPAC(KRP,NY)/12000,HCRCUP(J))
      HCUP(J)=HCUP(J)+CUP(J)
      CUS(J)=HCRCUP(J)-CUP(J)
      HCUSEEP(J)=HCUSEEP(J)+CUS(J)
C..*
      HCRCUA(J)=(1+PERCLS)*CUA(J)*HCRPAC(KRP,NY)/12000.
      PCRCUA(J)=(1+PERCLS)*CUA(J)*PCRPAC(KRP,NY)/12000.
      PCUA(J)=PCUA(J)+CUA(J)*PCRPAC(KRP,NY)/12000.
      HWATER(J)=HCRPAC(KRP,NY)*(FEETCR(J)+PERCLS* FTCUA(J) )/1000.
      PWATER(J)=PCRPAC(KRP,NY)*FEETCR(J)/1000.
      TCUPTC = TCUPTC + CUPPTC(J)
      TCHSMC = TCHSMC +AMAX1(CHSMCR(J),0.00)
      TCUA = TCUA + CUA(J)
      TCDEPL = TCDEPL + CRDEPL(J)
      TFTCR = TFTCR + FEETCR(J)
      HCRCUP(13)=HCRCUP(13)+HCRCUP(J)
      HCRCUA(13)=HCRCUA(13)+HCRCUA(J)
      THWATR = THWATR + HWATER(J)
      TPWATR=TPWATR+PWATER(J)
      THW(J)=THW(J)+HWATER(J)-HW(J)
      TOTHDP(J)= TOTHDP(J)+ HWATER(J)
      THDPAW(J)=THDPAW(J)+HCRCUA(J)
      TPDPAW(J)=TPDPAW(J)+PCRCUA(J)
      TOTPDP(J)=TOTPDP(J)+PWATER(J)+(PERCLS*FTCUA(J)*
     1          PCRPAC(KRP,NY)/1000.)
  280 CONTINUE
      IF(KR(KRP).EQ.1)GO TO 282
      GO TO 283
  282 TOTHDP(12)=TOTHDP(12)-HDRAIN
      TOTPDP(12)=TOTPDP(12)-PDRAIN
  283 STARTC(KRP)=CRSMST(12)
      IF(KRP.NE.1)GO TO 286
      THLNCU=0.
      TPLNCU=0.
      DO 285 J=1,12
      HLWNCU(J)=HURBAC(NY)*PCTLNH*(FEETCR(J)+PERCLS* FTCUA(J))/1000.
      HURCUW(J)=HURBAC(NY)*PCTLNH*((1+PERCLS)*FTCUA(J))/1000.
C..*
      TCUPLN(J)=HLWNCU(J)-HURCUW(J)
      CUPLN(J)=AMIN1(TCUPLN(J),P(J,NY)*FACTOR*HURBAC(NY)*PCTLNH/12000.)
      HCUP(J)=HCUP(J)+CUPLN(J)
      CUSLN(J)=TCUPLN(J)-CUPLN(J)
      HCUSEEP(J)=HCUSEEP(J)+CUSLN(J)
C..*
      THDPAW(J)=THDPAW(J)+HURCUW(J)
      PURCUW(J)=PURBAC*PCTLNP*((1+PERCLS)*FTCUA(J))/1000.
      PCUA(J)=PCUA(J)+(PURBAC*PCTLNP*CUA(J))/12000.
      TOTPDP(J)=TOTPDP(J)+PURBAC*PCTLNP*PERCLS*FTCUA(J)/1000.
      TPDPAW(J)=TPDPAW(J)+PURCUW(J)
      HL(J)=HURBAC(NY)*PCTLNH*FEETCR(J)/1000.
      THW(J)=THW(J)+HLWNCU(J)-HL(J)
      PLWNCU(J)=PURBAC*PCTLNP*FEETCR(J)/1000.
      THLNCU=THLNCU + HLWNCU(J)
      TPLNCU=TPLNCU + PLWNCU(J)
  285 CONTINUE
  286 CONTINUE
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
      HDRAIN=HDRAIN*1000.
      PDRAIN=PDRAIN*1000.
      HCRCUP(13)=HCRCUP(13)*1000.
      HCRCUA(13)=HCRCUA(13)*1000.
      THWATR=THWATR*1000.
      TPWATR=TPWATR*1000.
      DO 288 L=1,12
      HCRCUP(L)=HCRCUP(L)*1000.
      HCRCUA(L)=HCRCUA(L)*1000.
      HWATER(L)=HWATER(L)*1000.
      PWATER(L)=PWATER(L)*1000.
C..*
      CUP(L)=CUP(L)*1000.
      CUS(L)=CUS(L)*1000.
C..*
  288 CONTINUE
C******* END CONVERSION ***********************************************
ccc	In the files I have seen NOLIST is always "-" 
      IF(NOLIST.EQ.'-')GO TO 290
      WRITE(10,1019) NAREA,(AREAID(J),J=1,7)
      WRITE(10,1020) (CROP(J,KRP),J=1,2),CROOT(KRP)
      WRITE(10,1021)LEVEL
      WRITE(10,1022)LEVEL
      WRITE(10,1023)
      WRITE(10,1024)
      WRITE(10,1025)
      DO 296 M=1,12
      IF(M.EQ.7)WRITE(10,999)

ccc  296 WRITE(10,1026) MONTHS(M),
ccc	PRECIP# precip in inches (M),
ccc	CROPCU# area iii crop consumption use in inches (M,KRP),
ccc	CUPPTC# area iii crop consumption use of precip in inches(M),     1  
ccc	CHSMCR# area iii crop change in soil moisture in inches(M),
ccc	CRSMST# area iii crop soil moisture accum. in inches(M),
ccc	CUA# area iii crop consump. use of applied water in inches(M),
ccc	CRDEPL# area iii crop total monthly CU in inches(M),
ccc	FEETCR# area iii crop total monthly CU in feet(M),
ccc	HCRPAC# area iii crop Historic area in acres(KRP,NY),     2   
ccc	HCRCUP# area iii crop Historic consump. use of precip in AF(M),
ccc	HCRCUA# area iii crop Historic CUA + losses in AF(M),
ccc	HWATER# area iii crop total historic depletion in AF(M),
ccc	PCRPAC# area iii crop iii level area in acres(KRP,NY),
ccc	PWATER# area iii crop iii level of CU in AF(M)
  296 WRITE(10,1026) MONTHS(M),PRECIP(M),CROPCU(M,KRP),CUPPTC(M),
     1  CHSMCR(M),CRSMST(M),CUA(M),CRDEPL(M),FEETCR(M),HCRPAC(KRP,NY),
     2   HCRCUP(M),HCRCUA(M),HWATER(M),PCRPAC(KRP,NY),PWATER(M)

ccc      WRITE(10,1027) TOTPRC,
ccc	TCUCR# total area iii crop consumption use in inches ,
ccc	TCUPTC# total area iii crop consumption use of precip in inches,
ccc	TCHSMC# total area iii crop change in soil moisture in inches,
ccc	TCUA,# total area iii crop consump. use of applied water in inches
ccc	TCDEPL# total area iii crop total monthly CU in inches
ccc	TFTCR# total area iii crop total monthly CU in feet,     1   
ccc	HCRCUP(13)# total area iii crop Historic consump. use of precip in AF,
ccc	HCRCUA(13)# total area iii crop Historic CUA + losses in AF,
ccc	THWATR# total area iii crop total historic depletion in AF,
ccc	TPWATR# total area iii crop iii level of CU in AF
      WRITE(10,1027) TOTPRC,TCUCR,TCUPTC,TCHSMC,TCUA,TCDEPL,TFTCR,
     1   HCRCUP(13),HCRCUA(13),THWATR,TPWATR

      IF(KR(KRP).EQ.1)WRITE(10,1028)HDRAIN,PDRAIN
      IF(KRP.EQ.NOCRP.OR.KRP.EQ.KRP/2*2)WRITE(10,1998)NYR(NY)
C..*
      WRITE(41,4002)(CROP(J,KRP),J=1,2),(CUP(M),M=1,12),
     1    (CUS(M),M=1,12),(HCRCUP(M),M=1,12)
C..*
  290 IF(KRP-NOCRP)300,310,300
  300 KRP=KRP+1
      KCR=KCR+2
      GO TO 235
C...
C...DRY FARM HAY AND GRAIN CONSUMPTIVE USE  ***************
C...       DELTA ONLY
C...
  310 TOTPRC=0.
      TGRCU=0.
      TCUPTG=0.
      TCHSMG=0.
      TGDEPL=0.
      TFTGR=0.
      THGRWT=0.
      TPGRWT=0.0
      DO 2110 J=1,12
      PRECIP(J)=P(J,NY)*FACTOR+SEEPDG(J)
      TOTPRC=TOTPRC+PRECIP(J)
      GRANCU(J)=EVAP(J,NY)/AVEVAP(J)*DGET(J)
      CUPTGR(J)= AMIN1(PRECIP(J),GRANCU(J))
      IF(J-1)2060,2050,2060
 2050 CHSMGR(J)= AMIN1(DGMOIS,AMAX1(PRECIP(J)-GRANCU(J),0.00))
      GRSMST(J)= STRTDG + CHSMGR(J)
      GO TO 2100
 2060 IF(PRECIP(J)- GRANCU(J))2070,2080,2080
 2070 CHSMGR(J)= -AMIN1(GRANCU(J)-PRECIP(J),GRSMST(J-1))
      GO TO 2090
 2080 CHSMGR(J)= AMIN1(PRECIP(J)-GRANCU(J),DGMOIS-GRSMST(J-1))
 2090 GRSMST(J)= GRSMST(J-1)+ CHSMGR(J)
 2100 GRDEPL(J)= CUPTGR(J)+ AMAX1(CHSMGR(J),0.00)
      FEETGR(J)= GRDEPL(J)/12.
      TGRCU = TGRCU + GRANCU(J)
      TCUPTG= TCUPTG + CUPTGR(J)
      TCHSMG = TCHSMG + AMAX1(CHSMGR(J),0.00)
      TGDEPL = TGDEPL + GRDEPL(J)
      TFTGR = TFTGR + FEETGR(J)
      HGRWTR(J) =DGAC(NY) * FEETGR(J)/1000.
C..*
      CUP(J)=AMIN1(HGRWTR(J),P(J,NY)*FACTOR*DGAC(NY)/12000.)
      HCUP(J)=HCUP(J)+CUP(J)
      CUS(J)=HGRWTR(J)-CUP(J)
      HCUSEEP(J)=HCUSEEP(J)+CUS(J)
C..*
      PGRWTR(J) =PDGA * FEETGR(J)/1000.
      THGRWT = THGRWT + HGRWTR(J)
      TPGRWT = TPGRWT + PGRWTR(J)
      TOTHDP(J)=TOTHDP(J)+HGRWTR(J)
      TOTPDP(J)=TOTPDP(J)+PGRWTR(J)
 2110 CONTINUE
      STRTDG=GRSMST(12)
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
      THGRWT=THGRWT*1000.
      TPGRWT=TPGRWT*1000.
      DO 2119 L=1,12
      HGRWTR(L)=HGRWTR(L)*1000.
      PGRWTR(L)=PGRWTR(L)*1000.
C..*
      CUP(L)=CUP(L)*1000.
      CUS(L)=CUS(L)*1000.
C..*
 2119 CONTINUE
C******* END CONVERSION ***********************************************
ccc	In the files I have seen NOLIST is always "-" 
      IF(NOLIST.EQ.'-')GO TO 317
      WRITE(10,2700)NAREA,DGROOT
      WRITE(10,2430)
      WRITE(10,2440)
      WRITE(10,2450)
      WRITE(10,2460)
      DO 2120 M=1,12
      IF(M.EQ.7)WRITE(10,999)

ccc 2120 WRITE(10,2470) MONTHS(M),PRECIP(M),
ccc	GRANCU# area iii hay and grain total consump. use in inches(M),
ccc	CUPTGR# area iii hay and grain CU of seep and precip in inches(M),
ccc	CHSMGR# area iii hay and grain change in soil moisture in inches(M),     1
ccc	GRSMST# area iii hay and grain soil moisture accum. in inches(M),
ccc	GRDEPL# area iii hay and grain monthly depletion in inches(M),
ccc	FEETGR# area iii hay and grain monthly depletion in feet(M),
ccc	DGAC# area iii hay and grain historic area in acres(NY),
ccc	HGRWTR# area iii hay and grain historic consumptive use in AF(M),
ccc	PDGA# area iii hay and grain projected area in acres,
ccc	PGRWTR# area iii hay and grain projected CU in AF(M)
 2120 WRITE(10,2470) MONTHS(M),PRECIP(M),GRANCU(M),CUPTGR(M),CHSMGR(M),
     1GRSMST(M),GRDEPL(M),FEETGR(M),DGAC(NY),HGRWTR(M),PDGA,PGRWTR(M)

ccc      WRITE(10,2480) TOTPRC,
ccc	TGRCU# total area iii hay and grain total consump. use in inches,
ccc	TCUPTG# total area iii hay and grain CU of seep and precip in inches,
ccc	TCHSMG# total area iii hay and grain change in soil moisture in inches,
ccc	TGDEPL# total area iii hay and grain monthly depletion in inches,
ccc	TFTGR# total area iii hay and grain monthly depletion in feet,
ccc	THGRWT# total area iii hay and grain historic consumptive use in AF,     1
ccc	TPGRWT# total area iii hay and grain projected CU in AF
      WRITE(10,2480) TOTPRC,TGRCU,TCUPTG,TCHSMG,TGDEPL,TFTGR,THGRWT,
     1TPGRWT

C..*
      WRITE(41,4003)(CUP(M),M=1,12),(CUS(M),M=1,12),(HGRWTR(M),M=1,12)
C..*
  317 CONTINUE
C...
C...NATIVE VEGETATION CONSUMPTIVE USE  ********************
C...
      TPNVCU=0.
      THNVCU=0.
      TVGCU=0.
      TCUPTV=0.
      TCHSMV=0.
      TVDEPL=0.
      TFTVG=0.
      HNGWTR(13)=0.0
      NTCUPR(13)=0.
      NATRO(13)=0.0
      RVET(13)=0.
      CUPRV(13)=0.
      CUINRV(13)=0.
      RPLR2P(13)=0.
      RPLR1I(13)=0.
      RMRV2P(13)=0.
      RMRV1I(13)=0.
      RPLR3T(13)=0.
      RMRV3T(13)=0.
      HRUNOF(13)=0.
      PRUNOF(13)=0.0
      TOTPCP(13)=0.
      PNGWTR(13)=0.
      PMRV2P(13)=0.
      TPDPAW(13)=0.
      PMRV1I(13)=0.0
      DO 316 M=1,12
      VEGCU(M)=VEGET(M)
  316 CONTINUE
      DO 318 J=1,12
      PRECIP(J)=P(J,NY)*FACTOR+SEEPNV(J)
      VEGCU(J)=VEGET(J)*EVAP(J,NY)/AVEVAP(J)
  318 CONTINUE
  319 TOTPRC = 0.0
      DO 380 J=1,12
      TOTPRC=TOTPRC+PRECIP(J)
      CUPPTV(J)=AMIN1(PRECIP(J),VEGCU(J))
      IF(J.NE.1)GO TO 330
  320 CHSMVG(J)=AMIN1(VMOIST,AMAX1(PRECIP(J)-VEGCU(J),0.00))
      VGSMST(J)= STARTV+ CHSMVG(J)
      GO TO 370
  330 IF(PRECIP(J).GE.VEGCU(J))GO TO 350
  340 CHSMVG(J)= -AMIN1(VEGCU(J)-PRECIP(J),VGSMST(J-1))
      GO TO 360
  350 CHSMVG(J)=AMIN1(PRECIP(J)-VEGCU(J),VMOIST- VGSMST(J-1))
  360 VGSMST(J)= VGSMST(J-1) + CHSMVG(J)
  370 VGDEPL(J)= CUPPTV(J)+AMAX1(CHSMVG(J),0.00)
      FEETVG(J)= VGDEPL(J)/12.
      TVGCU = TVGCU + VEGCU(J)
      TCUPTV = TCUPTV + CUPPTV(J)
      TCHSMV = TCHSMV +AMAX1(CHSMVG(J),0.00)
      TVDEPL = TVDEPL + VGDEPL(J)
      TFTVG = TFTVG + FEETVG(J)
      A=HRNVAC*FEETVG(J)/1000.
      HNGWTR(J) = IFIX((A+0.05)*10.)/10.
      A=PRNVAC*FEETVG(J)/1000.
      PNGWTR(J) = IFIX((A+0.05)*10.)/10.
      HNGWTR(13)=HNGWTR(13)+HNGWTR(J)
      TOTHDP(J)=TOTHDP(J)+HNGWTR(J)
      TOTPDP(J)=TOTPDP(J)+PNGWTR(J)
  375 HUNVCU(J)= HURBAC(NY) * PCTNVH * FEETVG(J)/1000.
C..*
      CUPVC(J)=AMIN1(HUNVCU(J),P(J,NY)*FACTOR*HURBAC(NY)*PCTNVH/12000.)
      HCUP(J)=HCUP(J)+CUPVC(J)
      CUSVC(J)=HUNVCU(J)-CUPVC(J)
      HCUSEEP(J)=HCUSEEP(J)+CUSVC(J)
C..*
      PUNVCU(J)= PURBAC * PCTNVP * FEETVG(J)/1000.
      THNVCU = THNVCU + HUNVCU(J)
      TPNVCU = TPNVCU + PUNVCU(J)
      TOTHUB(J) = HLWNCU(J)+HUNVCU(J)
      HURCUP(J)=TOTHUB(J)-HURCUW(J)
      TOTPUB(J) = PLWNCU(J)+PUNVCU(J)
      TLHURB = TLHURB + TOTHUB(J)
      HURCUW(13)=HURCUW(13)+HURCUW(J)
      HURCUP(13)=HURCUP(13)+HURCUP(J)
      TLPURB = TLPURB + TOTPUB(J)
      A =TOTHDP(J)+TOTHUB(J)
      TOTHDP(J) = IFIX((A+0.05)*10.)/10.
      TOTHCP(J)=TOTHDP(J)-THDPAW(J)
      A =TOTPDP(J)+TOTPUB(J)
      TOTPDP(J) = IFIX((A+0.05)*10.)/10.
      TOTPCP(J)=TOTPDP(J)-TPDPAW(J)
      TOTHW(J)=TOTHDP(J)-THW(J)
      GRTHW=GRTHW+TOTHW(J)
      GRTHDP = GRTHDP + TOTHDP(J)
      TOTHCP(13)=TOTHCP(13)+TOTHCP(J)
      THDPAW(13)=THDPAW(13)+THDPAW(J)
      GRTPDP = GRTPDP + TOTPDP(J)
      TPCUW=TPCUW+PCUA(J)
      PDVIRR(J)=PCUA(J)/EFF(J,NY)
      TPDVIR=TPDVIR+PDVIRR(J)
      RFF(J)=1-(1+PERCLS)*EFF(J,NY)
      PIRRF(J)=PDVIRR(J)*RFF(J)
      IF(J.EQ.12)PIRRF(J)=PIRRF(J)+PDRAIN
      TPIRF=TPIRF+PIRRF(J)
      PDOMRB(J)=PCAPTA*POP*MONDAY(J)/325826.
      PURBRF(J)=PDOMRB(J)*RFFURB(J)
      TPDMRB=TPDMRB+PDOMRB(J)
      TPRBRF=TPRBRF+PURBRF(J)
      A =PDVIRR(J)+PDOMRB(J)
      PDVTOT(J) = IFIX((A+0.05)*10.)/10.
      TPDVTO=TPDVTO+PDVTOT(J)
      RPLR2P(J)=CUPRV(J)*RPLRAC(NY)/12000.
      RPLR1I(J)=CUINRV(J)*RPLRAC(NY)/12000.
      RPLR2P(13)=RPLR2P(13)+RPLR2P(J)
      RPLR1I(13)=RPLR1I(13)+RPLR1I(J)
      TOTPCP(13)=TOTPCP(13)+TOTPCP(J)
      PNGWTR(13)=PNGWTR(13)+PNGWTR(J)
      TPDPAW(13)=TPDPAW(13)+TPDPAW(J)
  380 CONTINUE
  391 CONTINUE
C..*
      DO 393 M=1,12
      CUP(M)=AMIN1(HNGWTR(M),P(M,NY)*FACTOR*HRNVAC/12000.)
      HCUP(M)=HCUP(M)+CUP(M)
      CUS(M)=HNGWTR(M)-CUP(M)
      HCUSEEP(M)=HCUSEEP(M)+CUS(M)
  393 CONTINUE
C..*
C..
C..NATIVE VEGETATION CU SUMMARY**************************
C..   DELTA ONLY
C..
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
      HNGWTR(13)=HNGWTR(13)*1000.
      PNGWTR(13)=PNGWTR(13)*1000.
      DO 394 L=1,12
      HNGWTR(L)=HNGWTR(L)*1000.
      PNGWTR(L)=PNGWTR(L)*1000.
C..*
      CUP(L)=CUP(L)*1000.
      CUS(L)=CUS(L)*1000.
C..*
  394 CONTINUE
C******* END CONVERSION ***********************************************
ccc	In the files I have seen NOLIST is always "-" 
      IF(NOLIST.EQ.'-')GO TO 399
      WRITE(10,1049)VROOT
      WRITE(10,1050)LEVEL
      WRITE(10,1051)
      WRITE(10,1052)
      WRITE(10,1053)
      DO 392 M=1,12
      IF(M.NE.7)GO TO 392
      WRITE(10,999)

ccc  392 WRITE(10,1054) MONTHS(M),
ccc	PRECIP# precip. in inches (M),
ccc	VEGCU# native veg. CU in inches (M),
ccc	CUPPTV# native veg. CU of precip. in inches (M),
ccc	CHSMVG# native veg. change in soil moisture in inches (M),     1
ccc	VGSMST# native veg. soil moisture accumulated in inches (M),
ccc	VGDEPL# native veg. monthly depletion in inches (M),
ccc	FEETVG# native veg. monthly depletion in feet (M),
ccc	HRNVAC# acreage of historic area,
ccc	HNGWTR# historic CU in AF(M),
ccc	PRNVAC# "LEVEL" level area acreage,
ccc	PNGWTR# projected CU in AF(M)
  392 WRITE(10,1054) MONTHS(M),PRECIP(M),VEGCU(M),CUPPTV(M),CHSMVG(M),
     1VGSMST(M),VGDEPL(M),FEETVG(M),HRNVAC,HNGWTR(M),PRNVAC,PNGWTR(M)

ccc	TOTPRC# Total for the year precip. in inches (M),
ccc	TVGCU# Total for the year native veg. CU in inches (M),
ccc	TCUPTV# Total for the year native veg. CU of precip. in inches (M),
ccc	TCHSMV# Total for the year native veg. change in soil moisture in inches (M),     1
ccc	TVDEPL# Total for the year native veg. monthly depletion in inches (M),
ccc	TFTVG# Total for the year native veg. monthly depletion in feet (M),
ccc	HNGWTR(13)# Total for the year historic CU in AF(M),
ccc	PNGWTR(13)# Total for the year projected CU in AF(M)
      WRITE(10,1055) TOTPRC,TVGCU,TCUPTV,TCHSMV,TVDEPL,TFTVG
     1   ,HNGWTR(13),PNGWTR(13)

      WRITE(10,1998)NYR(NY)
C..*
      WRITE(41,4004)(CUP(M),M=1,12),(CUS(M),M=1,12),(HNGWTR(M),M=1,12)
C..*
  399 CONTINUE
C..
C.. RIPARIAN VEG. CONSUMPTIVE USE****************************
C..     DELTA ONLY
C..
      TOTPRC=0.
      TWSCU=0.
      TCUPTW=0.
      TOTRES=0.
      THWSCU=0.
      THRSWS=0.
      TPWSCU=0.
      TPRSWS=0.0
      RVCU(13)=0.
      CUPRV(13)=0.
      CUINRV(13)=0.
      RMRV2P(13)=0.
      RMRV1I(13)=0.
      PMRV2P(13)=0.
      PMRV1I(13)=0.0
      TOTPRC=0.0
      DO 2121 J=1,12
      PRECIP(J)=P(J,NY)*FACTOR+SEEPRW(J)
      TOTPRC=TOTPRC+PRECIP(J)
      RVCU(J)=EVAP(J,NY)/AVEVAP(J)*RVET(J)
      CUPRV(J)=AMIN1(PRECIP(J),RVCU(J))
      CUINRV(J)=RVCU(J)-CUPRV(J)
      RMRV2P(J)=CUPRV(J)*RMRVAC(NY)/12000.
C..*
      CUP(J)=AMIN1(RMRV2P(J),P(J,NY)*FACTOR*RMRVAC(NY)/12000.)
      HCUP(J)=HCUP(J)+CUP(J)
      CUS(J)=RMRV2P(J)-CUP(J)
      HCUSEEP(J)=HCUSEEP(J)+CUS(J)
C..*
      RMRV1I(J)=CUINRV(J)*RMRVAC(NY)/12000.
      PMRV2P(J)=CUPRV(J)*PMRVAC/12000.
      PMRV1I(J)=CUINRV(J)*PMRVAC/12000.
      RVCU(13)=RVCU(13)+RVCU(J)
      CUPRV(13)=CUPRV(13)+CUPRV(J)
      CUINRV(13)=CUINRV(13)+CUINRV(J)
      RMRV2P(13)=RMRV2P(13)+RMRV2P(J)
      RMRV1I(13)=RMRV1I(13)+RMRV1I(J)
      PMRV2P(13)=PMRV2P(13)+PMRV2P(J)
      PMRV1I(13)=PMRV1I(13)+PMRV1I(J)
C..*
 2121 CONTINUE
C**** UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI (6/87) **********
      DO 2122 L=1,12
      CUP(L)=CUP(L)*1000.
      CUS(L)=CUS(L)*1000.
 2122 RMRV2P(L)=RMRV2P(L)*1000.
C*** END CONVERSIONS ************************************************
C
ccc	In the files I have seen NOLIST is always "-" 
      IF (NOLIST.EQ.'-')GO TO 2123
      WRITE(41,4005)(CUP(M),M=1,12),(CUS(M),M=1,12),(RMRV2P(M),M=1,12)
C..*
 2123 CONTINUE
C..
C..WATER SURFACE CONSUMPTIVE USE*****************************
C..    DELTA ONLY
C..
      DO 2125 J=1,12
      WSURCU(J)=EVAP(J,NY)/AVEVAP(J)*WSET(J)
      CUPTWS(J)= AMIN1(PRECIP(J),WSURCU(J))
      RESDCU(J)= AMAX1(WSURCU(J)-PRECIP(J),0.00)
      TWSCU= TWSCU+ WSURCU(J)
      TCUPTW= TCUPTW+ CUPTWS(J)
      TOTRES= TOTRES+ RESDCU(J)
      HWSCU(J)= (CUPTWS(J)* WSAC(NY))/12000.
C..*
      CUP(J)=AMIN1(HWSCU(J),P(J,NY)*FACTOR*WSAC(NY)/12000.)
      HCUP(J)=HCUP(J)+CUP(J)
      CUS(J)=HWSCU(J)-CUP(J)
      HCUSEEP(J)=HCUSEEP(J)+CUS(J)
C..*
      PWSCU(J)= (CUPTWS(J)* PWSA)/12000.
      HRSCUW(J)=(RESDCU(J)* WSAC(NY))/12000.
      PRSCUW(J)=(RESDCU(J)* PWSA)/12000.
      THWSCU= THWSCU+ HWSCU(J)
      TPWSCU= TPWSCU+ PWSCU(J)
      THRSWS= THRSWS+ HRSCUW(J)
      TPRSWS= TPRSWS+ PRSCUW(J)
C*** TOTAL APPLIED WATER CALC ADDED ********************* KG (5/21)
      THDPAW(J)=THDPAW(J)+RMRV1I(J)+HRSCUW(J)
C***
C..*
      RMRV2P(J)=RMRV2P(J)/1000.
C..*
      TOTHDP(J)=TOTHDP(J)+RMRV2P(J)+RMRV1I(J)+HWSCU(J)+HRSCUW(J)
      TOTPDP(J)=TOTPDP(J)+PMRV2P(J)+PMRV1I(J)+PWSCU(J)+PRSCUW(J)
 2125 CONTINUE
C..
C..RIPARIAN VEG. CONSUMPTIVE USE SUMMARY*******************
C..      DELTA ONLY
C..
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
      RMRV2P(13)=RMRV2P(13)*1000.
      RMRV1I(13)=RMRV1I(13)*1000.
      PMRV2P(13)=PMRV2P(13)*1000.
      PMRV1I(13)=PMRV1I(13)*1000.
      DO 2130 L=1,12
      RMRV2P(L)=RMRV2P(L)*1000.
      RMRV1I(L)=RMRV1I(L)*1000.
      PMRV2P(L)=PMRV2P(L)*1000.
      PMRV1I(L)=PMRV1I(L)*1000.
C..*
      CUP(L)=CUP(L)*1000.
      CUS(L)=CUS(L)*1000.
      HWSCU(L)=HWSCU(L)*1000.
      CUPLN(L)=CUPLN(L)*1000.
      CUSLN(L)=CUSLN(L)*1000.
      TCUPLN(L)=TCUPLN(L)*1000.
      CUPVC(L)=CUPVC(L)*1000.
      CUSVC(L)=CUSVC(L)*1000.
      HUNVCU(L)=HUNVCU(L)*1000.
      HCUP(L)=HCUP(L)*1000.
      HCUSEEP(L)=HCUSEEP(L)*1000.
C..*
 2130 CONTINUE
C******* END CONVERSION ***********************************************
cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
ccc	In the files I have seen NOLIST is always "-" so from here we would always go to 439
cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
      IF(NOLIST.EQ.'-')GO TO 439
C..*

ccc      WRITE(41,4006)(
ccc	CUP# CU of water surface precip. (month) (M),M=1,12),(
ccc	CUS# water surface seep (month) (M),M=1,12),(
ccc	HWSCU# total water surface CU (month) (M),M=1,12)
      WRITE(41,4006)(CUP(M),M=1,12),(CUS(M),M=1,12),(HWSCU(M),M=1,12)

ccc      WRITE(41,4007)(CUPLN# CU of precip. of urban lawn (month) (M),M=1,12),(CUSLN# CU of seep
ccc	of urban lawn (month) (M),M=1,12),
      WRITE(41,4007)(CUPLN(M),M=1,12),(CUSLN(M),M=1,12),
     1   (TCUPLN(M),M=1,12)

ccc      WRITE(41,4008)(
ccc	CUPVC# CU of precip URBAN VAC? (month) (M),M=1,12),(
ccc	CUSVC# CU of URBAN VAC 
ccc	seep. (month) (M),M=1,12),
ccc     1   (HUNVCU# total URBAN VAC CU (month) (M),M=1,12)
      WRITE(41,4008)(CUPVC(M),M=1,12),(CUSVC(M),M=1,12),
     1   (HUNVCU(M),M=1,12)

ccc      WRITE(41,4009)(
ccc	HCUP# total consumptive use of precipitation (month) (M),M=1,12),(
ccc	HCUSEEP# total consumptive use of seepage (month) (M) ,M=1,12)
      WRITE(41,4009)(HCUP(M),M=1,12),(HCUSEEP(M),M=1,12)

C..*
ccccccccccccccccccccccccccccccccccccccccccccccccccccc
ccc	RIPARIAN VEGETATION DEPLETION ANALYSIS
      WRITE(10,2801) NAREA
      WRITE(10,2803)
      WRITE(10,2804)
      WRITE(10,2805)
      WRITE(10,2806)
      WRITE(10,2807)
      DO 2160 M=1,12
      IF(M.EQ.7)WRITE(10,999)

ccc 2160 WRITE(10,2808) MONTHS(M),
ccc	PRECIP# precip and seep in inches (M) ,
ccc	RVCU# Riparian total consumptive use in inches (M) ,
ccc	CUPRV# Riparian CU of seep and precip. in inches (M),     1   
ccc	CUINRV# Riparian residual CU to be met from inflow in inches (M),
ccc	RMRVAC# Riparian acreage of historic area(NY),
ccc	RMRV2P# Riparian historic consump. use of precip in AF(M),
ccc	RMRV1I# Riparian historic residual consump. use in AF(M) ,	2
ccc	PMRVAC# Riparian acreage of projected area ,
ccc	PMRV2P# Riparian projected consump. use of precip. in AF (M), 
ccc	PMRV1I# Riparian projected residual consump. use in AF(M)
 2160 WRITE(10,2808) MONTHS(M),PRECIP(M),RVCU(M),CUPRV(M),
     1   CUINRV(M),RMRVAC(NY),RMRV2P(M),RMRV1I(M),
     2   PMRVAC,PMRV2P(M),PMRV1I(M)

ccc      WRITE(10,2809)
ccc	TOTPRC# total precip and seep in inches,
ccc	RVCU(13)# total for year Riparian total consumptive use in inches (13),
ccc	CUPRV(13)# total for year Riparian CU of seep and precip. in inches (13),
ccc	CUINRV(13)# total for year Riparian residual CU to met from inflow in inches (13),
ccc	RMRV2P(13)# total for year Riparian historic consump. use of precip in AF(13),     1   
ccc	RMRV1I(13)# total for year Riparian historic residual consump. use in AF(13),
ccc	PMRV2P(13)# total for year Riparian projected consump. use of precip. in AF (13),
ccc	PMRV1I(13)# total for year Riparian projected residual consump. use in AF(13)
      WRITE(10,2809)TOTPRC,RVCU(13),CUPRV(13),CUINRV(13),RMRV2P(13),
     1   RMRV1I(13),PMRV2P(13),PMRV1I(13)
C..
C..WATER SURFACE CU SUMMARY***********************
C..   DELTA ONLY
C..
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
      THWSCU=THWSCU*1000.
      THRSWS=THRSWS*1000.
      TPWSCU=TPWSCU*1000.
      TPRSWS=TPRSWS*1000.
      DO 2140 L=1,12
C..*      HWSCU(L)=HWSCU(L)*1000.
      HRSCUW(L)=HRSCUW(L)*1000.
      PWSCU(L)=PWSCU(L)*1000.
      PRSCUW(L)=PRSCUW(L)*1000.
 2140 CONTINUE
C******* END CONVERSION ***********************************************
ccccccccccccccccccccccccccccccccccccccccccccccccccccc
ccc	WATER SURFACE DEPLETION ANALYSIS
      WRITE(10,2802) NAREA
      WRITE(10,2803)
      WRITE(10,2804)
      WRITE(10,2805)
      WRITE(10,2806)
      WRITE(10,2807)
      DO 2170 M=1,12
      IF(M.EQ.7)WRITE(10,999)

ccc     WRITE(10,2808) MONTHS(M),
ccc	PRECIP# precip and seep in inches (M) ,
ccc	WSURCU# WATER SURFACE total consumptive use in inches (M) ,
ccc	CUPTWS# WATER SURFACE CU of seep and precip. in inches (M),     1   
ccc	RESDCU# WATER SURFACE residual CU to met from inflow in inches (M),
ccc	1WSAC# WATER SURFACE acreage of historic area(NY),
ccc	HWSCU# WATER SURFACE historic consump. use of precip in AF(M),
ccc	HRSCUW# WATER SURFACE historic residual consump. use in AF(M) ,	2
ccc	PWSA# WATER SURFACE acreage of projected area ,
ccc	PWSCU# WATER SURFACE projected consump. use of precip. in AF (M), 
ccc	PRSCUW# WATER SURFACE projected residual consump. use in AF(M)
      WRITE(10,2808) MONTHS(M),PRECIP(M),WSURCU(M),CUPTWS(M),RESDCU(M),
     1WSAC(NY),HWSCU(M),HRSCUW(M),PWSA,PWSCU(M),PRSCUW(M)

 2170 CONTINUE

ccc      WRITE(10,2809)
ccc	TOTPRC# total precip and seep in inches,
ccc	TWSCU# total for year WATER SURFACE total consumptive use in inches (13),
ccc	TCUPTW# total for year WATER SURFACE CU of seep and precip. in inches (13),
ccc	TOTRES# total for year WATER SURFACE residual CU to met from inflow in inches (13),
ccc	THWSCU# total for year WATER SURFACE historic consump. use of precip in AF(13),     1   
ccc	THRSWS# total for year WATER SURFACE historic residual consump. use in AF(13),
ccc	TPWSCU# total for year WATER SURFACE projected consump. use of precip. in AF (13),
ccc	TPRSWS# total for year WATER SURFACE projected residual consump. use in AF(13)
      WRITE(10,2809) TOTPRC,TWSCU,TCUPTW,TOTRES,THWSCU,THRSWS,
     1TPWSCU,TPRSWS
      WRITE(10,1998)NYR(NY)
C...
C..URBAN AND TOTAL BASIN CU SUMMARY********************
C..   DELTA ONLY
C..
      TOTHDP(13)=0.
      TOTPDP(13)=0.0
      DO 416 J=1,12
      TOTHDP(13)=TOTHDP(13)+TOTHDP(J)
      TOTPDP(13)=TOTPDP(13)+TOTPDP(J)
  416 CONTINUE
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
      TPR(13)=TPR(13)*1000.
      HURCUW(13)=HURCUW(13)*1000.
      HURCUP(13)=HURCUP(13)*1000.
      TOTHDP(13)=TOTHDP(13)*1000.
      TOTPDP(13)=TOTPDP(13)*1000.
      TLHURB=TLHURB*1000.
      TLPURB=TLPURB*1000.
  439 DO 2150 L=1,12
      TPR(L)=TPR(L)*1000.
      HURCUW(L)=HURCUW(L)*1000.
      TOTHDP(L)=TOTHDP(L)*1000.
      TOTPDP(L)=TOTPDP(L)*1000.
      TOTHUB(L)=TOTHUB(L)*1000.
      TOTPUB(L)=TOTPUB(L)*1000.
      HURCUP(L)=HURCUP(L)*1000.
      THDPAW(L)=THDPAW(L)*1000.
 2150 CONTINUE
C******* END CONVERSION ***********************************************
ccc	In the files I have seen NOLIST is always "-"
      IF(NOLIST.EQ.'-')GO TO 450
      WRITE(10,1008)NAREA,(AREAID(K),K=1,7),(DATE(K),K=1,2)
      WRITE(10,1041)
      WRITE(10,1042)
      WRITE(10,1043)
      WRITE(10,1044)LEVEL
      WRITE(10,1045)
      WRITE(10,1046)
      DO 440 M=1,12
      IF(M.NE.7)GO TO 440
      WRITE(10,999)

ccc  440 WRITE(10,1047) 
ccc	MONTHS# month (M),
ccc	HURBAC# Hist. urban area in acres (NY) ,
ccc	HURCUP# Hist CU of precip in AF(M) ,
ccc	HURCUW# Hist depletion of applied water in AF (M)     1    ,
ccc	TOTHUB# Hist total depl. in AF (M),
ccc	PURBAC# num. level urban area in acres ,
ccc	TOTPUB# num. level urban consump. use in AF (M),
ccc	TPR# total basin precip in AF (M),
ccc	TOTHDP# total consumptive use, historic in AF (M),
ccc	TOTPDP# total consumptive use, num. level in AF(M)
  440 WRITE(10,1047) MONTHS(M),HURBAC(NY),HURCUP(M),HURCUW(M)
     1    ,TOTHUB(M),PURBAC,TOTPUB(M),TPR(M),TOTHDP(M),TOTPDP(M)

ccc	HURCUP(13)# total for year Hist CU of precip in AF(M) ,
ccc	HURCUW(13)# total for year Hist depletion of applied water in AF (M)     1    ,
ccc	TLHURB# total for year Hist total depl. in AF (M),
ccc	TLPURB# total for year num. level urban consump. use in AF (M),
ccc	TPR(13)# total for year total basin precip in AF (M),
ccc	TOTHDP(13)# total for year total consumptive use, historic in AF (M),
ccc	TOTPDP(13)# total for year total consumptive use, num. level in AF(M)
      WRITE(10,1048)HURCUP(13),HURCUW(13),TLHURB,TLPURB,TPR(13),
     1   TOTHDP(13),TOTPDP(13)

      WRITE(10,1998)NYR(NY)
C...
C...DATA OUTPUT FILES  **************************************
C...
ccc	TPR# precipitation by month and water year for each subarea in AF, see p.33 Feb. '95 report
  450 WRITE(12,3003)NAREA,KODE(12),NYR(NY),(TPR(M),M=1,12)

ccc	NOH is equal to " " so this block writes out         
      IF(NOH.EQ.'-')GO TO 454
C..*
      WRITE(13,3003)NAREA,KODE(13),NYR(NY),(HCUP(M),M=1,12)
ccc	HCUSEEP# seepage used to satisfy consumptive use demands by months and water years
ccc	for each subarea in AF. p.33 of DICU report
      WRITE(14,3003)NAREA,KODE(14),NYR(NY),(HCUSEEP(M),M=1,12)
C..*
ccc	THDPAW# depletion of applied water by month and water year in AF for each subarea.
      WRITE(17,3003)NAREA,KODE(17),NYR(NY),(THDPAW(M),M=1,12)
ccc	TOTHDP# consumptive use by month and water year for each subarea in AF.
      WRITE(27,3003)NAREA,KODE(27),NYR(NY),(TOTHDP(M),M=1,12)
  454 CONTINUE

  500 CONTINUE
C********************...END OF YEARLY  DO LOOP  **********************

  999 FORMAT(1H )                                                       
 1008 FORMAT(///'  AREA',I4,3X,7A10,5X,'STUDY',2A6/)                    
 
ccc 1019 FORMAT(                                 AREA iiii  CROP DEPLETION ANALYSIS
ccc                                      AAAAAAAAAAaaaaaaaaaaAAAAAAAAAAaaaaaaaaaaAAAAAAAAAAaaaaaaaaaaAAAAAAAAAA)    
ccc 1020 FORMAT(OIRRIGATED AAAAAAaaaaaa                                                                               EFF. ROOT DEPTH ff.f FT.)  
ccc 1021 FORMAT(O                                                          CONSUMP.                     HISTORIC   HIST.   TOTAL          iiii)  
ccc 1022 FORMAT(                             CONSUMP.   CHANGE     SOIL     USE OF    TOTAL             CONSUMP.   CUA HISTORIC   iiii    LEVEL')                                                   
ccc 1023 FORMAT(                   CONSUMP.   USE OF   IN SOIL   MOISTURE  APPLIED   MONTHLY   HISTORIC  USE OF      +   DEPLET.  LEVEL  CONSUMP.') 
ccc 1024 FORMAT(         PRECIP.     USE     PRECIP.   MOISTURE   ACCUM.    WATER     C. U.      AREA    PRECIP. LOSSES           AREA    USE')                                                            
ccc 1025 FORMAT( MONTH    INCHES    INCHES    INCHES    INCHES    INCHES    INCHES    IN.  FT.   ACRES       AF      AF      AF  ACRES        AF'/)

ccc 1026 FORMAT(   aaaFFFFFFF.FFfffffff.ffFFFFFFF.FFfffffff.ffFFFFFFF.FFfffffff.ffFFFFF.FFff.ffFFFFFFFFFfffffffffFFFFFFFFFfffffffffFFFFFFFFfffffffff)           
ccc 1027 FORMAT(  TOTALffffff.ffFFFFFFF.FFfffffff.ffFFFFFFF.FF          fffffff.ffFFFFF.FFff.ff         FFFFFFFFFfffffffffFFFFFFFFF        fffffffff)                                             

 1019 FORMAT(//33X,5HAREA ,I4,25H  CROP DEPLETION ANALYSIS/25X,7A10)    
 1020 FORMAT(1H0,10HIRRIGATED ,2A6,80X,'EFF. ROOT DEPTH ',F4.1,4H FT.)  
 1021 FORMAT(1H0,58X,8HCONSUMP.,21X,'HISTORIC   HIST.   TOTAL',10X,I4)  
 1022 FORMAT(1H ,17X,'           CONSUMP.   CHANGE     SOIL     USE OF'
     1   ,4X,'TOTAL             CONSUMP.   CUA HISTORIC   ',I4,         
     2   '    LEVEL')                                                   
 1023 FORMAT(1H ,18X,   'CONSUMP.   USE OF   IN SOIL   MOISTURE  APPLIED'
     1'   MONTHLY   HISTORIC  USE OF      +   DEPLET.  LEVEL  CONSUMP.') 
 1024 FORMAT(1H , 8X,   'PRECIP.     USE     PRECIP.   MOISTURE   ACCUM.',
     1'    WATER     C. U.      AREA    PRECIP. LOSSES           AREA    ',
     2 'USE')                                                            
 1025 FORMAT(1H0,      'MONTH    INCHES    INCHES    INCHES    INCHES   ',
     1' INCHES    INCHES    IN.  FT.   ACRES       AF      AF      AF  AC',
     2'RES        AF'/)                                                  
 1026 FORMAT(1H ,1X,A3,6F10.2,F8.2,F5.2,F9.0,3F9.0,F8.0,F9.0)           
 1027 FORMAT(1H0,5HTOTAL,F9.2,3F10.2,10X,F10.2,F8.2,F5.2,               
     1    9X,3F9.0,8X,F9.0)                                             

 1028 FORMAT(81X,'SEPT. RICE DRAINAGE = ',F9.0,8X,F9.0)                 

ccc 1041 FORMAT(            URBAN CONSUMPTIVE USE                                   TOTAL BASIN CONSUMPTIVE USE SUMMARY'/)                    

ccc 1042 FORMAT(                           HISTORIC          #####NNNNN LEVEL             TOTAL  TOTAL CONSUMPTIVE USE')                                                          
ccc 1043 FORMAT(              HIST.  HIST. DEPL. OF  HIST.   LEVEL    URBAN               BASIN')                                                   
ccc 1044 FORMAT(             URBAN  CU  OF  APPLIED  TOTAL   URBAN   CONSUMP.            PRECIP  HISTORIC     #####)                                
ccc 1045 FORMAT(              AREA PRECIP   WATER   DEPL.    AREA     USE                                     LEVEL'/)                                                  

ccc 1046 FORMAT(    MONTH    ACRES      AF      AF      AF   ACRES       AF                  AF        AF        AF'/)                            

ccc 1047 FORMAT(   AAAAAAFFFFFFFFFffffffffFFFFFFFFffffffffFFFFFFFFFffffffff          FFFFFFFFFFffffffffffFFFFFFFFFF)                     

ccc 1048 FORMAT(    TOTAL         FFFFFFFFffffffffFFFFFFFF         ffffffff          FFFFFFFFFFffffffffffFFFFFFFFFF)                   

 1041 FORMAT(12X,'URBAN CONSUMPTIVE USE',35X,                           
     1       'TOTAL BASIN CONSUMPTIVE USE SUMMARY'/)                    
 1042 FORMAT(27X,'HISTORIC',10X,2I5,' LEVEL',13X,'TOTAL  TOTAL CONSUMPTI',
     1'VE USE')                                                          
 1043 FORMAT(14X,'HIST.  HIST. DEPL. OF  HIST.   LEVEL    URBAN',       
     1   15X,'BASIN')                                                   
 1044 FORMAT(13X,'URBAN  CU  OF  APPLIED  TOTAL   URBAN   CONSUMP.',    
     1   12X,'PRECIP  HISTORIC     ',I5)                                
 1045 FORMAT(14X,'AREA PRECIP   WATER   DEPL.    AREA     USE',         
     1   37X,'LEVEL'/)                                                  
 1046 FORMAT('    MONTH    ACRES      AF      AF      AF   ACRES       A',
     1'F',13X,'     AF        AF        AF'/)                            
 1047 FORMAT(3X,A6,F9.0,3F8.0,F9.0,F8.0,10X,3F10.0)                     
 1048 FORMAT(/4X,'TOTAL',9X,3F8.0,9X,F8.0,10X,3F10.0)                   

ccc 1049 FORMAT(                /22X,'NATIVE VEGETATION DEPLETION ANALYSIS                       
ccc	        28X,'                       EFF. ROOT DEPTH  ',F4.1/)                                  
ccc
ccc 1050 FORMAT(                           32X,'CONSUMP.   CHANGE     SOIL                    ',27X,'HISTORIC',5X, I5,' PROJECTD')                                                  
ccc 1051 FORMAT(                 22X,'CONSUMP.   USE OF   IN SOIL   MOISTURE   MONTHLY     HISTORIC  CONSUMP.     LEVEL  CONSUMP.')                  
ccc 1052 FORMAT(       12X,'PRECIP.     USE     PRECIP.   MOISTURE   ACCUM.   DEPLETION        AREA     USE        AREA     USE'/)                    
ccc 1053 FORMAT(    MONTH    INCHES    INCHES    INCHES    INCHES    INCHES     IN.   FT.     ACRES      AF       ACRES      AF'/)        

ccc 1054 FORMAT(3X,A6,5F10.2,F8.2,F6.2,F10.0,F10.0,F10.0,F10.0)            
ccc                MONTHS PRECIP(M), VEGCU(M),CUPPTV(M),CHSMVG(M),VGSMST(M),VGDEPL(,FEETVG HRNVAC  , HNGWTR(M),   PRNVAC, PNGWTR(M)

ccc 1055 FORMAT(/'TOTAL',4F10.2,10X,F8.2,F6.2,10X,F10.0,10X,F10.0//)                                        
ccc                        TOTPRC ,   TVGCU,    TCUPTV,  TCHSMV,            TVDEPL,  TFTVG,          HNGWTR(13),          PNGWTR(13)

 1049 FORMAT(/22X,'NATIVE VEGETATION DEPLETION ANALYSIS',               
     1 /28X,'EFF. ROOT DEPTH  ',F4.1/)                                  
 1050 FORMAT(32X,'CONSUMP.   CHANGE     SOIL',27X,'HISTORIC',5X,I5      
     1   ,' PROJECTD')                                                  
 1051 FORMAT(22X,'CONSUMP.   USE OF   IN SOIL   MOISTURE   MONTHLY'     
     1   ,5X,'HISTORIC  CONSUMP.     LEVEL  CONSUMP.')                  
 1052 FORMAT(12X,'PRECIP.     USE     PRECIP.   MOISTURE   ACCUM.   DEPL',
     1'ETION',8X,'AREA     USE        AREA     USE'/)                    
 1053 FORMAT('    MONTH    INCHES    INCHES    INCHES    INCHES    INCHE',
     1'S     IN.   FT.     ACRES        AF     ACRES        AF'/)        
 1054 FORMAT(3X,A6,5F10.2,F8.2,F6.2,F10.0,F10.0,F10.0,F10.0)            
 1055 FORMAT(/'    TOTAL',4F10.2,10X,F8.2,F6.2                          
     1   ,10X,F10.0,10X,F10.0//)                                        
 1998 FORMAT(/55X,'WATER YEAR 19',I4/1H1)                               

ccc 2700 FORMAT(                               AREA iiii   HAY AND GRAIN DEPLETION ANALYSIS         EFF. ROOT DEPTH fff.ff FT.)                        
ccc 2430 FORMAT(

ccc	        O              PRECIP   TOTAL     CU OF     CHANGE     SOIL                        HISTORIC            PROJECTD)                                    
ccc 2440 FORMAT(              AND     CONSUMP.  SEEP AND  IN SOIL   MOISTURE  MONTHLY   HISTORIC  CONSUMP.  PROJECTD  CONSUMP.')               
ccc 2450 FORMAT(              SEEP      USE      PRECIP   MOISTURE   ACCUM.   DEPLET.     AREA      USE       AREA      USE')                   
ccc 2460 FORMAT(O   MONTH    INCHES    INCHES    INCHES    INCHES    INCHES    IN.  FT.   ACRES        AF     ACRES        AF'//)

ccc 2470 FORMAT(   aaaaaaFFFFFFF.FFfffffff.ffFFFFFFF.FFfffffff.ffFFFFFFF.FFfffff.ffFF.FF ffffffffFFFFFFFF    ffffffffFFFFFFFFffffffff.fFFFFFFFF.F)                                                        
ccc 2480 FORMAT(O   TOTALffffff.ffFFFFFFF.FFfffffff.ffFFFFFFF.FF            fff.ffFF.FF          fffffff             FFFFFFFffffffff.fFFFFFFFF.F)                                                          

 2430 FORMAT(1H0,//14X,44HPRECIP   TOTAL     CU OF     CHANGE     SOIL, 
     124X,8HHISTORIC,12X,8HPROJECTD)                                    
 2440 FORMAT(1H ,13X,   'AND     CONSUMP.  SEEP AND  IN SOIL   MOISTURE ',
     1' MONTHLY   HISTORIC  CONSUMP.  PROJECTD  CONSUMP.')               
 2450 FORMAT(1H ,13X,'SEEP      USE      PRECIP   MOISTURE   ACCUM.  ',
     1' DEPLET.     AREA      USE       AREA      USE')                   
 2460 FORMAT(1H0,   '   MONTH    INCHES    INCHES    INCHES    INCHES   ',
     1' INCHES    IN.  FT.   ACRES        AF     ACRES        AF'//)     
 2470 FORMAT(1H ,1X,A6,5F10.2,F8.2,F5.2,1X,F8.0,F8.0,4X,F8.0,F8.0,      
     1   2F10.1)                                                        
 2480 FORMAT(1H0,8H   TOTAL,F9.2,3F10.2,12X,F6.2,F5.2,10X,F7.0,13X,F7.0 
     1,2F10.1)                                                          
 
 
 2700 FORMAT(///31X,5HAREA ,I4,'   HAY AND GRAIN DEPLETION ANALYSIS'    
     1      ,9X,16HEFF. ROOT DEPTH ,F6.2,4H FT.)                        
 2801 FORMAT(//30X,5HAREA ,I4,                                          
     1      40H  RIPARIAN VEGETATION DEPLETION ANALYSIS)                
 2802 FORMAT(//29X,5HAREA ,I4,'  WATER SURFACE DEPLETION ANALYSIS')     
 2803 FORMAT(1H0,/ ,42X,'RESIDUAL',12X,'HISTORIC  HISTORIC',12X,'PROJECTD  PROJECTD')                                          
 2804 FORMAT(1H ,12X,'PRECIP    TOTAL     CU OF    CU TO BE           ',
     1' CONSUMP.  RESIDUAL             CONSUMP.  RESIDUAL')               
 2805 FORMAT(1H ,13X,'AND     CONSUMP.  SEEP AND  MET FROM  HISTORIC  ',
     1' USE OF   CONSUMP.  PROJECTED   USE OF   CONSUMP.')                
 2806 FORMAT(1H ,12X,'SEEP       USE      PRECIP    INFLOW     AREA   ',
     1' PRECIP.     USE       AREA     PRECIP.     USE')                  
 2807 FORMAT(1H0,'   MONTH    INCHES    INCHES    INCHES    INCHES   ',
     1'  ACRES        AF        AF     ACRES        AF        AF'/)       
ccc 2803 FORMAT(1H0,/ ,                                 42X,'RESIDUAL      ,12X,'HISTORIC  HISTORIC'      ,12X,'PROJECTD  PROJECTD')                                          
ccc 2804 FORMAT(1H        ,12X,'PRECIP    TOTAL     CU OF    CU TO BE            CONSUMP.  RESIDUAL             CONSUMP.  RESIDUAL')               
ccc 2805 FORMAT(1H         ,13X,'AND     CONSUMP.  SEEP AND  MET FROM  HISTORIC   USE OF   CONSUMP.  PROJECTED   USE OF   CONSUMP.')                
ccc 2806 FORMAT(1H        ,12X,'SEEP       USE      PRECIP    INFLOW     AREA    PRECIP.     USE       AREA     PRECIP.     USE')                  
ccc 2807 FORMAT(1H0'   MONTH    INCHES    INCHES    INCHES    INCHES    ACRES        AF        AF     ACRES        AF        AF'/)       
 2808 FORMAT(1H ,1X,A6,4F10.2,2(3X,F9.0,F8.0,F10.0))                    
 2809 FORMAT(1H0,3X,5HTOTAL,F9.2,3F10.2,2(10X,2F10.0))                  
 3003 FORMAT(I3,A1,3X,I4,12F8.0)                                        
 4001 FORMAT('  YEAR 19',I4/16X,12A8)                                   
 4002 FORMAT(2X,2A6/4X,'CU OF PRECIP',12F8.0/                           
     1   12X,'SEEP',12F8.0/11X,'TOTAL',12F8.0)                          
 4003 FORMAT(7X,'DRY GRAIN'/4X,'CU OF PRECIP',12F8.0/                   
     1   12X,'SEEP',12F8.0/11X,'TOTAL',12F8.0)                          
 4004 FORMAT(6X,'NATIVE VEG',/4X,'CU OF PRECIP',12F8.0/                 
     1   12X,'SEEP',12F8.0/11X,'TOTAL',12F8.0)                          
 4005 FORMAT(5X,'RIPARIAN VEG'/4X,'CU OF PRECIP',12F8.0/                
     1   12X,'SEEP',12F8.0/11X,'TOTAL',12F8.0)                          
 4006 FORMAT(6X,'WATER SURF'/4X,'CU OF PRECIP',12F8.0/                  
     1   12X,'SEEP',12F8.0/11X,'TOTAL',12F8.0)                          
 4007 FORMAT(6X,'URBAN LAWN'/4X,'CU OF PRECIP',12F8.0/                  
     1   12X,'SEEP',12F8.0/11X,'TOTAL',12F8.0)                          
 4008 FORMAT(7X,'URBAN VAC'/4X,'CU OF PRECIP',12F8.0/                   
     1   12X,'SEEP',12F8.0/11X,'TOTAL',12F8.0)                          
 4009 FORMAT(11X,'TOTAL FOR YEAR'/4X,'CU OF PRECIP',12F8.0/             
     1   12X,'SEEP',12F8.0/)                                            
      RETURN
      END
