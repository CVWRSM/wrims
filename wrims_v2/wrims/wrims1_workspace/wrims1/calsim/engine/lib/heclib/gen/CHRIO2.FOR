      SUBROUTINE CHRIO2
C     PERFORM CHARACTER IO.
C     THIS ROUTINE USES HARRIS SPECIFIC CABILITIES AT THIS TIME
C     IT CAN BE MODIFIED TO BE USED ON OTHER MACHINES IF THE
C     INTERNALS OF THE MACHINE ARE KNOWN.
C
C     FORTRAN WRITES MAY BE USED WHEN CHARACTER I/O IS
C     IN USE.
C
C********************************************************************
C NOTE: FORTRAN READ MAY NOT, NOT, BE DONE WHEN IN USE !!!!
C********************************************************************
C
C
      PARAMETER (IBLEN=256)                                             M
      CHARACTER CSTRM*127                                               M
      CHARACTER CBUFF*(IBLEN)                                           M
      CHARACTER*(*) CTAG,CACT,CSTR,CSTR1,CSTR3,CSTR4,CSTR5              M
C     INTEGER              JBUFF(86)                                    H
C     JBUFF(86) X 3 = 258 CHARACTERS.
      LOGICAL LINIT1, L8BIT
C     DATA LINIT1 /.FALSE./
      DATA L8BIT / .FALSE. /
C
      DATA IBPOS,IBNUM  /0,0/                                           M
C
      ENTRY CHRIT2(ILFN,IBUFF,NBUFF)
C     INTEGER IBUFF(NBUFF)                                              H
C     INITIALIZE CHARACTER CHANNEL #2
C
      LINIT1 = .TRUE.
C     JLFN=ILFN                                                         H
C     CALL GIOPLW(JLFN,'51,IBUFF,NBUFF,JSTAT)                           H
      GO TO 1000
C
C
      ENTRY CHRSE2 ( CTAG, CACT, IACT )
C     CHARACTER*(*) CTAG, CACT                                          H
      IF ( CTAG .EQ. '8BIT' ) THEN
      IF ( CACT .EQ. 'ON' ) THEN
      L8BIT = .TRUE.
      ELSE
      L8BIT = .FALSE.
      ENDIF
      IACT = 0
      ELSE
      IACT = -1
      ENDIF
      GO TO 1000
C
C
      ENTRY CHRFN2
C     FINISH CHARACTER CHANNEL #2
C
      LINIT1 = .FALSE.
C     CALL GIOPSW(JLFN,'24,JSTAT)                                       H
      GO TO 1000
C
C
      ENTRY CHRFL2
C     FLUSH CHARACTER CHANNEL #2 TYPE AHEAD BUFFER
C     CALL GIOPSW(JLFN,'54,JSTAT)                                       H
      CALL STDINC ('N','N','N','Y',J,K)                                 M
      GO TO 1000
C
C
C
      ENTRY CHRBK2(CSTR,NSTR)
C     CHARACTER*(*) CSTR                                                H
C     BACKSTORE ON CHANNEL #2
C     CALL CHRHOL(CSTR,1,NSTR,JBUFF,1)                                  H
C     CALL GIOPLW(JLFN,'27,JBUFF,NSTR,JSTAT)                            H
      DO 110 I=NSTR,1,-1                                                M
      CBUFF(IBPOS+1:IBPOS+1) = CSTR(I:I)                                M
      IBPOS = MOD(IBPOS+1,IBLEN)                                        M
      IBNUM = IBNUM+1                                                   M
 110  CONTINUE                                                          M
      GO TO 1000
C
C
C
      ENTRY CHRST2(ISTAT1,KSTAT1)
C     PERFORM A STATUS (WAIT UNTIL COMPLETE) OF MOST RECIENT OPERATION
C     CALL GIOPSW (JLFN,'00,JSTAT)                                      H
C     KSTAT1 = JSTAT                                                    H
C     K = JSTAT.AND.'00400000                                           H
C     IF (K.EQ.0) THEN                                                  H
      ISTAT1 = 0
C     ELSE                                                              H
C     ISTAT1 = 1                                                        H
C     ENDIF                                                             H
      GO TO 1000
C
C
C
      ENTRY CHRSI2(ISTAT,KSTAT)
C     PERFORM A STATUS (WITHOUT WAIT) OF MOST RECIENT OPERATION
C     CALL GIOPSW (JLFN,'77,JSTAT)                                      H
C     KSTAT = JSTAT                                                     H
C     J = JSTAT.AND.'40000000                                           H
C     K = JSTAT.AND.'00400000                                           H
C     IF (J.NE.0) THEN                                                  H
C     ISTAT = -1                                                        H
C     ELSE                                                              H
C     IF (K.EQ.0) THEN                                                  H
      ISTAT = 0
C     ELSE                                                              H
C     ISTAT = 1                                                         H
C     ENDIF                                                             H
C     ENDIF                                                             H
      GO TO 1000
C
C
      ENTRY CHRWT2(CSTR1,NSTR1)
C     CHARACTER*(*) CSTR1                                               H
C     WRITE CHARACTERS TO CHANNEL #2
      IF (.NOT.LINIT1) GOTO 2000
C     IF(NSTR1.EQ.0) GO TO 1000                                         H
      MSTR1 = NSTR1
      IF(NSTR1.LT.0) MSTR1 = LEN ( CSTR1 )
C     CALL CHRHOL(CSTR1,1,MSTR1,JBUFF,1)                                H
C     CALL GIOPLW(JLFN,'52,JBUFF,MSTR1,JSTAT)                           H
C     GO TO 1000                                                        H
      CSTRM = CSTR1                                                     M
      NSTRM = MSTR1                                                     M
      GOTO 120                                                          M
C
C
C
      ENTRY CHRWI2(CSTR3,NSTR3)
C     CHARACTER*(*) CSTR3                                               H
C     DO AN IMMEDIATE WRITE WITHOUT WAITING FOR COMPLETION
C     THIS MUST BE FOLLOWED BY A STATUS CALL BEFORE OTHER I/O
C
C     WRITE CHARACTERS TO CHANNEL #2
      IF (.NOT.LINIT1) GOTO 2000
C     IF(NSTR3.LT.1) GO TO 1000                                         H
C     CALL CHRHOL(CSTR3,1,NSTR3,JBUFF,1)                                H
C     CALL GIOPL (JLFN,'52,JBUFF,NSTR3,JSTAT)                           H
      CSTRM = CSTR3                                                     M
      NSTRM = NSTR3                                                     M
 120  IF(NSTRM.LT.1) GO TO 1000                                         M
      DO 150 I=1,NSTRM                                                  M
         CALL STDOUT ('Y',ICHAR(CSTRM(I:I)))                            M
 150  CONTINUE                                                          M
      GO TO 1000
C
C
      ENTRY CHRRD2(CSTR4,NSTR4)
C     CHARACTER*(*) CSTR4                                               H
      IF (.NOT.LINIT1) GOTO 2000
C     READ CHARACTERS FROM CHANNEL #2, WAIT FOR AT LEAST ONE CHARACTER
C     CALL GIOPSW(JLFN,'51,JSTAT)                                       H
C     NSTR4 = JSTAT .AND. '177777                                       H
C     IF (NSTR4 .GT. LEN (CSTR4)) NSTR4 = LEN(CSTR4)                    H
C     IF (NSTR4 .GT. 0) THEN                                            H
C     IF ( L8BIT ) THEN                                                 H
C     CALL HOLCH8(IBUFF,CSTR4,NSTR4)                                    H
C     ELSE                                                              H
C     CALL HOLCH7(IBUFF,CSTR4,NSTR4)                                    H
C     ENDIF                                                             H
C     ENDIF                                                             H
      IF (IBNUM.NE.0) THEN                                              M
      DO 202 I=1,IBNUM                                                  M
      CSTR4(I:I) = CBUFF(MOD(IBPOS+IBLEN,IBLEN):                        M
     +   MOD(IBPOS+IBLEN,IBLEN))                                        M
      IBPOS = MOD(IBPOS-1+IBLEN,IBLEN)                                  M
      NSTR4 = NSTR4+1                                                   M
 202  CONTINUE                                                          M
      IBNUM = 0                                                         M
      CALL STDINC ('N','N','N','N',JCHAR,K)                             M
      GOTO 220                                                          M
      ELSE                                                              M
      NSTR4 = 0                                                         M
      ENDIF                                                             M
 210  CALL STDINC ('Y','N','Y','N',JCHAR,K)                             M
C
C         Wait for at least one character
C
 220  IF (JCHAR .GT. 0) THEN                                            M
C
C         Add character to string
C
         NSTR4 = NSTR4 + 1                                              M
         CSTR4 (NSTR4:NSTR4) = CHAR(JCHAR)                              M
         CALL STDOUT ('Y',JCHAR)                                        M
         CALL STDINC ('N','N','N','N',JCHAR,K)                          M
         GOTO 220                                                       M
      ENDIF                                                             M
      GOTO 1000
C
C
C
      ENTRY CHRRI2(CSTR5,NSTR5)
C     CHARACTER*(*) CSTR5                                               H
      IF (.NOT.LINIT1) GOTO 2000
C
C        Get characters from keyboard, do not wait.
C           NO-WAIT, ECHO, CHECK, NO-FLUSH
C
C     CALL GIOPSW(JLFN,'53,JSTAT)                                       H
C100  NSTR5=JSTAT .AND. '177777                                         H
C     IF(NSTR5 .GT. LEN(CSTR5)) NSTR5=LEN(CSTR5)                        H
C     IF (NSTR5 .GT. 0) THEN                                            H
C     IF ( L8BIT ) THEN                                                 H
C     CALL HOLCH8(IBUFF,CSTR5,NSTR5)                                    H
C     ELSE                                                              H
C     CALL HOLCH7(IBUFF,CSTR5,NSTR5)                                    H
C     ENDIF                                                             H
C     ENDIF                                                             H
C
      NSTR5 = 0                                                         M
      IF (IBNUM.NE.0) THEN                                              M
      DO 302 I=1,IBNUM                                                  M
      CSTR5(I:I) = CBUFF(MOD(IBPOS+IBLEN,IBLEN):                        M
     +   MOD(IBPOS+IBLEN-1,IBLEN))                                      M
      IBPOS = MOD(IBPOS+IBLEN,IBLEN)                                    M
      NSTR5 = NSTR5+1                                                   M
 302  CONTINUE                                                          M
      IBNUM = 0
      ELSE                                                              M
      NSTR4 = 0                                                         M
      ENDIF                                                             M
 310  CALL STDINC('N','N','N','N',JCHAR,K)                              M
C
 320  IF (JCHAR .GT. 0) THEN                                            M
C
C         Add character to string
C
         NSTR5 = NSTR5 + 1                                              M
         CSTR (NSTR5:NSTR5) = CHAR(JCHAR)                               M
         CALL STDOUT ('Y',JCHAR)                                        M
         GOTO 310                                                       M
      ENDIF                                                             M
C
C
1000  CONTINUE
      RETURN
C
 2000 CONTINUE
      WRITE (3,2010)
 2010 FORMAT (/' ERROR - CHANNEL #2 NOT INITIALIZED')
      CALL ABORT
C
C
      END
