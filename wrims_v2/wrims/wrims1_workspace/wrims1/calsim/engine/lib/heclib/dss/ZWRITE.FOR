      SUBROUTINE ZWRITE ( IFLTAB, CPATH, NPATH, IUHEAD, NUHEAD,
     * IDATA, NDATA, IPLAN, LFOUND)
C
C     Replaced by ZWRITX
C     This stores short integer words.  For long words, call ZWRITX
C
C     IPLAN = 0   Always write
C     IPLAN = 1   Only write if new record
C     IPLAN = 2   Only write if old record
C
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdssiz.h'
C
      INCLUDE 'zdssmz.h'
C
      COMMON /WORDS/ IWORD(10)
C
      INTEGER IFLTAB(*), IIHEAD(2)
      INTEGER IUHEAD(*), IDATA(*)
      CHARACTER CPATH*(*)
      LOGICAL LFOUND
C
C
C
      IIHEAD(1) = NUHEAD
      IIHEAD(2) = NDATA
C
      ND = ((NDATA  - 1) / IWORD(8)) + 1
      NH = ((NUHEAD - 1) / IWORD(8)) + 1
C
      JTYPE = ITYPE
C
      CALL ZWRITX ( IFLTAB, CPATH, NPATH, IIHEAD, 2, ICHEAD, 0,
     * IUHEAD, NH, IDATA, ND, JTYPE, IPLAN, ISTAT, LFOUND)
C
      RETURN
      END
