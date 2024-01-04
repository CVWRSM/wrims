      SUBROUTINE ZTSRANGE (IFLTAB, CPATH, ISEARCH, CFIRSTP, CLASTP,
     *                     NFOUND)
C
C     Given a time series pathname, this routine will return the
C     pathname of the first and last data set in that series
C     (varying D (date) part).
C
C     The input pathname may or may not have a valid D (date part)
C
C     This routine can do a quick search or an exhaustive search.
C
C     The quick search will find a valid record and search forwards
C     and backwards until up to ISEARCH records are not found
C     (for example, for a monthly block record, this will check
C     records forwards until a check fails.  At that point the
C     date will be incremented for up to ISEARCH values (e.g., 10)
C     checking for the records existence.  If no more are found, the
C     last one that was found is returned.
C     For most normal database files, this is fast and satisfactory.
C
C     The exhaustive search searches every record in the database,
C     then returns the first and last in the series.  This method
C     will always be accurate, but may take considerable time for
C     larger database files.
C
C     Typically data blocks of one day (10 minute interval or less)
C     use the exhaustive search, while larger block use the quick
C     search.
C
C
C     IFLTAB - (input) Array from the zopen call.
C     CPATH  - (input) Pathname to start from.  It does not
C              have to have a valid (or known) D part.
C     ISEARCH - (input)  The number of records to check beyond
C              the last one found (typically 10), OR set to
C              zero (0) to do an exhaustive search.
C     CFIRSTP - (output)  The first pathname in the series.
C     CLASTP  - (output)  The last pathname in the series.
C     NFOUND  - (output)  The number of valid (checked) records found.
C              (this is set to -1 on an error)
C
      INTEGER IFLTAB(*)
      CHARACTER CPATH*(*), CFIRSTP*(*), CLASTP*(*)
      INTEGER ISEARCH, NFOUND
C
      LOGICAL LFOUND, LOPEN
      CHARACTER CPATH1*392, CPATH2*392, CPATH3*392
      INTEGER IBPART(6), IEPART(6), ILPART(6)
C
C     Standard quick search
      IF (ISEARCH.GT.0) THEN
C        Does the record exist?
         CALL CHRLNB (CPATH, NPATH)
         CALL ZCHECK (IFLTAB, CPATH, NPATH, NHEAD, NDATA, LFOUND)
C
C        If not, search for a valid record
         IF (.NOT.LFOUND) THEN
            CALL ZTSSRCH (IFLTAB, CPATH, 0, CPATH1, NFOUND)
            IF (NFOUND.LE.0) GO TO 900
         ELSE
            CPATH1 = CPATH
         ENDIF
C
C        Now loop backwards in time
         CPATH2 = CPATH1
         CFIRSTP = CPATH1
         NFOUND = 1
C
         JSEARCH = 0
 40      CONTINUE
            CALL ZNEXTTS (IFLTAB, CPATH2, CPATH3, .FALSE., ISTAT)
            IF (ISTAT.NE.0) GO TO 900
            CALL ZCHECK (IFLTAB, CPATH3, NPATH, NHEAD, NDATA, LFOUND)
            IF (LFOUND) THEN
               NFOUND = NFOUND + 1
               JSEARCH = 0
               CFIRSTP = CPATH3
            ELSE
               JSEARCH = JSEARCH + 1
               IF (JSEARCH.GE.ISEARCH) GO TO 100
            ENDIF
            CPATH2 = CPATH3
            GO TO 40
C
 100     CONTINUE
C        Search forwards in time
         JSEARCH = 0
         CPATH2 = CPATH1
         CLASTP = CPATH1
C
 140     CONTINUE
            CALL ZNEXTTS (IFLTAB, CPATH2, CPATH3, .TRUE., ISTAT)
            IF (ISTAT.NE.0) GO TO 900
            CALL ZCHECK (IFLTAB, CPATH3, NPATH, NHEAD, NDATA, LFOUND)
            IF (LFOUND) THEN
               NFOUND = NFOUND + 1
               JSEARCH = 0
               CLASTP = CPATH3
            ELSE
               JSEARCH = JSEARCH + 1
               IF (JSEARCH.GE.ISEARCH) GO TO 800
            ENDIF
            CPATH2 = CPATH3
            GO TO 140
C
C           can not ever get here!
C
      ELSE
C        Exhaustive search
C        Try to find a scrach unit to open
         JUNIT = 80
         DO 160 I=1,30
            INQUIRE (UNIT=JUNIT, OPENED=LOPEN)
            IF (.NOT.LOPEN) GO TO 180
            JUNIT = JUNIT + 1
 160     CONTINUE
C        Shouldn't get here
         GO TO 920
C
 180     CONTINUE
         CALL TEMPNAME (CPATH3, JUNIT)
         CALL CHRLNB (CPATH3, N)
         OPEN (UNIT=JUNIT, FILE=CPATH3(1:N), ERR=920)
         CALL ZTSSRCH (IFLTAB, CPATH, JUNIT, CPATH1, NFOUND)
         IF (NFOUND.LE.0) THEN
            CLOSE (UNIT=JUNIT, STATUS='DELETE')
            GO TO 900
         ENDIF
C
         REWIND (UNIT=JUNIT)
         DO 240 I=1,NFOUND
            READ (JUNIT,220,END=300) CPATH1
 220        FORMAT (A)
            IF (I.EQ.1) THEN
               CPATH2 = CPATH1
C              Get the location of the "D" part
               CALL ZUPATH (CPATH1, IBPART, IEPART, ILPART, ISTAT)
               IF (ISTAT.NE.0) GO TO 900
               I4TH = IBPART(4)
               I5TH = IEPART(4)
            ENDIF
            CALL DATJUL (CPATH1(I4TH:I5TH), JULIAN, ISTAT)
            IF (ISTAT.NE.0) GO TO 900
            IF (I.EQ.1) THEN
               JULS = JULIAN
               JULE = JULIAN
            ELSE
               IF (JULIAN.LT.JULS) JULS = JULIAN
               IF (JULIAN.GT.JULE) JULE = JULIAN
            ENDIF
 240     CONTINUE
C
 300     CONTINUE
         CLOSE (UNIT=JUNIT, STATUS='DELETE')
         CFIRSTP = CPATH2
         CLASTP  = CPATH2
         CALL JULDAT (JULS, 104, CFIRSTP(I4TH:I5TH), N)
         CALL JULDAT (JULE, 104, CLASTP(I4TH:I5TH), N)
      ENDIF
C
C
 800  CONTINUE
      RETURN
C
 900  CONTINUE
      NFOUND = -1
      GO TO 800
C
 920  CONTINUE
      NFOUND = -2
      GO TO 800
C
      END
