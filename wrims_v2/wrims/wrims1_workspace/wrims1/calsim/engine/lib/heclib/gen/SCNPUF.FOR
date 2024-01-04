      SUBROUTINE SCNPUF(                                                M
     . CSNAME,IF34,IRECSC,NCOL,NROWS,IROW,ICOL,ISTAT)                   M
      CHARACTER CSNAME*(*)                                              M
C     THIS COMMON BLOCK MUST BE AT LEAST 5000 INEGER WORDS LONG
      COMMON /ZDSSTS/ IBUF1,IMODE,IPAGE,IOROW,                          M
     . ICOLN,IOCOL,IOTOP,IOBOTM,NOSCS,                                  M
     . CCSCNS                                                           M
C----------------------------------------------------------------------
C
      INTEGER*2 IBUF1(80,25),IROW,ICOL,NROWS,NCOL                       M
      CHARACTER CCSCNS(200)*8                                           M
C     GET THE SCREEN STATUS
      CALL VSTAT (IMODE,ICOLN,IPAGE)                                    M
      CALL VGETCR (IPAGE,IOROW,IOCOL,IOTOP,IOBOTM)                      M
C     CHECK TO SEE IF SCREEN MODE IS APPROPRIATE
      IF(IMODE .GT. 3) THEN                                             M
         GO TO 500                                                      M
      ENDIF                                                             M
C
C     CHECK TO SEE IF THE SCREEN SHOULD BE PUFFED OUT
      IF(IRECSC .GT. 0 ) THEN                                           M
       READ(IF34,REC=IRECSC+1)IROW,ICOL,NCOL,NROWS,IBUF1                M
       CALL PUFBFW(IBUF1,IROW-1,ICOL-1,NCOL,NROWS)                      M
      ELSE                                                              M
C     CAPTURE THE SCREEN AND STORE IT
       READ(IF34,REC=1,ERR=500) NOSCS,CCSCNS                            M
C     CHECK FOR MAXIMUM NUMBER OF POSSIBLE SCREENS
       IF(NOSCS.GE.200) GO TO 500                                       M
       CALL PUFBFR(IBUF1,IROW-1,ICOL-1,NCOL,NROWS)                      M
       NOSCS = NOSCS + 1                                                M
       CCSCNS(NOSCS) = CSNAME                                           M
       WRITE(IF34,REC=1) NOSCS,CCSCNS                                   M
       WRITE(IF34,REC=NOSCS+1)IROW,ICOL,NCOL,NROWS,IBUF1                M
      ENDIF                                                             M
      ISTAT = 0                                                         M
      RETURN                                                            M
 500  ISTAT = -1                                                        M
      RETURN                                                            M
      END                                                               M
