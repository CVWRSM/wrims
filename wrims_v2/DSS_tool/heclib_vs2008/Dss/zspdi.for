      SUBROUTINE ZSPDI (IFLTAB, CPATH, NORD, NCURVE, IHORIZ,
     * C1UNIT, C1TYPE, C2UNIT, C2TYPE, SVALUES, DVALUES,
     * LDOUBLE, CLABEL, LABEL, IUHEAD, NUHEAD, IPLAN, ISTAT)
C
C     Internal Store Paired DATA
C
C     Written by Bill Charley
C
C
      INTEGER IFLTAB(*), IUHEAD(*)
      CHARACTER CPATH*(*), CLABEL(*)*(*)
      CHARACTER C1UNIT*(*), C1TYPE*(*), C2UNIT*(*), C2TYPE*(*)
      CHARACTER CTEMP*12
      REAL SVALUES(*)
      DOUBLE PRECISION DVALUES(*)
      LOGICAL LABEL, LFOUND, LDOUBLE
	INTEGER MAXLABEL
C
      INCLUDE 'zdssmz.h'
C
      INCLUDE 'zdssbf.h'
C
      INCLUDE 'zdssts.h'
C
      INCLUDE 'zdsscc.h'
C
      INCLUDE 'zdsskz.h'
C
C
      CALL CHRLNB (CPATH, NPATH)
C
C     If a debug level is on, print out information
      IF (MLEVEL.GE.7) THEN
      WRITE (MUNIT,20) NORD, NCURVE, IHORIZ, LABEL, IPLAN, NUHEAD,
     * CPATH(1:NPATH)
 20   FORMAT (T5,'----- Enter ZSPD  -----',/,
     * T11,'NORD:',I6,',  NCURVE:',I4,',  IHORIZ:',I4,/,
     * T11,'LABEL: ',L1,',  IPLAN:',I3,',  NUHEAD:',I5,/,
     * T11,'Pathname: ',A)
      ENDIF
C
C
C     Check that IFLTAB is valid (e.g., the DSS file is open)
      IF (IFLTAB(1).NE.6) CALL ZERROR (IFLTAB, 5, 'ZSPD  ',
     * 0, IFLTAB, ' ', 0, ' ',0)
C
C
C     Error checking
      IF (NORD.LT.1) GO TO 900
      IF (NCURVE.LT.1) GO TO 910
      IF (NCURVE.GT.50) GO TO 920
C
      ILBUFF(1) = NORD
      ILBUFF(2) = NCURVE
      ILBUFF(3) = 1
      IF (IHORIZ.EQ.2) ILBUFF(3) = 2
C
      CTEMP = C1UNIT
      CALL CHRHOL (CTEMP, 1, 8, ILBUFF(4), 1)
      CTEMP = C1TYPE
      CALL CHRHOL (CTEMP, 1, 8, ILBUFF(6), 1)
      CTEMP = C2UNIT
      CALL CHRHOL (CTEMP, 1, 8, ILBUFF(8), 1)
      CTEMP = C2TYPE
      CALL CHRHOL (CTEMP, 1, 8, ILBUFF(10), 1)
C
      CTEMP = ' '
	MAXLABEL = 0
      DO 100 I=1,NCURVE
      IF (LABEL) THEN
      CTEMP = CLABEL(I)
	CALL CHRLNB(CLABEL(I), NLAB)
	IF (NLAB.GT.MAXLABEL) MAXLABEL = NLAB
      ENDIF
      CALL CHRHOL (CTEMP, 1, 12, ILBUFF((I*3)+9), 1)
 100  CONTINUE
C     
C
      NIHEAD = (NCURVE*3) + 11
      NVALS = (NORD * (NCURVE + 1))
C
C     Do we have extended labels?  If so, store their size in words
C     and the full labels following the standard labels (for compatibility)
C
      IF (MAXLABEL.GT.12) THEN
	   NWDS = ((MAXLABEL - 1) / 4) + 1
	   NIHEAD = NIHEAD + 1
	   ILBUFF(NIHEAD) = NWDS
	   NIHEAD = NIHEAD + 1
	   DO 120 I=1,NCURVE	   
	      CPPATH = CLABEL(I)
	      CALL CH2HOL (CPPATH, ILBUFF(NIHEAD), NWDS)
	      NIHEAD = NIHEAD + NWDS
 120  CONTINUE
	ENDIF
C
      IF (LDOUBLE) THEN
         JTYPE = 205
         N = NVALS * 2
C        Swap words on unix to keep compatitable with PC
         IF (IFLTAB(KDSWAP).NE.0) CALL ZDSWAP(DVALUES, N)
         CALL ZWRITX (IFLTAB, CPATH, NPATH, ILBUFF, NIHEAD,
     *   ICHEAD, 0, IUHEAD, NUHEAD, DVALUES, N, JTYPE,
     *   IPLAN, ISTAT, LFOUND)
C        Swap back so we don't mess up the user's data
         IF (IFLTAB(KDSWAP).NE.0) CALL ZDSWAP(DVALUES, N)
      ELSE
         JTYPE = 200
         CALL ZWRITX (IFLTAB, CPATH, NPATH, ILBUFF, NIHEAD,
     *   ICHEAD, 0, IUHEAD, NUHEAD, SVALUES, NVALS, JTYPE,
     *   IPLAN, ISTAT, LFOUND)
      ENDIF
C
 
C
C
 800  CONTINUE
      IF (MLEVEL.GE.7) WRITE (MUNIT,820) NVALS, ISTAT
 820  FORMAT(T5,'----- Exit ZSPD, Number of data values ',
     * 'stored:',I7,',  Status:',I4,/)
C
      RETURN
C
C
 900  CONTINUE
      IF (MLEVEL.GE.1) WRITE (MUNIT, 901) NORD, CPATH(1:NPATH)
 901  FORMAT (/,' *** ERROR:  ZSPD;  The Number of Ordinates is Less',
     * ' than One ***',/,' Number Supplied:',I6,/,' Pathname: ',A,/)
      ISTAT = -4
      GO TO 800
C
 910  CONTINUE
      IF (MLEVEL.GE.1) WRITE (MUNIT, 911) NCURVE, CPATH(1:NPATH)
 911  FORMAT (/,' *** ERROR:  ZSPD;  The Number of Curves is Less',
     * ' than One ***',/,' Number Supplied:',I6,/,' Pathname: ',A,/)
      ISTAT = -5
      GO TO 800
C
 920  CONTINUE
      IF (MLEVEL.GE.1) WRITE (MUNIT, 921) NCURVE, CPATH(1:NPATH)
 921  FORMAT (/,' *** ERROR:  ZSPD;  The Number of Curves is Greater',
     * ' than 50 ***',/,' Number Supplied:',I6,'  (Up to 50 curves may',
     * ' be stored in one record)',/,' Pathname: ',A)
      ISTAT = -5
      GO TO 800
C
      END
