      SUBROUTINE WAITS (SECS)                                           MLH
C
C     WAIT THE SPECIFIED # OF SECONDS

C
	USE  DFLIB

	Real SECS
	Integer Millisecs
C
	Millisecs = INT(SECS * 1000.0)
	call SLEEPQQ (Millisecs)
C

      RETURN                                                            MLH
      END                                                               MLH
