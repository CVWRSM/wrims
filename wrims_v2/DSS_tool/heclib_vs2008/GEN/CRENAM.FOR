      SUBROUTINE CRENAM (COLDNM,CNEWNM,IERR)
	USE DFPORT
C
C
C     INTEGER NAME1(6),NAME2(6)                                         H
      CHARACTER COLDNM*(*),CNEWNM*(*)
      CHARACTER*254 CNMOLD, CNMNEW                                       MLu
C
      CNMOLD = COLDNM                                                   MLu
      CNMNEW = CNEWNM                                                   MLu
C
C

C     CALL CHRLNB(CNMOLD,J1)                                            MLu
C     CALL CHRLNB(CNMNEW,J2)                                            MLu
*      CNMOLD(J1+1:J1+1) = CHAR(0)                                       MLu
*      CNMNEW(J2+1:J2+1) = CHAR(0)                                       MLu
C
*     CALL RNAMF (CNMOLD, CNMNEW, IERR)                                 MLu
      IERR = RENAME (CNMOLD, CNMNEW)
C
 
C
      RETURN
      END
