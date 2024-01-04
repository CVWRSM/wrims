BLOCK DATA MON
c      REAL NTCUPR,NATRO
c      CHARACTER*1 KODE,NOH,NOLIST,SKIP,WYTYP
c      CHARACTER*2 ACR(20),KODE2,NTYP
c      CHARACTER*3 MONTHS
c      CHARACTER*6 CROP,DATE
c      CHARACTER*10 AREAID,IDET,IDLU,IDSM,PNM
      COMMON/CC/ AREAID(7),CROP(2,20),DATE(2),MONTHS(12),
     1   NOLIST,NOH,KODE(32),KODE2(2),NTYP(9),WYTYP(200)
     2   ,IDET(7),IDLU(7),IDSM(7),PNM(14)
ccc      DATA MONTHS# months of the water year /'OCT','NOV','DEC','JAN','FEB','MAR',
ccc     1            'APR','MAY','JUN','JUL','AUG','SEP'/
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
C********************************************************************
      REAL NTCUPR,NATRO
      CHARACTER*1 KODE,NOH,NOLIST,SKIP,WYTYP
      CHARACTER*2 ACR(20),KODE2,NTYP
      CHARACTER*3 MONTHS
      CHARACTER*6 CROP,DATE
      CHARACTER*10 AREAID,IDET,IDLU,IDSM,PNM
      CHARACTER*40 DICU5_1, DICU5_2, DICU5_3, DICU5_4

C..      COMMON blocks.....


ccc      CALL INIZ# call to zero out the variables
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
ccc	NOH# no historic output? trigger "-" = not hydro output to dicu5.13,14,17,27, blank = output
      READ(1,3)numSubareas,NOH,numYears,numCrops

      READ(2,2008) SKIP,SKIP# just a read to get over the next two lines

      READ(3,2008) (wtrYrType(i),i=1,numYears)

C...        NON-CRITICAL / CRITICAL WY IRRIG SCEDULES      - KG 2/26/88
ccc	0=No Irrigation, 1=Irrigation
      DO crop=1,numCrops
         READ(4,91)(noncritWYIrrigSched(mth,crop),mth=1,12)
      enddo

      DO crop=1,numCrops
         READ(4,91)(critWYIrrigSched(mth,crop),mth=1,12)
      enddo

C...        PAN EVAPORATION / ET ADJUSTMENT FACTORS        - KG 3/3/88
      DO year=1,numYears

         read(5,'(9x,12f6.0)')(ET_AdjFactor(mth,year),mth=1,12) 

ccc	 convert the ET adj. factor to a fraction
         do mth=1,12
            ET_AdjFactor(mth,year)=ET_AdjFactor(mth,year)/100.
         enddo

      enddo
      
C..
C   ISLAND (STUDY) DO LOOP
C..

cccccccc	This is the big loop	ccccccc

      Do 510 subarea=1,numSubareas

        READ(1,10)numAreas,begWY,lastWY,numCrops,studyName,studyLevelYr,altOutputTrig

	AREAID# heading statement from dicu5.1 broken into 7 lines (K),K=1,7)
        READ(1,12)(areaID(i),i=1,7)

        WRITE(12,3000)numAreas,KODE(12),numAreas,numAreas,KODE(12),(studyName(K),K=1,2)

ccc	NOH is equal to " " so this block writes out the heading for each subarea        
        IF(noOutputTrig.NE.'-') then 
            WRITE(13,4020)numAreas,KODE(13),numAreas,KODE(13),(areaID(i),i=1,7)
            WRITE(14,4021)NAREA,KODE(14),NAREA,KODE(14),(AREAID(K),K=1,7)
            WRITE(17,3006)NAREA,KODE(17),NAREA,NAREA,KODE(17),(DATE(K),K=1,2)
            WRITE(27,2001)NAREA,KODE(27),NAREA,NAREA,KODE(27),(DATE(K),K=1,2)
         endif

ccc	read the 4th line of the subarea data - descriptive info
        READ(1,11)(info1(i),i=1,7)

ccc	read the 5th line of the subarea data - descriptive info
        READ(1,11)(info2(K),K=1,7)

ccc	reads the 6th line of the subarea data - descriptive info
        READ(1,11)(info3(K),K=1,7)

ccc	NOYR# number of years in the run
c         totalYears=lastWY-begWY+1
        NOYR=LYR-NFYR+1

        DO crop=1,numCrops
           READ(1,71)cropSymbol(crop),(cropDescription(i,crop),i=1,2)
        enddo
          
        DO crop=1,numCrops
ccc	KR() a switch to tell if the crop is RICE
           IF(cropSymbol(crop).EQ.'RI')riceCropTrig(crop)=1
           IF(cropSymbol(crop).NE.'RI')riceCropTrig(crop)=0
        enddo

        DO crop=1,numCrops
ccc	crop ET in inches (month,crop), p.24 of Feb '95 report (Hari?)
           READ(1,84)(cropET(mth,crop),mth=1,12)
        enddo

ccc	ET for dry grain native (month)
ccc	ET for native vegetation (month)
ccc	ET for riparian vegetation (month)
ccc	ET for water surface (month)
         READ(1,84)(dryGrainET(mth),mth=1,12)
         READ(1,84)(natVegET(mth),mth=1,12)
         READ(1,84)(riparianVegET(mth),mth=1,12)
         READ(1,84)(waterSurfaceET(mth),mth=1,12)

         READ(1,85)percentLoss
      
        DO crop=1,numCrops
ccc	Initial Soil Moisture in Oct (in beggining WY?) (crop)
           READ(1,100)rootDepth(crop),initSoilMoist(crop)
        enddo

        READ(1,100)dryGrnRtDpth,initDryGrnRtDpth,dryGrnMoist

        READ(1,100)natvVegRtDpth,initNatVegSoilMoist,natVegSoilMoist

        DO crop=1,numCrops
ccc	max. soil moisture, i.e. upper limit (month,crop)
ccc	min. soil moisture, i.e. lower limit (month, crop)
           READ(1,102)(maxSoilMoist(mth,crop),mth=1,12),(minSoilMoist(mth,crop),mth=1,12)
        enddo 

        DO crop=1,numCrops
ccc	crop seepage (month, crop) in lowlands only, p20 Feb. '95 report
           READ(1,102)(cropSeep(mth,crop),mth=1,12)
        enddo
      
c	seepage in lowlands only
        READ(1,102)(dryGrnSeep(mth),mth=1,12)
        READ(1,102)(natVegSeep(mth),mth=1,12)
        READ(1,102)(ripVegSeep(mth),mth=1,12)

ccc     PNM# read two lines of descriptive info.
        READ(1,110)(PNM(K),K=1,14)
      
ccc	precipitation index factor (1.0)
        READ(1,84)precipFactor

ccc	usually skipped, I have only seen a "-" for altOutputTrig
C         IF(altOutputTrig.NE.'-') then
C            WRITE(10,112) level,numAreas,(areaID(i),i=1,7)
C            WRITE(10,114) studyName(1),studyName(2)
C            WRITE(10,115)(PNM(i),i=1,14),precipFactor,percentLoss,(info1(j),j=1,7),
C     &           (info2(j),j=1,7),(info3(j),j=1,7)
C            WRITE(41,4000)(areaID(i),i=1,7)
C         endif

ccc	num of days in each wateryear month, e.g. month 1 is Oct.
         daysInMth(1)=31
         daysInMth(3)=31
         daysInMth(4)=31
         daysInMth(6)=31
         daysInMth(8)=31
         daysInMth(10)=31
         daysInMth(11)=31
         daysInMth(2)=30
         daysInMth(7)=30
         daysInMth(9)=30
         daysInMth(12)=30
         daysInMth(5)=28

c         DO runYear=1,totalYears
         DO N=1,NOYR
            READ(1,'(7x,i4,12f6.2)')waterYear(runYear),(precip(mth,runYear),mth=1,12)
         enddo

ccc	first reading at line 192 in DICU5.1 =>   1URPCT     25    34    37    19    38    47                                     
         READ(1,2000)histPrcntLawn,levelPrcntLawn,histPrcntNatVeg,lvlPrcntNatVeg

C...     NON-CRITICAL / CRITICAL WY TOTAL ISLAND ACRERAGES - KG 2/25/88

         DO runYear=1,totalYears
ccc	# acreage of current subarea for each year (years from 1921), It can vary with the water year 
ccc	  type, e.g. For the first subarea critical wyt has 11920, all others (A,B,D,W) have 12170 
            READ(2,2002)subareaAcres(runYear)
         enddo

         DO N=1,NOYR
            READ(1,'(11x,19F6.0)')histUrbAC(runYear),(histCropAcres(k,runYear),k=1,numCrops)
         enddo

         READ(1,2008)SKIP

ccc	RFFURB# ? urban - nothing in the data
         READ(1,2003)(rffUrb(mth),mth=1,12)

ccc	These do not seem to be used elsewhere
         HPL# ? "01" (N),
         HCAP# ? "all 0s" (N)
         READ(1,2009)HPL(N),HCAP(N)

ccc	 index for where in the crop "array" is 'RI' and 'FI'
         riceIndex=0
         fieldIndex=0

         DO i=1,10
            KDEF1(i)=0
            KDEF2(i)=0
         enddo

ccc	these are critical(KDEF1()) and consecutive critical water years(KDEF2())
cccccccc 1939 was left out, why?
         KDEF1(1)=24
         KDEF1(2)=29
         KDEF1(3)=31
         KDEF1(4)=33
         KDEF1(5)=76
         KDEF2(1)=34
         KDEF2(2)=77

         DO crop=1,numCrops
ccc	 switches set to the crop array index if a crop is RICE or FIELD to determine the 
ccc 	 index numbers of the rice and field crops cropSymbol
            IF(cropSymbol(crop).EQ.'RI')riceIndex=crop
            IF(cropSymbol(crop).EQ.'FI')fieldIndex=crop
         enddo

C..       SET EFF TO 1.0 (NOT USED IN ISLAND STUDY)  -KG 3/3/88
         DO year=1,numYears
            DO mth=1,12
               EFF(mth,year)=1.0
            enddo
         enddo

ccc	RMRVBA# ? "0"
	 READ(1,2003)RMRVBA
      
         DO NY=1,NOYR
ccc  	    dry grain acreage ="0" in critical years (NY)
            READ(1,'(11X,19F6.0)')histRipVegAC(NY),wtrSurfAC(NY),dryGrnAC(NY)
            RPLRAC(NY)=RMRVBA-histRipVegAC(NY)
            RPLRAC(NY)=RMRVBA-RMRVAC(NY)
         enddo 
         
ccc	AVEVAP# ? average evap. "all 100"
         READ(1,'(9x,12f6.0)')(avgEvap(M),M=1,12)
         do m=1,12
            avgEvap(m)=avgEvap(m)/100.
         enddo

C...
C...SUBROUTINE FOR YEARLY CALCULATIONS*********************
C...
ccc	call to calculate for each wateryear the CU for the current subarea
         CALL CUYEAR
C...
         GO TO 510
 510  CONTINUE
ccc	In the files I have seen altOutputTrig is always "-" 
c      IF(altOutputTrig.EQ.'-')GO TO 900
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

C..
C..*****************  YEARLY  DO LOOP  *********************
C..
c      DO 500 year=1,numYears
      DO 500 NY=1,NOYR
C..*                                 KG 2/26/88

ccc	if the water year type is critical
        IF(WYTYP(NY).EQ.'C') THEN
ccc	apply the critical year irrigation schedule to current year (year)
        DO 219 mth=1,12
          DO 218 crop=1,numCrops
ccc	    MFI# irrigation scedule (month, crop)
            irrigSched(mth,crop)=critWYIrrigSched(mth,crop)
  218     CONTINUE
  219   CONTINUE

ccc	if the water year type is not critical
        ELSE
ccc	apply the non-critical year irrigation schedule to current year (year)
        DO 222 mth=1,12
          DO 221 crop=1,numCrops
c            irrigSched(mth,crop)=noncritWYIrrigSched(mth,crop)
  221     CONTINUE
  222   CONTINUE
        ENDIF
C...

ccc	Is the optional output (to dicu5.41) trigger, not '-', in the data file? 
        IF(altOutputTrig.EQ.'-')GO TO 216
ccc	write to optional output        
        WRITE(41,4001) waterYear(year),(MONTHS(mth),mth=1,12)
C..*
  216   TPR(13)=0.0
        DO 217 mth=1,12
C..*
          histCU_Ppt(mth)=0.0
          histCU_WtrSurfSeep(mth)=0.0
C..*
          totPrecipTAF(mth)=subareaAcres(year)*precip(mth,year)*precipFactor/12000.
ccc	round off total precip to the 10ths place
          totPrecipTAF(mth)=IFIX((totPrecipTAF(mth)+0.05)*10.)/10.
ccc	TPR(13)# total precipitation in TAF (year)
          totPrecipTAF(13)=totPrecipTAF(13)+totPrecipTAF(mth)

  217   CONTINUE

ccc	initialize values for the year
  223   HURCUW(13)=0.
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
          THW(J)=0.
  225   CONTINUE
  
  228   histNatVegAC=subareaAcres(year)-histUrbAC(year)-histRipVegAC(year)-wtrSurfAC(year)-dryGrnAC(year)

        DO 230 crop=1,numCrops

          histNatVegAC=histNatVegAC-histCropAcres(crop,year)
          DO 229 mth=1,12
ccc	calculate CU in inches for each crop for each month with pan evap. adjustment
ccc	See eqtn 4-1 p28 in '95 DICU Report
            cropCU(mth,crop)=cropET(mth,crop)*ET_AdjFactor(mth,year)/avgEvap(mth)
  229     CONTINUE
  230   CONTINUE
C...
C...CROP AND URBAN CONSUMPTIVE USE  ***********************
C...
ccc	initialize crop counters
  234   crop=1

ccc	The beginning of a confusing way to loop through each crop - See the GOTO 235 statement below
  235   CONTINUE

          totPrecip=0.
ccc	  calculate precip
          DO 220 mth=1,12
ccc	  monthly precip (mth) for the current crop (crop) = monthly precip * adj.factor {usually 1}
ccc							+ seepage for month's the crop in lowlands
            currentPrecip(mth)=precip(mth,year)*precipFactor+cropSeep(mth,crop)
ccc	  accumulate total precip for the year
            totPrecip=totPrecip+currentPrecip(mth)
  220     CONTINUE

  236     TCUPTC=0.
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
          totCropCU=0.
          TCUCR=0.
ccc-----loop through each month for current year, current crop type, and current subarea 
          DO 280 mth=1,12
          
            totCropCU=totCropCU + cropCU(mth,crop)
ccc	Take the min value of the month's precip or crop CU to calculate the
ccc	area iii crop consumption use of precip in inches. The crop will only take what it needs
ccc	but will take all of precip if its needs are not met by precip.
            cropCU_ofPrecip(mth)= AMIN1(currentPrecip(mth),cropCU(mth,crop))

ccc	this is to skip to 251 the first month i.e. Oct
            IF(mth-1<>0) 251:else 240

ccc*** Soil moisture calcs. Nov-Sep ***
  240       IF(accumCropSoilMoist(mth-1).GE.minSoilMoist(mth,crop))GO TO 242

            cropSoilMoistChange(mth)=AMIN1(AMAX1(currentPrecip(mth)-cropCU(mth,crop),minSoilMoist(mth,crop)
     1      -accumCropSoilMoist(mth-1)),maxSoilMoist(mth,crop)-accumCropSoilMoist(mth-1))

            accumCropSoilMoist(mth)=accumCropSoilMoist(mth-1)+cropSoilMoistChange(mth)
            GO TO 267

  242       IF(currentPrecip(mth)-cropCU(mth,crop))243,245,245
  243       cropSoilMoistChange(mth)=-AMIN1(accumCropSoilMoist(mth-1)-minSoilMoist(mth,crop),AMAX1(
     1      cropCU(mth,crop)-currentPrecip(mth),accumCropSoilMoist(mth-1)-maxSoilMoist(mth,crop)))
            GO TO 246

  245       cropSoilMoistChange(mth)=AMIN1(currentPrecip(mth)-cropCU(mth,crop),maxSoilMoist(mth,crop)-accumCropSoilMoist(mth-1))
ccc	update accum. crop soil moist.
  246       accumCropSoilMoist(mth)= accumCropSoilMoist(mth-1) + cropSoilMoistChange(mth)

ccc*** end Soil moisture calcs. Nov-Sep ***
  

ccc	if this is Sep and is rice, goto 250
            IF(mth.EQ.12.AND.riceCropTrig(crop).EQ.1)GO TO 250
            GO TO 273

ccc	establish drains
  250       drain=AMAX1(accumCropSoilMoist(mth-1)-minSoilMoist(mth,crop)-cropCU(mth,crop),0.0)
            histDrainTAF=drain*histCropAcres(crop,year)/12000.
            cropCU_ofPrecip(mth)=0.0
            GO TO 274


ccc*** Soil moisture calcs. Oct ***
  251       IF(initSoilMoist(crop)-minSoilMoist(mth,crop))252,260,260

  252       cropSoilMoistChange(mth)=AMIN1(AMAX1(currentPrecip(mth)-cropCU(mth,crop),minSoilMoist(mth,crop)
     1          -initSoilMoist(crop)),maxSoilMoist(mth,crop)-initSoilMoist(crop))
            accumCropSoilMoist(mth)=initSoilMoist(crop)+cropSoilMoistChange(mth)
            GO TO 267

  260       IF(currentPrecip(mth)-cropCU(mth,crop))261,265,265
  261       cropSoilMoistChange(mth)=-AMIN1(initSoilMoist(crop)-minSoilMoist(mth,crop),AMAX1(
     1      cropCU(mth,crop)-currentPrecip(mth),initSoilMoist(crop)-maxSoilMoist(mth,crop)))
            GO TO 266

ccc	crop change in soil moist. = min(precip - crop cu, upper soilmoist limit - init.soilmoist.)
  265       cropSoilMoistChange(mth)=AMIN1(currentPrecip(mth)-cropCU(mth,crop),maxSoilMoist(mth,crop)-initSoilMoist(crop))
  266       accumCropSoilMoist(mth)= initSoilMoist(crop)+ cropSoilMoistChange(mth)
            GO TO 273

ccc	crop consump. use of applied water = max(crop change in soil moist. + crop cu - precip, 0.)
  267       cropCU_AppldWtr(mth)=AMAX1(cropSoilMoistChange(mth)+cropCU(mth,crop)-currentPrecip(mth),0.00)

            IF(irrigSched(mth,crop)-1)270,275,275
ccc	if month is Oct goto 271, elso goto 275
  270       IF(mth-1)271,272,271

  271       cropSoilMoistChange(mth)=AMIN1(maxSoilMoist(mth,crop)-accumCropSoilMoist(mth-1),AMAX1(currentPrecip(mth)
     1      -cropCU(mth,crop),0.00))
            maxSoilMoist(mth)=maxSoilMoist(mth-1)+cropSoilMoistChange(mth)
            GO TO 274

  272       cropSoilMoistChange(mth)=AMIN1(maxSoilMoist(mth,crop)-initSoilMoist(crop),AMAX1(currentPrecip(mth)
     1      -cropCU(mth,crop),0.00))
ccc	Oct accum. crop soil moist.
            maxSoilMoist(mth)=initSoilMoist(crop)+cropSoilMoistChange(mth)
            GO TO 274

  273       cropCU_AppldWtr(mth)=AMAX1(cropCU(mth,crop)- cropCU_ofPrecip(mth)-ABS(cropSoilMoistChange(mth)),0.00)

            IF(irrigSched(mth,crop)-1)274,275,275
  274       cropCU_AppldWtr(mth)=0.

  275       totCropCU(mth)= cropCU_ofPrecip(mth)+ cropCU_AppldWtr(mth)+AMAX1(cropSoilMoistChange(mth),0.00)
     1               -AMAX1(0.,cropSoilMoistChange(mth)+cropCU_ofPrecip(mth)-currentPrecip(mth))
            totCropCU_Ft(mth)= totCropCU(mth)/12.
            cropCU_AppldWtr_Ft(mth)= cropCU_AppldWtr(mth)/12.

            histWtrUseTAF(mth)=histCropAcres(crop,year)*totCropCU_Ft(mth)/1000.
            histCropCU_PrecipAF(mth)=(totCropCU(mth)-cropCU_AppldWtr(mth))*histCropAcres(crop,year)/12000.

            CU_precip(mth)=AMIN1(precip(mth,year)*precipFactor*histCropAcres(crop,year)/12000,histCropCU_PrecipAF(mth))
            histCU_precip(mth)=histCU_precip(mth)+CU_precip(mth)

            CU_WtrSurfSeep(mth)=histCropCU_PrecipAF(mth)-CU_precip(mth)
            histCU_WtrSurfSeep(J)=histCU_WtrSurfSeep(J)+CU_WtrSurfSeep(J)

ccc	crop Historic CUA + losses in AF = (1+ percent loss) * crop consump. use of applied 
ccc					water in inches * crop Historic area in acres / 12000
            histCU_Appld(mth)=(1+percentLoss)*cropCU_AppldWtr(mth)*histCropAcres(crop,year)/12000.

ccc	crop total historic depletion in AF =  crop Historic area in acres * crop total monthly CU in
ccc					feet + percent loss * CUA in ft ) /1000
c            totHistDepletion(mth)=histCropAcres(crop,year)*(totCropCU_Ft(mth)+percentLoss * cropCU_AppldWtr_Ft(mth) )/1000.
            HWATER(J)=HCRPAC(KRP,NY)*(FEETCR(J)+PERCLS* FTCUA(J) )/1000.
ccc	crop total Level depletion in AF =  crop Level area in acres * crop total monthly CU in ft/1000
c            totLvlDepletion(mth)=percentLevelAcres(crop,year)*totCropCU_Ft(mth)/1000.
            PWATER(J)=PCRPAC(KRP,NY)*FEETCR(J)/1000.

ccc	update accum. totals
ccc	    total crop consumption use of precip
c            totCU_Precip = totCU_Precip + cropCU_ofPrecip(mth)
            TCUPTC = TCUPTC + CUPPTC(J)
ccc	    total crop change in soil moisture in inches
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
ccc-----end of monthly loop
  280     CONTINUE

ccc	skip to 282 if crop is rice
c          IF(riceCropTrig(crop).EQ.1)GO TO 282
          IF(KR(KRP).EQ.1)GO TO 282
          GO TO 283
c  282     runingSumHistCU(12)=runingSumHistCU(12)-histDrainTAF
c        total consumptive use, num. level in TAF
c          totHistCU_AF(12)=totHistCU_AF(12)-lvlDrainTAF
  282     TOTHDP(12)=TOTHDP(12)-HDRAIN
c          totLvlCU_AF(12)=totLvlCU_AF(12)-PDRAIN
          TOTPDP(12)=TOTPDP(12)-PDRAIN
ccc	init. soil moist.= crop soil moisture accum. in inches for Sep
c  283     initSoilMoist(crop)=maxSoilMoist(12)
  283     STARTC(KRP)=CRSMST(12)
ccc	if not the first crop goto 286 
c          IF(crop.NE.1)GO TO 286
          IF(KRP.NE.1)GO TO 286
          THLNCU=0.
          TPLNCU=0.
c          DO 285 mth=1,12
          DO 285 J=1,12
ccc	historic lawn cu = hist.urb.ac.*total percent of lawn*(crop total monthly CU
ccc				+ percent loss * CUA in ft ) /1000
c            histCU_lawn(mth)=histUrbAC(year)*histPrcntLawn*(totCropCU_Ft(mth)+percentLoss* cropCU_AppldWtr_Ft(mth))/1000.
            HLWNCU(J)=HURBAC(NY)*PCTLNH*(FEETCR(J)+PERCLS* FTCUA(J))/1000.
ccc	Hist depletion of applied water = hist.urb.ac. * total percent of lawn *
ccc						((1+percent loss) * CUA in ft ) /1000
c            histUrbCU_AppldWtr(mth)=histUrbAC(year)*histPrcntLawn*((1+percentLoss)*cropCU_AppldWtr_Ft(mth))/1000.
            HURCUW(J)=HURBAC(NY)*PCTLNH*((1+PERCLS)*FTCUA(J))/1000.
C.. 
ccc	total CU of precip. of urban lawn? = historic lawn cu - Hist depletion of applied water
c            totCU_UrbLawnPpt(mth)=histCU_lawn(mth)-histUrbCU_AppldWtr(mth)
            TCUPLN(J)=HLWNCU(J)-HURCUW(J)
ccc	    CU_UrbLawnPpt = min(total CU of precip. of urban lawn?, precip*factor*
ccc					histUrbanAcr*pctHistLawn/12000)
c            CU_UrbLawnPpt(mth)=AMIN1(totCU_UrbLawnPpt(mth),precip(mth,year)*precipFactor*histUrbAC(year)*histPrcntLawn/12000.)
            CUPLN(J)=AMIN1(TCUPLN(J),P(J,NY)*FACTOR*HURBAC(NY)*PCTLNH/12000.)
c	    histCU_Ppt(mth) = histCU_Ppt(mth) + CU_UrbLawnPpt(mth)
            HCUP(J)=HCUP(J)+CUPLN(J)
ccc	    CU_UrbLawnSeep(mth) = totCU_UrbLawnPpt(mth) - CU_UrbLawnPpt(mth)
            CUSLN(J)=TCUPLN(J)-CUPLN(J)
c            histCU_WtrSurfSeep(mth)=histCU_WtrSurfSeep(mth)+CU_UrbLawnSeep(mth)
            HCUSEEP(J)=HCUSEEP(J)+CUSLN(J)
C..
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
  285     CONTINUE
  286     CONTINUE


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
  288     CONTINUE
C******* END CONVERSION ***********************************************


ccc	In the files I have seen altOutputTrig is always "-" 
c          IF(altOutputTrig.EQ.'-')GO TO 290
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
  296     WRITE(10,1026) MONTHS(M),PRECIP(M),CROPCU(M,KRP),CUPPTC(M),
     1    CHSMCR(M),CRSMST(M),CUA(M),CRDEPL(M),FEETCR(M),HCRPAC(KRP,NY),
     2    HCRCUP(M),HCRCUA(M),HWATER(M),PCRPAC(KRP,NY),PWATER(M)

ccc      WRITE(10,1027) TOTPRC,
ccc	TCUCR# total area iii crop consumption use in inches ,
ccc	TCUPTC# total, area iii, crop consumption use of precip in inches,
ccc	TCHSMC# total area iii crop change in soil moisture in inches,
ccc	TCUA,# total area iii crop consump. use of applied water in inches
ccc	TCDEPL# total area iii crop total monthly CU in inches
ccc	TFTCR# total area iii crop total monthly CU in feet,     1   
ccc	HCRCUP(13)# total area iii crop Historic consump. use of precip in AF,
ccc	HCRCUA(13)# total area iii crop Historic CUA + losses in AF,
ccc	THWATR# total area iii crop total historic depletion in AF,
ccc	TPWATR# total area iii crop iii level of CU in AF
          WRITE(10,1027) TOTPRC,TCUCR,TCUPTC,TCHSMC,TCUA,TCDEPL,TFTCR,
     1    HCRCUP(13),HCRCUA(13),THWATR,TPWATR

          IF(KR(KRP).EQ.1)WRITE(10,1028)HDRAIN,PDRAIN
          IF(KRP.EQ.NOCRP.OR.KRP.EQ.KRP/2*2)WRITE(10,1998)NYR(NY)
C..*
          WRITE(41,4002)(CROP(J,KRP),J=1,2),(CUP(M),M=1,12),
     1    (CUS(M),M=1,12),(HCRCUP(M),M=1,12)
C..*

  290     IF(KRP-NOCRP)300,310,300
  300     KRP=KRP+1
          KCR=KCR+2

ccc	end of the loop through each crop
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
c      DO 2110 mth=1,12
      DO 2110 J=1,12 
c      currentPrecip(mth)=precip(mth,year)*precipFactor+dryGrnSeep(mth)
      PRECIP(J)=P(J,NY)*FACTOR+SEEPDG(J)
c      totPrecip=totPrecip+currentPrecip(J)
      TOTPRC=TOTPRC+PRECIP(J)
c      totCU_DryGrn#(J)=ET_AdjFactor(J,NY)/avgEvap(J)*dryGrainET(J)
      GRANCU(J)=EVAP(J,NY)/AVEVAP(J)*DGET(J)
c      CU_DryGrnSeep(J)= AMIN1(currentPrecip(J),totCU_DryGrn(J))
      CUPTGR(J)= AMIN1(PRECIP(J),GRANCU(J))
c      IF(J-1)2060,2050,2060
      IF(J-1)2060,2050,2060
c 2050 dryGrnSoilMoistChange(J)= AMIN1(dryGrnMoist,AMAX1(currentPrecip(J)-totCU_DryGrn(J),0.00))
 2050 CHSMGR(J)= AMIN1(DGMOIS,AMAX1(PRECIP(J)-GRANCU(J),0.00))
c      accumDryGrnSoilMoist(J)= initDryGrnRtDpth + dryGrnSoilMoistChange(J)
      GRSMST(J)= STRTDG + CHSMGR(J)
      GO TO 2100
c 2060 IF(currentPrecip(J)- totCU_DryGrn(J))2070,2080,2080
 2060 IF(PRECIP(J)- GRANCU(J))2070,2080,2080
c 2070 dryGrnSoilMoistChange(J)= -AMIN1(totCU_DryGrn(J)-currentPrecip(J),accumDryGrnSoilMoist(J-1))
 2070 CHSMGR(J)= -AMIN1(GRANCU(J)-PRECIP(J),GRSMST(J-1))
      GO TO 2090
c 2080 dryGrnSoilMoistChange(J)= AMIN1(currentPrecip(J)-totCU_DryGrn(J),dryGrnMoist-accumDryGrnSoilMoist(J-1))
 2080 CHSMGR(J)= AMIN1(PRECIP(J)-GRANCU(J),DGMOIS-GRSMST(J-1))
c 2090 accumDryGrnSoilMoist(J)= accumDryGrnSoilMoist(J-1)+ dryGrnSoilMoistChange(J)
 2090 GRSMST(J)= GRSMST(J-1)+ CHSMGR(J)
c 2100 dryGrnDepl_in(mth)= CU_DryGrnSeep(mth)+ AMAX1(dryGrnSoilMoistChange(mth),0.00)
 2100 GRDEPL(J)= CUPTGR(J)+ AMAX1(CHSMGR(J),0.00)
c      dryGrnDepl_ft(mth)= dryGrnDepl_in(mth)/12.
      FEETGR(J)= GRDEPL(J)/12.
c      totDryGrnCU = totDryGrnCU + totCU_DryGrn(J)
      TGRCU = TGRCU + GRANCU(J)
c      totDryGrnCU_SeepAndPrecip= totDryGrnCU_SeepAndPrecip + CU_DryGrnSeep(J)
      TCUPTG= TCUPTG + CUPTGR(J)
**********************************************************************************************
Stopped Here Friday
**********************************************************************************************
c      totDryGrnSoilMoistChange = totDryGrnSoilMoistChange + AMAX1(dryGrnSoilMoistChange(J),0.00)
      TCHSMG = TCHSMG + AMAX1(CHSMGR(J),0.00)
c      totDryGrnDepletion = totDryGrnDepletion + dryGrnDepl_in(J)
      TGDEPL = TGDEPL + GRDEPL(J)
c      totDryGrnDepl_ft = totDryGrnDepl_ft + dryGrnDepl_ft(J)
      TFTGR = TFTGR + FEETGR(J)
c      histCU_HayGrn(mth) = histDryGrnAC(year) * dryGrnDepl_ft(mth)/1000.
      HGRWTR(J) =DGAC(NY) * FEETGR(J)/1000.
C..*
c      CU_precip(J)=AMIN1(histCU_HayGrn(J),P(J,NY)*precipFactor*histDryGrnAC(NY)/12000.)
      CUP(J)=AMIN1(HGRWTR(J),P(J,NY)*FACTOR*DGAC(NY)/12000.)
c      histCU_Ppt(J)=histCU_Ppt(J)+CU_precip(J)
      HCUP(J)=HCUP(J)+CUP(J)
c      CU_WtrSurfSeep(J)=histCU_HayGrn(J)-CU_precip(J)
      CUS(J)=HGRWTR(J)-CUP(J)
c      histCU_WtrSurfSeep(J)=histCU_WtrSurfSeep(J)+CU_WtrSurfSeep(J)
      HCUSEEP(J)=HCUSEEP(J)+CUS(J)
C..*
c      projCU_HayGrn(mth) = projDryGrnAC * dryGrnDepl_ft(mth)/1000.
      PGRWTR(J) =PDGA * FEETGR(J)/1000.
c      totHistDryGrnCU_AF = totHistDryGrnCU_AF + histCU_HayGrn(mth)
      THGRWT = THGRWT + HGRWTR(J)
c      totProjDryGrnCU_AF = totProjDryGrnCU_AF + projCU_HayGrn(J)
      TPGRWT = TPGRWT + PGRWTR(J)
c      totHistCU_AF (J)=totHistCU_AF(J)+histCU_HayGrn(mth)
      TOTHDP(J)=TOTHDP(J)+HGRWTR(J)
c      totLvlCU_AF(J)=totLvlCU_AF(J)+projCU_HayGrn(J)
      TOTPDP(J)=TOTPDP(J)+PGRWTR(J)
 2110 CONTINUE accumDryGrnSoilMoist
c      initDryGrnRtDpth=accumDryGrnSoilMoist(12)
      STRTDG=GRSMST(12)
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
c      totHistDryGrnCU_AF=totHistDryGrnCU_AF*1000.
      THGRWT=THGRWT*1000.
c      totProjDryGrnCU_AF=totProjDryGrnCU_AF*1000.
      TPGRWT=TPGRWT*1000.
c      DO 2119 mth=1,12
      DO 2119 L=1,12
c      histCU_HayGrn(mth)=histCU_HayGrn(mth)*1000.
      HGRWTR(L)=HGRWTR(L)*1000.
c      projCU_HayGrn(L)=projCU_HayGrn(L)*1000.
      PGRWTR(L)=PGRWTR(L)*1000.
C..*
c      CU_precip(L)=CU_precip(L)*1000.
      CUP(L)=CUP(L)*1000.
c      CU_WtrSurfSeep(L)=CU_WtrSurfSeep(L)*1000.
      CUS(L)=CUS(L)*1000.
C..*
 2119 CONTINUE
C******* END CONVERSION ***********************************************
ccc	In the files I have seen altOutputTrig is always "-" 
c      IF(altOutputTrig.EQ.'-')GO TO 317
      IF(NOLIST.EQ.'-')GO TO 317
      WRITE(10,2700)NAREA,DGROOT
      WRITE(10,2430)
      WRITE(10,2440)
      WRITE(10,2450)
      WRITE(10,2460)
c      DO 2120 M=1,12
      DO 2120 M=1,12
c      IF(M.EQ.7)WRITE(10,999)
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
c      totLvlNatVegCU=0.
c      totHistNatVegCU=0.
c      totNatVegCU=0.
c      totNatVegCU_Precip=0.
c      totNatVegSoilMoistChange=0.
c      totNatVegDeplet=0.
c      totNatVegDeplet_ft=0.
c      totHistNatVegCU_AF(13)=0.0
c      NTCUPR(13)=0.
c      NATRO(13)=0.0
c      RVET(13)=0.
c      CUPRV(13)=0.
c      CUINRV(13)=0.
c      RPLR2P(13)=0.
c      RPLR1I(13)=0.
c      RMRV2P(13)=0.
c      ripHistResidualCU(13)=0.
c      RPLR3T(13)=0.
c      RMRV3T(13)=0.
c      HRUNOF(13)=0.
c      PRUNOF(13)=0.0
c      totLvlCU_Precip(13)=0.
c      totProjCU_AF(13)=0.
c      PMRV2P(13)=0.
c      totLvlCU_AppldWtr(13)=0.
c      PMRV1I(13)=0.0

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
c      DO 316 M=1,12
      DO 316 M=1,12
c      natVegCU(M)=natVegET(M)
      VEGCU(M)=VEGET(M)
  316 CONTINUE
c      DO 318 J=1,12 
      DO 318 J=1,12 
c      currentPrecip(J)=P(J,NY)*precipFactor+natVegSeep(J)
      PRECIP(J)=P(J,NY)*FACTOR+SEEPNV(J)
c      natVegCU(J)=natVegET(J)*ET_AdjFactor(J,NY)/avgEvap(J)
      VEGCU(J)=VEGET(J)*EVAP(J,NY)/AVEVAP(J)
  318 CONTINUE 
c  319 totPrecip = 0.0
  319 TOTPRC = 0.0
c      DO 380 J=1,12
      DO 380 J=1,12
c      totPrecip=totPrecip+currentPrecip(J)
      TOTPRC=TOTPRC+PRECIP(J)
c      natVegCU_Precip(J)=AMIN1(currentPrecip(J),natVegCU(J))
      CUPPTV(J)=AMIN1(PRECIP(J),VEGCU(J))
c      IF(J.NE.1)GO TO 330
      IF(J.NE.1)GO TO 330
c  320 natVegSoilMoistChange(J)=AMIN1(natVegSoilMoist,AMAX1(currentPrecip(J)-natVegCU(J),0.00))
  320 CHSMVG(J)=AMIN1(VMOIST,AMAX1(PRECIP(J)-VEGCU(J),0.00))
c      accumNatVegSoilMoisture(J)= initNatVegSoilMoist + natVegSoilMoistChange(J)
      VGSMST(J)= STARTV + CHSMVG(J)
      GO TO 370
c  330 IF(currentPrecip(J).GE.natVegCU(J))GO TO 350
  330 IF(PRECIP(J).GE.VEGCU(J))GO TO 350
c  340 natVegSoilMoistChange(J)= -AMIN1(natVegCU(J)-currentPrecip(J),accumNatVegSoilMoisture(J-1))
  340 CHSMVG(J)= -AMIN1(VEGCU(J)-PRECIP(J),VGSMST(J-1))
      GO TO 360
c  350 natVegSoilMoistChange(J)=AMIN1(currentPrecip(J)-natVegCU(J),natVegSoilMoist- accumNatVegSoilMoisture(J-1))
  350 CHSMVG(J)=AMIN1(PRECIP(J)-VEGCU(J),VMOIST- VGSMST(J-1))
c  360 accumNatVegSoilMoisture(J)= accumNatVegSoilMoisture(J-1) + natVegSoilMoistChange(J)
  360 VGSMST(J)= VGSMST(J-1) + CHSMVG(J)
c  370 natVegDepletion(J)= natVegCU_Precip(J)+AMAX1(natVegSoilMoistChange(J),0.00)
  370 VGDEPL(J)= CUPPTV(J)+AMAX1(CHSMVG(J),0.00)
c      natVegDeplet_ft(J)= natVegDepletion(J)/12.
      FEETVG(J)= VGDEPL(J)/12.
c      totNatVegCU  = totNatVegCU + natVegCU(J)
      TVGCU = TVGCU + VEGCU(J)
c      totNatVegCU_Precip = totNatVegCU_Precip + natVegCU_Precip(J)
      TCUPTV = TCUPTV + CUPPTV(J)
c      totNatVegSoilMoistChange = totNatVegSoilMoistChange +AMAX1(natVegSoilMoistChange(J),0.00)
      TCHSMV = TCHSMV +AMAX1(CHSMVG(J),0.00)
c      totNatVegDeplet = totNatVegDeplet + natVegDepletion(J)
      TVDEPL = TVDEPL + VGDEPL(J)
c      totNatVegDeplet_ft = totNatVegDeplet_ft + natVegDeplet_ft(J)
      TFTVG = TFTVG + FEETVG(J)
c      A=histNatVegAC*natVegDeplet_ft(J)/1000.
      A=HRNVAC*FEETVG(J)/1000.
c      totHistNatVegCU_AF(J) = IFIX((A+0.05)*10.)/10.
      HNGWTR(J) = IFIX((A+0.05)*10.)/10.
c      temp=PRNVAC*natVegDeplet_ft(J)/1000.
      A=PRNVAC*FEETVG(J)/1000.
c      totProjCU_AF(J) = IFIX((temp+0.05)*10.)/10.
      PNGWTR(J) = IFIX((A+0.05)*10.)/10.
c      totHistNatVegCU_AF(13)=totHistNatVegCU_AF(13)+totHistNatVegCU_AF(J)
      HNGWTR(13)=HNGWTR(13)+HNGWTR(J)
c      totHistCU_AF(J)=totHistCU_AF(J)+totHistNatVegCU_AF(J)
      TOTHDP(J)=TOTHDP(J)+HNGWTR(J)
c      totLvlCU_AF(J)=totLvlCU_AF(J)+totProjCU_AF(J)
      TOTPDP(J)=TOTPDP(J)+PNGWTR(J)
c  375 totUrbVegCU(J)= histUrbAC(NY) * histPrcntNatVeg * natVegDeplet_ft(J)/1000.
  375 HUNVCU(J)= HURBAC(NY) * PCTNVH * FEETVG(J)/1000.
C..* 
c      urbVegCU_Precip(J)=AMIN1(totUrbVegCU(J),pricip(J,NY)*precipFactor*histUrbAC(NY)*histPrcntNatVeg/12000.)
      CUPVC(J)=AMIN1(HUNVCU(J),P(J,NY)*FACTOR*HURBAC(NY)*PCTNVH/12000.)
c      histCU_Ppt(J)=histCU_Ppt(J)+urbVegCU_Precip(J)
      HCUP(J)=HCUP(J)+CUPVC(J)
c      urbVegCU(J)=totUrbVegCU(J)-urbVegCU_Precip(J)
      CUSVC(J)=HUNVCU(J)-CUPVC(J)
c      histCU_WtrSurfSeep(J)=histCU_WtrSurfSeep(J)+urbVegCU(J)
      HCUSEEP(J)=HCUSEEP(J)+CUSVC(J)
C..*
c      lvlNatVegCU(J)= lvlUrbAC * lvlPrcntNatVeg * natVegDeplet_ft(J)/1000.
      PUNVCU(J)= PURBAC * PCTNVP * FEETVG(J)/1000.
c      totHistNatVegCU = totHistNatVegCU + totUrbVegCU(J)
      THNVCU = THNVCU + HUNVCU(J)
c      totLvlNatVegCU = totLvlNatVegCU + lvlNatVegCU(J)
      TPNVCU = TPNVCU + PUNVCU(J)
c      totHistUrbDpltn(J) = histCU_lawn(J)+totUrbVegCU(J)
      TOTHUB(J) = HLWNCU(J)+HUNVCU(J)
c      totHistUrbCU_Precip(J)=totHistUrbDpltn(J)-histUrbCU_AppldWtr(J)
      HURCUP(J)=TOTHUB(J)-HURCUW(J)
c      lvlUrbCU(J) = lvlLawnCU(J)+lvlNatVegCU(J)
      TOTPUB(J) = PLWNCU(J)+PUNVCU(J)
c      totHistUrbCU = totHistUrbCU + totHistUrbDpltn(J)
      TLHURB = TLHURB + TOTHUB(J)
c      histUrbCU_AppldWtr(13)=histUrbCU_AppldWtr(13)+histUrbCU_AppldWtr(J)
      HURCUW(13)=HURCUW(13)+HURCUW(J)
c      totHistUrbCU_Precip(13)=totHistUrbCU_Precip(13)+totHistUrbCU_Precip(J)
      HURCUP(13)=HURCUP(13)+HURCUP(J)
c      totLvlUrbCU = totLvlUrbCU + lvlUrbCU(J)
      TLPURB = TLPURB + TOTPUB(J)
c      temp =totHistCU_AF(J)+totHistUrbDpltn(J)
      A =TOTHDP(J)+TOTHUB(J)
c      totHistCU_AF(J) = IFIX((temp+0.05)*10.)/10.
      TOTHDP(J) = IFIX((A+0.05)*10.)/10.
c      totHistCU_Precip(J)=totHistCU_AF(J)-totHistCU_AppldWtr(J)
      TOTHCP(J)=TOTHDP(J)-THDPAW(J)
c      temp =totLvlCU_AF(J)+lvlUrbCU(J)
      A =TOTPDP(J)+TOTPUB(J)
c      totLvlCU_AF(J) = IFIX((temp+0.05)*10.)/10.
      TOTPDP(J) = IFIX((A+0.05)*10.)/10.
c      totLvlCU_Precip(J)=totLvlCU_AF(J)-totLvlCU_AppldWtr(J)
      TOTPCP(J)=TOTPDP(J)-TPDPAW(J)
c ???? TOTHW(J)=totHistCU_AF(J)-THW(J)  ??????
      TOTHW(J)=TOTHDP(J)-THW(J)
c ???? GRTHW=GRTHW+TOTHW(J)
      GRTHW=GRTHW+TOTHW(J)
c ???? GRTHDP = GRTHDP + totHistCU_AF(J)
      GRTHDP = GRTHDP + TOTHDP(J)
c      totHistCU_Precip(13)=totHistCU_Precip(13)+totHistCU_Precip(J)
      TOTHCP(13)=TOTHCP(13)+TOTHCP(J)
c      totHistCU_AppldWtr(13)=totHistCU_AppldWtr(13)+totHistCU_AppldWtr(J)
      THDPAW(13)=THDPAW(13)+THDPAW(J)
c ???? GRTPDP = GRTPDP + totLvlCU_AF(J)
      GRTPDP = GRTPDP + TOTPDP(J)
c      totLvlCU_AppldWtr=totLvlCU_AppldWtr+lvlCU_Appld(J)
      TPCUW=TPCUW+PCUA(J)
c ????      PDVIRR(J)=lvlCU_Appld(J)/EFF(J,NY)
      PDVIRR(J)=PCUA(J)/EFF(J,NY)
c ????      TPDVIR=TPDVIR+PDVIRR(J)
      TPDVIR=TPDVIR+PDVIRR(J)
c ????      RFF(J)=1-(1+PERCLS)*EFF(J,NY)
      RFF(J)=1-(1+PERCLS)*EFF(J,NY)
c ????      PIRRF(J)=PDVIRR(J)*RFF(J)
      PIRRF(J)=PDVIRR(J)*RFF(J)
c ????      IF(J.EQ.12)PIRRF(J)=PIRRF(J)+PDRAIN
      IF(J.EQ.12)PIRRF(J)=PIRRF(J)+PDRAIN
c ????      TPIRF=TPIRF+PIRRF(J)
      TPIRF=TPIRF+PIRRF(J)
c ????      PDOMRB(J)=PCAPTA*POP*MONDAY(J)/325826.
      PDOMRB(J)=PCAPTA*POP*MONDAY(J)/325826.
c ????      PURBRF(J)=PDOMRB(J)*RFFURB(J)
      PURBRF(J)=PDOMRB(J)*RFFURB(J)
c ????      TPDMRB=TPDMRB+PDOMRB(J)
      TPDMRB=TPDMRB+PDOMRB(J)
c ????      TPRBRF=TPRBRF+PURBRF(J)
      TPRBRF=TPRBRF+PURBRF(J)
c ????      A =PDVIRR(J)+PDOMRB(J)
      A =PDVIRR(J)+PDOMRB(J)
c ????      PDVTOT(J) = IFIX((A+0.05)*10.)/10.
      PDVTOT(J) = IFIX((A+0.05)*10.)/10.
c ????      TPDVTO=TPDVTO+PDVTOT(J)
      TPDVTO=TPDVTO+PDVTOT(J)
c ????      RPLR2P(J)=CUPRV(J)*RPLRAC(NY)/12000.
      RPLR2P(J)=CUPRV(J)*RPLRAC(NY)/12000.
c ????      RPLR1I(J)=CUINRV(J)*RPLRAC(NY)/12000.
      RPLR1I(J)=CUINRV(J)*RPLRAC(NY)/12000.
c ????      RPLR2P(13)=RPLR2P(13)+RPLR2P(J)
      RPLR2P(13)=RPLR2P(13)+RPLR2P(J)
c ????      RPLR1I(13)=RPLR1I(13)+RPLR1I(J)
      RPLR1I(13)=RPLR1I(13)+RPLR1I(J)
c      totLvlCU_Precip(13)=totLvlCU_Precip(13)+totLvlCU_Precip(J)
      TOTPCP(13)=TOTPCP(13)+TOTPCP(J)
c      totProjCU_AF(13)=totProjCU_AF(13)+totProjCU_AF(J)
      PNGWTR(13)=PNGWTR(13)+PNGWTR(J)
c      totLvlCU_AppldWtr(13)=totLvlCU_AppldWtr(13)+totLvlCU_AppldWtr(J)
      TPDPAW(13)=TPDPAW(13)+TPDPAW(J)
  380 CONTINUE
  391 CONTINUE
C..* 
c      DO 393 M=1,12
      DO 393 M=1,12
c      CU_precip(M)=AMIN1(totHistNatVegCU_AF(M),P(M,NY)*precipFactor*histNatVegAC/12000.)
      CUP(M)=AMIN1(HNGWTR(M),P(M,NY)*FACTOR*HRNVAC/12000.)
c      histCU_Ppt(M)=histCU_Ppt(M)+CU_precip(M)
      HCUP(M)=HCUP(M)+CUP(M)
c      CU_WtrSurfSeep(M)=totHistNatVegCU_AF(M)-CU_precip(M)
      CUS(M)=HNGWTR(M)-CUP(M)
c      histCU_WtrSurfSeep(M)=histCU_WtrSurfSeep(M)+CU_WtrSurfSeep(M)
      HCUSEEP(M)=HCUSEEP(M)+CUS(M)
  393 CONTINUE
C..*
C..
C..NATIVE VEGETATION CU SUMMARY**************************
C..   DELTA ONLY
C..
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
c      totHistNatVegCU_AF(13)=totHistNatVegCU_AF(13)*1000.
      HNGWTR(13)=HNGWTR(13)*1000.
c      totProjCU_AF(13)=totProjCU_AF(13)*1000.
      PNGWTR(13)=PNGWTR(13)*1000.
c      DO 394 L=1,12
      DO 394 L=1,12
c      totHistNatVegCU_AF(L)=totHistNatVegCU_AF(L)*1000.
      HNGWTR(L)=HNGWTR(L)*1000.
c      totProjCU_AF(L)=totProjCU_AF(L)*1000.
      PNGWTR(L)=PNGWTR(L)*1000.
C..*
c      CU_precip(L)=CU_precip(L)*1000.
      CUP(L)=CUP(L)*1000.
c      CU_WtrSurfSeep(L)=CU_WtrSurfSeep(L)*1000.
      CUS(L)=CUS(L)*1000.
C..*
  394 CONTINUE
C******* END CONVERSION ***********************************************
ccc	In the files I have seen altOutputTrig is always "-" 
c      IF(altOutputTrig.EQ.'-')GO TO 399
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
ccc	HRNVAC# histNatVegAC acreage of historic area,
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
c      TOTPRC=0.
c      TWSCU=0.
c      TCUPTW=0.
c      TOTRES=0.
c      THWSCU=0.
c      THRSWS=0.
c      TPWSCU=0.
c      TPRSWS=0.0
c      ripVegCU(13)=0.
c      CUPRV(13)=0.
c      CUINRV(13)=0.
c      RMRV2P(13)=0.
c      ripHistResidualCU(13)=0.
c      PMRV2P(13)=0.
c      PMRV1I(13)=0.0
c      TOTPRC=0.0
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
c      DO 2121 J=1,12 
      DO 2121 J=1,12 
c      currentPrecip(J)=P(J,NY)*precipFactor+SEEPRW(J)
      PRECIP(J)=P(J,NY)*FACTOR+SEEPRW(J)
c      totPrecip=totPrecip+currentPrecip(J)
      TOTPRC=TOTPRC+PRECIP(J)
c      ripVegCU(J)=ET_AdjFactor(J,NY)/avgEvap(J)*RVET(J)
      RVCU(J)=EVAP(J,NY)/AVEVAP(J)*RVET(J)
c      total for year Riparian CU of seep and precip(J)=AMIN1(currentPrecip(J),ripVegCU(J))
      CUPRV(J)=AMIN1(PRECIP(J),RVCU(J))
c      CUINRV(J)=ripVegCU(J)-CUPRV(J)
      CUINRV(J)=RVCU(J)-CUPRV(J)
c      ripHistCU_Precip(J)=CUPRV(J)*RMRVAC(NY)/12000.
      RMRV2P(J)=CUPRV(J)*RMRVAC(NY)/12000.
C..*
c      CU_precip(J)=AMIN1(ripHistCU_Precip(J),P(J,NY)*precipFactor*RMRVAC(NY)/12000.)
      CUP(J)=AMIN1(RMRV2P(J),P(J,NY)*FACTOR*RMRVAC(NY)/12000.)
c      histCU_Ppt(J)=histCU_Ppt(J)+CU_precip(J)
      HCUP(J)=HCUP(J)+CUP(J)
c      CU_WtrSurfSeep(J)=ripHistCU_Precip(J)-CU_precip(J)
      CUS(J)=RMRV2P(J)-CUP(J)
c      histCU_WtrSurfSeep(J)=histCU_WtrSurfSeep(J)+CU_WtrSurfSeep(J)
      HCUSEEP(J)=HCUSEEP(J)+CUS(J)
C..*
c      ripHistResidualCU(J)=CUINRV(J)*RMRVAC(NY)/12000.
      RMRV1I(J)=CUINRV(J)*RMRVAC(NY)/12000.
c      ripProjCU_Precip(J)=CUPRV(J)*ripProjAreaAC/12000.
      PMRV2P(J)=CUPRV(J)*PMRVAC/12000.
c      ripProjResidualCU(J)=CUINRV(J)*ripProjAreaAC/12000.
      PMRV1I(J)=CUINRV(J)*PMRVAC/12000.
c      ripVegCU(13)=ripVegCU(13)+ripVegCU(J)
      RVCU(13)=RVCU(13)+RVCU(J)
c      CUPRV(13)=CUPRV(13)+CUPRV(J)
      CUPRV(13)=CUPRV(13)+CUPRV(J)
c      CUINRV(13)=CUINRV(13)+CUINRV(J)
      CUINRV(13)=CUINRV(13)+CUINRV(J)
c      ripHistCU_Precip(13)=ripHistCU_Precip(13)+ripHistCU_Precip(J)
      RMRV2P(13)=RMRV2P(13)+RMRV2P(J)
c      ripHistResidualCU(13)=ripHistResidualCU(13)+ripHistResidualCU(J)
      RMRV1I(13)=RMRV1I(13)+RMRV1I(J)
c      ripProjCU_Precip(13)=ripProjCU_Precip(13)+ripProjCU_Precip(J)
      PMRV2P(13)=PMRV2P(13)+PMRV2P(J)
c      ripProjResidualCU(13)=ripProjResidualCU(13)+ripProjResidualCU(J)
      PMRV1I(13)=PMRV1I(13)+PMRV1I(J)
C..*
 2121 CONTINUE
C**** UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI (6/87) **********
c      DO 2122 L=1,12
      DO 2122 L=1,12
c      CU_precip(L)=CU_precip(L)*1000.
      CUP(L)=CUP(L)*1000.
c      CU_WtrSurfSeep(L)=CU_WtrSurfSeep(L)*1000.
      CUS(L)=CUS(L)*1000.
c 2122 ripHistCU_Precip(L)=ripHistCU_Precip(L)*1000.
 2122 RMRV2P(L)=RMRV2P(L)*1000.
C*** END CONVERSIONS ************************************************
C
ccc	In the files I have seen altOutputTrig is always "-" 
c      IF (altOutputTrig.EQ.'-')GO TO 2123
      IF (NOLIST.EQ.'-')GO TO 2123
      WRITE(41,4005)(CUP(M),M=1,12),(CUS(M),M=1,12),(RMRV2P(M),M=1,12)
C..*
 2123 CONTINUE
C..
C..WATER SURFACE CONSUMPTIVE USE*****************************
C..    DELTA ONLY
C..
c      DO 2125 J=1,12 
      DO 2125 J=1,12 
c      totWtrSurfCU(J)=ET_AdjFactor(J,NY)/avgEvap(J)*WSET(J)
      WSURCU(J)=EVAP(J,NY)/AVEVAP(J)*WSET(J)
c      wtrSurfCU_SeepPrecip(J)= AMIN1(currentPrecip(J),totWtrSurfCU(J))
      CUPTWS(J)= AMIN1(PRECIP(J),WSURCU(J))
c      inflowWtrSurfResidCU(J)= AMAX1(totWtrSurfCU(J)-currentPrecip(J),0.00)
      RESDCU(J)= AMAX1(WSURCU(J)-PRECIP(J),0.00)
c      TWSCU= TWSCU+ totWtrSurfCU(J)
      TWSCU= TWSCU+ WSURCU(J)
c      TCUPTW= TCUPTW+ wtrSurfCU_SeepPrecip(J)
      TCUPTW= TCUPTW+ CUPTWS(J)
c      TOTRES= TOTRES+ inflowWtrSurfResidCU(J)
      TOTRES= TOTRES+ RESDCU(J)
c      waterSurfHistCU_Precip(J)= (wtrSurfCU_SeepPrecip(J)* WSAC(NY))/12000.
      HWSCU(J)= (CUPTWS(J)* WSAC(NY))/12000.
C..* 
c      CU_precip(J)=AMIN1(waterSurfHistCU_Precip(J),P(J,NY)*precipFactor*WSAC(NY)/12000.)
      CUP(J)=AMIN1(HWSCU(J),P(J,NY)*FACTOR*WSAC(NY)/12000.)
c      histCU_Ppt(J)=histCU_Ppt(J)+CU_precip(J)
      HCUP(J)=HCUP(J)+CUP(J)
c      CU_WtrSurfSeep(J)=waterSurfHistCU_Precip(J)-CU_precip(J)
      CUS(J)=HWSCU(J)-CUP(J)
c      histCU_WtrSurfSeep(J)=histCU_WtrSurfSeep(J)+CU_WtrSurfSeep(J)
      HCUSEEP(J)=HCUSEEP(J)+CUS(J)
C..*
c      projWtrSurfCU_Precip(J)= (wtrSurfCU_SeepPrecip(J)* progWtrSurfAC)/12000.
      PWSCU(J)= (CUPTWS(J)* PWSA)/12000.
c      wtrSurfHistResidCU(J)=(inflowWtrSurfResidCU(J)* WSAC(NY))/12000.
      HRSCUW(J)=(RESDCU(J)* WSAC(NY))/12000.
c      projResidWtrSurfCU(J)=(inflowWtrSurfResidCU(J)* progWtrSurfAC)/12000.
      PRSCUW(J)=(RESDCU(J)* PWSA)/12000.
c      THWSCU= THWSCU+ waterSurfHistCU_Precip(J)
      THWSCU= THWSCU+ waterSurfHistCU_Precip(J)
c      TPWSCU= TPWSCU+ projWtrSurfCU_Precip(J)
      TPWSCU= TPWSCU+ PWSCU(J)
c      THRSWS= THRSWS+ wtrSurfHistResidCU(J)
      THRSWS= THRSWS+ HRSCUW(J)
c      TPRSWS= TPRSWS+ projResidWtrSurfCU(J)
      TPRSWS= TPRSWS+ PRSCUW(J)
C*** TOTAL APPLIED WATER CALC ADDED ********************* KG (5/21)
c      totHistCU_AppldWtr(J)=totHistCU_AppldWtr(J)+ripHistResidualCU(J)+wtrSurfHistResidCU(J)
      THDPAW(J)=THDPAW(J)+RMRV1I(J)+HRSCUW(J)
C***
C..*
c      ripHistCU_Precip(J)=ripHistCU_Precip(J)/1000.
      RMRV2P(J)=RMRV2P(J)/1000.
C..*
c      totHistCU_AF(J)=totHistCU_AF(J)+ripHistCU_Precip(J)+ripHistResidualCU(J)+waterSurfHistCU_Precip(J)+wtrSurfHistResidCU(J)
      TOTHDP(J)=TOTHDP(J)+RMRV2P(J)+RMRV1I(J)+HWSCU(J)+HRSCUW(J)
c      totLvlCU_AF(J)=totLvlCU_AF(J)+ripProjCU_Precip(J)+ripProjResidualCU(J)+projWtrSurfCU_Precip(J)+projResidWtrSurfCU(J)
      TOTPDP(J)=TOTPDP(J)+PMRV2P(J)+PMRV1I(J)+PWSCU(J)+PRSCUW(J)
 2125 CONTINUE
C..
C..RIPARIAN VEG. CONSUMPTIVE USE SUMMARY*******************
C..      DELTA ONLY
C..
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
c      ripHistCU_Precip(13)=ripHistCU_Precip(13)*1000.
      RMRV2P(13)=RMRV2P(13)*1000.
c      ripHistResidualCU(13)=ripHistResidualCU(13)*1000.
      RMRV1I(13)=RMRV1I(13)*1000.
c      ripProjCU_Precip(13)=ripProjCU_Precip(13)*1000.
      PMRV2P(13)=PMRV2P(13)*1000.
c      ripProjResidualCU(13)=ripProjResidualCU(13)*1000.
      PMRV1I(13)=PMRV1I(13)*1000.
c      DO 2130 L=1,12
      DO 2130 L=1,12
c      ripHistCU_Precip(L)=ripHistCU_Precip(L)*1000.
      RMRV2P(L)=RMRV2P(L)*1000.
c      ripHistResidualCU(L)=ripHistResidualCU(L)*1000.
      RMRV1I(L)=RMRV1I(L)*1000.
c      ripProjCU_Precip(L)=ripProjCU_Precip(L)*1000.
      PMRV2P(L)=PMRV2P(L)*1000.
c      ripProjResidualCU(L)=ripProjResidualCU(L)*1000.
      PMRV1I(L)=PMRV1I(L)*1000.
cC..*
C..*
c      CU_precip(L)=CU_precip(L)*1000.
      CUP(L)=CUP(L)*1000.
c      CU_WtrSurfSeep(L)=CU_WtrSurfSeep(L)*1000.
      CUS(L)=CUS(L)*1000.
c      waterSurfHistCU_Precip(L)=waterSurfHistCU_Precip(L)*1000.
      HWSCU(L)=HWSCU(L)*1000.
c      CUPLN(L)=CUPLN(L)*1000.
      CUPLN(L)=CUPLN(L)*1000.
c      CUSLN(L)=CUSLN(L)*1000.
      CUSLN(L)=CUSLN(L)*1000.
c      TCUPLN(L)=TCUPLN(L)*1000.
      TCUPLN(L)=TCUPLN(L)*1000.
c      urbVegCU_Precip(L)=urbVegCU_Precip(L)*1000.
      CUPVC(L)=CUPVC(L)*1000.
c      urbVegCU(L)=urbVegCU(L)*1000.
      CUSVC(L)=CUSVC(L)*1000.
c      totUrbVegCU(L)=totUrbVegCU(L)*1000.
      HUNVCU(L)=HUNVCU(L)*1000.
c      histCU_Ppt(L)=histCU_Ppt(L)*1000.
      HCUP(L)=HCUP(L)*1000.
c      histCU_WtrSurfSeep(L)=histCU_WtrSurfSeep(L)*1000.
      HCUSEEP(L)=HCUSEEP(L)*1000.
C..*
 2130 CONTINUE
C******* END CONVERSION ***********************************************
cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
ccc	In the files I have seen altOutputTrig is always "-" so from here we would always go to 439
cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
c      IF(altOutputTrig.EQ.'-')GO TO 439
      IF(NOLIST.EQ.'-')GO TO 439
C..*

ccc      WRITE(41,4006)(
ccc	CUP# CU of precip. (month) (M),M=1,12),(
ccc	CUS# water surface seep (month) (M),M=1,12),(
ccc	HWSCU# total water surface CU (month) (M),M=1,12)
      WRITE(41,4006)(CUP(M),M=1,12),(CUS(M),M=1,12),(HWSCU(M),M=1,12)

ccc      WRITE(41,4007)(CUPLN# CU of precip. on urban lawn (month) (M),M=1,12),(CUSLN# CU of seep
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
ccc	HCUP# historic consumptive use of precipitation (month) (M),M=1,12),(
ccc	HCUSEEP# historic consumptive use of seepage (month) (M) ,M=1,12)
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
c      THWSCU=THWSCU*1000.
      THWSCU=THWSCU*1000.
c      THRSWS=THRSWS*1000.
      THRSWS=THRSWS*1000.
c      TPWSCU=TPWSCU*1000.
      TPWSCU=TPWSCU*1000.
c      TPRSWS=TPRSWS*1000.
      TPRSWS=TPRSWS*1000.
c      DO 2140 L=1,12
      DO 2140 L=1,12
C..*      HWSCU(L)=HWSCU(L)*1000.
c      wtrSurfHistResidCU(L)=wtrSurfHistResidCU(L)*1000.
      HRSCUW(L)=HRSCUW(L)*1000.
c      projWtrSurfCU_Precip(L)=projWtrSurfCU_Precip(L)*1000.
      PWSCU(L)=PWSCU(L)*1000.
c      projResidWtrSurfCU(L)=projResidWtrSurfCU(L)*1000.
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
ccc	RESDCU# WATER SURFACE residual CU to be met from inflow in inches (M),
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
c      totHistCU_AF(13)=0.
      TOTHDP(13)=0.
c      totLvlCU_AF(13)=0.0
      TOTPDP(13)=0.0
c      DO 416 J=1,12
      DO 416 J=1,12
c      totHistCU_AF(13)=totHistCU_AF(13)+totHistCU_AF(J)
      TOTHDP(13)=TOTHDP(13)+TOTHDP(J)
c      totLvlCU_AF(13)=totLvlCU_AF(13)+totLvlCU_AF(J)
      TOTPDP(13)=TOTPDP(13)+TOTPDP(J)
  416 CONTINUE
C******* UNIT CONVERSION FROM TAF TO AF - K. GUIVETCHI   3/87 *********
c      TPR(13)=TPR(13)*1000.
      TPR(13)=TPR(13)*1000.
c      histUrbCU_AppldWtr(13)=histUrbCU_AppldWtr(13)*1000.
      HURCUW(13)=HURCUW(13)*1000.
c      totHistUrbCU_Precip(13)=totHistUrbCU_Precip(13)*1000.
      HURCUP(13)=HURCUP(13)*1000.
c      totHistCU_AF(13)=totHistCU_AF(13)*1000.
      TOTHDP(13)=TOTHDP(13)*1000.
c      totLvlCU_AF(13)=totLvlCU_AF(13)*1000.
      TOTPDP(13)=TOTPDP(13)*1000.
c      totHistUrbCU=totHistUrbCU*1000.
      TLHURB=TLHURB*1000.
c      totLvlUrbCU=totLvlUrbCU*1000.
      TLPURB=TLPURB*1000.
c  439 DO 2150 L=1,12
  439 DO 2150 L=1,12
c      TPR(L)=TPR(L)*1000.
      TPR(L)=TPR(L)*1000.
c      histUrbCU_AppldWtr(L)=histUrbCU_AppldWtr(L)*1000.
      HURCUW(L)=HURCUW(L)*1000.
c      totHistCU_AF(L)=totHistCU_AF(L)*1000.
      TOTHDP(L)=TOTHDP(L)*1000.
c      totLvlCU_AF(L)=totLvlCU_AF(L)*1000.
      TOTPDP(L)=TOTPDP(L)*1000.
c      totHistUrbDpltn(L)=totHistUrbDpltn(L)*1000.
      TOTHUB(L)=TOTHUB(L)*1000.
c      lvlUrbCU(L)=lvlUrbCU(L)*1000.
      TOTPUB(L)=TOTPUB(L)*1000.
c      totHistUrbCU_Precip(L)=totHistUrbCU_Precip(L)*1000.
      HURCUP(L)=HURCUP(L)*1000.
c      totHistCU_AppldWtr(L)=totHistCU_AppldWtr(L)*1000.
      THDPAW(L)=THDPAW(L)*1000.
 2150 CONTINUE
C******* END CONVERSION ***********************************************
ccc	In the files I have seen altOutputTrig is always "-"
c      IF(altOutputTrig.EQ.'-')GO TO 450
      IF(NOLIST.EQ.'-')GO TO 450
c      WRITE(10,1008)NAREA,(AREAID(K),K=1,7),(DATE(K),K=1,2)
      WRITE(10,1008)NAREA,(AREAID(K),K=1,7),(DATE(K),K=1,2)
      WRITE(10,1041)
      WRITE(10,1042)
      WRITE(10,1043)
c      WRITE(10,1044)LEVEL
      WRITE(10,1044)LEVEL
      WRITE(10,1045)
      WRITE(10,1046)
c      DO 440 M=1,12
      DO 440 M=1,12
c      IF(M.NE.7)GO TO 440
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
c      IF(NOH.EQ.'-')GO TO 454
      IF(NOH.EQ.'-')GO TO 454
C..*
      WRITE(13,3003)NAREA,KODE(13),NYR(NY),(HCUP(M),M=1,12)
ccc	HCUSEEP# historic seepage used to satisfy consumptive use demands by months and water years
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
