
       PROGRAM NODCU14

c-----Added more output to file NODCU10-WY.21. Changes bracketed by CNM-10/04/95
c-----Added island diversion data without seepage (variable ID1)
c-----I'll use the data to compare DICU results to Report 4 AW data that
c-----did not include seepage.

c-----Modified program NODCU11                           NM (8/7/95)
c-----Modified to include Paul's new seepage estimates and irrigation efficiency by 
c-----DOC subregion. See Chap 5 (Paul's work) in the 16th Annual Report.
c-----Seepage changes: A drained seepage component has been added.
c-----Irrigation efficiency: Varies by MWQI subregions.
c-----As a quick fix the drained seepage values will be added to both the div and returns.
c-----Both the seepage and irr eff. values are dependent on MWQI subregions. 

c-----MWQI subregion   Drained seepage(AF/acre)     Irrigation efficiency
c-----High DOC region         0.095                    57%
c-----Midrange DOC            0.074                    67%
c-----Lower DOC region        0.013                    85%
c-----Changes are bracketed by C12

c-----Modified program NODCU10                          PNT (6/9/95)
c-----NODCU10 was used to process one water year at a time. The new version can
c-----process all the water years for which the hydrologic data is available
c-----in one run, resulting in considerable savings in CPU time. In addition
c-----a new ASCII file is created (junk.txt), which in turn can be used
c-----to convert this data into DSS format. To do this just type:

c-----         dssts <junk.txt

c-----This may take some time to process. After this conversion is completed,
c-----you may want to delete the file junk.txt, because it's fairly big.

c-----Modified program NODCU9                          NM (3/10/95)
c-----Modified to seperate seepage from irrigation diversions mainly 
c-----for particle tracking modeling reasons. Total channel diversions will 
c-----now be segregated and shown in two extra columns:irrigation diversions and seepage.
c-----Changes are bracketed by CNM
c-----ID1 is the irrigation diversion component of the total diversion from the channel
c-----ID2 is the seepage component of the total diversion from the channel
c-----Segregation of channel diversions was not calculated for ag den files and no
c-----changes were made to NODCU output files (NODCU10-WY.2?)


c-----Modified program NODCU8                          - NM (2/25/92)
c-----The main change is in the Ag Drain files. Parviz (PNT) needs
c-----a new format for the Ag Drain Model. Instead of P-ET, we want 
c-----the change in soil moisture. This file soil.prc is created by a new
c-----version of dicu5 called dicu5.1. Changes can be noted by C@.
c-----Change hard coding of the number of nodes. Now, the number of nodes willbe read 
c-----from the GEOM-NODES file.
c-----Changed DO LOOP maximum index for reading in DICU5.XX files

c-----PROGRAM NODCU8
c-----Modified NODCU7                                   - NM (5/7/91)

c----- Reads input files straight from dicu program. (eliminates pickwy.f)

c----- Asks whether drainage conc should be written in TDS or CL

c----- Output files have the water year as part of the filename

c----- The nodes written to the drn and div file are exactly the same as those
c        written to the drn quality file. ALL the nodes in the model are included
c        in the output file regardless of the drainage and diversion values. to do
c        this, node numbers (419 total) are read from a file called geom-nodes.
c
c----- Writes drn,div and precip-ET in Ag-Drain model input format.12 monthly files.
c        (All values in CFS). UNIT 30

C********************************************************************
C  MODIFIED NODCU6 :                                    - KG 11/06/89
C  *  RE-FORMAT OUTPUT FOR USE IN THE DELTA ISLAND MODEL
C  *  NEW PARAMETERS: WX,TAF2CFS,D2,DIV,DRN
C  *  NEW OUTPUT UNITS: 25,26,27
C  *  DETERMINE NET ATMOSPHERIC WATER EXCHANGE    ==> UNIT 25
C  *  DETERMINE NODAL DIVERSIONS BY ISLAND        ==> UNIT 26
C     INCLUDES SEEPAGE, APPLIED WATER, AND
C       APPLIED LEACH WATER
C  *  DETERMINE NODAL DRAINAGE RETURNS BY ISLAND  ==> UNIT 27
C     INCLUDES EXCESS APPLIED WATER AND
C       DRAINED LEACH WATER
C     DOES NOT INCLUDE PRECIP RUNOFF
C
C
C  MODIFIED NODCU5 :                                    - KG 09/13/89
C   * PREPARED FOR USING DRAINAGE TDS CONC FROM DWR BULLETIN 123
C     BY REGIONS (NORTH, SOUTHEAST, WEST)
C   * RE-ASSIGN ISLAND DRAINAGE TDS CONC :
C      ISL 102: NODE 274 SET FROM SOUTHEAST TO NORTH
C      ISL 103: NODES 253, 274, 276, 278 SET FROM SOUTHEAST TO NORTH
C      ISL 121: NODES 354, 355 SET FROM NORTH TO WEST
C      ISL 130: NODES 66 - 70, SET FROM WEST TO SOUTHEAST
C
C  MODIFIED NODCU3 :                                    - KG 06/21/89
C   * READ IN REPRESENTATIVE MONTHLY DRAINAGE TDS CONCENTRATION
C     FOR EACH 142 ISLAND AREAS (FILE IDRNTDS.DAT; UNIT 12; ARRAY DS)
C     POSSIBLE SOURCE: DWR BULLETIN 123
C   * ASSIGN NODAL DRAINAGE TDS CONCENTRATIONS
C     WEIGHTED BY ISLAND-TO-NODE DRAINAGE RETURN FLOW (ARRAY NDS)
C   * WRITE OUT NODAL DRAINAGE TDS CONCENTRATIONS
C     REPORT FORMAT ==> FILE NODCU5.24; UNIT 24
C     SEPARATE MONTHLY FILES : OCT-DTDS THRU SEP-DTDS (UNITS 51-62)
C
C  DETERMINATION OF IRRIGATION DIVERSIONS AND DRAINAGES FOR DELTA
C  ISLANDS AND ALLOCATION TO DWR/RMA DELTA MODEL NODES   - KG FEB. 88
C  INPUT FROM DICU5 RUN FOR CONSECUTIVE YEARS
C    -INCLUDING MONTHLY DELTA NET CHANNEL DEPLETIONS     - KG MAR. 88
C  RUNTIME SCREEN PRINT ADDED; INPUT FILE NAME CHANGE    - KG APR. 88
C  
C  INPUT DATA INCLUDE:
C
C      1 - APPLIED WATER AND SEEPAGE (CONSUMPTIVE USE PROGRAM DICU4)
C      2 - LOWLANDS LEACHING REQUIREMENT ESTIMATES (G. SATO, 1981)
C      3 - NODAL ALLOCATION FACTORS (D. TAYLOR, 1987)
C      4 - IRRIGATION EFFICIENCY ESTIMATE (CALIBRATION PARAMETER)
C
C********************************************************************
C..
      CHARACTER*3 WMONTH
      CHARACTER*8 QUANTITY, tempconc
C12      CHARACTER*11 AGFILE
C12      CHARACTER*13 QUALITY
      CHARACTER*16 OUTFILE
      INTEGER*4 LPYR,YR,IFLAG,JIDFLAG,JDFLAG,CONC
C12      INTEGER*2 AGUNIT
      REAL*4 AW,S,IIE,LRA,LRD,FI,FD,ID,ID1,ID2,D,NID,NID1,NID2,ND,NIDSUB,NIDSUB1,NIDSUB2,NDSUB
      REAL*4 TAID,TAD,TMDID,TMDD,TADID,TADD,NTMDID,NTMDD,FISUM,FDSUM
      REAL*4 TCU,PREC,RO,CDNET,CDDNET,NCDNET
      REAL*4 NDS,TAF2CFS,WX,DIV,DIV1,DIV2,DRN,D2
C..   
      INTEGER     MAXISL,MAXNODE
      PARAMETER   (MAXISL=142, MAXNODE=500)
      DIMENSION WMONTH(12),TAF2CFS(12),A(MAXISL,12),B(MAXISL,12),C(MAXISL,12,1922:2100),NODE(MAXNODE)
C..
      COMMON/A/ YR,LPYR
      COMMON/B/ TADID,TADD,CDDNET
      COMMON/C/ TAID(MAXISL),TAD(MAXISL),TMDID(12),TMDD(12),CDNET(12),
     .          NTMDID(12),NTMDD(12),FISUM(MAXISL),FDSUM(MAXISL),IIE(MAXISL)
      COMMON/D/ S(MAXISL,12,1922:2100),AW(MAXISL,12,1922:2100),LRA(MAXISL,12),LRD(MAXISL,12),
     .          FI(MAXNODE,MAXISL),FD(MAXNODE,MAXISL),TCU(MAXISL,12,1922:2100),PREC(MAXISL,12,1922:2100),
     .          RO(MAXISL,12)
      COMMON/E/ ID(MAXISL,12),ID1(MAXISL,12),ID2(MAXISL,12),D(MAXISL,12),NID(MAXNODE,12),NID1(MAXNODE,12),
     .          NID2(MAXNODE,12),ND(MAXNODE,12),NIDSUB(MAXNODE,12),NIDSUB1(MAXNODE,12),NIDSUB2(MAXNODE,12),
     .          NDSUB(MAXNODE,12),NCDNET(12)
      COMMON/F/ DS(MAXISL,12),NDS(MAXNODE,12)
      COMMON/G/ WX(MAXISL,12),D2(MAXISL,12),DIV(MAXISL,12),DIV1(MAXISL,12),DIV2(MAXISL,12),DRN(MAXISL,12)
C..
      DATA WMONTH/'OCT','NOV','DEC','JAN','FEB','MAR','APR','MAY',
     .            'JUN','JUL','AUG','SEP'/

      DATA TAF2CFS/0.01626,0.01681,0.01626,0.01626,0.018,0.01626,
     .             0.01681,0.01626,0.01681,0.01626,0.01626,0.01681/

      CHARACTER*1 JUNK, ANSWER
	CHARACTER*4 tempyear
	CHARACTER*80 DICU5_14,DICU5_17,DICU5_12,DICU5_27,DIVFCTR_RMA,DRNFCTR_RMA,LEACHAPL_DAT
	CHARACTER*80 LEACHDRN_DAT,IDRNTDS_DAT,DRNCL_123,GEOM_NODES,IRREFF_DAT,subarea_info

      REAL QDRN(MAXNODE,1922:2100,12),QIRR(MAXNODE,1922:2100,12),QSEEP(MAXNODE,1922:2100,12)
      INTEGER NUMDIV(MAXISL),NUMDRN(MAXISL),LISTDIV(MAXISL,65),LISTDRN(MAXISL,65)
      LOGICAL NDFLG(MAXNODE)

C12A
      CHARACTER*1 docregion(maxisl)
      CHARACTER*100 junk1
	CHARACTER*120 FLE
      INTEGER uplow(maxisl),area(maxisl),drnseep(maxisl)
C12B

C..
      WRITE(*,'(47H**** PROGRAM NODCU12 --- VERSION MARCH 96 ****//)')

c      WRITE(*,'(47H**** PROGRAM NODCU11 --- VERSION JULY 95 ****//)')

C     WRITE(*,'(47H**** PROGRAM NODCU10 --- VERSION 3/10/95 ****//)')

CNM      WRITE(*,'(46H **** PROGRAM NODCU9 --- VERSION 2/25/92 ****//)')

C@-----WRITE(*,'(46H **** PROGRAM NODCU8 --- VERSION 04/30/91 ****//)')
C..
C  INITIALIZE
C..
      DO N=1,MAXNODE
         NDFLG(N)=.FALSE.
      ENDDO

      IYR1=1922
      IYR2=2100
      
 901  FORMAT(' THE CURRENT VERSION OF NODCU PROGRAM ASSUMES THAT THE'/
     &     ' DATABASE CONTAINS DATA FROM  YEAR: ',I4,'  TO  ',I4//
     &     ' IS THIS ASSUMPTION CORRECT? (Y/N): ',$)
ccccccccccccccccccccccccccccccccccc      READ(*,801)ANSWER
	CALL GETENV('years_ok',ANSWER)
c      READ(yn_answr,'(I4)') NFYR   

c 801  FORMAT(A1)
      IF(ANSWER.EQ.'N' .OR. ANSWER.EQ.'n')THEN
         WRITE(*,902)
 902     FORMAT(' ENTER BEGINNING AND ENDING YEAR yyyy yyyy: ',$)
cccccccccccccccccccccccccccccccccc         READ*,IYR1,IYR2
 	   CALL GETENV('begwy',tempyear)
         READ(tempyear,'(I4)') IYR1   
	   CALL GETENV('endwy',tempyear)
         READ(tempyear,'(I4)') IYR2   

      ENDIF

      DO I=1,MAXISL
         TAID(I)=0.0
         TAD(I)=0.0
         FISUM(I)=0.0
         FDSUM(I)=0.0
         IIE(I)=0.0
         DO N=1,MAXNODE
            FI(N,I)=0.0
            FD(N,I)=0.0
         ENDDO
      ENDDO

      DO M=1,12
         TMDID(M)=0.0
         TMDD(M)=0.0
         CDNET(M)=0.0
         NTMDID(M)=0.0
         NTMDD(M)=0.0
         NCDNET(M)=0.0
C--------DIV(M)=0.0
C--------DRN(M)=0.0
         DO I=1,MAXISL
            DIV(I,M)=0.0
            DIV1(I,M)=0.0
            DIV2(I,M)=0.0
            DRN(I,M)=0.0
            LRA(I,M)=0.0
            LRD(I,M)=0.0
            RO(I,M)=0.0
            WX(I,M)=0.0
            ID(I,M)=0.0
            ID1(I,M)=0.0
            ID2(I,M)=0.0
            D(I,M)=0.0
            D2(I,M)=0.0
            DS(I,M)=0.0
            DO YR=IYR1,IYR2
               S(I,M,YR)=0.0
               AW(I,M,YR)=0.0
               TCU(I,M,YR)=0.0
               PREC(I,M,YR)=0.0
               C(I,M,YR)=0.0
            ENDDO
         ENDDO

         DO N=1,MAXNODE
            NIDSUB(N,M)=0.0
            NIDSUB1(N,M)=0.0
            NIDSUB2(N,M)=0.0
            NDSUB(N,M)=0.0
            NID(N,M)=0.0
            NID1(N,M)=0.0
            NID2(N,M)=0.0
            ND(N,M)=0.0
            NDS(N,M)=0.0
         ENDDO
      ENDDO
C..      
c      WRITE(*,'(30H ENTER WATER YEAR AS <YY> ==> )')
c      READ(*,*)YR
c      WRITE(*,'(49H IF LEAP YEAR ENTER < 1 > IF NOT ENTER < 0 > ==> )')
c      READ(*,*)LPYR
C*
      WRITE(*,1800)
 1800 FORMAT(/'ENTER TYPE OF DRAINAGE CONCENTRATION DATA (ENTER 1 FOR TDS,2 FOR CL): ',$)
cccccccccccccccccccccccccccccccccccccccccccccccccc      READ(*,*) CONC
	CALL GETENV('datatype',tempconc)
      READ(tempconc,'(I4)') CONC   

      WRITE(*,951)
 951  FORMAT(/' DO YOU WANT TO CREATE AN ASCII FILE FOR LOADING DATA IN DSS?(Y/N): ',$)
cccccccccccccccccccccccccccccccccccccccccccccccccccc      READ(*,801)ANSWER
	CALL GETENV('ascii',ANSWER)
      write(*,*) "  "
      IF(ANSWER.EQ.'Y' .OR. ANSWER.EQ.'y')THEN
         WRITE(*,928)
 928     FORMAT(/' THERE IS AN ASCII FILE CREATED NAMED junk.txt.'/
     &        ' TO CONVERT THIS TO A DSS FILE, JUST TYPE:'//
     &        '       dssts <junk.txt'//,
     &        ' AFTER THE MODEL RUN IS COMPLETED.'/
     &        ' AFTER MAKING SURE THAT THE DSS FILE IS PROPERLY'/
     &        ' CREATED, YOU CAN DELETE THE FILE junk.txt'//
     &        ' ENTER THE NAME OF THE DSS FILE: ',$)
cccccccccccccccccccccccccccccccccccccccccccccccccc         READ(*,929)FLE
	   CALL GETENV('dssfile',FLE)
c 929     FORMAT(A120)
      ENDIF


C-----1. READS INPUT FILES FROM THE NEW PICKWY PROGRAM (YEAR IS PART OF FILENAME)
	CALL GETENV('DICU5.14',DICU5_14)
	CALL GETENV('DICU5.17',DICU5_17)
	CALL GETENV('IRREFF.DAT',IRREFF_DAT)
	CALL GETENV('DIVFCTR.RMA',DIVFCTR_RMA)
	CALL GETENV('DRNFCTR.RMA',DRNFCTR_RMA)
	CALL GETENV('LEACHAPL.DAT',LEACHAPL_DAT)
	CALL GETENV('LEACHDRN.DAT',LEACHDRN_DAT)
	CALL GETENV('DICU5.12',DICU5_12)
	CALL GETENV('DICU5.27',DICU5_27)
	
	OPEN(1,FILE=DICU5_14,FORM='FORMATTED',STATUS='OLD')
      OPEN(2,FILE=DICU5_17,FORM='FORMATTED',STATUS='OLD')
      OPEN(3,FILE=IRREFF_DAT,FORM='FORMATTED',STATUS='OLD')
      OPEN(4,FILE=DIVFCTR_RMA,FORM='FORMATTED',STATUS='OLD')
      OPEN(7,FILE=DRNFCTR_RMA,FORM='FORMATTED',STATUS='OLD')
      OPEN(8,FILE=LEACHAPL_DAT,FORM='FORMATTED',STATUS='OLD')
      OPEN(9,FILE=LEACHDRN_DAT,FORM='FORMATTED',STATUS='OLD')
      OPEN(10,FILE=DICU5_12,FORM='FORMATTED',STATUS='OLD')
      OPEN(11,FILE=DICU5_27,FORM='FORMATTED',STATUS='OLD')

C*    MODIFY PROGRAM TO OUTPUT EITHER CL OR TDS CONCENTRATIONS --NM(5/7/91)

      IF (CONC.EQ.1) THEN
	CALL GETENV('IDRNTDS.DAT',IDRNTDS_DAT)
      OPEN(12,FILE=IDRNTDS_DAT,FORM='FORMATTED',STATUS='OLD')
      ENDIF

      IF (CONC.EQ.2) THEN
	CALL GETENV('DRNCL.123',DRNCL_123)
      OPEN(12,FILE=DRNCL_123,FORM='FORMATTED',STATUS='OLD')
      ENDIF
C-----READ THE SOIL MOISTURE TO WRITE TO THE ISLAND FILES (FOR THE AG DRAIN MODEL)

C-----OPEN(99,FILE="SOIL.PRC",FORM='FORMATTED',STATUS="OLD")
C-----
C-----2. OUTPUT FILES HAVE THE WATER YEAR AS PART OF THE FILENAME
C      WRITE(*,911)
C 911  FORMAT(/' PICK A YEAR FOR ISLAND DATA TO BE PRINTED: ',$)
C      READ*,IYRPICK
C      IF (IYRPICK.GT.1900) IYRPICK=IYRPICK-1900
C      DO 99 I=20,27
C         WRITE(OUTFILE,'(8HNODCU10-,I2.2,1H.,I2.2)') IYRPICK,I
C         OPEN(I,FILE=OUTFILE,FORM='FORMATTED',STATUS='UNKNOWN')
C 99   CONTINUE

C...
C      OPEN(20,FILE='NODCU8.20',FORM='FORMATTED',STATUS='NEW')
C      OPEN(21,FILE='NODCU8.21',FORM='FORMATTED',STATUS='NEW')
C      OPEN(22,FILE='NODCU8.22',FORM='FORMATTED',STATUS='NEW')
C      OPEN(23,FILE='NODCU8.23',FORM='FORMATTED',STATUS='NEW')
C      OPEN(24,FILE='NODCU8.24',FORM='FORMATTED',STATUS='NEW')
C      OPEN(25,FILE='NODCU8.25',FORM='FORMATTED',STATUS='NEW')
C      OPEN(26,FILE='NODCU8.26',FORM='FORMATTED',STATUS='NEW')
C      OPEN(27,FILE='NODCU8.27',FORM='FORMATTED',STATUS='NEW')
C
C..
C  READ INPUT DATA AND ECHO PRINT
C..
C      WRITE(20,999)YR
C      WRITE(25,996)YR
C      WRITE(20,'(23HLEAP YEAR SPECIFICATION)')
C      WRITE(20,*)LPYR

C*----MODIFY TO READ DATA STRAIGHT FROM DICU OUTPUT FILES RATHER THAN USING PICKWY.F.

C*      READ(2,10)((AW(I,M),M=1,12),I=1,MAXISL)

C*----READ APPLIED WATER
C@----MAX INCREASED FROM 10000 to 50000

      DO ISL=1,MAXISL
         READ(2,910)JUNK
         READ(1,910)JUNK
         READ(10,910)JUNK
         READ(11,910)JUNK
 910     FORMAT(/A1)

         DO YR=IYR1,IYR2

c            READ(2,('(I3,4X,I4,12F8.0)')) ISLDICU,IYR,(AW(ISLDICU,M,YR),M=1,12)
            READ(2,980) ISLDICU,IYR,(AW(ISLDICU,M,YR),M=1,12)
            IF (IYR.NE.YR.OR. ISL.NE.ISLDICU) THEN
               WRITE(*,990)
 990           FORMAT(' ERROR... Changes in the format of DICU output files'/
     &                '          are detected. NODCU program must be modified'/
     &                '          to accomodate the changes')
               STOP
            ENDIF
            READ(1,980) ISLDICU,IYR,(S(ISLDICU,M,YR),M=1,12)
            READ(10,980) ISLDICU,IYR,(PREC(ISLDICU,M,YR),M=1,12)
            READ(11,980) ISLDICU,IYR,(TCU(ISLDICU,M,YR),M=1,12)
 980		  FORMAT(I3,4X,I4,12F8.0)
C            READ(99,('(1X,I4,I5,12F6.3)')) ISLDICU,IYR,(C(ISLDICU,M,YR),M=1,12)
         ENDDO
         IF(MOD(ISL,10).EQ.0 .OR. ISL.EQ.MAXISL)THEN
            WRITE(*,991)ISL
 991        FORMAT(' FINISHED READING DATA FOR SUBAREA: ',I4)
         ENDIF
      ENDDO

      READ(3,'(f4.2)')(IIE(I),I=1,MAXISL)
      READ(8,15)((LRA(I,M),M=1,12),I=1,MAXISL)
      READ(9,15)((LRD(I,M),M=1,12),I=1,MAXISL)

C12A---Read in data to calculate new seepage component (drained seepage)

	CALL GETENV('subarea-info',subarea_info)
      open(100,file=subarea_info,form='formatted',status='old')

      do j=1,6
         read(100,'(a100)') junk1
      enddo

      do i=1,maxisl
         read(100,'(57X,I1,5X,I10,8X,A1)') uplow(i),area(i),docregion(i)
      end do
      
      close(100)

C---Calculte drained seepage for each subarea

      do 100 i=1,maxisl

         if (uplow(i).eq.2.or.i.eq.133.or.i.eq.134.or.i.eq.135.or.i.eq.136.
     &or.i.eq.137.or.i.eq.140.or.i.eq.141.or.i.eq.142) then 
            drnseep(i)=0
            go to 100
         endif
         if (uplow(i).eq.1) then
            if (docregion(i).eq."L") drnseep(i)=0.013*area(i)
            if (docregion(i).eq."M") drnseep(i)=0.074*area(i)
            if (docregion(i).eq."H") drnseep(i)=0.095*area(i)  
            go to 100
         endif
         write(*,*) " Aghhhh! Subarea is neither in the Delta Lowlands or Uplands"   
         stop
 100   continue

C--------Add drn seepage component to seepage calculated by DICU program amd reported in DICU5.14

         do yr=iyr1,iyr2
            do i=1,maxisl
               do m=1,12
                  s(i,m,yr)=s(i,m,yr)+drnseep(i)
               enddo
            enddo
         enddo

c12B---End of changes

C-----READ THE LIST OF DWRDSM MODEL NODES

	CALL GETENV('GEOM-NODES',GEOM_NODES)
      OPEN(50,FILE=GEOM_NODES,FORM='FORMATTED',STATUS="OLD")
      READ(50,*) NUMBER
      DO 805 I=1,NUMBER
         READ (50,*) NODE(I)
 805  CONTINUE

C-----READ DRAINAGE AND DIVERSION NODE ALLOCATION FACTORS
C-----FI = DIVERSION ALLOCATION FACTOR
C-----FD = DRAINAGE ALLOCATION FACTOR

      READ(4,19)
      READ(7,19)
      IPICK=0
 5    READ(4,20,END=6) I,N,FI(N,I)
C      WRITE(20,21) I,N,FI(N,I)
      IF(I.NE.IPICK)THEN
         IPICK=I
         K=1
         NUMDIV(IPICK)=K
      ELSE
         K=K+1
         NUMDIV(IPICK)=K
         IF(K.GT.65)THEN
            STOP ' ERROR ...EXCEEDED SIZE OF ARRAY LISTDIV'
         ENDIF
      ENDIF
      LISTDIV(I,K)=N
      GOTO 5
    6 CONTINUE
      IPICK=0
 7    READ(7,20,END=8) I,N,FD(N,I)
      IF(I.NE.IPICK)THEN
         IPICK=I
         K=1
         NUMDRN(IPICK)=K
      ELSE
         K=K+1
         NUMDRN(IPICK)=K
         IF(K.GT.65)THEN
            STOP ' ERROR ...EXCEEDED SIZE OF LISTDRN'
         ENDIF
      ENDIF
      LISTDRN(I,K)=N

C      WRITE(20,21) I,N,FD(N,I)
      GOTO 7
    8 CONTINUE
      READ(12,19)
      READ(12,13) ((DS(I,M),M=1,12),I=1,MAXISL)

C*
      WRITE(*,*) " FINISHED READING AND WRITING INPUT DATA"

C..
C  NODAL ALLOCATION FACTOR CHECK
C..
      DO 150 I=1,MAXISL
      DO 125 N=1,MAXNODE
      FISUM(I)=FISUM(I)+FI(N,I)
      FDSUM(I)=FDSUM(I)+FD(N,I)
  125 CONTINUE
  150 CONTINUE
C..
C..
C  DETERMINE PRECIPITATION RUNOFF IN ACRE-FT
C
C  DETERMINE NET ATMOSPHERIC WATER EXCHANGE IN ACRE-FT
C     POSITIVE ==> NET WATER ADDED TO ISLAND FROM ATMOSPHERE
C     NEGATIVE ==> NET WATER REMOVED FROM ISLAND TO ATMOSPHERE
C..
      DO YR=IYR1,IYR2
         TADID=0.
         TADD=0.
         CDDNET=0.
         print*,' processing yr:',YR
         LPYR=0
         RealYR=float(YR)
         IF(MOD(RealYR,4.).EQ.0) LPYR=1
         DO 300 I=1,MAXISL
            DO 200 M=1,12
               WX(I,M)=PREC(I,M,YR) - TCU(I,M,YR)
               RO(I,M)=PREC(I,M,YR) - TCU(I,M,YR)
               IF(RO(I,M).LT.0) THEN
                  RO(I,M)=0.
               ENDIF
 200        CONTINUE
 300     CONTINUE
C..
         DO 99 I=20,27
            IF(YR.NE.IYR1) CLOSE(I)
			PRINT *,YR,IYR1,I
            WRITE(OUTFILE,'(8HNODCU12-,I4.4,1H.,I2.2)') YR,I

            OPEN(I,FILE=OUTFILE,FORM='FORMATTED',STATUS='UNKNOWN')
 99      CONTINUE
C--------WRITE ISLAND DATA
         WRITE(20,999)YR
         WRITE(25,996)YR
         WRITE(20,'(23HLEAP YEAR SPECIFICATION)')
         WRITE(20,*)LPYR
         
         WRITE(20,'(46HMONTHLY APPLIED WATER FOR EACH ISLAND, ACRE-FT)')
         WRITE(20,11)(I,(AW(I,M,YR),M=1,12),I=1,MAXISL)
         
         WRITE(20,'(40HMONTHLY SEEPAGE FOR EACH ISLAND, ACRE-FT)')      
         WRITE(20,11)(I,(S(I,M,YR),M=1,12),I=1,MAXISL)
         
         WRITE(20,'(45HMONTHLY PRECIPIATION FOR EACH ISLAND, ACRE-FT)')
         WRITE(20,11)(I,(PREC(I,M,YR),M=1,12),I=1,MAXISL)
         WRITE(25,'(45HMONTHLY PRECIPIATION FOR EACH ISLAND, ACRE-FT)')
         WRITE(25,11)(I,(PREC(I,M,YR),M=1,12),I=1,MAXISL)
         
         WRITE(20,'(47HMONTHLY TOTAL CONSUMPTIVE USE EACH ISL, ACRE-FT)')
         WRITE(20,11)(I,(TCU(I,M,YR),M=1,12),I=1,MAXISL)
         WRITE(25,'(47HMONTHLY TOTAL CONSUMPTIVE USE EACH ISL, ACRE-FT)')
         WRITE(25,11)(I,(TCU(I,M,YR),M=1,12),I=1,MAXISL)
         WRITE(20,'(30HISLAND IRRIGATION EFFICIENCIES)')
         WRITE(20,12)(I,IIE(I),I=1,MAXISL)
         WRITE(20,'(39HLEACH WATER APPLIED TO ISLANDS, ACRE-FT)')
         WRITE(20,11)(I,(LRA(I,M),M=1,12),I=1,MAXISL)
         WRITE(20,'(41HLEACH WATER DRAINED FROM ISLANDS, ACRE-FT)')
         WRITE(20,11)(I,(LRD(I,M),M=1,12),I=1,MAXISL)

C--------WRITE(20,'(41HMONTHLY SOIL MOISTURE FOR EACH ISLAND, IN)')      
C--------WRITE(20,('(I4,4X,12F6.3)')) (I,(C(I,M,YR),M=1,12),I=1,MAXISL)

         WRITE(20,'(49HISLAND DIVERSION ALLOCATION FACTORS FOR DSM NODES)')

         DO I=1,MAXISL
            DO N=1,NUMDIV(I)
               NN=LISTDIV(I,N)
               WRITE(20,21) I,NN,FI(NN,I)
            ENDDO
         ENDDO

         WRITE(20,'(48HISLAND DRAINAGE ALLOCATION FACTORS FOR DSM NODES)')
         DO I=1,MAXISL
            DO N=1,NUMDRN(I)
               NN=LISTDRN(I,N)
               WRITE(20,21) I,NN,FD(NN,I)
            ENDDO
         ENDDO

         IF (CONC.EQ.1) THEN
            WRITE(20,'(49HMONTHLY ISLAND DRAINAGE TDS CONCENTRATION IN MG/L)')
         ENDIF
         IF (CONC.EQ.2) THEN
            WRITE(20,'(49HMONTHLY ISLAND DRAINAGE  CL CONCENTRATION IN MG/L)')
         ENDIF
         WRITE(20,11) (I,(DS(I,M),M=1,12),I=1,MAXISL)

         WRITE(20,'(40HNODAL ALLOCATION FACTOR SUMMATION BY ISL)')
         WRITE(20,22)(I,FISUM(I),FDSUM(I),I=1,MAXISL)

         WRITE(20,'(38HISLAND PRECIPITATION RUNOFF IN ACRE-FT)')
         WRITE(20,11)(I,(RO(I,M),M=1,12),I=1,MAXISL)
C..---------
         WRITE(20,'(38HISLAND NET ATM WATER EXCHANGE, ACRE-FT)')
         WRITE(20,11)(I,(WX(I,M),M=1,12),I=1,MAXISL)
C..---------
         WRITE(25,'(38HNET ISLAND ATM WATER EXCHANGE, ACRE-FT)')
         WRITE(25,11)(I,(WX(I,M),M=1,12),I=1,MAXISL)
C..------
C  DETERMINE ISLAND IRRIGATION DIVERSIONS AND DRAINAGES IN ACRE-FT
C  USING ISLAND IRRIGATION EFFICIENCIES
C-----ID = IRRIGATION DIVERSION
C-----D  = IRRIGATION DRAINAGE
C--------
CNM1-----
c--------ID1 = the irrigation diversion component of the total diversion from the channel
c--------ID2 = the seepage component of the total diversion from the channel
CNM2

C  D2 IS FOR DELTA ISLAND MODEL
C..
         DO 500 I=1,MAXISL
            DO 400 M=1,12
               ID(I,M)=S(I,M,YR) + (AW(I,M,YR)/IIE(I)) + LRA(I,M)
CNM1-----------
               ID1(I,M)= (AW(I,M,YR)/IIE(I)) + LRA(I,M)
               ID2(I,M)= S(I,M,YR)
CNM2-----------

C12A------------Add the drained seepage component to the drainage also

C------------D(I,M)=( AW(I,M,YR) * ((1.-IIE(I))/IIE(I)) ) + LRD(I,M) + RO(I,M)
C------------D2(I,M)=( AW(I,M,YR) * ((1.-IIE(I))/IIE(I)) ) + LRD(I,M)
               
               D(I,M)=( AW(I,M,YR) * ((1.-IIE(I))/IIE(I)) ) + LRD(I,M) + RO(I,M) + drnseep(i)
               D2(I,M)=( AW(I,M,YR) * ((1.-IIE(I))/IIE(I)) ) + LRD(I,M) + drnseep(i)             
C12B
 400        CONTINUE
 500     CONTINUE
C..
C  SUMMATIONS BY ISLAND AND DELTA OVER ALL MONTHS
C..
         DO 550 I=1,MAXISL
            TAID(I)=0.
            TAD(I)=0.
            DO 525 M=1,12
               TAID(I)=TAID(I) + ID(I,M)
               TAD(I)=TAD(I) + D(I,M)
 525        CONTINUE
            TADID=TADID + TAID(I)
            TADD=TADD + TAD(I)
 550     CONTINUE
         CDDNET=TADID-TADD
C..
C  SUMMATIONS BY MONTH OVER ALL ISLANDS
C..
         DO 600 M=1,12
            TMDID(M)=0.
            TMDD(M)=0.
            DO 575 I=1,MAXISL
               TMDID(M)=TMDID(M) + ID(I,M)
               TMDD(M)=TMDD(M) + D(I,M)
 575        CONTINUE
            CDNET(M)=TMDID(M)-TMDD(M)
 600     CONTINUE
C..
C  WRITE OUT ISLAND RESULTS IN ACRE-FEET
C..
         WRITE(21,1001)YR,(WMONTH(M),M=1,12)
         WRITE(21,1000)(I,(ID(I,M),M=1,12),TAID(I),I=1,MAXISL)
         WRITE(21,1005)(TMDID(M),M=1,12), TADID
         WRITE(21,1002)YR,(WMONTH(M),M=1,12)
         WRITE(21,1000)(I,(D(I,M),M=1,12),TAD(I),I=1,MAXISL)
         WRITE(21,1006)(TMDD(M),M=1,12), TADD
         WRITE(21,1012)(CDNET(M),M=1,12), CDDNET

CNM-10/04/95

         WRITE(21,1025)YR,(WMONTH(M),M=1,12) 
         WRITE(21,1024)(I,(ID1(I,M),M=1,12),I=1,MAXISL)

CNM-10/04/95


C..
C  ALLOCATE TO DWR/DSM DELTA MODEL NODES
C  USING ISLAND-TO-NODE ALLOCATION FACTORS
C
C  DETERMINE WEIGHTED NODAL DRAINAGE TDS CONCENTRATIONS - KG 06/20/89
C  USING ISLAND DRAINAGE TDS CONCENTRATIONS IN ARRAY DS
C  AND ISLAND VOLUME CONTRIBUTIONS TO EACH NODE (NDSUB);
C  LOAD ARRAY NDS
C..
         DO 750 N=1,MAXNODE
            DO 700 M=1,12
               NID(N,M)=0.
CNM1-----------
               NID1(N,M)=0.
               NID2(N,M)=0.
CNM2-----------
               ND(N,M)=0.
               NDS(N,M)=0.
               DO 650 I=1,MAXISL
                  NIDSUB(N,M)=FI(N,I)/100. * ID(I,M)

CNM1--------------
                  NIDSUB1(N,M)=FI(N,I)/100. * ID1(I,M)
                  NIDSUB2(N,M)=FI(N,I)/100. * ID2(I,M)
CNM2--------------


                  NDSUB(N,M)=FD(N,I)/100. * D(I,M)
C..---------------
                  NID(N,M)=NID(N,M) + NIDSUB(N,M)
CNM1--------------

                  NID1(N,M)=NID1(N,M) + NIDSUB1(N,M)
                  NID2(N,M)=NID2(N,M) + NIDSUB2(N,M)
CNM2--------------

                  ND(N,M)=ND(N,M) + NDSUB(N,M)
C...
C  REASSIGN SELECTED ISLAND DRAINAGE CONC           - KG 09/14/89
C  CONSISTANT WITH BULLETIN 123
C...
                  IF(I.EQ.102 .AND. N.EQ.274) THEN
                     DSZ=DS(9,M)
                     NDS(N,M)=NDS(N,M) + (NDSUB(N,M)*DSZ)
                  ELSEIF((I.EQ.103) .AND. (N.EQ.253 .OR. N.EQ.274 .OR. N.EQ.276 .OR.
     >                    N.EQ.278)) THEN
                     DSZ=DS(9,M)
                     NDS(N,M)=NDS(N,M) + (NDSUB(N,M)*DSZ)
                  ELSEIF((I.EQ.121) .AND. (N.EQ.354 .OR. N.EQ.355)) THEN
                     DSZ=DS(8,M)
                     NDS(N,M)=NDS(N,M) + (NDSUB(N,M)*DSZ)
                  ELSEIF((I.EQ.130) .AND. (N.GE.66 .AND. N.LE.70)) THEN
                     DSZ=DS(31,M)
                     NDS(N,M)=NDS(N,M) + (NDSUB(N,M)*DSZ)
                  ELSE
C...
C...
                     NDS(N,M)=NDS(N,M) + (NDSUB(N,M)*DS(I,M))
                  ENDIF
 650           CONTINUE
               IF(ND(N,M).GT.0.) NDS(N,M)=NDS(N,M)/ND(N,M)
 700        CONTINUE
 750     CONTINUE
C..
C  SUM UP OVER ALL NODES FOR EACH MONTH
C..
         DO 785 M=1,12
            NTMDID(M)=0.
            NTMDD(M)=0.
            DO 775 N=1,MAXNODE
               NTMDID(M)=NTMDID(M) + NID(N,M)
               NTMDD(M)=NTMDD(M) + ND(N,M)
 775        CONTINUE
            NCDNET(M)=NTMDID(M)-NTMDD(M)
 785     CONTINUE
C..
C  WRITE OUT NODAL RESULTS IN ACRE-FEET
C..
         WRITE(22,1013)YR,(WMONTH(M),M=1,12)
         WRITE(22,1019)(N,(NID(N,M),M=1,12),N=1,MAXNODE)
         WRITE(22,1020)(NTMDID(M),M=1,12)
         WRITE(22,1014)YR,(WMONTH(M),M=1,12)
         WRITE(22,1019)(N,(ND(N,M),M=1,12),N=1,MAXNODE)
         WRITE(22,1021)(NTMDD(M),M=1,12)
         WRITE(22,1015)(NCDNET(M),M=1,12)
C..
C  CONVERT FROM ACRE-FEET TO MEAN MONTHLY CFS
C  ALLOWING FOR LEAP YEARS
C..
         DO 900 N=1,MAXNODE
            DO 800 M=1,12
               IF(M.EQ.2 .OR. M.EQ.7 .OR. M.EQ.9 .OR. M.EQ.12) THEN
                  NID(N,M)=NID(N,M) * 0.01681
CNM1---

                  NID1(N,M)=NID1(N,M) * 0.01681
                  NID2(N,M)=NID2(N,M) * 0.01681

CNM2--------------


                  ND(N,M)=ND(N,M) * 0.01681
C
               ELSE IF(M.EQ.5 .AND. LPYR.EQ.0) THEN
                  NID(N,M)=NID(N,M) * 0.018
CNM1--------------
                  NID1(N,M)=NID1(N,M) * 0.018
                  NID2(N,M)=NID2(N,M) * 0.018
CNM2--------------

                  ND(N,M)=ND(N,M) * 0.018
C
               ELSE IF(M.EQ.5 .AND. LPYR.EQ.1) THEN
                  NID(N,M)=NID(N,M) * 0.01738
CNM1--------------
                  NID1(N,M)=NID1(N,M) * 0.01738
                  NID2(N,M)=NID2(N,M) * 0.01738
CNM2--

                  ND(N,M)=ND(N,M) * 0.01738
C-----------------
               ELSE
                  NID(N,M)=NID(N,M) * 0.01626

CNM1--------------
                  NID1(N,M)=NID1(N,M) * 0.01626
                  NID2(N,M)=NID2(N,M) * 0.01626
CNM2--

                  ND(N,M)=ND(N,M) * 0.01626
               ENDIF
 800        CONTINUE
 900     CONTINUE
C..
         DO 950 M=1,12
            IF(M.EQ.2 .OR. M.EQ.7 .OR. M.EQ.9 .OR. M.EQ.12) THEN
               NTMDID(M)=NTMDID(M) * 0.01681
               NTMDD(M)=NTMDD(M) * 0.01681
               NCDNET(M)=NCDNET(M) * 0.01681
C
            ELSE IF(M.EQ.5 .AND. LPYR.EQ.0) THEN
               NTMDID(M)=NTMDID(M) * 0.018
               NTMDD(M)=NTMDD(M) * 0.018
               NCDNET(M)=NCDNET(M) * 0.018
C
            ELSE IF(M.EQ.5 .AND. LPYR.EQ.1) THEN
               NTMDID(M)=NTMDID(M) * 0.01738
               NTMDD(M)=NTMDD(M) * 0.01738
               NCDNET(M)=NCDNET(M) * 0.01738
C
            ELSE
               NTMDID(M)=NTMDID(M) * 0.01626
               NTMDD(M)=NTMDD(M) * 0.01626
               NCDNET(M)=NCDNET(M) * 0.01626
            ENDIF
 950     CONTINUE
C..
C           END CONVERSIONS
C..
C  WRITE OUT NODAL RESULTS IN CFS
C
C  WRITE OUT NODAL DRAINAGE TDS CONCENTRATIONS IN MG/L
C..
         WRITE(23,1003)YR,(WMONTH(M),M=1,12)
         WRITE(23,1009)(N,(NID(N,M),M=1,12),N=1,MAXNODE)
         WRITE(23,1010)(NTMDID(M),M=1,12)
         WRITE(23,1004)YR,(WMONTH(M),M=1,12)
         WRITE(23,1009)(N,(ND(N,M),M=1,12),N=1,MAXNODE)
         WRITE(23,1011)(NTMDD(M),M=1,12)
         WRITE(23,1016)(NCDNET(M),M=1,12)
         WRITE(24,1017)YR,(WMONTH(M),M=1,12)
C...
         DO 955 N=1,MAXNODE
            IFLAG=0
            DO 953 M=1,12
               IF(NDS(N,M).NE.0.) IFLAG=IFLAG+1
 953        CONTINUE
            IF(IFLAG.NE.0) WRITE(24,1019) N,(NDS(N,M),M=1,12)
 955     CONTINUE
C...
C...
C  DETERMINE NODAL DIVERSIONS AND DRAINAGE RETURNS BY ISLAND AND NODE
C  FOR THE DELTA ISLAND MODEL
C
C  WRITE NON ZERO DIVERSIONS BY ISLAND       TO UNIT 26
C  WRITE NON ZERO DRAINAGE RETURNS BY ISLAND TO UNIT 27
C...
         TAF2CFS(5)=0.018
         IF(LPYR.EQ.1) TAF2CFS(5)=0.01738
         WRITE(26,997)YR,(WMONTH(M),M=1,12)
         WRITE(27,998)YR,(WMONTH(M),M=1,12)
C...
         DO 780 I=1,MAXISL
            DO 776 N=1,MAXNODE
               JIDFLAG=0
               JDFLAG=0
               DO 765 M=1,12
C-----------------DIV(M)=0.0
C-----------------DRN(M)=0.0
 765           CONTINUE
               DO 770 M=1,12
                  DIV(I,M)=FI(N,I)/100. * ID(I,M) * TAF2CFS(M)

CNM1--------------
                  DIV1(I,M)=FI(N,I)/100. * ID1(I,M) * TAF2CFS(M)
                  DIV2(I,M)=FI(N,I)/100. * ID2(I,M) * TAF2CFS(M)
CNM2--------------


                  DRN(I,M)=FD(N,I)/100. * D2(I,M) * TAF2CFS(M)
                  IF(DIV(I,M).NE.0.) JIDFLAG=1
CNM1--------------
                  IF(DIV1(I,M).NE.0.) JIDFLAG=1
                  IF(DIV2(I,M).NE.0.) JIDFLAG=1
CNM2--------------
                  IF(DRN(I,M).NE.0.) JDFLAG=1
 770           CONTINUE
               IF(JIDFLAG.EQ.1) WRITE(26,23) I,N,(DIV(I,M),M=1,12)
               IF(JDFLAG.EQ.1) WRITE(27,23) I,N,(DRN(I,M),M=1,12)
 776        CONTINUE
 780     CONTINUE

C-----4. FILES WRITTEN FOR AG-DRAIN (DELTA ISLAND) MODEL.
         DO 771 M=1,12
C            AGUNIT=30+M
C            WRITE(AGFILE,'(A3,3H-AG,1H.,I2.2)') WMONTH(M),YR
C            OPEN(AGUNIT,FILE=AGFILE,FORM='FORMATTED',STATUS='UNKNOWN')
C            WRITE(AGUNIT,1023) WMONTH(M),YR
            DO 772 I=1,MAXISL
               A(I,M)=TAF2CFS(M)*ID(I,M)
               B(I,M)=TAF2CFS(M)*D(I,M)            
C@-------------C(I,M)=TAF2CFS(M)*(PREC(I,M)-TCU(I,M))
C--------------CONVERT SOIL MOISTURE FROM INCHES TO FEET.
C               C(I,M,YR)=C(I,M,YR)/12
C               WRITE(AGUNIT,'(I5,2F10.2,F10.3)') I,A(I,M),B(I,M),C(I,M)
 772        CONTINUE
 771     CONTINUE
C...
C..
C  RESULTS IN DWR/DSM DELTA MODEL INPUT FORMAT  (CFS)
C..
         DO 975 M=1,12
            IUNIT=M+30
            JUNIT=M+50

C-----2. OUTPUT FILES HAVE THE WATER YEAR AS PART OF THE FILENAME

            WRITE(QUANTITY,'(A3,1H.,I4.4)')WMONTH(M),YR
            OPEN(IUNIT,FILE=QUANTITY,FORM='FORMATTED',STATUS='UNKNOWN')      
            WRITE(IUNIT,1007) WMONTH(M),YR

C-----------IF (CONC.EQ.1) THEN
C-----------WRITE(QUALITY,'(A3,6H-DTDS.,I2.2)')WMONTH(M),YR
C-----------OPEN(JUNIT,FILE=QUALITY,FORM='FORMATTED',STATUS='UNKNOWN')
C-----------WRITE(JUNIT,1018) WMONTH(M),YR
C-----------ELSEIF (CONC.EQ.2) THEN
C-----------WRITE(QUALITY,'(A3,4H-CL.,I2.2)')WMONTH(M),YR
C-----------OPEN(JUNIT,FILE=QUALITY,FORM='FORMATTED',STATUS='UNKNOWN')      
C-----------WRITE(JUNIT,2018) WMONTH(M),YR
C-----------ENDIF


C-----3. THE NODES WRITTEN TO THE DRN AND DIV FILE ARE EXACTLY THE SAME AS THOSE
C        WRITTEN TO THE DRN QUALITY FILE( NO MISMATCH IN NODE NUMBERS OR TOTAL
C        NUMBER OF NODES).

            DO 957 K=1,NUMBER
               N=NODE(K)
CNM------------WRITE(IUNIT,1008) N,ND(N,M),NID(N,M)

               WRITE(IUNIT,1008) N,ND(N,M),NID(N,M),NID1(N,M),NID2(N,M)   
C--------------WRITE(JUNIT,1022) N,NDS(N,M),ND(N,M)
               QDRN(N,YR,M)=ND(N,M)
               QIRR(N,YR,M)=NID1(N,M)
               QSEEP(N,YR,M)=NID2(N,M)
               IF(.NOT. NDFLG(N))THEN
                  IF((QDRN(N,YR,M)+QIRR(N,YR,M)+QSEEP(N,YR,M)).NE.0)THEN
                     NDFLG(N)=.TRUE.
                  ENDIF
               ENDIF
 957        CONTINUE

            CLOSE(IUNIT)
C-----------CLOSE(JUNIT)
 975     CONTINUE
      ENDDO

      IF(ANSWER.EQ.'Y' .OR. ANSWER.EQ.'y')THEN
         OPEN(FILE='junk.txt',UNIT=52,STATUS='UNKNOWN')
         WRITE(52,930)FLE
 930     FORMAT(A120)
         WRITE(*,940)
 940     FORMAT(/////'   ........ WRITING THE RESULTS TO BE LOADED INTO DSS .....'///
     &               '                     <<<< PLEASE WAIT >>>> ')
         DO IFLOW=1,3
            IF(IFLOW.EQ.3) WRITE(*,941)
 941        FORMAT(//'                     <<<< ALMOST DONE >>>> ')
            DO K=1,NUMBER
               N=NODE(K)
               IF(NDFLG(N))THEN
                  IF(N.NE.193)THEN
                     IF(IFLOW.EQ.1)THEN
                        WRITE(52,1910)N
 1910                   FORMAT('A=DICU-HIST+NODE  B=',I4,'  C=DRAIN-FLOW  D=01JAN1920  E=1MONTH  F=DWR-BDO'/
     &                       'CFS'/
     &                       'PER-AVER'/
     &                       '31OCT1921 2400')
                     ELSEIF(IFLOW.EQ.2)THEN
                        WRITE(52,1911)N
 1911                   FORMAT('A=DICU-HIST+NODE  B=',I4,'  C=DIV-FLOW  D=01JAN1920  E=1MONTH  F=DWR-BDO'/
     &                       'CFS'/
     &                       'PER-AVER'/
     &                       '31OCT1921 2400')
                     ELSEIF(IFLOW.EQ.3)THEN
                        WRITE(52,1912)N
 1912                   FORMAT('A=DICU-HIST+NODE  B=',I4,'  C=SEEP-FLOW  D=01JAN1920  E=1MONTH  F=DWR-BDO'/
     &                       'CFS'/
     &                       'PER-AVER'/
     &                       '31OCT1921 2400')
                     ENDIF
                  ELSE
C--------------------Name for node 193 is changed to BBID (Byron Bethany)
                     IF(IFLOW.EQ.1)THEN
                        WRITE(52,1980)
 1980                   FORMAT('A=DICU-HIST+RSVR  B=','BBID','  C=DRAIN-FLOW  D=01JAN1920  E=1MONTH  F=DWR-BDO'/
     &                       'CFS'/
     &                       'PER-AVER'/
     &                       '31OCT1921 2400')
                     ELSEIF(IFLOW.EQ.2)THEN
                        WRITE(52,1981)
 1981                   FORMAT('A=DICU-HIST+RSVR  B=','BBID','  C=DIV-FLOW  D=01JAN1920  E=1MONTH  F=DWR-BDO'/
     &                       'CFS'/
     &                       'PER-AVER'/
     &                       '31OCT1921 2400')
                     ELSEIF(IFLOW.EQ.3)THEN
                        WRITE(52,1982)
 1982                   FORMAT('A=DICU-HIST+RSVR  B=','BBID','  C=SEEP-FLOW  D=01JAN1920  E=1MONTH  F=DWR-BDO'/
     &                       'CFS'/
     &                       'PER-AVER'/
     &                       '31OCT1921 2400')
                     ENDIF
                  ENDIF
                  DO YR=iyr1,iyr2
                     DO M=1,12
 
c                        if(yr.eq.98) then
                                                  
c       write(*,*) "???? WARNING: Hard wiring to write dss data till only sep 1997), NM (12/1/97)"
c                        endif

c       if(yr.eq.98.and.m.gt.1) go to 9999
          
                        IF(IFLOW.EQ.1)THEN
                           WRITE(52,1920)QDRN(N,YR,M)
 1920                      FORMAT(F7.2)
                        ELSEIF(IFLOW.EQ.2)THEN
                           WRITE(52,1921)QIRR(N,YR,M)
 1921                      FORMAT(F7.2)
                        ELSEIF(IFLOW.EQ.3)THEN
                           WRITE(52,1922)QSEEP(N,YR,M)
 1922                      FORMAT(F7.2)
                        ENDIF

 9999                ENDDO
                  ENDDO
                  WRITE(52,1940)
 1940             FORMAT('END')
               ENDIF
            ENDDO
         ENDDO
         WRITE(52,1950)
 1950     FORMAT('FINISH')
         CLOSE(52)
      ENDIF


C..
C  FORMAT STATEMENTS
C..
   10 FORMAT(//9X,12F8.0)
   11 FORMAT(I4,4X,12F8.0)
   12 FORMAT(I4,4X,F8.2)
   13 FORMAT(5X,12F8.0)
   15 FORMAT(8X,12F8.0)
   19 FORMAT(///)
   20 FORMAT(I4,1X,I4,2X,F10.2)
   21 FORMAT(1X,I4,1X,I4,2X,F10.2)
   22 FORMAT(I4,4X,2F10.2)
   23 FORMAT(2I4,12F8.2)
  996 FORMAT( 'DELTA ISLAND PRECIP AND TOTAL CONSUMPTIVE USE'/
     .'ECHO PRINT OF INPUT DATA FOR WATER YEAR ',I4)
  997 FORMAT('DWR DELTA MODEL NODAL CHANNEL DIVERSIONS BY ISLAND (CFS)'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' ISL  NODE',
     .T8,12(5X,A3)/)
  998 FORMAT('DWR DELTA MODEL NODAL DRAINAGE RETURNS BY ISLAND (CFS) WARNING: RUNOFF NOT INCLUDED'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' ISL  NODE',
     .T8,12(5X,A3)/)
  999 FORMAT( 'DELTA ISLAND CHANNEL DIVERSIONS AND DRAINAGES'/
     .'ECHO PRINT OF INPUT DATA FOR WATER YEAR ',I4)
 1000 FORMAT(I4,4X,12F8.0,4X,F8.0)
 1001 FORMAT('DELTA ISLAND CHANNEL DIVERSIONS (ACRE-FEET)'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' ISLAND',
     .T7,12(5X,A3),4X,'ISL TOTAL'/)
 1002 FORMAT(//'DELTA ISLAND DRAINAGE RETURNS (ACRE-FEET)'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' ISLAND',
     .T7,12(5X,A3),4X,'ISL TOTAL'/)
 1003 FORMAT('DWRDSM DELTA MODEL NODAL CHANNEL DIVERSIONS (CFS)'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' NODE',
     .T7,12(5X,A3),4X/)
 1004 FORMAT(//'DWRDSM DELTA MODEL NODAL DRAINAGE RETURNS (CFS)'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' NODE',
     .T7,12(5X,A3)/)
 1005 FORMAT('TOTAL: MONTHLY AND ANNUAL DELTA DIVERSIONS (ACRE-FT)'/
     .       8X,12F8.0,2X,F10.0)
 1006 FORMAT('TOTAL: MONTHLY AND ANNUAL DELTA DRAINAGE (ACRE-FT)'/
     .       8X,12F8.0,2X,F10.0)
CNM---1007 FORMAT('DWR/RMA DELTA MODEL HYDROLOGY ENTRIES BY NODE (CFS)'/
CNM---.'DRAINAGES (INPUT) AND DIVERSIONS (OUTPUT) FOR ',A3,
CNM---.' - WATER YEAR ',I4 //' NODE      DRN	     DIV'/)
CNM---1008 FORMAT(I5,2F10.2)

 1007 FORMAT('DWRDSM DELTA MODEL HYDROLOGY ENTRIES BY NODE (CFS)'/,
     .'DRAINAGES (INPUT) AND DIVERSIONS (OUTPUT) FOR ',A3,' - WATER YEAR ',I4 /,
     .'CHANNEL DIVERSIONS ARE SEPARATED INTO IRRIGATION DIVERSIONS AND SEEPAGE'/,
     .' NODE      DRN	   CH DIV  IRRIG DIV   SEEPAGE  '/)

 1008  FORMAT(I5,4F10.2)

 1009 FORMAT(I4,4X,12F8.2)
 1010 FORMAT(/'MONTHLY DELTA DIVERSIONS OVER ALL NODES (CFS)'/
     .       8X,12F8.0)
 1011 FORMAT(/'MONTHLY DELTA DRAINAGE RETURNS OVER ALL NODES (CFS)'/
     .       8X,12F8.0)
 1012 FORMAT(//'MONTHLY AND ANNUAL NET DELTA CD (ACRE-FT)'/
     .       8X,12F8.0,2X,F10.0)
 1013 FORMAT('DWRDSM DELTA MODEL NODAL CHANNEL DIVERSIONS (ACRE-FT)'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' NODE',
     .T7,12(5X,A3)/)
 1014 FORMAT(//'DWRDSM DELTA MODEL NODAL DRAINAGE RETURNS (ACRE-FT)'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' NODE',
     .T7,12(5X,A3)/)
 1015 FORMAT(//'MONTHLY NET DELTA CD OVER ALL NODES (AF)'/
     .       8X,12F8.0)
 1016 FORMAT(//'MONTHLY NET DELTA CD OVER ALL NODES (CFS)'/
     .       8X,12F8.0)
 1017 FORMAT('DWR DELTA MODEL NODAL DRAINAGE TDS CONCENTRATION (MG/L)'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' NODE',
     .T7,12(5X,A3),4X/)
 1018 FORMAT('DWR DELTA MODEL NODAL DRAINAGE TDS CONCENTRATION (MG/L)'/
     .'AND DRAINAGE RETURN FLOW (CFS) FOR  ',A3,' - WATER YEAR ',I4 //
     .' NODE    MG/L      CFS'/)
 2018 FORMAT('DWR DELTA MODEL NODAL DRAINAGE CL CONCENTRATION (MG/L)'/
     .'AND DRAINAGE RETURN FLOW (CFS) FOR  ',A3,' - WATER YEAR ',I4 //
     .' NODE    MG/L      CFS'/)
 1019 FORMAT(I4,4X,12F8.0)
 1020 FORMAT('MONTHLY DELTA DIVERSIONS OVER ALL NODES (AF)'/
     .       8X,12F8.0)
 1021 FORMAT('MONTHLY DELTA DRAINAGE RETURNS OVER ALL NODES (AF)'/
     .       8X,12F8.0)
 1022 FORMAT(I5,F10.0,F10.2)
C-----1023 FORMAT('DWR/RMA DELTA MODEL HYDROLOGY ENTRIES BY ISLAND (CFS)'/
C-----.'DRAINAGES (INPUT) AND DIVERSIONS (OUTPUT) FOR ',A3,
C-----.' - WATER YEAR ',I4 //' ISLAND CH. DEP    AG-RET    P - ET'/)
 1023 FORMAT('DWRDSM DELTA MODEL HYDROLOGY ENTRIES BY ISLAND (CFS)'/
     .'DRAINAGES (INPUT) AND DIVERSIONS (OUTPUT) FOR ',A3,
     .' - WATER YEAR ',I4 //' ISLAND CH. DEP    AG-RET    H(FT.)'/)
C..

CNM-10/04/95
 1025 FORMAT(//'DELTA ISLAND CHANNEL DIVERSIONS: AW+LW, NO SEEPAGE INCLUDED (ACRE-FEET)'/
     .'FOR OCTOBER THRU SEPTEMBER - WATER YEAR ',I4 //' ISLAND',
     .T7,12(5X,A3))

 1024 FORMAT(I4,4X,12F8.0)

CNM-10/04/95

      STOP 'SUCCESS'
      END
      
