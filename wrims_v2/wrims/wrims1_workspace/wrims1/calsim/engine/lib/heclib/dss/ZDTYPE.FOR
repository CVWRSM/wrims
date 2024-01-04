      SUBROUTINE ZDTYPE (IFLTAB, CPATH, NDATA, LFOUND, CDTYPE, IDTYPE)
C
C     Determine the data type of a record and whether it exists
C
C     Written by Bill Charley at HEC, 1990.
C
      INTEGER IFLTAB(*)
      CHARACTER CPATH*(*), CDTYPE*(*)
      LOGICAL LFOUND
      INTEGER IBPART(6), IEPART(6), ILPART(6)
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdssmz.h'
C
C
      IF (MLEVEL.GE.11) WRITE ( MUNIT, 20) IFLTAB(KUNIT), CPATH
 20   FORMAT (T6,'-----DSS---Debug: Enter ZDTYPE',/,T10,
     * 'UNIT =',I4,'  PATH: ',A)
C
C     Check that IFLTAB is valid (e.g., the DSS file is open)
      IF (IFLTAB(1).NE.6) CALL ZERROR (IFLTAB, 5, 'ZDTYPE', 0,
     * IFLTAB, ' ', 0, ' ',0)
C
C
C     Check if this record exists
C
      CALL CHRLNB (CPATH, NPATH)
      CALL ZCHECK (IFLTAB, CPATH, NPATH, NHEAD, NDATA, LFOUND)
C
      IF (LFOUND) THEN
C     Get data type
      IDTYPE = IFLTAB(KDTYPE)
C
      ELSE
C
      NDATA = 0
      IDTYPE = 0
C     See if we can determine if this is a time series record
      CALL ZUPATH (CPATH, IBPART, IEPART, ILPART, ISTAT)
      IF ((ISTAT.EQ.0).AND.(ILPART(5).GE.4)) THEN
      IST = 1
      CALL ZGINTL (INTL, CPATH(IBPART(5):IEPART(5)), N, IST)
      IF (IST.EQ.0) THEN
      IDTYPE = 100
      ELSE IF (IST.EQ.1) THEN
      IDTYPE = 110
      ENDIF
C     CHECK FOR TIME SERIES PATTERN TYPE
      IF (CPATH(IBPART(4):IBPART(4)+2).EQ.'TS-') THEN
         IDTYPE = IDTYPE + 1
      ENDIF
      ENDIF
C
      ENDIF
C
C
      IF (IDTYPE.EQ.100) THEN
      CDTYPE = 'RTS'
      ELSE IF (IDTYPE.EQ.101) THEN
      CDTYPE = 'RTP'
      ELSE IF (IDTYPE.EQ.110) THEN
      CDTYPE = 'ITS'
      ELSE IF (IDTYPE.EQ.111) THEN
      CDTYPE = 'ITP'
      ELSE IF (IDTYPE.EQ.200) THEN
      CDTYPE = 'PD '
      ELSE IF (IDTYPE.EQ.300) THEN
      CDTYPE = 'TXT'
      ELSE
      CDTYPE = 'UND'
      ENDIF
C
C
      IF (MLEVEL.GE.11) WRITE (MUNIT, 820) LFOUND, IDTYPE, CDTYPE
 820  FORMAT (T6,'-----DSS--Debug: EXIT ZDTYPE;  Found: ',L1,
     * ';  Type:',I4,2X,A)
C
      RETURN
C
      END
