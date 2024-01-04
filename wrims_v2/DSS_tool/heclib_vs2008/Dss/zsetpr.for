      SUBROUTINE ZSETPR (IFLTAB, CFLG, CSTR, INUMB)
C
C     Sets Items in the Permanent section of a DSS file
C     This routine is to be called only by DSSUTL and
C     internal DSS subroutines
C
C     Written by Bill Charley at HEC, January 1990.
C
C
      INTEGER IFLTAB(*)
      CHARACTER CFLAG*4, CFLG*(*), CSTR*(*)
C
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdssnz.h'
C
      INCLUDE 'zdssiz.h'
C
      INCLUDE 'zdssts.h'
C
      INCLUDE 'zdssmz.h'
C
C
C
      CFLAG = CFLG
      IF (MLEVEL.GE.12) WRITE ( MUNIT, 20) CFLAG
 20   FORMAT (T2,'-----DSS---Debug:  Enter ZSETPR;  Flag: ',A)
C
C     Loc file and get the root section
      CALL ZMULTU (IFLTAB, .TRUE., .TRUE.)
C
C
      IF (CFLAG.EQ.'SEQN') THEN
      IFLTAB(KSEQNO) = INUMB
C
      ELSE
      GO TO 820
      ENDIF
C
C
 800  CONTINUE
C     Store new information
      IADD = 1
      CALL ZPTREC (IFLTAB, IFLTAB(KPERM), NPERM, IADD, .FALSE.)
C
 820  CONTINUE
C     Dump buffers and unlock file
      CALL ZMULTU (IFLTAB, .FALSE., .TRUE.)
C
      RETURN
      END
