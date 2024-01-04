      SUBROUTINE ZFNAME (CIN, CNAME, NNAME, LEXIST)
C
C     Add machine dependent extensions to form DSS file name
C     Determine if the file exists in the current directory
C
C     Written by Bill Charley, HEC, May 1990
C
C     Modified Sept 1998 to call fullpathname
C     Handles filenames up to 256 characters
C
      CHARACTER CIN*(*), CNAME*(*), CTEMP*256, CTEMP2*256
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
      IF (IEND.GT.4) THEN
         IF (CIN(IEND-3:IEND-3).EQ.'.') LEXTEN = .TRUE.
      ENDIF         
C
C     Default extension of '.dss'
      IF (.NOT.LEXTEN) THEN
         CTEMP = CIN(IBEG:IEND) // '.dss'
      ELSE
         CTEMP = CIN(IBEG:IEND)
      ENDIF
C
      CALL CHRLNB (CTEMP, NNAME)
      INQUIRE (FILE=CTEMP(1:NNAME), EXIST=LEXIST)                       u
      CNAME = CTEMP                                                     u
C
      CTEMP2 = ' '
C     CALL FULLPATHNAME (CTEMP(1:NNAME), CTEMP2, NNAME)                 Md
C     CALL CHRLNB (CTEMP2, NNAME)                                       Md
C     INQUIRE (FILE=CTEMP2(1:NNAME), EXIST=LEXIST)                      Md
C     CNAME = CTEMP2                                                    Md
C
C
 800  CONTINUE
      RETURN
      END
