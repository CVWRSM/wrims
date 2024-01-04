      FUNCTION INTCHR (CSTRING)
C
C     CONVERT A NUMERIC STRING OF CHARACTERS TO AN INTEGER NUMBER
C
C
      CHARACTER CSTRING*(*), CFORMT*5
C     INTEGER INTCHR                                                     u
      INTEGER*4 INTCHR                                                   ML
C
C
      NLEN = LEN (CSTRING)
C
      WRITE ( CFORMT, 10, ERR=900) NLEN
 10   FORMAT (I3)
      CFORMT(1:2) = '(I'
      CFORMT(5:5) = ')'
C
      READ ( CSTRING, CFORMT, ERR=900) INTCHR
      RETURN
C
 900  CONTINUE
      INTCHR = -1
      RETURN
      END
