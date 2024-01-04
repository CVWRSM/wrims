      SUBROUTINE GSTRRG (CNAME, CSTR, NSTR, ISTAT)
C
C
      CHARACTER CNAME*(*), CSTR*(*)
C     CHARACTER CRNAME*3                                                H
C     INTEGER IRNAME                                                    H
C     EQUIVALENCE (IRNAME,CRNAME)                                       H
C     COMMON /RGBUFF/ IBUFF(88)                                         H
C
      CHARACTER CN*4, CC*64, CT*1, C*1                                  Mu
      CHARACTER CREGFI*64, CLINE*80                                     Mu
C     CHARACTER CUSER*40, CTTY*40                                       u
      LOGICAL LEXIST                                                    Mu
      CHARACTER CCALL*4, CTEST*4, CTS*1, CCS*64                         Mu
C
C
C
C     CRNAME = CNAME(1:3)                                               H
C     CALL VGTRGC (IRNAME, 88, IBUFF, ISTAT)                            H
C
C     IF ( ISTAT .EQ. 0 ) THEN                                          H
C     NSTR = IBUFF(3)                                                   H
C     ILEN = LEN(CSTR)                                                  H
C     JLEN = MIN0 ( NSTR, ILEN )                                        H
C     CALL HOLCHR (IBUFF,10, JLEN, CSTR, 1)                             H
C     ELSE                                                              H
C     NSTR = 0                                                          H
C     ENDIF                                                             H
C
C
C
C------ Which call, then service all, save some code space!!
C
      CCALL = 'GSTR'                                                    Mu
      GO TO 1                                                           Mu
C------
      ENTRY SSTRRG ( CNAME, CSTR, NSTR, ISTAT )                         Mu
      CCALL = 'SSTR'                                                    Mu
      GO TO 1                                                           Mu
C------
      ENTRY GNUMRG ( CNAME, NRANGE, NVALUE, ISTAT )                     Mu
      CCALL = 'GNUM'                                                    Mu
      GO TO 1                                                           Mu
C------
      ENTRY SNUMRG ( CNAME, NRANGE, NVALUE, ISTAT )                     Mu
      CCALL = 'SNUM'                                                    Mu
C------
 1    CONTINUE                                                          Mu
C
C
C     Get the register file name.
      CALL GETSUP (' ', CLINE, N)                                       M
      CREGFI = CLINE(1:N) // 'REGISTER.FIL'                             M
C
C     On UNIX, the register file is in \tmp and has the parent
C     process ID attached to the name.
C     Get the parent process ID.
C     CALL GETPID (CUSER, CTTY, IPID, IPPID)                            u
C     CUSER = '/tmp/register.'                                          u
C     CALL INTGRC (IPPID, CUSER, 15, 7)                                 u
C     CREGFI = ' '                                                      u
C     CALL REMBLK (CUSER, CREGFI, N)                                    u
C
C
C     If we are getting, see if the file exists
      IF (CCALL(1:1).EQ.'G') THEN                                       Mu
      INQUIRE (FILE=CREGFI, EXIST=LEXIST)                               Mu
      IF (.NOT.LEXIST) GO TO 910                                        Mu
      ENDIF                                                             Mu
C
C
C     Obtain some unit numbers to use.  Don't conflict with
C     a program's use of unit number.
      IRUNIT = -1                                                       Mu
C     Do we need to open a scratch file?
      IF (CCALL(1:1).EQ.'S') THEN                                       Mu
      ITUNIT = -1                                                       Mu
      ELSE                                                              Mu
      ITUNIT = 1                                                        Mu
      ENDIF                                                             Mu
C     Try unit numbers from 40 to 70.  (Typically, this will not loop!)
      DO 10 I=40,70                                                     Mu
      INQUIRE (UNIT=I, OPENED=LEXIST)                                   Mu
      IF (.NOT.LEXIST) THEN                                             Mu
      IF (IRUNIT.LT.0) THEN                                             Mu
      IRUNIT = I                                                        Mu
      ELSE IF (ITUNIT.LT.0) THEN                                        Mu
      ITUNIT = I                                                        Mu
      ENDIF                                                             Mu
      ENDIF                                                             Mu
      IF ((IRUNIT.GT.0).AND.(ITUNIT.GT.0)) GO TO 15                     Mu
 10   CONTINUE                                                          Mu
      GO TO 910                                                         Mu
C
C
 15   CONTINUE                                                          Mu
      OPEN ( IRUNIT, FILE=CREGFI, IOSTAT=IST)                           Mu
      IF (IST.NE.0) GO TO 900                                           Mu
C
C
      CTEST = CNAME                                                     Mu
      CALL UPCASE ( CTEST )                                             Mu
      NSTRX = NSTR                                                      Mu
      IF ( NSTRX .LT. 0 ) NSTRX = LEN ( CSTR )                          Mu
      NSTRX = MIN0 ( 64, NSTRX )                                        Mu
C
C
C------ Now check if a SET call
C
C     WRITE(*,*) CCALL,':::',CTEST,':::',CSTR,'::',CNAME,'::'           D
C     WRITE(*,*) NSTRX                                                  D
      IF ( CCALL(1:1) .EQ. 'S' ) THEN                                   Mu
C------ Fix up STR and NUM formats
      IF ( CCALL(2:4) .EQ. 'STR' ) THEN                                 Mu
      CTS = 'S'                                                         Mu
      CCS = CSTR                                                        Mu
      ELSE                                                              Mu
      WRITE (CCS,   20) NRANGE, NVALUE                                  Mu
 20   FORMAT ( 1X, I14.14, 1X, I14.14 )                                 Mu
      CTS = 'N'                                                         Mu
      NSTRX = 30                                                        Mu
      ENDIF                                                             Mu
C------
      IF ( NSTRX .EQ. 0 ) CTS = 'Z'                                     Mu
C
C------ Now update file
C
      OPEN ( ITUNIT, STATUS='SCRATCH')                                  Mu
 30   CONTINUE
      READ ( IRUNIT, 40, END=60 ) C, CN, CT, NC, CC                     Mu
 40   FORMAT ( A,A,1X,A,I2,1X,A)                                        Mu
      IF ( CTEST .NE. CN ) THEN                                         Mu
      WRITE (ITUNIT, 50) C, CN, CT,  NC, CC                             Mu
 50   FORMAT (A,A,':',A,I2.2,':',A)                                     Mu
      ENDIF                                                             Mu
      GO TO 30                                                          Mu
C------
 60   CONTINUE
      IF ( CTS .NE. 'Z' ) THEN                                          Mu
      WRITE (ITUNIT, 50) ' ', CTEST, CTS, NSTRX, CCS                    Mu
      ENDIF                                                             Mu
C
      REWIND (UNIT=ITUNIT)                                              Mu
      REWIND (UNIT=IRUNIT)                                              Mu
 70   CONTINUE                                                          Mu
      READ (ITUNIT, 80, END=90) CLINE                                   Mu
 80   FORMAT (A)                                                        Mu
      CALL CHRLNB (CLINE, N)                                            Mu
      IF (N.EQ.0) N = 1                                                 Mu
      WRITE (IRUNIT, 80) CLINE(1:N)                                     Mu
      GO TO 70                                                          Mu
C
 90   CONTINUE                                                          Mu
      CLOSE ( ITUNIT, STATUS='DELETE')                                  Mu
      CLOSE ( IRUNIT )                                                  Mu
      ISTAT = 0                                                         Mu
C
      RETURN                                                            Mu
      ENDIF                                                             Mu
C
C------  Below here for GET calls only
C
 100  CONTINUE
      READ ( IRUNIT, 40, END=900) C, CN, CT, NC, CC                     Mu
      IF ( CTEST .EQ. CN ) THEN                                         Mu
      IF     ( CCALL .EQ. 'GSTR' .AND. CT .EQ. 'S' ) THEN               Mu
      CSTR = CC                                                         Mu
      NSTR = NC                                                         Mu
      ELSEIF ( CCALL .EQ. 'GNUM' .AND. CT .EQ. 'N' ) THEN               Mu
      READ ( CC, 20, ERR=900) NRANGE, NVALUE                            Mu
      ELSE                                                              Mu
      GO TO 900                                                         Mu
      ENDIF                                                             Mu
      ISTAT = 0                                                         Mu
      CLOSE ( IRUNIT )                                                  Mu
      RETURN                                                            Mu
      ELSE                                                              Mu
      GO TO 100                                                         Mu
      ENDIF                                                             Mu
C
C------ No entry found or error
 900  CONTINUE                                                          Mu
      CLOSE (UNIT=IRUNIT)                                               Mu
 910  CONTINUE                                                          Mu
      IF (CCALL(2:4).EQ.'NUM') THEN                                     Mu
      NRANGE = -9999                                                    Mu
      NVALUE = -9999                                                    Mu
      ELSE                                                              Mu
      CSTR = ' '                                                        Mu
      NSTR = 0                                                          Mu
      ENDIF                                                             Mu
      ISTAT = -1                                                        Mu
C
      RETURN
      END
