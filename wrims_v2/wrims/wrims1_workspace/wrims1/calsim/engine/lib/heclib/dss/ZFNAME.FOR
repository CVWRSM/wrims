      SUBROUTINE ZFNAME (CIN, CNAME, NNAME, LEXIST)
C
C     Add machine dependent extensions to form DSS file name
C     Determine if the file exists in the current directory
C
C     Written by Bill Charley, HEC, May 1990
C
C
      CHARACTER CIN*(*), CNAME*(*)
      LOGICAL LEXIST, LEXTEN
C
C
      CALL CHRFLB (CIN, IBEG, IEND)
      IF (IEND.LE.0) THEN
         CNAME = ' '
         LEXIST = .FALSE.
         GO TO 800
      ENDIF
C
C
C     See if there is already an extension on this name
      LEXTEN = .FALSE.
      IF ((IEND.GT.4).AND.(CIN(IEND-3:IEND-3).EQ.'.')) LEXTEN = .TRUE.
C
C     Default extension of '.dss'
      IF (.NOT.LEXTEN) THEN
         CNAME = CIN(IBEG:IEND) // '.dss'
      ELSE
         CNAME = CIN(IBEG:IEND)
      ENDIF
C
      CALL CHRLNB (CNAME, NNAME)
      INQUIRE (FILE=CNAME(1:NNAME), EXIST=LEXIST)
C
 800  CONTINUE
      RETURN
      END
