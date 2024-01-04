      SUBROUTINE WAITS (SECS)                                           MLH
C
C     WAIT THE SPECIFIED # OF SECONDS
C
      INTEGER*4 IHUND, JHUND, JULB, JULE                                ML
C
C
C     I=SECS*120.0                                                      H
C     CALL WAIT (I,0,K)                                                 H
C
C
      CALL CURTIM (JULB, MIN)                                           ML
      CALL CBTIME(IHUND)                                                M
C     CALL TIMER (IHUND)                                                L
C
 5    CONTINUE                                                          ML
      CALL CBTIME(JHUND)                                                M
C     CALL TIMER (JHUND)                                                L
      CALL CURTIM (JULE, MIN)                                           ML
      T = (JHUND - IHUND) * .01                                         ML
      T = T + (FLOAT(JULE - JULB) * 86400.0)                            ML
      CALL CURTIM (JULE, MIN)                                           ML
      IF ( T .LT. SECS ) GO TO 5                                        ML
      RETURN                                                            MLH
      END                                                               MLH
