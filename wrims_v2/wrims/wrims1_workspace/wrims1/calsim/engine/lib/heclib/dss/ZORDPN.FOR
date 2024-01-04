      SUBROUTINE ZORDPN (LSELCA, LERR)
C
C     Read pathnames from a catalog file, then call ZSELCA,
C     which will select pathname based on input parameters.
C
C     Written by Bill Charley at HEC, 1983.
C
      LOGICAL LMATCH, LSELCA, LEND, LERR
      CHARACTER CTAG*8, CDUM*1, CPATH*80
C
C
      INCLUDE 'zdssca.h'
C
      INCLUDE 'zdsscc.h'
C
      INCLUDE 'zdsscm.h'
C
      COMMON /ZDSSC1/ CPDATE, CPPROG
      CHARACTER CPDATE*7, CPPROG*6
C
C
C
      IF (LSORT) LTWCAT = .FALSE.
      IF (LTWCAT) THEN
      OPEN (UNIT=68,FILE='SCRATCHC.CAT',ACCESS='DIRECT',
     *RECL=80,IOSTAT=I)
      IF (I.NE.0) LTWCAT = .FALSE.
      ENDIF
C
      INUMB = 0
      IPOS = 0
      NTWPAT = 0
      CTAG = ' '
C
      NOPTHS = 0
      REWIND (UNIT=JNUNIT)
C     LOOP
 20   CONTINUE
      INUMB = INUMB + 1
      CALL ZRDPAT (JNUNIT, IPOS, INUMB, CTAG, CPATH, NPATH, LEND)
      IF (LEND) GO TO 800
      CALL ZSELCA (CPATH, NPATH, CTAG, CPPROG, CPDATE, CDUM, CDUM,
     * IDUM, IDUM, IDUM, INUMB, LSELCA, LMATCH, .FALSE., LERR)
      IF (LERR) GO TO 800
      IF (LMATCH) NOPTHS = IPOS
      GO TO 20
C
 800  CONTINUE
      IF (LTWCAT) CLOSE (UNIT=68, STATUS='DELETE')
      LTWCAT = .FALSE.
      RETURN
      END
