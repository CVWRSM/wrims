      SUBROUTINE ZINQIR ( IFLTAB, CFLG, CALPHA, INUMB)
C
C
C     Inquire about settings in a DSS file or a record
C
C     Written by Bill Charley at HEC, 1988.
C
      INTEGER IFLTAB(*)
      CHARACTER CFLG*(*), CALPHA*(*)
      CHARACTER CTEMP*128, CFLAG*4
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdsslz.h'
C
      INCLUDE 'zdssnz.h'
C
      INCLUDE 'zdssiz.h'
C
      INCLUDE 'zdsscz.h'
C
      INCLUDE 'zdssmz.h'
C
      INCLUDE 'zdssts.h'
C
      COMMON /WORDS/ IWORD(10)
C
      COMMON /ZDSSFZ/ LFIRST
      LOGICAL LFIRST
      INTEGER IFPOS, ISTAT
C
C
C
      IF (LFIRST) THEN
      CALL ZINIT
      LFIRST = .FALSE.
      ENDIF
C
      CFLAG = CFLG
C
      IF (MLEVEL.GE.15) WRITE (MUNIT,20) CFLAG
 20   FORMAT (T10,'-----DSS---Debug:  Enter ZINQIR;  Flag: -',A,'-')
C
C
C     UNIT: UNIT NUMBER OF DSS FILE
      IF (CFLAG.EQ.'UNIT') THEN
      INUMB = IFLTAB(KUNIT)
C
C     MLEVEL: MESSAGE LEVEL
      ELSE IF ((CFLAG.EQ.'MLEV').OR.(CFLAG.EQ.'MLVL')) THEN
      INUMB = MLEVEL
C
C     PREC: Data precision of last record read
      ELSE IF (CFLAG.EQ.'PREC') THEN
      IF (INFO(KIFLAG).EQ.NPFLAG) THEN
      INUMB = INFO(NPPWRD+KIPREC)
      ELSE
      INUMB = 0
      ENDIF
C
C     MUNIT: MESSAGE UNIT
      ELSE IF ((CFLAG.EQ.'MUNI').OR.(CFLAG.EQ.'MLFN')) THEN
      INUMB = MUNIT
C
C     NAME: NAME OF DSS FILE
      ELSE IF (CFLAG.EQ.'NAME') THEN
      CTEMP = ' '
C     IUNIT = IFLTAB(KUNIT)
C     CALL GETNAM ( IUNIT, CALPHA, INUMB)
      CALL HOL2CH (IFLTAB(KNAME), CTEMP, 16)
      CALPHA = CTEMP
C
C     RDATE: RECORD DATE
      ELSE IF (CFLAG.EQ.'RDAT') THEN
      CTEMP = ' '
      CALL HOLCHR (INFO(NPPWRD+KIDATE), 1, NDATEC, CTEMP, 1)
      CALPHA = CTEMP
C
C     RTIME: RECORD TIME
      ELSE IF (CFLAG.EQ.'RTIM') THEN
      CTEMP = ' '
      CALL HOLCHR (INFO(NPPWRD+KITIME), 1, NTIMEC, CTEMP, 1)
      CALPHA = CTEMP
C
C     PROG: PROGRAM
      ELSE IF (CFLAG.EQ.'PROG') THEN
      CTEMP = ' '
      CALL HOLCHR (INFO(NPPWRD+KIPROG), 1, NPROGC, CTEMP, 1)
      CALPHA = CTEMP
C
C     RVERS: RECORD VERSION
      ELSE IF (CFLAG.EQ.'RVER') THEN
      INUMB = INFO(NPPWRD+KIVER)
C
C
C     Get the time zone offset for the last record read
      ELSE IF (CFLAG.EQ.'ZONE') THEN
         INUMB = IRTZONE
C        The time zone identifier can contain up to 12 characters
C        Examples include 'PST', 'GMT+0800'
         CALPHA = CRTZONE
C
C
C     FLAGS: RECORD DATA FLAGS STORED
      ELSE IF ((CFLAG.EQ.'FLAG').OR.(CFLAG.EQ.'QUAL')) THEN
      INUMB = INFO(NPPWRD+KIQUAL)
C
C     FDATE: FILE DATE (CREATION DATE)
      ELSE IF (CFLAG.EQ.'FDAT') THEN
      CTEMP = ' '
      CALL HOLCHR (IFLTAB(KCREAT), 1, NDATEC, CTEMP, 1)
      CALPHA = CTEMP
C
C     FVERS: FILE VERSION
      ELSE IF (CFLAG.EQ.'FVER') THEN
      CTEMP = ' '
      CALL HOLCHR (IFLTAB(KVERS), 1, NVERSC, CTEMP, 1)
      CALPHA = CTEMP
      INUMB = IFLTAB(KVERNO)
C
C     TABLE:  Dynamic or Stable Hash Table
      ELSE IF (CFLAG.EQ.'TABL') THEN
      INUMB = IFLTAB(KTABLE)
      IF (IFLTAB(KTABLE).EQ.1) THEN
      CALPHA = 'DYNAMIC'
      ELSE
      CALPHA = 'STABLE'
      ENDIF
C
      ELSE IF (CFLAG.EQ.'HSIZ') THEN
      INUMB = IFLTAB(KHSIZE)
C
C     NREC: NUMBER OF RECORDS
      ELSE IF (CFLAG.EQ.'NREC') THEN
      CALL ZRDPRM (IFLTAB, .FALSE.)
      INUMB = IFLTAB(KNRECS)
C
C     SIZE: FILE SIZE (IN KBYTES)
      ELSE IF (CFLAG.EQ.'SIZE') THEN
C     DETERMINE THE NUMBER OF WORDS IN THE FILE
      CALL ZRDPRM (IFLTAB, .FALSE.)
      FILSIZ = REAL(IFLTAB(KFSIZE)) -1.
      FILSIZ = FILSIZ * (512./508.)
C     GET THE NUMBER OF KILOBYTES
      FILSIZ = (FILSIZ/1000.) * REAL(IWORD(2))
      INUMB = FILSIZ
C
C     DEAD: PRECENTAGE DEAD SPACE
      ELSE IF (CFLAG.EQ.'DEAD') THEN
      CALL ZRDPRM (IFLTAB, .FALSE.)
      DEAD = REAL(IFLTAB(KDEAD))
      FILSIZ = REAL(IFLTAB(KFSIZE)) -1.
      DEADS = (DEAD/FILSIZ) * 100.
      INUMB = DEADS
C
C     VERSION: DSS SOFTWARE VERSION
      ELSE IF (CFLAG.EQ.'VERS') THEN
      CALPHA = CVERS
      INUMB = IFLTAB(KNV)
C
C     TYPE:  DATA TYPE (E.G. PARIED DATA)
      ELSE IF (CFLAG.EQ.'TYPE') THEN
      IDTYPE = IFLTAB(KDTYPE)
      INUMB = IDTYPE
C     Get the character record data type
      DO 40 I=1,NRTYPE
         IF (IDTYPE.EQ.IRTYPE(I)) THEN
            CALPHA = CRTYPE(I)
            GO TO 50
         ENDIF
 40   CONTINUE
      CALPHA = 'UND'
 50   CONTINUE
C
C     80COL:  80 COLUMN OUTPUT ON OR OFF (VS. 132 COL)
      ELSE IF (CFLAG.EQ.'80CO') THEN
      IF (L80COL) THEN
      CALPHA = 'ON'
      INUMB = 1
      ELSE
      CALPHA = 'OFF'
      INUMB = 0
      ENDIF
C
C     TAG:  TAG OF LAST RECORD ACCESSED
      ELSE IF (CFLAG(1:3).EQ.'TAG') THEN
      CTEMP = ' '
      IF ((JPNBIN.GE.1).AND.(NPPWRD.GT.1)) THEN
      CALL HOLCHR (IPNBIN(JPNBIN+NPPWRD+KBTAG), 1, NTAGC, CTEMP, 1)
      ENDIF
      CALPHA = CTEMP
C
C     Read Only Access
      ELSE IF (CFLAG.EQ.'READ') THEN
      IF (IFLTAB(KREADO).EQ.1) THEN
      CALPHA = 'ON'
      INUMB = 1
      ELSE
      CALPHA = 'OFF'
      INUMB = 0
      ENDIF
C
C     Abort on fatal error (should be yes!)
      ELSE IF (CFLAG.EQ.'ABOR') THEN
      IF (.NOT.LNOABT) THEN
      CALPHA = 'ON'
      INUMB = 1
      ELSE
      CALPHA = 'OFF'
      INUMB = 0
      ENDIF
C
C     Multi user state
      ELSE IF (CFLAG.EQ.'MULT') THEN
      INUMB = IFLTAB(KMULT)
      CALPHA = ' '
C
C     Status of operation
      ELSE IF (CFLAG.EQ.'STAT') THEN
      INUMB = IFLTAB(KSTAT)
C
C     Clear error / status flags
      ELSE IF (CFLAG.EQ.'CLEA') THEN
         IFLTAB(KSTAT) = INUMB
         IERRMS = INUMB
         CERRMS = CALPHA
C
C     If a system error occurred, the system error message
C     and number associated with that error
      ELSE IF (CFLAG.EQ.'ERRO') THEN
         INUMB = IERRMS
         CALPHA = CERRMS
C
C     System error index location in IFLTAB
      ELSE IF (CFLAG.EQ.'KSTA') THEN
         INUMB = KSTAT
C
C     Should we observe the Quality Protect bit in flags
C     when storing time series data
      ELSE IF (CFLAG.EQ.'QPRO') THEN
         IF (LQPBIT) THEN
            CALPHA = 'ON'
            INUMB = 1
         ELSE
            CALPHA = 'OFF'
            INUMB = 0
         ENDIF
C
C     Remote file
      ELSE IF (CFLAG.EQ.'REMO') THEN
      IF (IFLTAB(KREMOTE).EQ.1) THEN
      CALPHA = 'ON'
      INUMB = 1
      ELSE
      CALPHA = 'OFF'
      INUMB = 0
      ENDIF
C
C     Number of deleted records in file
      ELSE IF (CFLAG.EQ.'DELE') THEN
         IFPOS = 0
         INUMB = 0
 60      CONTINUE
            CALL ZPLIST(IFLTAB, 'DELETE', IFPOS, CKPATH, NP, ISTAT)
            IF (ISTAT.EQ.0) THEN
               INUMB = INUMB + 1
               GO TO 60
            ENDIF
C
C
C
      ELSE
      IF (MLEVEL.GE.4) WRITE (MUNIT,80) CFLG
 80   FORMAT(' ----ZINQIR - Unrecognized Inquire: ',A)
      INUMB = -1
      CALPHA = ' '
      ENDIF
C
C
C
 800  CONTINUE
      IF (MLEVEL.GE.15) THEN
      CALL CHRLNB (CALPHA, N)
      IF (N.EQ.0) N = 1
      IF (MLEVEL.GE.15) WRITE (MUNIT,820) INUMB, CALPHA(1:N)
 820  FORMAT (T10,'-----DSS---Debug:  Exit ZINQIR',/,
     * T10,'Number:',I8,',  Character: ',A)
      ENDIF
      RETURN
C
      END
