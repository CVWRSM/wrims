      SUBROUTINE CPARMS ( C , M )
C
C------ RETURN ARGUMENTS ON COMMAND LINE AND ITS LENGTH
C
      USE DFLIB

      CHARACTER C*(*), CT*132
C
      K = LEN ( C )
      M = 1
C     N = IARGC (  )                                                    u
      N = NARGS () - 1                                                  Mp
C
      IF ( N .GE. 1 ) THEN
      DO 10 I = 1, N
      CT = ' '
      CALL GETARG ( I, CT, KK)                                          Mp
      IF (KK.LT.0) GO TO 50                                             Mp
C     CALL GETARG ( I, CT )                                             u
      CALL CHRLNB ( CT, J )
      IF ( M + J + 1 .LE. K ) THEN
      C(M:) = CT
      M = M + J + 1
      ELSE
      GO TO 50
      ENDIF
   10 CONTINUE
      ENDIF
C------
   50 CONTINUE
      M = M - 2
      M = MAX0 ( M, 0 )
      RETURN
      END
