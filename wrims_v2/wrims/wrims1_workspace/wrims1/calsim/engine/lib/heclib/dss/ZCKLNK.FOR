      SUBROUTINE ZCKLNK (IFLTAB, NERROR)
C
C     Check a DSS file's Links for possible errors.
C     (Can find all the records_.
C
C     Written by Bill Charley at HEC, 1993.
C
C
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdsslz.h'
C
      INCLUDE 'zdssnz.h'
C
      INCLUDE 'zdssiz.h'
C
      INCLUDE 'zdsscz.h'
C
      INCLUDE 'zdssmz.h'
C
C
      CHARACTER CPATH*90
      INTEGER IFLTAB(*), IARRAY(NBSIZE)
      LOGICAL LFOUND
C
C
C
      IF (MLEVEL.GE.12) WRITE (MUNIT,20) IFLTAB(KUNIT)
 20   FORMAT (T8,'-----DSS---Debug:  Enter ZCKLNK;  Unit:',I4)
C
C     Check that IFLTAB is valid (e.g., the DSS file is open)
      IF (IFLTAB(1).NE.6) THEN
      IF (MLEVEL.GE.1) WRITE (MUNIT, 30)
 30   FORMAT (/,' ERROR:  DSS File is not version 6, or file is not',
     * ' open.',/,' Only a version 6 DSS file can be checked.',/)
      GO TO 880
      ENDIF
C
C     Lock the file so we don't get erroneous results during
C     the check
      CALL ZMULTU (IFLTAB, .TRUE.)
      CALL ZSET ('ABORT', 'OFF', I)
C
C     Search for all information blocks in the file.
      NERROR = 0
      NH1PAT = 0
      NH2PAT = 0
      NH3PAT = 0
      NH4PAT = 0
C
C     Get the number of physical records in file (not data recs)
      IADD = IFLTAB(KFSIZE) - 1
      CALL ZGETRW (IADD, NRECS, IW)
C
      DO 200 IREC=2,NRECS
C
C     Read physical record IREC from file
      CALL ZGETAD (IADD, IREC, 1)
      CALL ZGTREC (IFLTAB, IARRAY, NBSIZE, IADD, .FALSE.)
C
C     Search through this record, looking for pathname flags
C
      DO 200 IWRD=1,NBSIZE
C
      IF (IARRAY(IWRD).EQ.NPFLAG) THEN
C
C     Found a flag - Get the first three words of the information
C     block to see if this indeed is the start of a data record
      CALL ZGETAD (IADD, IREC, IWRD)
      CALL ZGTREC ( IFLTAB, INFO, 3, IADD, .FALSE.)
C
      IF (INFO(KISTAT).EQ.2) NH2PAT = NH2PAT + 1
      IF (INFO(KISTAT).EQ.3) NH3PAT = NH3PAT + 1
      IF (INFO(KISTAT).EQ.4) NH4PAT = NH4PAT + 1
C
C     Check for a valid status flag (1)
      IF (INFO(KISTAT).EQ.1) THEN
C     Check for a valid pathname length
      NPATH = INFO(KINPAT)
      IF ((NPATH.GT.4).AND.(NPATH.LE.80)) THEN
C
C     Passed, therefore a valid record - Get the full information block
      NPPWRD = (NPATH - 1)/NCPW + 1
      NPMWRD = (NPATH - 1)/NCMW + 1
      CALL ZGTREC ( IFLTAB, INFO, NINFO+NPPWRD, IADD, .FALSE.)
C
C     Get the pathname
      CPATH = ' '
      CALL HOL2CH (INFO(KIPATH), CPATH, NPMWRD)
C
      CALL ZCHECK (IFLTAB, CPATH, NPATH, NHEAD, NDATA, LFOUND)
C
      IF (LFOUND) THEN
      JADD = IPNBIN (JPNBIN+NPPWRD+KBAINF)
C
      IF (JADD.NE.IADD) THEN
      IF (MLEVEL.GE.1) WRITE (MUNIT, 100) CPATH(1:NPATH), JADD, IADD
 100  FORMAT (/' *** ZCKLNK:  Lost address link.  Bin address does not',
     * ' match info address',/,' Pathname: ',A,/,
     * ' Bin address:',I12,',  Info address:',I12)
      NERROR = NERROR + 1
      ELSE
      IF (MLEVEL.GE.7) WRITE (MUNIT, 120) CPATH(1:NPATH)
 120  FORMAT (' Record Checks: ',A)
      ENDIF
C
      ELSE
      IF (MLEVEL.GE.1) WRITE (MUNIT, 140) CPATH(1:NPATH)
 140  FORMAT(/' *** ZCKLNK:  Lost address link.  Unable to find record',
     * /,' Pathname: ',A)
      NERROR = NERROR + 1
      ENDIF
C
C
      NH1PAT = NH1PAT + 1
C
      ENDIF
      ENDIF
      ENDIF
C
C
C
 200  CONTINUE
C
C
 800  CONTINUE
C
      IF (NH1PAT.NE.IFLTAB(KNRECS)) THEN
      NERROR = NERROR + 1
      IF (MLEVEL.GE.1) WRITE (MUNIT, 810) IFLTAB(KNRECS), NH1PAT
 810  FORMAT (' *** ZCKLNK:  Inconsistency in the number of records',
     * ' in the file.',/,' Number Recorded: ',I6,',  Number Found:',I6)
      ENDIF
C
      IF ((MLEVEL.GE.1).AND.(NERROR.GT.0)) WRITE (MUNIT, *)' '
      IF (MLEVEL.GE.1) WRITE (MUNIT, 820) NERROR
 820  FORMAT (' Pathname Link Check Complete,',I4,' Errors found.')
      IF (MLEVEL.GE.3) WRITE (MUNIT, 830) NH1PAT, NH2PAT, NH3PAT,
     * NH4PAT
 830  FORMAT (' Number of Records:        ',I7,/,
     * ' Number of Deleted Records: ',I6,/,
     * ' Number of Renamed Records: ',I6,/,
     * ' Number of Replaced Records:',I6)
C
      CALL ZMULTU (IFLTAB, .FALSE.)
      CALL ZSET ('ABORT', 'ON', I)
C
 880  CONTINUE
      IF (MLEVEL.GE.12) WRITE (MUNIT,890) IFLTAB(KUNIT)
 890  FORMAT (T8,'-----DSS---Debug:  Exit ZCKLNK;   Unit:',I4)
C
      RETURN
C
      END
