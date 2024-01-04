      PROGRAM DICU5IN1                                                   

C	Updated for running on the PC, Jim Wilde 9/27/04

C#----New version that combines dicu5in.f, precip.f and areax.f
C#----NM (3/30/92)

C#    PROGRAM DICU5IN

C********************************************************************  
C  PREPARATION OF INPUT FILE DICU5.1 TO THE                            
C  DELTA ISLAND CONSUMPTIVE USE PROGRAM (DICU5)                        
C  FOR RUNNING CONSECUTIVE WATER YEARS:  NFYR THRU LFYR (PROMPTED INPUT)
C  CURRENT VERSION SET FOR:                                            
C    142 DELTA ISLAND AREAS                                            
C    15 CROP (LAND USE) CATEGORIES                                     
C    NO HEADER (NOH) PARAMETER DISABLED (SET TO ' ')                   
C                                                       - KG MAR 1988  
C********************************************************************  
C..                                                                    

C	FOR PC VERSION
C	use dfport	
	CHARACTER*50 STAPREC
	CHARACTER*20 dilldat
	CHARACTER*20 diuldat
	CHARACTER*20 areaid
	CHARACTER*20 arealu_00
	CHARACTER*20 arealu_77
	CHARACTER*20 rwlu_00
	CHARACTER*20 rwlu_77
	CHARACTER*20 AREAID_00
	CHARACTER*20 AREAID_77
	CHARACTER*15 ename
	CHARACTER*20 temp
	CHARACTER*4 tempyear

      INTEGER*4 NSTUDY,NFYR,LYR,NOYR,NOCRP,LEVEL,YR                       
      INTEGER*4 ALAND,ACODE,AREA,NPTYPE,WY                             
      CHARACTER*1 NOH,NOLIST,WYTYP
C#      CHARACTER*5 PTYPE                                                
      CHARACTER*12 DATE                                                
      CHARACTER*18 RWLU,RWLUN,RWLUC                                    
      CHARACTER*50 ANAME                                               
C#      CHARACTER*78 PRECIP                                                
      CHARACTER*79 STMEMO,CROP,ET,IR,RTSTL,RTSTU,SML,SMU,SEL,SEU       
      CHARACTER*79 PRMEMO,PINDX,URLINE,PLINE,PLLINE,PURF,PPLINE        
      CHARACTER*79 RWL21,PRW,EV21,PCTLS,DUM                            
      CHARACTER*96 AREALU,AREALUN,AREALUC                              
      CHARACTER*104 SKIP                                               
      
C#----Precip.f variable declarations inserted here

      REAL D,R,B,T,S,LI,G
      CHARACTER*15 INFILE
      DIMENSION D(200,12),R(200,12),B(200,12),T(200,12),S(200,12),LI(200,12),G(200,12),
     &PRECIP(200,37,12)
C..                                                                    
      COMMON/A/ NSTUDY,NFYR,LYR,NOYR,NOCRP,LEVEL,NPTYPE,               
     .   ALAND,ACODE,AREA                                              
      COMMON/B/ NOH,NOLIST,DATE,STMEMO(3),CROP(20),ET(24),IR(20),      
     .   RTSTL(22),RTSTU(22),SML(40),SMU(40),SEL(23),SEU(23),          
     .   PRMEMO(2),PINDX,URLINE,PLINE,SKIP,PLLINE(4),PURF,PPLINE(5),   
     .   RWL21,PRW(4),EV21,AREALU,AREALUN,AREALUC,                     
     .   PREC(200),RWLU,RWLUN,RWLUC,ANAME,PCTLS,WYTYP         
C..                                                                    
      NSTUDY=142                                                       
      NOCRP=15                                                         
      NOH=' '                                                          

C..   Retrieve the start and end years from the environmental variables                                                                
      WRITE(*,'(28H THE STARTING WATER YEAR IS:)')          
	CALL GETENV('styr',tempyear)
      READ(tempyear,'(I4)') NFYR   
	print *,NFYR                                                   

      WRITE(*,'(26H THE ENDING WATER YEAR IS:)')            
	CALL GETENV('ndyr',tempyear)
      READ(tempyear,'(I4)') LYR   
	print *,LYR                                                   
      
      
      NOYR=LYR-NFYR+1                                                  
C..

C#----Precip.f program inserted here (with minor changes)
c#      program precip

c#-----This program reads rainfall data for 7 stations in the delta (Davis, Rio Vista,
c#-----Brentwood, Stockton, Lodi and Gault). 
c#-----Kamyar came up with 37 different kinds of precip distributions
c#-----in the delta after using the Theissen polygon method (2/5/87).
c#-----Those distributions are used in this program to create input files for program DICU5IN.FOR
c#-----NM (4/25/91)



      DO 21 I=1,NOYR
	  MUNIT=100+I        
        YR=NFYR+ICOUNTER
	  write (temp,'(i4)') YR
        ename = '7STAPREC.WY'//adjustl(temp)
	  CALL GETENV(ename,STAPREC)
	  WRITE(INFILE,'(7HSTAPREC)')
        OPEN(MUNIT,FILE=STAPREC,STATUS='OLD')

C#------Read precip data

        READ(MUNIT,'(A80)') JUNK
          DO 22 M=1,12
          READ(MUNIT,'(7(5X,F5.2))') D(I,M),R(I,M),B(I,M),T(I,M),S(I,M),
     &LI(I,M),G(I,M)
 22       CONTINUE

      DO 23 N=1,37
            DO 24 M=1,12
              IF (N.EQ.1) PRECIP(I,N,M)=G(I,M)
			IF (N.EQ.2) PRECIP(I,N,M)=LI(I,M)
              IF (N.EQ.3) PRECIP(I,N,M)=0.09*S(I,M)+0.91*LI(I,M) 
              IF (N.EQ.4) PRECIP(I,N,M)=0.18*S(I,M)+0.82*LI(I,M)
              IF (N.EQ.5) PRECIP(I,N,M)=0.27*S(I,M)+0.73*LI(I,M)

              IF (N.EQ.6) PRECIP(I,N,M)=S(I,M)
              IF (N.EQ.7) PRECIP(I,N,M)=0.8*T(I,M)+0.2*S(I,M)
              IF (N.EQ.8) PRECIP(I,N,M)=T(I,M)
              IF (N.EQ.9) PRECIP(I,N,M)=0.09*B(I,M)+0.21*T(I,M)+0.7*S(I,M)
              IF (N.EQ.10) PRECIP(I,N,M)=0.16*B(I,M)+0.84*T(I,M)

              IF (N.EQ.11) PRECIP(I,N,M)=0.25*B(I,M)+0.75*S(I,M)
              IF (N.EQ.12) PRECIP(I,N,M)=0.50*B(I,M)+0.50*S(I,M)
              IF (N.EQ.13) PRECIP(I,N,M)=0.50*B(I,M)+0.50*T(I,M)
              IF (N.EQ.14) PRECIP(I,N,M)=B(I,M)
              IF (N.EQ.15) PRECIP(I,N,M)=0.04*R(I,M)+0.96*LI(I,M)

              IF (N.EQ.16) PRECIP(I,N,M)=0.05*R(I,M)+0.95*B(I,M)
              IF (N.EQ.17) PRECIP(I,N,M)=0.08*R(I,M)+0.33*LI(I,M)+0.59*G(I,M)
              IF (N.EQ.18) PRECIP(I,N,M)=0.10*R(I,M)+0.90*B(I,M)
              IF (N.EQ.19) PRECIP(I,N,M)=0.12*R(I,M)+0.88*G(I,M)
              IF (N.EQ.20) PRECIP(I,N,M)=0.18*R(I,M)+0.31*B(I,M)+
     &0.38*S(I,M)+0.13*LI(I,M)

              IF (N.EQ.21) PRECIP(I,N,M)=0.25*R(I,M)+0.75*B(I,M)
              IF (N.EQ.22) PRECIP(I,N,M)=0.40*R(I,M)+0.60*B(I,M)
              IF (N.EQ.23) PRECIP(I,N,M)=0.50*R(I,M)+0.30*B(I,M)+0.2*G(I,M)
              IF (N.EQ.24) PRECIP(I,N,M)=0.50*R(I,M)+0.50*B(I,M)
              IF (N.EQ.25) PRECIP(I,N,M)=0.71*R(I,M)+0.29*G(I,M)

              IF (N.EQ.26) PRECIP(I,N,M)=0.82*R(I,M)+0.18*G(I,M)
              IF (N.EQ.27) PRECIP(I,N,M)=0.86*R(I,M)+0.14*G(I,M)
              IF (N.EQ.28) PRECIP(I,N,M)=0.86*R(I,M)+0.14*B(I,M)
              IF (N.EQ.29) PRECIP(I,N,M)=0.90*R(I,M)+0.10*G(I,M)
              IF (N.EQ.30) PRECIP(I,N,M)=0.90*R(I,M)+0.10*LI(I,M)

              IF (N.EQ.31) PRECIP(I,N,M)=R(I,M)
              IF (N.EQ.32) PRECIP(I,N,M)=0.07*D(I,M)+0.07*R(I,M)+0.86*G(I,M)
              IF (N.EQ.33) PRECIP(I,N,M)=0.06*D(I,M)+0.40*G(I,M)
              IF (N.EQ.34) PRECIP(I,N,M)=0.97*D(I,M)+0.01*R(I,M)+0.02*G(I,M)
              IF (N.EQ.35) PRECIP(I,N,M)=D(I,M)

              IF (N.EQ.36) PRECIP(I,N,M)=0.50*S(I,M)+0.50*G(I,M)
              IF (N.EQ.37) PRECIP(I,N,M)=0.60*T(I,M)+0.10*S(I,M)+0.30*G(I,M)

 24           CONTINUE
 23           CONTINUE

      ICOUNTER=ICOUNTER+1
      CLOSE(MUNIT)
 21    CONTINUE
c------OKAY, ganesh Aug. 20, 01
C..                                                                    
C#      open(8,file='pfiles2',form='formatted',status='old')             
      open(9,file='WYTYPES',form='formatted',status='old')             
c-----OK 8/20/01, ganesh
	
c	get the file names and locations from the environmental variable  
	CALL GETENV('dilldat',dilldat)
	CALL GETENV('diuldat',diuldat)
	CALL GETENV('areaid',areaid)
	CALL GETENV('arealu.00',arealu_00)
	CALL GETENV('arealu.77',arealu_77)
	CALL GETENV('rwlu.00',rwlu_00)
	CALL GETENV('rwlu.77',rwlu_77)

      open(10,file='dicudat',form='formatted',status='old')            
      open(11,file=dilldat,form='formatted',status='old')            
      open(12,file=diuldat,form='formatted',status='old')            
      open(15,file=areaid,form='formatted',status='old')             
      open(16,file=arealu_00,form='formatted',status='old')          
      open(26,file=arealu_77,form='formatted',status='old')          
      open(17,file=rwlu_00,form='formatted',status='old')            
      open(27,file=rwlu_77,form='formatted',status='old')            
      open(20,file='dicu5.1',form='formatted',status='unknown')            

C...                                                                   
C  READ DATA COMMON TO ALL ISLANDS                                     
C...                                                                   
      READ(10,2)NOCRP,DATE,LEVEL,NOLIST                                
      READ(10,3)(STMEMO(I),I=1,3)                                      
      READ(10,3)(CROP(I),I=1,NOCRP)                                    
      READ(10,3)(ET(I),I=1,NOCRP+4)                                    
      READ(10,3)PCTLS                                                  
      READ(10,3)(PRMEMO(J),J=1,2)                                      
      READ(10,3)PINDX                                                  
      READ(10,3)URLINE                                                 
      READ(10,3)PLINE                                                  
      READ(10,4)SKIP                                                   
      READ(10,3)(PLLINE(L),L=1,4)                                      
      READ(10,3)PURF                                                   
      READ(10,3)(PPLINE(L),L=1,5)                                      
      READ(10,3)RWL21                                                  
      READ(10,3)(PRW(L),L=1,4)                                         
      READ(10,3)EV21                                                   
      READ(11,3)(RTSTL(I),I=1,NOCRP+2)                                 
      READ(11,3)(SML(I),I=1,2*NOCRP)                                   
      READ(11,3)(SEL(I),I=1,NOCRP+3)                                   
      READ(12,3)(RTSTU(I),I=1,NOCRP+2)                                 
      READ(12,3)(SMU(I),I=1,2*NOCRP)                                   
      READ(12,3)(SEU(I),I=1,NOCRP+3)                                   
      DO 100 K=1,37                                                    
C#      READ(8,16) PTYPE(K)                                              
  100 CONTINUE                                                         
C...                                                                   
C  READ ISLAND SPECIFIC AND WATER YEAR SPECIFIC DATA AND               
C  WRITE SINGLE OUTPUT FILE CONTAINING ALL ISLANDS AND WATER YEARS     
C...                                                                   
      WRITE(20,1)NSTUDY,NOH,NOYR,NOCRP                                 
C..                                                                    
      DO 900 K=1,NSTUDY                                                
      READ(15,5)ANAME,ALAND,ACODE,NPTYPE                               
      WRITE(20,8)K,NFYR,LYR,NOCRP,DATE,LEVEL,NOLIST        
      WRITE(20,9)ANAME,ALAND,ACODE,NPTYPE                  

      WRITE(20,3)(STMEMO(J),J=1,3)                                     
      WRITE(20,10)(K,CROP(I),I=1,NOCRP)                                
      WRITE(20,10)(K,ET(I),I=1,NOCRP+4)                                
      WRITE(20,10)K,PCTLS                                              
C...                                                                   
      IF(ALAND.EQ.1) THEN                                              
      WRITE(20,10)(K,RTSTL(I),I=1,NOCRP+2)                             
      WRITE(20,10)(K,SML(I),I=1,2*NOCRP)                               
      WRITE(20,10)(K,SEL(I),I=1,NOCRP+3)                               
      ELSEIF(ALAND.EQ.2) THEN                                          
      WRITE(20,10)(K,RTSTU(I),I=1,NOCRP+2)                             
      WRITE(20,10)(K,SMU(I),I=1,2*NOCRP)                               
      WRITE(20,10)(K,SEU(I),I=1,NOCRP+3)                               
      ELSE                                                             
      STOP 'LANDERR'                                                   
      ENDIF                                                            
C..                                                                    
      WRITE(20,10)(K,PRMEMO(J),J=1,2)                                  
      WRITE(20,10)K,PINDX                                              

C#----precip data is not read from the pfiles in the new program
C#      OPEN(18,FILE=PTYPE(NPTYPE),FORM='FORMATTED',STATUS='OLD')        
C#      READ(18,3)DUM                                                    
C#      READ(18,3)DUM                                                    
C#      READ(18,14)(PREC(I),I=1,NOYR)
C#      CLOSE(18)                                                        
C..                                                                    

      DO 901 J=1,NOYR                                                  
      READ(9,13) WYTYP,WY                                              
      WRITE(20,'(i3,"PR  ",i4,12(f6.2))') K,WY,(PRECIP(J,NPTYPE,M),M=1,12)
 901  continue
      REWIND 9                                                         

      WRITE(20,10)K,URLINE                                             
      WRITE(20,10)K,PLINE                                              
C...                                                                   
      READ(16,6)AREALUN                                                
      READ(26,6)AREALUC                                                
      DO 200 J=1,NOYR                                                  
      READ(9,13) WYTYP,WY                                              
      IF(WYTYP.EQ.'C') THEN                                            
      AREALU=AREALUC                                                   
      ELSE                                                             
      AREALU=AREALUN                                                   
      ENDIF                                                            
      WRITE(20,11)K,WYTYP,WY,AREALU                                    
  200 CONTINUE                                                         
      REWIND 9                                                         
C..                                                                    
      WRITE(20,4)SKIP                                                  
      WRITE(20,10)(K,PLLINE(L),L=1,4)                                  
      WRITE(20,10)K,PURF                                               
      WRITE(20,10)(K,PPLINE(J),J=1,5)                                  
C..                                                                    
      WRITE(20,10)K,RWL21                                              
      READ(17,7)RWLUN                                                  
      READ(27,7)RWLUC                                                  
      DO 300 I=1,NOYR                                                  
      READ(9,13) WYTYP,WY                                              
      IF(WYTYP.EQ.'C') THEN                                            
      RWLU=RWLUC                                                       
      ELSE                                                             
      RWLU=RWLUN                                                       
      ENDIF                                                            
      WRITE(20,12)K,WYTYP,WY,RWLU                                      
  300 CONTINUE                                                         
      REWIND 9                                                         
C..                                                                    
      WRITE(20,10)(K,PRW(L),L=1,4)                                     
      WRITE(20,10)K,EV21                                               
C...                                                                   
  900 CONTINUE                                                         
C..                                                                    
    1 FORMAT(I3,A1,2I4)                                                
    2 FORMAT(9X,I3,8X,A12,I5,1X,A1)                                    
    3 FORMAT(A79)                                                      
    4 FORMAT(A104)                                                     
    5 FORMAT(A50,1X,I2,1X,I4,7X,I2)                                    
    6 FORMAT(6X,A96)                                                   
    7 FORMAT(6X,A18)                                                   
    8 FORMAT(I3,2I5,I3,8X,A12,I5,1X,A1)                                      
    9 FORMAT(8X,A50,1X,I2,1X,I4,2X,I2)                                 
   10 FORMAT(I3,A79)                                                   
   11 FORMAT(I3,'LL',1X,A1,I4,A96)                                     
   12 FORMAT(I3,'RW',1X,A1,I4,A18)                                     
   13 FORMAT(A1,1X,I4)                                                 
   14 FORMAT(A78)                                                      
   15 FORMAT(I3,A78)                                                   
   16 FORMAT(A5)                                                       

C#----Program areax.f insertes here

C#      PROGRAM AREAX
C********************************************************************
C  PROGRAM TO DEVELOP FILE OF DELTA ISLAND AREAS (IN ACRES) FOR WATER
C  YEARS NFYR THRU LYR.  TOTAL ISLAND AREAS WILL BE ASSIGNED ACCORDING
C  TO WATER YEAR TYPES NON-CRITICAL AND CRITICAL.
C  NON-CRITICAL WATER YEAR AREAS ARE FROM FILE AREAID.00
C     - COMBINED YEAR LAND USE DATA (G. SATO, CENTRAL DISTRICT)
C  CRITICAL WATER YEAR AREAS ARE FROM FILE AREAID.77
C     - WATER YEAR 1977 LAND USE DATA (G. SATO, CENTRAL DISTRICT)
C  OUTPUT ORGANIZATION:  EACH ISLAND FOLLOWED BY ALL WATER YEARS
C                                                       - KG MAR 1988
C********************************************************************
C#      CHARACTER*1 WYTYP
C...
      LAREAN=0
      LAREAC=0
C...
	CALL GETENV('AREAID.00',AREAID_00)
	CALL GETENV('AREAID.77',AREAID_77)

      OPEN(31,FILE=AREAID_00,FORM='FORMATTED',STATUS='OLD')
      OPEN(32,FILE=AREAID_77,FORM='FORMATTED',STATUS='OLD')
C#----WYTPES has already been opened (Unit 9)
C#      OPEN(33,FILE='WYTYPES',FORM='FORMATTED',STATUS='OLD')
      OPEN(34,FILE='DICU5.2',FORM='FORMATTED',STATUS='UNKNOWN')
C...
      WRITE(34,500)
 500  FORMAT('  ISL  WY TYPE  AREA (ACRES)'/)
      DO 510 I=1,142
        READ(31,501) LAREAN
        READ(32,501) LAREAC
 501    FORMAT(T59,I6)
          DO 502 L=NFYR,LYR
          READ(9,503,END=505) WYTYP
 503      FORMAT(A1)
          IF(WYTYP.EQ.'C') THEN
            WRITE(34,504) I,L,WYTYP,LAREAC
          ELSE
            WRITE(34,504) I,L,WYTYP,LAREAN
          ENDIF
 504      FORMAT(2I5,2X,A1,2X,I10)
 502     CONTINUE
         REWIND 9
 505  CONTINUE
 510  CONTINUE

C...                                                                   
      STOP 'SUCCESS'                                                   
      END                                                              
