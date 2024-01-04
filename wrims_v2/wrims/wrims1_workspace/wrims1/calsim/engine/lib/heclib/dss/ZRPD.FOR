      SUBROUTINE ZRPD (IFLTAB, CPATH, NORD, NCURVE, IHORIZ,
     * C1UNIT, C1TYPE, C2UNIT, C2TYPE, VALUES, KVALS, NVALS,
     * CLABEL, KLABEL, LABEL, IUHEAD, KUHEAD, NUHEAD, ISTAT)
C
C     Retrieve Paired Data
C
C     Written by Bill Charley at HEC, 1989
C
C
      INTEGER IFLTAB(*), IUHEAD(*)
      CHARACTER CPATH*(*), CLABEL(*)*(*)
      CHARACTER C1UNIT*(*), C1TYPE*(*), C2UNIT*(*), C2TYPE*(*)
      CHARACTER CTEMP*12
      REAL VALUES(*)
      LOGICAL LFOUND, LABEL
C
      INCLUDE 'zdssmz.h'
C
      INCLUDE 'zdssbf.h'
C
C     This common block is for HARRIS only!!!
C
C
C
C     If debug is on, print out information
      IF (MLEVEL.GE.7) THEN
      WRITE (MUNIT,20) CPATH, KVALS, KLABEL, KUHEAD
 20   FORMAT (T5,'----- Enter ZRPD  -----',/,
     * T11,'Pathname: ',A,/,
     * T11,'KVALS:',I7,',  KLABEL:',I5,',  KUHEAD:',I6)
      ENDIF
C
C
C     Check that IFLTAB is valid (e.g., the DSS file is open)
      IF (IFLTAB(1).NE.6) CALL ZERROR (IFLTAB, 5, 'ZRPD  ', 0,
     * IFLTAB, ' ', 0, ' ',0)
C
C
C
C     Get the info block, and read the first portion of the header
      CALL ZREADX (IFLTAB, CPATH, IGBUFF, NGBUFF, NIHEAD,
     * ICHEAD, 0, NCHEAD, IUHEAD, KUHEAD, NUHEAD, VALUES,
     * KVALS, NVALS, 0, LFOUND)
      CALL ZINQIR (IFLTAB, 'STATUS', CTEMP, JSTAT)
      ISTAT = JSTAT
      IF (ISTAT.NE.0) GO TO 900
C
      IF (.NOT.LFOUND) THEN
      ISTAT = -1
      GO TO 900
      ENDIF
C
      CALL ZINQIR (IFLTAB, 'TYPE', CTEMP, JTYPE)
      IF (JTYPE.NE.200) THEN
      IF (MLEVEL.GT.1) WRITE (MUNIT,40) CPATH, JTYPE
 40   FORMAT (' ----DSS---ZRPD;  ERROR:  Record specified was',
     * ' not stored as Paired Data',/,' Record: ',A,/,
     * ' Type:',I5)
      ISTAT = 20
      GO TO 900
      ENDIF
C
C
      NUHEAD = MIN0 (NUHEAD,KUHEAD)
      NIHEAD = MIN0 (NIHEAD,NGBUFF)
      NORD = IGBUFF(1)
      NCURVE = IGBUFF(2)
      IHORIZ = IGBUFF(3)
C
C     Check that there was enough space in the data array to retrieve
C     all the data specified
      N = (NCURVE + 1) * NORD
      IF (N.GT.KVALS) ISTAT = 1
C
      CTEMP = ' '
      CALL HOLCHR (IGBUFF(4), 1, 8, CTEMP, 1)
      C1UNIT = CTEMP
      CALL HOLCHR (IGBUFF(6), 1, 8, CTEMP, 1)
      C1TYPE = CTEMP
      CALL HOLCHR (IGBUFF(8), 1, 8, CTEMP, 1)
      C2UNIT = CTEMP
      CALL HOLCHR (IGBUFF(10), 1, 8, CTEMP, 1)
      C2TYPE = CTEMP
C
C
      CTEMP = ' '
      LABEL = .FALSE.
      DO 100 I=1,NCURVE
      JLOC = (I * 3) + 9
      IF ((JLOC+2.GT.NIHEAD).OR.(I.GT.KLABEL)) THEN
      IF ((MLEVEL.GT.1).AND.(I.GT.1)) WRITE (MUNIT, 80) NCURVE, KLABEL
 80   FORMAT (' -----DSS---ZRPD;  CAUTION:  More Lables Stored than',
     * ' Buffer Size for Retrieving',/,' Number Stored:',I6,
     * ',  Space Available:',I6)
      GO TO 800
      ENDIF
      CALL HOLCHR (IGBUFF(JLOC), 1, 12, CTEMP, 1)
      CLABEL(I) = CTEMP
      IF (CTEMP(1:8).NE.'        ') LABEL = .TRUE.
 100  CONTINUE
C
C
 800  CONTINUE
      IF (MLEVEL.GE.7) WRITE (MUNIT,820) NVALS, LFOUND, LABEL, ISTAT
 820  FORMAT(T10,'----- Exit ZRPD, Number of data values retrieved:',
     * I7,/,T20,'Found:',L2,',  Labels:',L2,',  Status:',I4)
      RETURN
C
 900  CONTINUE
      NVALS = 0
      NUHEAD = 0
      GO TO 800
C
      END
