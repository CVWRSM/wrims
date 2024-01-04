      SUBROUTINE TEMPNAME (CNAME, IUNIT)
C
C     Generates the name of a temporary file in directory /tmp
C     based on the process id and unit number.
C
      CHARACTER CNAME*(*)
      CHARACTER CTTY*30
C
C     DATA IPID /0/                                                     u
C
C
C     IF (IPID.LE.0) CALL GTUINF (CNAME, CTTY, IPID, IDUM)              u
C     WRITE (CTTY, 20) IUNIT, IPID                                      u
C20   FORMAT ('/tmp/tmp.', I3, '.', I8)                                 u
      WRITE (CTTY, 20) IUNIT                                            M
 20   FORMAT ('SCRATCH.',I3)                                            M
      CNAME = ' '
      CALL REMBLK (CTTY, CNAME, IDUM)
C
      RETURN
      END
