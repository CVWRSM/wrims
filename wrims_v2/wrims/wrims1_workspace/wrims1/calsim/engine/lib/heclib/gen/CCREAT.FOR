      SUBROUTINE CCREAT (CNAME,IGRAN,IPACK,ITYPE,IERR)
C
C
      CHARACTER CNAME*(*)
C     LOGICAL L                                                         L
      CHARACTER CNAM*80                                                 Mu
C     INTEGER NAME(6)                                                   H
C
C
C     DO 10 I = 1,6                                                     H
C     NAME(I) = 3H                                                      H
C10   CONTINUE                                                          H
C
C     ILEN = 18                                                         H
C     J = LEN(CNAME)                                                    H
C     IF (J .LT. ILEN) ILEN = J                                         H
C     CALL CHRHOL(CNAME,1,ILEN,NAME,1)                                  H
C     CALL CREATE(NAME,IGRAN,IPACK,ITYPE,IERR)                          H
C
      CNAM = CNAME                                                      Mu
      J = NINDXR(CNAM,' ')                                              Mu
      CALL CREAF(CNAM(1:J)//CHAR(0),0,IH,IERR)                          Mu
      CALL CLOSF(IH,IERR)                                               Mu
C
C     DO 20 I=50,90                                                     L
C     INQUIRE (UNIT=I,OPENED=L)                                         L
C     IF (.NOT.L) THEN                                                  L
C     OPEN (UNIT=I,FILE=CNAME,IOSTAT=IERR)                              L
C     CLOSE (UNIT=I)                                                    L
C     RETURN                                                            L
C     ENDIF                                                             L
C20   CONTINUE                                                          L
C
      RETURN
      END
