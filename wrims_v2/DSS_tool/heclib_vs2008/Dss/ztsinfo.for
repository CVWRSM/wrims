      SUBROUTINE ZTSINFO (IFLTAB, CPATH, JULS, ISTIME, JULE, IETIME,
     * CUNITS, CTYPE, LQUAL, LDOUBLE, LFOUND)
C
C     Retrieve information about a time series record
C     (both regular interval and irregular interval).
C     The pathname must be valid (with a correct D part).
C
C     Input:
C        IFLTAB:  Working DSS array used in ZOPEN call.
C        CPATH:   Pathname of the info to be retrieved.
C
C     Output:
C        JULS:    The julian date of the first valid (non-missing)
C                 data value in the record.
C        ISTIME:  The time in minutes past midnight of this value
C        JULE:    The julian date of the last valid (non-missing)
C                 data value in the record.
C        IETIME:  The time in minutes past midnight of this value.
C        CUNITS:  Character string returning the units of the data.
C                 CUNITS must be declared CHARACTER*8
C        CTYPE:   Character string returning the type of the data
C                 (e.g., PER-AVER).  CTYPE must be declared CHARACTER*8
C        LQUAL:   Logical flag indicating whether quality values are
C                 store with the data set.
C        LDOUBLE: Logical flag indicating whether the data is stored
C                 as double precision.
C        LFOUND:  Logical flag indicating if the record exists.
C                 If this is returned FALSE, all other output
C                 is undefined.
C
C     Written by Bill Charley
C
C
C     DIMENSION STATEMENTS
C
C     Argument Dimensions
      CHARACTER CPATH*(*), CTYPE*(*), CUNITS*(*)
      INTEGER IFLTAB(*)
      LOGICAL LQUAL, LDOUBLE, LFOUND
C
C     Local Dimensions
      INTEGER IBPART(6), IEPART(6), ILPART(6)
      CHARACTER CDTYPE*3, CTEMP*8
      PARAMETER (KIHEAD=10)
      INTEGER IIHEAD(KIHEAD)
C
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdssiz.h'
C
      INCLUDE 'zdssmz.h'
C
      INCLUDE 'zdssts.h'
C
C
C
      IFUNIT = IFLTAB(KUNIT)
      CTEMP = ' '
C
C     If a debug level is on, print out information
      IF (MLEVEL.GE.7) THEN
      WRITE (MUNIT,20) IFUNIT
 20   FORMAT (T10,'----- Entering ZTSINFO for unit',I5,' -----')
      ENDIF
C
C     Check that IFLTAB is valid (e.g., the DSS file is open)
      IF (IFLTAB(1).NE.6) CALL ZERROR (IFLTAB, 5, 'ZTSINFO', 0,
     * IFLTAB, ' ', 0, ' ',0)
C
C
C
C     Check the pathname
      CALL CHRLNB (CPATH, NPATH)
      IF ((NPATH.GT.392).OR.(NPATH.LE.1)) GO TO 900
      IF (CPATH(1:1).NE.'/') GO TO 900
C
      CALL ZDTYPE (IFLTAB, CPATH, NDATA, LFOUND, CDTYPE, IDTYPE)
      IF (.NOT.LFOUND) GO TO 800
C
      IF (MLEVEL.GE.7) WRITE (MUNIT, 40) CPATH(1:NPATH)
 40   FORMAT (' Pathname: ',A)
C
C
C
C     Regular Interval time series
      IF ((IDTYPE.EQ.100).OR.(IDTYPE.EQ.105)) THEN
C
      IF (IDTYPE.EQ.105) THEN
         LDOUBLE = .TRUE.
      ELSE
         LDOUBLE = .FALSE.
      ENDIF
C
C        Determine the time, interval in minutes
         CALL ZUPATH (CPATH(1:NPATH), IBPART, IEPART, ILPART, IERR)
         IF (IERR.NE.0) GO TO 900
         IF (ILPART(5).LT.4) GO TO 960
         I = 1
         CALL ZGINTL (INTL, CPATH(IBPART(5):IEPART(5)), NUMDAT, I)
         IF (I.LT.0) GO TO 910
C
C        Compute the starting date and time of the first data position
         IF (ILPART(4).LT.5) GO TO 900
         CALL DATJUL (CPATH(IBPART(4):IEPART(4)), JUL, IERR)
         IF (IERR.NE.0) GO TO 900
         ITIME = 0
         I = INCTIM (INTL, 0, 1, JUL, ITIME, JUL, ITIME)
C
C
C        Read the data directly into BUFF.  Because we are only
C        reading a single record, this array can be used.  If
C        compression is used, we may be copying into itself
C        (e.g., BUFF(1) = BUFF(1)), which is ok.
         NVALS = KSBUFF
         ISTAT = -1
         CALL ZRRTS (IFLTAB, CPATH, CTEMP, CTEMP, NVALS, BUFF,
     *               CUNITS, CTYPE, IOFSET, ISTAT)
C
C        Determine if the data quality flag is set
         IF (INFO(NPPWRD+KIQUAL).EQ.0) THEN
            LQUAL = .FALSE.
         ELSE
            LQUAL = .TRUE.
         ENDIF
C
C        Determine the date and time of the first and last
C        valid (non-missing) data set.
C
         DO 100 I=1,NVALS
            IF ((BUFF(I).NE.-901.).AND.(BUFF(I).NE.-902.)) THEN
               IFIRST = I
               GO TO 120
            ENDIF
 100     CONTINUE
C
C        All missing!
         LFOUND = .FALSE.
         GO TO 800
C
 120     CONTINUE
          DO 140 I=NVALS,1,-1
            IF ((BUFF(I).NE.-901.).AND.(BUFF(I).NE.-902.)) THEN
               ILAST = I
               GO TO 160
            ENDIF
 140     CONTINUE
C        (cannot get here)
C
 160     CONTINUE
C        Compute dates and times
         I = INCTIM (INTL, 0, (IFIRST-1), JUL, ITIME, JULS, ISTIME)
         I = INCTIM (INTL, 0, (ILAST -1), JUL, ITIME, JULE, IETIME)
C        Correct for any offset
         IF (IOFSET.GT.0) THEN
            CALL ZOFSET (JULS, ISTIME, INTL, 2, IOFSET)
            CALL ZOFSET (JULE, IETIME, INTL, 2, IOFSET)
         ENDIF
C
C
      ELSE IF ((IDTYPE.EQ.110).OR.(IDTYPE.EQ.115)) THEN
C
      IF (IDTYPE.EQ.115) THEN
         LDOUBLE = .TRUE.
      ELSE
         LDOUBLE = .FALSE.
      ENDIF
C
C        Read the internal header, and only one value
C        (which is the time of the first value)
         CALL ZREADX (IFLTAB, CPATH, IIHEAD, KIHEAD, NIHEAD,
     *   IDUM, 0, N, IDUM, 0, N, ILBUFF, 1, NB, 2, LFOUND)
         IF (.NOT.LFOUND) GO TO 800
C
C        Determine if the data quality flag is set
         IF (INFO(NPPWRD+KIQUAL).EQ.0) THEN
            LQUAL = .FALSE.
         ELSE
            LQUAL = .TRUE.
         ENDIF
C
C        Get the date / time of the first data value
         JUL = IIHEAD(3)
         CALL DATCLL (JUL, ILBUFF(1), JULS, ISTIME)
C        Get the units and type
         CALL HOLCHR (IIHEAD(6),  1, 8, CTEMP, 1)
         CUNITS = CTEMP
         CALL HOLCHR (IIHEAD(8), 1, 8, CTEMP,  1)
         CTYPE = CTEMP
C
C        Now read the time of the last value
         IMULT = 2
         IF (LDOUBLE) IMULT = IMULT + 1
         IF (LQUAL) IMULT = IMULT + 1
         IADD = INFO(NPPWRD+KIADAT) + (IIHEAD(2) * IMULT) - IMULT
         CALL ZGTREC (IFLTAB, ILBUFF, 1, IADD, .FALSE.)
         CALL DATCLL (JUL, ILBUFF(1), JULE, IETIME)
C
      ENDIF
C
C
 800  CONTINUE
      IF (.NOT.LFOUND) THEN
         CUNITS = ' '
         CTYPE = ' '
      ENDIF
C
      RETURN
C
C
C     --- ERROR STATEMENTS ---
C
 900  CONTINUE
      IF (MLEVEL.GE.1) WRITE (MUNIT,901) NPATH, CPATH(1:NPATH)
 901  FORMAT (/,' *****DSS*** ERROR  - ILLEGAL PATHNAME',
     * ' OR PATHAME LENGTH',/,' Length: ',I5,/,' Pathname: ',A,/)
      GO TO 990
C
 910  CONTINUE
      IF (MLEVEL.GE.1) WRITE (MUNIT,911) CPATH(IBPART(5):IEPART(5)),
     * INTL, CPATH
 911  FORMAT (/,' *****DSS*** ERROR  - NON-STANDARD TIME',
     * ' INTERVAL',/,' Interval: ',A,2X,I8,/,' Pathname: ',A,/)
      GO TO 990
C
C
 960  CONTINUE
      IF (MLEVEL.GE.1) WRITE (MUNIT,961) CPATH(1:NPATH)
 961  FORMAT (/,' *****DSS*** ERROR  - NO TIME',
     * ' INTERVAL',/,' Pathname: ',A,/)
      GO TO 990
C
 990  CONTINUE
      LFOUND = .FALSE.
C
      RETURN
      END
