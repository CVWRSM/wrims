      SUBROUTINE STDINC(CWAIT,CECHO,CBREAK,CFLUSH,IASCII,ICODE)
cjdk	use msflib
cjdk	use portlib
	USE DFPORT                                                           
	USE DFLIB                                                            

      CHARACTER CWAIT*1, CECHO*1, CBREAK*1, CFLUSH*1
      INTEGER*2 IASCII, ICODE
      CHARACTER*1 CH1
      INTEGER*2 KEYTYPE
      LOGICAL KBHIT  /.FALSE./
C     CHECK FOR CLEARING THE KEYBOARD BUFFER
      IF(CFLUSH .EQ. 'Y') CALL FLUSH(5)
C     CHECK FOR WAIT FOR A KEY TO BE PRESSED
      IF(CWAIT .EQ. 'Y') THEN
        CH1 = GETCHARQQ()
	    ELSE
C     DO NOT WAIT FOR A KEY TO BE PRESSED
	  KBHIT=PEEKCHARQQ()
        IF(KBHIT) THEN
          CH1 = GETCHARQQ()
        ELSE
          IASCII = -1
          RETURN
        ENDIF
      ENDIF
 151  FORMAT(A,$)
	KEYTYPE=ICHAR(CH1)
      IF(KEYTYPE.EQ.0.OR.KEYTYPE.EQ.224) THEN
	  CH1=GETCHARQQ()                                               
        IASCII=0
        ICODE=ICHAR(CH1)
      ELSE
        IASCII=ICHAR(CH1)
        ICODE=0
      ENDIF
      IF(CECHO .EQ. 'Y' .AND. IASCII .GT. 0) WRITE(6,151) CH1
      RETURN
      END
