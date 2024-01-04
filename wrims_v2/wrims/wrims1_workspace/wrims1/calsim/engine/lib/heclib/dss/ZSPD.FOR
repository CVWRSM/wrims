      SUBROUTINE ZSPD (IFLTAB, CPATH, NORD, NCURVE, IHORIZ,
     * C1UNIT, C1TYPE, C2UNIT, C2TYPE, VALUES, CLABEL, LABEL,
     * IUHEAD, NUHEAD, IPLAN, ISTAT)
C
C     Store Paired DATA
C
C     Written by Bill Charley
C
C
      INTEGER IFLTAB(*), IUHEAD(*)
      CHARACTER CPATH*(*), CLABEL(*)*(*)
      CHARACTER C1UNIT*(*), C1TYPE*(*), C2UNIT*(*), C2TYPE*(*)
      CHARACTER CTEMP*12
      REAL VALUES(*)
      LOGICAL LABEL, LFOUND
C
      INCLUDE 'zdssmz.h'
C
      INCLUDE 'zdssbf.h'
C
C     This common block is for HARRIS only!!!
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
      IGBUFF(1) = NORD
      IGBUFF(2) = NCURVE
      IGBUFF(3) = 1
      IF (IHORIZ.EQ.2) IGBUFF(3) = 2
C
      CTEMP = C1UNIT
      CALL CHRHOL (CTEMP, 1, 8, IGBUFF(4), 1)
      CTEMP = C1TYPE
      CALL CHRHOL (CTEMP, 1, 8, IGBUFF(6), 1)
      CTEMP = C2UNIT
      CALL CHRHOL (CTEMP, 1, 8, IGBUFF(8), 1)
      CTEMP = C2TYPE
      CALL CHRHOL (CTEMP, 1, 8, IGBUFF(10), 1)
C
      CTEMP = ' '
      DO 100 I=1,NCURVE
      IF (LABEL) THEN
      CTEMP = CLABEL(I)
      ENDIF
      CALL CHRHOL (CTEMP, 1, 12, IGBUFF((I*3)+9), 1)
 100  CONTINUE
C
      NIHEAD = (NCURVE*3) + 11
      NVALS = (NORD * (NCURVE + 1))
      JTYPE = 200
C
      CALL ZWRITX (IFLTAB, CPATH, NPATH, IGBUFF, NIHEAD,
     * ICHEAD, 0, IUHEAD, NUHEAD, VALUES, NVALS, JTYPE,
     * IPLAN, ISTAT, LFOUND)
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
