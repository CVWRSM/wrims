      SUBROUTINE opendssoutput_  (fileName, status,
     +                                      fileNameLength, length)
C
      CHARACTER fileName               ![REFERENCE]
      INTEGER status                   ![REFERENCE]
      INTEGER fileNameLength           ![REFERENCE]
      INTEGER length                   ![VALUE]
C
      OPEN (UNIT=10, FILE=fileName(1:fileNameLength), IOSTAT=status)
C
      if (status.eq.0) then
         CALL ZSET ('MUNIT', ' ', 10)
      endif
c
      RETURN
      END
