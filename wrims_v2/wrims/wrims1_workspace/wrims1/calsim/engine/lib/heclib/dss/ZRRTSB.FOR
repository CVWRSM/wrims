      SUBROUTINE ZRRTSB (IFLTAB, JULS, ISTIME, INTL, JULSD, NSTART,
     * NLDATA, NADATA, NVALS, VALUES, JQUAL, LQBLOK, LQUAL, JCOMP,
     * LFOUND, ISTAT)
C
C     Get Regular Interval Time-Series Data Block
C     Accomplishes essentially a ZREAD
C
C     This is an internal subroutine called by ZRRTSX
C
C     Written by Bill Charley at HEC, 1989.
C
C
C     Passed arguments
      INTEGER IFLTAB(*), JQUAL(*)
      REAL VALUES(*)
      LOGICAL LFOUND, LQUAL, LQBLOK
C
      CHARACTER CDATE1*9, CDATE2*9
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdssiz.h'
C
      INCLUDE 'zdssmz.h'
C
      INCLUDE 'zdssts.h'
C
      INCLUDE 'zdssdc.h'
C
C
C
      IF (MLEVEL.GE.9) WRITE (MUNIT,20) IFLTAB(KUNIT)
 20   FORMAT (T8,'----DSS---Debug:  Enter ZRRTSB;  Unit:',I4)
C
C
C
C     Obtain the number of periods from the start of the stored
C     record to the current data position
      NPOS = NOPERS (INTL, 0, JULSD, 0, JULS, ISTIME) + NSTART - 1
      IF (NPOS.LT.1) GO TO 900
C
C     Find number of data to read for this record (NREAD)
C
C     End of stored record controlling
      NREAD = NLDATA - NPOS + 1
C     End of data desired controlling
      J = NVALS - NSTART + 1
      IF (J.LT.NREAD) NREAD = J
      ILIM = NSTART + NREAD - 1
C
      IF (MLEVEL.GE.9) THEN
      CALL JULDAT (JULS, 114, CDATE1, N)
      CALL JULDAT (JULSD, 114, CDATE2, N)
      WRITE (MUNIT,40) NSTART, NVALS, JULS, ISTIME, NLDATA, JULSD,
     * CDATE1, CDATE2, LQBLOK, LQUAL
 40   FORMAT (T10,'NSTART:',I5,T25,'NVALS:',I5,T40,'JULS:',I8,
     * T55,'ISTIME:',I6,/,T10,'NLDATA:',I5,T25,'JULSD:',I8,
     */,T10,'JULS: ',A,T30,'JULSD: ',A,/,
     * T10, 'Quality Read: ',L1,',  Quality Requested:',L2)
C
      WRITE (MUNIT,45) NPOS, NLDATA, NREAD, ILIM
 45   FORMAT (T10,'---ZRRTSB Calculations:  ',
     *'NPOS:',I5,'  NDATA:',I5,'  NREAD:',I5,'  ILIM:',I5)
      ENDIF
C
C
      IF (LFOUND) THEN
C
C     If the compression flag was set, uncompress the data
C
      IF (INFO(NPPWRD+KICOMP).GT.0) THEN
      JCOMP = INFO(NPPWRD+KICOMP)
C     Get the compression header
      NDCH = INFO(NPPWRD+KINCHE)
      NDCH = MIN0 (NDCH, KDCH)
      CALL ZGTREC (IFLTAB, IDCH, NDCH, INFO(NPPWRD+KIACHE), .FALSE.)
      IF (IFLTAB(KSTAT).NE.0) GO TO 800
C
C     Don't uncompress the data if we do not have enough room
      IF (INFO(NPPWRD+KILNDA).GT.KSBUFF) THEN
C        Try and use the integer buffer, if we have room!
         IF (NADATA.GT.NIBUFF) GO TO 910
         CALL ZGTREC (IFLTAB, INTBUF, NADATA, INFO(NPPWRD+KIADAT),
     *    .FALSE.)
         IF (IFLTAB(KSTAT).NE.0) GO TO 800
C        Uncompress the data, with new data in buffer
         NELMS = -1
         CALL DUREAL(BUFF, KLBUFF, 1, NELMS, INTBUF, NADATA, NIBUFF,
     *    IDCH, NDCH, IST)
         IF (IST.NE.0) GO TO 920
      ELSE
C
C        Get the data block
         CALL ZGTREC (IFLTAB, SBUFF2, NADATA, INFO(NPPWRD+KIADAT),
     *    .FALSE.)
         IF (IFLTAB(KSTAT).NE.0) GO TO 800
C        Uncompress the data, with new data in buffer
         NELMS = -1
         CALL DUREAL(SBUFF1, KSBUFF, 1, NELMS, SBUFF2, NADATA, KSBUFF,
     *    IDCH, NDCH, IST)
         IF (IST.NE.0) GO TO 910
C
      ENDIF
C
      M = NPOS - 1
      DO 60 I=NSTART, ILIM
      M = M + 1
      VALUES(I) = BUFF(M)
 60   CONTINUE
C
      ELSE
C
C     No data compression...
C     If we don't have data quality flags, read data directly
C
      IF (.NOT.LQBLOK) THEN
C     Compute starting address of data
      IADD = INFO(NPPWRD+KIADAT) + NPOS - 1
      CALL ZGTREC (IFLTAB, VALUES(NSTART), NREAD, IADD, .FALSE.)
      IF (IFLTAB(KSTAT).NE.0) GO TO 800
C
      IF (LQUAL) THEN
      DO 80 I=NSTART,ILIM
      JQUAL(I) = 0
 80   CONTINUE
      ENDIF
C
      ELSE
C
C     We have data quality flags
C     Compute starting address of data
      IADD = INFO(NPPWRD+KIADAT) + ((NPOS-1) * 2)
      IREAD = NREAD * 2
      CALL ZGTREC (IFLTAB, BUFF, IREAD, IADD, .FALSE.)
      IF (IFLTAB(KSTAT).NE.0) GO TO 800
C
      DO 100 I=1,NREAD
      J = NSTART + I - 1
      K = ((I - 1) * 2) + 1
      VALUES(J) = BUFF(K)
      IF (LQUAL) THEN
      K = K + 1
      JQUAL(J) = ILBUFF(K)
      ENDIF
 100  CONTINUE
C
      ENDIF
C
      ENDIF
C
      ELSE
C     Record not found.  Fill in -902.'s
      DO 200 I=NSTART,ILIM
      VALUES(I) = -902.0
      IF (LQUAL) JQUAL(I) = 0
 200  CONTINUE
      ENDIF
C
C
C     Update the position of VALUES for the next record
      NSTART = NSTART + NREAD
C
C
 800  CONTINUE
      IF (MLEVEL.GE.12) WRITE (MUNIT,820)
 820  FORMAT (T8,'----DSS---Debug:  Exit  ZRRTSB')
      RETURN
C
C
C     Probably will never get here - a terse error message is ok
 900  CONTINUE
      WRITE (MUNIT,901) JULS, ISTIME
 901  FORMAT (/,' **** ERROR - ZRRTSB:  Illegal starting date/time',/,
     * ' Julian date:',I8,'  Time:',I8,/)
      ISTAT = 15
      GO TO 800
C
 910  CONTINUE
      WRITE (MUNIT,911) NADATA, NIBUFF
 911  FORMAT (/,' **** ERROR - ZRRTSB:  Insufficient internal memory',
     * ' to Uncompress Data',/,
     * ' Amount required:',I5,',  Amount available:',I5)
      ISTAT = 54
      GO TO 800
C
 920  CONTINUE
      WRITE (MUNIT,921) IST
 921  FORMAT (/,' **** ERROR - ZRRTSB:  Unable to Uncompress Data',/,
     * ' Status:',I6,/)
      ISTAT = 53
      GO TO 800
C
      END
