C     Last change:  DM3  23 Jan 98    1:46 pm
      SUBROUTINE CHRIO1
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
      PARAMETER (IBLEN=256)                                             Mu
C
C     INTEGER IARRAY(9)                                                 L
C     INTEGER*4 IARRAY(9)                                               L3
      CHARACTER*(*) CSTR, CSTR1,CSTR2,CSTR3,CSTR4,CSTR5                 MLu
      CHARACTER*(*) CTAG, CACT                                          u
      CHARACTER CSTRM*127                                               MLu
      CHARACTER CBUFF*(IBLEN)                                           Mu
C     INTEGER              JBUFF(86)                                    H
C     JBUFF(86) X 3 = 258 CHARACTERS.
      LOGICAL LINIT1, L8BIT
C     DATA LINIT1 /.FALSE./
      DATA L8BIT / .FALSE. /
C
      DATA IBPOS,IBNUM  /0,0/                                           Mu
C
      ENTRY CHRIT1(ILFN,IBUFF,NBUFF)
C     INTEGER IBUFF(NBUFF)                                              H
C     INITIALIZE CHARACTER CHANNEL #1
C
      LINIT1 = .TRUE.
C     JLFN=ILFN                                                         H
C     CALL GIOPLW(JLFN,'51,IBUFF,NBUFF,JSTAT)                           H
      GO TO 1000
C
C
      ENTRY CHRSE1 ( CTAG, CACT, IACT )
c     CHARACTER*(*) CTAG, CACT                                          HL
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
      ENTRY CHRFN1
C     FINISH CHARACTER CHANNEL #1
C
      LINIT1 = .FALSE.
C     CALL GIOPSW(JLFN,'24,JSTAT)                                       H
      GO TO 1000
C
C
      ENTRY CHRFL1
C     FLUSH CHARACTER CHANNEL #1 TYPE AHEAD BUFFER
C     CALL GIOPSW(JLFN,'54,JSTAT)                                       H
      CALL STDINC ('N','N','N','Y',J,K)                                 Mu
      GO TO 1000
C
C
C
      ENTRY CHRFL ( JLFNX )
C     FLUSH CHARACTER  TYPE AHEAD BUFFER
C     CALL GIOPSW(JLFNX,'54,JSTAT)                                      H
      CALL STDINC ('N','N','N','Y',J,K)                                 Mu
      GO TO 1000
C
C
      ENTRY CHRBK1(CSTR,NSTR)
C     CHARACTER*(*) CSTR                                                H
C     BACKSTORE ON CHANNEL #1
C     CALL CHRHOL(CSTR,1,NSTR,JBUFF,1)                                  H
C     CALL GIOPLW(JLFN,'27,JBUFF,NSTR,JSTAT)                            H
      DO 110 I=NSTR,1,-1                                                Mu
      CBUFF(IBPOS+1:IBPOS+1) = CSTR(I:I)                                Mu
      IBPOS = MOD(IBPOS+1,IBLEN)                                        Mu
      IBNUM = IBNUM+1                                                   Mu
 110  CONTINUE                                                          Mu
      GO TO 1000
C
C
C
      ENTRY CHRST1(ISTAT1,KSTAT1)
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
      ENTRY CHRSI1(ISTAT,KSTAT)
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
      ENTRY CHRWT1(CSTR1,NSTR1)
C     CHARACTER*(*) CSTR1                                               H
C     WRITE CHARACTERS TO CHANNEL #1
C     IF (.NOT.LINIT1) GOTO 2000                                        H
C     IF(NSTR1.EQ.0) GO TO 1000                                         H
      MSTR1 = NSTR1
      IF(NSTR1.LT.0) MSTR1 = LEN ( CSTR1 )
C     CALL CHRHOL(CSTR1,1,MSTR1,JBUFF,1)                                H
C     CALL GIOPLW(JLFN,'52,JBUFF,MSTR1,JSTAT)                           H
C     GO TO 1000                                                        H
      CSTRM = CSTR1                                                     MLu
      NSTRM = MSTR1                                                     MLu
      GOTO 120                                                          MLu
C
C
C
      ENTRY CHRWT (KLFN,CSTR2,NSTR2)
C     CHARACTER*(*) CSTR2                                               H
C     WRITE CHARACTERS TO UNIT KLFN
C     IF(NSTR2.EQ.0) GO TO 1000                                         H
      MSTR2 = NSTR2
      IF(NSTR2.LT.0) MSTR2 = LEN ( CSTR2 )
C     CALL CHRHOL(CSTR2,1,MSTR2,JBUFF,1)                                H
C     CALL GIOPLW(KLFN,'52,JBUFF,MSTR2,JSTAT)                           H
      CSTRM = CSTR2                                                     ML
      NSTRM = MSTR2                                                     ML
      GOTO 120                                                          ML
C     WRITE (KLFN, 151) CSTR2(1:MSTR2)                                  u
C     GO TO 1000                                                        Hu
C
C
      ENTRY CHRWI1(CSTR3,NSTR3)
C     CHARACTER*(*) CSTR3                                               H
C     DO AN IMMEDIATE WRITE WITHOUT WAITING FOR COMPLETION
C     THIS MUST BE FOLLOWED BY A STATUS CALL BEFORE OTHER I/O
C
C     WRITE CHARACTERS TO CHANNEL #1
C     IF (.NOT.LINIT1) GOTO 2000                                        H
C     IF(NSTR3.LT.1) GO TO 1000                                         H
C     CALL CHRHOL(CSTR3,1,NSTR3,JBUFF,1)                                H
C     CALL GIOPL (JLFN,'52,JBUFF,NSTR3,JSTAT)                           H
      CSTRM = CSTR3                                                     MLu
      NSTRM = NSTR3                                                     MLu
 120  IF(NSTRM.LT.1) GO TO 1000                                         MLu
      DO 150 I=1,NSTRM                                                  ML
      CALL STDOUT ('Y',ICHAR(CSTRM(I:I)))                               M
C     IARRAY(1) = 512                                                   L
C     IARRAY(4) = ICHAR(CSTRM(I:I))                                     L
C     CALL INTRUP (IARRAY,33)                                           L
 150  CONTINUE                                                          ML
C     WRITE (6, 151) CSTRM(1:NSTRM)                                     u
C151  FORMAT (A,$)                                                      lms
C151  FORMAT (' ',A,$)                                                  g
      GO TO 1000
C
C
      ENTRY CHRRD1(CSTR4,NSTR4)
C     CHARACTER*(*) CSTR4                                               H
C     IF (.NOT.LINIT1) GOTO 2000                                        H
C     READ CHARACTERS FROM CHANNEL #1, WAIT FOR AT LEAST ONE CHARACTER
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
      IF (IBNUM.NE.0) THEN                                              Mu
      DO 202 I=1,IBNUM                                                  Mu
      CSTR4(I:I) = CBUFF(MOD(IBPOS+IBLEN,IBLEN):                        Mu
     +   MOD(IBPOS+IBLEN,IBLEN))                                        Mu
      IBPOS = MOD(IBPOS-1+IBLEN,IBLEN)                                  Mu
      NSTR4 = NSTR4+1                                                   Mu
 202  CONTINUE                                                          Mu
      IBNUM = 0                                                         Mu
      CALL STDINC ('N','N','N','N',JCHAR,K)                             Mu
      GOTO 220                                                          Mu
      ELSE                                                              Mu
      NSTR4 = 0                                                         Mu
      ENDIF                                                             Mu
 210  CALL STDINC ('Y','N','Y','N',JCHAR,K)                             Mu
C
C         Wait for at least one character
C
 220  IF (JCHAR .GT. 0) THEN                                            Mu
C
C         Add character to string
C
         NSTR4 = NSTR4 + 1                                              Mu
         CSTR4 (NSTR4:NSTR4) = CHAR(JCHAR)                              Mu
         CALL STDINC ('N','N','N','N',JCHAR,K)                          Mu
         GOTO 220                                                       Mu
      ENDIF                                                             Mu
      GOTO 1000
C
C
C
      ENTRY CHRRI1(CSTR5,NSTR5)
C     CHARACTER*(*) CSTR5                                               H
C     IF (.NOT.LINIT1) GOTO 2000                                        H
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
      NSTR5 = 0                                                         Mu
      IF (IBNUM.NE.0) THEN                                              Mu
      DO 302 I=1,IBNUM                                                  Mu
      CSTR5(I:I) = CBUFF(MOD(IBPOS+IBLEN,IBLEN):                        Mu
     +   MOD(IBPOS+IBLEN-1,IBLEN))                                      Mu
      IBPOS = MOD(IBPOS+IBLEN,IBLEN)                                    Mu
      NSTR5 = NSTR5+1                                                   Mu
 302  CONTINUE                                                          Mu
      IBNUM = 0
      ELSE                                                              Mu
      NSTR4 = 0                                                         Mu
      ENDIF                                                             Mu
 310  CALL STDINC('N','N','N','N',JCHAR,K)                              Mu
C
 320  IF (JCHAR .GT. 0) THEN                                            Mu
C
C         Add character to string
C
         NSTR5 = NSTR5 + 1                                              Mu
         CSTR (NSTR5:NSTR5) = CHAR(JCHAR)                               Mu
         CALL STDOUT ('Y',JCHAR)                                        Mu
         GOTO 310                                                       Mu
      ENDIF                                                             Mu
C
C
1000  CONTINUE
      RETURN
C
 2000 CONTINUE
C     WRITE (3,2010)                                                    H
C2010 FORMAT (/' ERROR - CHANNEL #1 NOT INITIALIZED')                   H
C     CALL ABORT                                                        H
C
C
      END
