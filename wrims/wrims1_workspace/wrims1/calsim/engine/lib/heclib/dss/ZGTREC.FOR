C  ** ** CHECK FOR READS FROM DIFFERENT UNITS !!!!
      SUBROUTINE ZGTREC ( IFLTAB, IARRAY, NARRAY, JADD, LSAVEB)
C
C     Read Logical records from the DSS file
C
C     Variable Definitions:
C         IARRAY:  array to place data read in
C         NARRAY:  number of integer words to read
C         NREC:    beginning record number to start read
C         NWORD:   relative word address where data starts
C         LSAVEB:  Flag to save records read
C
C     Written by Bill Charley at HEC, 1985. Original version
C        written by Brent Cullimore, 1981.
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdssbz.h'
C
      INCLUDE 'zdssnz.h'
C
      INCLUDE 'zdsslz.h'
C
      INCLUDE 'zdssmz.h'
C
C
      INTEGER IFLTAB(*), IARRAY(*)
      LOGICAL LSAVEB
C
C
      IUNIT = IFLTAB(KUNIT)
      IHANDL = IFLTAB(KHANDL)
      CALL ZGETRW (JADD, NREC, NWORD)
      ISTAT = 0
C
      IF (MLEVEL.GE.14) THEN
      WRITE (MUNIT,20) IHANDL, JADD, NREC, NWORD, NARRAY,
     * IFLTAB(KLOCK), JBUFF, IFLTAB(KMXREC), LSAVEB
      WRITE (MUNIT,21) JCREC
      WRITE (MUNIT,22) JBUNIT
      WRITE (MUNIT,23) LSBUFF
      WRITE (MUNIT,24) JWRITE
      WRITE (MUNIT,25) LOCKBF
      WRITE (MUNIT,26) JMXREC
 20   FORMAT (T12,'----DSS---Debug:  Enter ZGTREC;  Unit:',I3,/,
     * T16,'Read Address:',I10,',  Rec:',I7,', Word:',I4,', Size:',I6,/,
     * T16,'Lock:',I2,',  JBUFF:',I3,',  MXREC:',I8,',  Save Buff:',L2)
 21   FORMAT (T16,'Current Record:',9I7)
 22   FORMAT (T16,'Unit:          ',9I7)
 23   FORMAT (T16,'Save Record:   ',9L7)
 24   FORMAT (T16,'Write Flags:   ',9I7)
 25   FORMAT (T16,'Lock Flags:    ',9L7)
 26   FORMAT (T16,'Max File Rec:  ',9I7)
      ENDIF
C
C
C     Check Key locations against corruption
C
      IF ((IFLTAB(KEY1).NE.NKEY).OR.(IFLTAB(KEY2).NE.NKEY).OR.
     * (IFLTAB(KEY3).NE.NKEY)) GO TO 940
C
C
      IF ((IUNIT.LE.0).OR.(IUNIT.GT.200)) THEN
      WRITE ( MUNIT, 40) IUNIT
 40   FORMAT (//,' ***** ILLEGAL UNIT NUMBER FOR DSS FILE:',I9)
      CALL ZABORT (IFLTAB, 20, 'ZGTREC', IUNIT, IFLTAB(1),
     * 'Illegal Unit')
      RETURN
      ENDIF
C
C     Error out if we have received a trashed address
      IF (JADD.LE.0) GO TO 910
      IF ((JADD-2000.GT.IFLTAB(KFSIZE)).AND.(IFLTAB(KMULT).EQ.2))
     * GO TO 910
C
      ISIZE = NARRAY
C
C     Initialize pointers
      IBEG = NWORD
      IREC = NREC
      IARRP = 0
C
C     ** LOOP **
 100  CONTINUE
C     Calculate pointers for this record
      JSIZE = NBSIZE - IBEG + 1
      IEND = MIN0(JSIZE,ISIZE) + IBEG - 1
C     Are we done yet?
      IF (IEND.LT.IBEG) GO TO 800
C
C     Does the record need to be physically read?
 120  CONTINUE
      JBUFF = 0
      DO 140 I=1,MXBUFF
      IF ((IREC.EQ.JCREC(I)).AND.(IHANDL.EQ.JBUNIT(I))) JBUFF = I
 140  CONTINUE
C
      IF (JBUFF.EQ.0) THEN
      DO 160 I=MXBUFF,1,-1
      IF (.NOT.LSBUFF(I)) JBUFF = I
 160  CONTINUE
      IF (JBUFF.EQ.0) THEN
C     Buffers are all full, must dump them to make room!
      CALL ZBDUMP (IFLTAB, 1)
      GO TO 120
      ENDIF
C
      IF (JWRITE(JBUFF).EQ.1) THEN
      IF (JCREC(JBUFF).LE.0) GO TO 950
      CALL ZWREC (JBUNIT(JBUFF), JCREC(JBUFF), IBUFF(1,JBUFF), NBSIZE,
     * IFLTAB(KSWAP), ISTAT, JSTAT)
      IF (ISTAT.NE.0) GO TO 950
      IF (IHANDL.EQ.JBUNIT(JBUFF)) THEN
      IF (JCREC(JBUFF).GT.IFLTAB(KMXREC)) IFLTAB(KMXREC) = JCREC(JBUFF)
      ENDIF
      JWRITE(JBUFF) = 0
      ENDIF
C
      CALL ZRREC  (IHANDL, IREC, IBUFF(1,JBUFF), NBSIZE,
     * IFLTAB(KSWAP), ISTAT, JSTAT)
      JCREC(JBUFF) = IREC
      JBUNIT(JBUFF) = IHANDL
C     Check for a read error
      IF (ISTAT.GT.0) GO TO 920
C     Its ok to read an EOF on the first record
      IF (ISTAT.LT.0) THEN
      IF ((IREC.EQ.1).AND.(IFLTAB(KFSIZE).EQ.0)) THEN
      DO 200 J=1,NBSIZE
      IBUFF(J,JBUFF) = 0
 200  CONTINUE
      ELSE IF (IREC.EQ.IFLTAB(KMXREC)) THEN
C     Its ok to read an end of file on the very last record
      ELSE
C     Not first or last, a real error must have occurred
          GO TO 920
      ENDIF
      ENDIF
      ENDIF
C
C     Transfer data into the buffer array
      DO 220 I=IBEG,IEND
      IARRP = IARRP + 1
      IARRAY(IARRP) = IBUFF(I,JBUFF)
 220  CONTINUE
C
C     Calculate next record pointers
      IF (LSAVEB) LSBUFF(JBUFF) = .TRUE.
      IREC = IREC + 1
      ISIZE = ISIZE - (IEND - IBEG + 1)
      IBEG = 1
      GO TO 100
C     ** END OF LOOP **
C
C
800   CONTINUE
C     Write any debug exit messages
      IF (MLEVEL.GE.14) THEN
      CALL ZGETRW (IFLTAB(KFSIZE), IREC, IWORD)
      WRITE (MUNIT,810) IFLTAB(KFSIZE), IREC, IWORD
      WRITE (MUNIT,21) JCREC
      WRITE (MUNIT,22) JBUNIT
      WRITE (MUNIT,24) JWRITE
 810  FORMAT (T12,'-----DSS---Debug:  Exit ZGTREC',/,
     * T16,'File Size:',I9,' Words (',I7,' Records,',I5,' Words)')
      ENDIF
C
      RETURN
C
C
C     Bad address
 910  CONTINUE
      WRITE (MUNIT, 911) JADD, IFLTAB(KFSIZE)
 911  FORMAT (///,' ********** DSS ******** ERROR DURING READ',/
     * ' ZGTREC, Invalid address:',I14,',  File Size:',I14,/)
      WRITE (MUNIT,922) IUNIT, NREC, NWORD, NARRAY, JADD
C
      JSTAT = IFLTAB(KFSIZE)
      CALL ZABORT (IFLTAB, 70, 'ZGTREC', JSTAT, JADD, 'Bad Address')
      RETURN
C
C     Error on read request
 920  CONTINUE
      IF (LCOFIL.AND.(ISTAT.LT.0)) THEN
      JADD = -1
      GO TO 800
      ENDIF
      WRITE (MUNIT, 921) JADD, IREC, ISTAT, JSTAT
 921  FORMAT (///,' ********* DSS ******** ERROR DURING READ',
     * /,' ZGTREC, ADDRESS:',I9,'  RECORD:',I8,'  STATUS:',8I8)
C
      WRITE (MUNIT,922) IUNIT, NREC, NWORD, NARRAY, JADD
 922  FORMAT (' UNIT =',I8,' RECORD =',I8,'  WORD =',I8,'  SIZE:',I8,/,
     * ' ADDRESS: ',I12)
C
      CALL ZABORT (IFLTAB, 30, 'ZGTREC', JSTAT, JADD, 'Error on Read')
      RETURN
C
 940  CONTINUE
      WRITE ( MUNIT, 945) IFLTAB(KUNIT), NKEY, IFLTAB(KEY1),
     * IFLTAB(KEY2), IFLTAB(KEY3)
 945  FORMAT (///,' ******** DSS: ERROR; IFLTAB HAS BECOME CORRUPT',
     * /,'  This is due to a program error (array Overwritten)',/,
     * ' UNIT =',I8,'  NKEY =',I8,'  KEYS 1, 3, 5 =',3I8,/,
     * ' Note: All keys must equal NKEY',//)
C
      I = IFLTAB(KEY1)
      CALL ZABORT (IFLTAB, 50, 'ZGTREC', I, IFLTAB(KEY1), 'Corrupt Key')
      RETURN
C
C
C     Error on dumping buffer
 950  CONTINUE
      WRITE (MUNIT, 960) IREC, ISTAT, JSTAT, JCREC
 960  FORMAT (///,' ********* DSS ********* ERROR ON WRITE REQUEST',
     * /,' ROUTINE ZGTREC, RECORD =',I8,'  STATUS =',I8,/,
     * '  Record Buffer: ',8I9)
C
C     WRITE (MUNIT,970) JUNIT, IREC, IWORD, NARRAY
C970  FORMAT (' UNIT =',I8,' ORIGNAL RECORD =',I8,'  WORD =',I8,/,
C    * '  REQUESTED SIZE =',I8,/)
C
      CALL ZABORT (IFLTAB, 40, 'ZGTREC', JSTAT, JADD, 'Write Error')
      RETURN
C
      END
