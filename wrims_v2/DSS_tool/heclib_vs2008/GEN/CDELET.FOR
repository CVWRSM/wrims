      SUBROUTINE CDELET (CNAME,IERR)
	use msflib
C
C
      CHARACTER CNAME*(*)
      CHARACTER CNAM*80                                                 Mu
C     INTEGER NAME(6)                                                   H
C
C     DO 10 I = 1,6                                                     H
C     NAME(I) = 3H                                                      H
C10   CONTINUE                                                          H
C     ILEN = 18                                                         H
C     J = LEN(CNAME)                                                    H
C     IF (J .LT. ILEN) ILEN = J                                         H
C     CALL CHRHOL(CNAME,1,ILEN,NAME,1)                                  H
C     CALL DELETE(NAME,IERR)                                            H
C
      CNAM = CNAME                                                      Mu
      CALL CHRLNB (CNAM, J)                                             Mu
C     CALL ERASF(CNAM(1:J)//CHAR(0),IERR)                               Mu
      IERR = DELFILESQQ (CNAM(1:J)//CHAR(0))                            Mu
	IF (IERR.GT.0) IERR = 0

C
C     OPEN (UNIT=99, FILE=CNAME, IOSTAT=IERR)                           L
C     IF (IERR.EQ.0) CLOSE (UNIT=99, STATUS='DELETE')                   L
C
      RETURN
      END
