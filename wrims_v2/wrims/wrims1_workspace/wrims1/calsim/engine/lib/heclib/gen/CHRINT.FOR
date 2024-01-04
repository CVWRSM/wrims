      SUBROUTINE CHRINT (NUMB, CSTRING)
C
C
      CHARACTER CSTRING*(*), CFORMT*5
      INTEGER*4 NUMB                                                    ML
C
C
      NLEN = LEN(CSTRING)
      WRITE ( CFORMT(3:4), 10, ERR=900) NLEN
 10   FORMAT (I2)
      CFORMT(1:2) = '(I'
      CFORMT(5:5) = ')'
C
      WRITE ( CSTRING, CFORMT, ERR=900) NUMB
      RETURN
C
 900  CONTINUE
      CALL CHRFIL ( CSTRING, '*')
      RETURN
      END
