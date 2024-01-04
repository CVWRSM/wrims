      SUBROUTINE ZSRTS ( IFLTAB, CPATH, CDATE, CTIME, NVALS, VALUES,
     * CUNITS, CTYPE, IPLAN, ISTAT)
C
C     Store regular interval time series data short version
C     For data compression, quality flags or user header informaiton
C     use the extended version (ZSRTSX)
C
C
      INTEGER IFLTAB(*)
      CHARACTER CPATH*(*), CDATE*(*), CTIME*(*), CUNITS*(*), CTYPE*(*)
      REAL VALUES(*)
      INTEGER NVALS, NHEAD, IPLAN, ISTAT
C
      COMMON /ZDSSCP/ JCOMP, BASEV, LBASEV, LDHIGH, NPREC
      LOGICAL LBASEV, LDHIGH
C
C
      NHEAD = -1
      JCOMP = 0
      BASEV = 0.0
      LBASEV = .FALSE.
      LDHIGH = .FALSE.
C
      CALL ZSRTSX ( IFLTAB, CPATH, CDATE, CTIME, NVALS, VALUES,
     * IQUAL, .FALSE., CUNITS, CTYPE, IHEAD, NHEAD, IPLAN,
     * JCOMP, BASEV, LBASEV, LDHIGH, NPREC, ISTAT)
C
C
      RETURN
      END
