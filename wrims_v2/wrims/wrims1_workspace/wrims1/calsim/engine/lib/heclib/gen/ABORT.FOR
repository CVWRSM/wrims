      SUBROUTINE ABORT                                                  MLu
C
C     TEMPOARY ABORT ROUTINE FOR NON-HARRIS SITES
C
      WRITE (6,10)                                                      MLu
 10   FORMAT (//,' ***** PROGRAM CALLED ABORT *****',//)                MLu
C
*     CALL EXIT (2)                                                     u
C
C     GET THE SQUARE ROOT OF A NEGATIVE NUMBER TO CAUSE A FATAL ERROR
*     X = SIN (0.2)                                                     ML
*     X = -X                                                            ML
*     Y = SQRT (X)                                                      ML
*     WRITE (6,*) Y                                                     ML
      STOP                                                              MLu
      END                                                               MLu
