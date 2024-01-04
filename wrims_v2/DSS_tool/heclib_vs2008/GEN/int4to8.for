      SUBROUTINE INT4TO8 (INT4, INT8)
C
C     Move integer*4 value bits into an integer*8 value,
C     without having to worry about the sign changing
C
      INTEGER*4 INT4
      INTEGER*8 INT8
C
      INTEGER*8 I64
      INTEGER*4 I32(2)
      EQUIVALENCE (I64, I32)
C
      I64 = 0     
      I32(1) = INT4
C     Next line is to try and keep optimizers from killing this function
      IF (INT4.EQ.0) I64 = I32(1)
      INT8 = I64
      RETURN
      END
