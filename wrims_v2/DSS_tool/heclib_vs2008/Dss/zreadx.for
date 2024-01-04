      SUBROUTINE ZREADX (IFLTAB, CPATH, IIHEAD, KIHEAD, NIHEAD,
     * ICHEAD, KCHEAD, NCHEAD, IUHEAD, KUHEAD, NUHEAD, IDATA,
     * KDATA, NDATA, IPLAN, LFOUND)
C
C     Main routine for retrieving data (ZRDBUF may also be used)
C
C     Written by Bill Charley at HEC, 1989
C
      INTEGER IFLTAB(*), IIHEAD(*), ICHEAD(*), IUHEAD(*), IDATA(*)
      CHARACTER CPATH*(*)
      LOGICAL LFOUND, LEND
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdssiz.h'
C
      INCLUDE 'zdssmz.h'
C
C
      IF (MLEVEL.GE.11) WRITE ( MUNIT, 20) IFLTAB(KUNIT), CPATH,
     * KIHEAD, KCHEAD, KUHEAD, KDATA, IPLAN
 20   FORMAT (T6,'-----DSS---Debug: Enter ZREADX',/,T10,
     * 'UNIT =',I5,'  PATH: ',A,/,T10,'KIHEAD:',I5,',  KCHEAD:',I5,
     * ',  KUHEAD:',I5,',  KDATA:',I5,',  IPLAN:',I4)
C
C
C
C     Get the info block, and read the first portion of the header
      IFLTAB(KRBNPA) = -1
      CALL ZRDBUF (IFLTAB, CPATH, IIHEAD, 0, N, IDATA, 0, J,
     * LEND, IPLAN, LFOUND)
      IF (IFLTAB(KSTAT).NE.0) RETURN
C
      IF (LFOUND) THEN
C
C     Get get any internal header area
      JSIZE = INFO(NPPWRD+KINIHE)
      NIHEAD = JSIZE
      JSIZE = MIN0 (JSIZE, KIHEAD)
      IF (JSIZE.GT.0)
     * CALL ZGTREC (IFLTAB, IIHEAD, JSIZE, INFO(NPPWRD+KIAIHE), .FALSE.)
C
C     Get get the compression header area
      JSIZE = INFO(NPPWRD+KINCHE)
      NCHEAD = JSIZE
      JSIZE = MIN0 (JSIZE, KCHEAD)
      IF (JSIZE.GT.0)
     * CALL ZGTREC (IFLTAB, ICHEAD, JSIZE, INFO(NPPWRD+KIACHE), .FALSE.)
C
C     Get get any user header area
      JSIZE = INFO(NPPWRD+KINUHE)
      NUHEAD = JSIZE
      JSIZE = MIN0 (JSIZE, KUHEAD)
      IF (JSIZE.GT.0)
     * CALL ZGTREC (IFLTAB, IUHEAD, JSIZE, INFO(NPPWRD+KIAUHE), .FALSE.)
      IF (IFLTAB(KSTAT).NE.0) RETURN
C
C     Get the data
      JSIZE = INFO(NPPWRD+KINDAT)
      NDATA = JSIZE
      JSIZE = MIN0 (JSIZE, KDATA)
      IF (JSIZE.GT.0)
     * CALL ZGTREC (IFLTAB, IDATA, JSIZE, INFO(NPPWRD+KIADAT), .FALSE.)
C
      ELSE
C
      NIHEAD = 0
      NCHEAD = 0
      NUHEAD = 0
      NDATA = 0
C
      ENDIF
C
C
      IF (MLEVEL.GE.11) WRITE ( MUNIT,820) NIHEAD, NCHEAD, NUHEAD,
     * NDATA
 820  FORMAT (T6,'-----DSS---Debug: Exit  ZREADX',/,T10,
     * 'NIHEAD:',I5,',  NCHEAD:',I5,',  NUHEAD:',I5,',  NDATA:',I5)
C
      RETURN
C
      END
