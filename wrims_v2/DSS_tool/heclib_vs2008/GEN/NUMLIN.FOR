      INTEGER FUNCTION NUMLIN (CFNAME)
C
C     This routine returns the number of
C     lines in an ASCII file.
C
      CHARACTER  CLINE*1, CFNAME*(*)
      LOGICAL LOPEN                                                     MLu
C
      IUNIT = 99
C     NWORD = 2058                                                      H
      INQUIRE (FILE=CFNAME,OPENED=LOPEN,NUMBER=IUNIT)                   MLu
      IF (.NOT. LOPEN) THEN                                             MLu
      OPEN (IUNIT,FILE=CFNAME)
      ENDIF                                                             MLu
C     CALL DKBFOP (IH,CFNAME,IBUF,NWORD,ISTAT)
      I = 0
 10   I = I+1
      READ (IUNIT,'(A)',END=90) CLINE
C     CALL DKBFRD (IH,CLINE,NLINE,IBUF,ISTAT)
C     IF (ISTAT .NE. 0) GOTO 90
      GOTO 10
 90   CONTINUE
      NUMLIN = I-1
      IF (.NOT.LOPEN) THEN                                              MLu
      CLOSE (IUNIT)
      ENDIF                                                             MLu
C     CALL DKBFCL (IH, IBUF, ISTAT)
      RETURN
      END
