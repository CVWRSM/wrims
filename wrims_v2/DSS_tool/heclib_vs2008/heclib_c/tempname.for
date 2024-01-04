      SUBROUTINE TEMPNAME (CNAME, IUNIT)
C
C     Generates the name of a temporary file in directory /tmp
C     based on the process id and unit number.
C
      !USE IFPORT,ONLY: CHRLNB
C
      CHARACTER CNAME*(*)
      CHARACTER CTTY*200
C
C     DATA IPID /0/                                                     u
C
C
      CTTY = ' '
C     IF (IPID.LE.0) CALL GTUINF (CNAME, CTTY, IPID, IDUM)              u
C     WRITE (CTTY, 20) IUNIT, IPID                                      u
C20   FORMAT ('/tmp/tmp.', I3.3, '.', I8.8)                             u
C
C
      IDUM = GETENVQQ('TEMP', CTTY)                                     M
      CALL CHRLNB(CTTY, N)                                              M
      WRITE (CTTY(N+1:), 20) IUNIT                                      M
 20   FORMAT ('\scratch.',I3.3)                                         M
      CNAME = CTTY      
C
      RETURN
      END
