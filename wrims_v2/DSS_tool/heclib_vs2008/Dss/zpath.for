      SUBROUTINE ZPATH (CA, CB, CC, CD, CE, CF, CPATH, NPATH)
C
C     ZPATH forms a pathname from the input pathname parts.
 
C
C     CA, CB, CC, CD, CE, CF - Input character strings
C     CPATH -     Pathname character string returned
C     NPATH -     Length of pathname string, also used as an indexer
C
C     Written by John Miwa at HEC, 1988.
C
C
      INCLUDE 'zdssnz.h'
C
C     Declare variables
         CHARACTER CA*(*),CB*(*),CC*(*),CD*(*),CE*(*),CF*(*)
         CHARACTER CPART(6)*64, CPATH*(*)
         INTEGER IBEG, IEND
C
C
C
C     Fill pathname string with blanks
      CPATH = ' '
C
C     Fill dummy character array
      CPART(1) = CA
      CPART(2) = CB
      CPART(3) = CC
      CPART(4) = CD
      CPART(5) = CE
      CPART(6) = CF
C
C
      MAXLEN = MIN0(LEN(CPATH),MAXPATH)
C
      NPATH = 1
      DO 40 I=1,6
C
C     Get the beginning and ending positions of each pathname part
      CALL CHRFLB (CPART(I), IBEG, IEND)
C     Calculate string lengths
      IF (IEND.EQ.0) THEN
      ILEN = 0
      ELSE
      ILEN = IEND - IBEG + 1
C     Check for illegal characters
      DO 10 J=IBEG,IEND
         JCHAR = ICHAR (CPART(I)(J:J))
         IF (JCHAR.LT.32) CPART(I)(J:J) = '?'
         IF (CPART(I)(J:J).EQ.'/') CPART(I)(J:J) = '?'
 10   CONTINUE
      ENDIF
C
C     Place Slash in path
      CPATH (NPATH:NPATH) = '/'
C
      IF ((NPATH+ILEN).LT.MAXLEN) THEN
C
C     Add pathname part to pathname
      IF (ILEN.NE.0) CPATH(NPATH+1:) = CPART(I)(IBEG:IEND)
C
C     Compute new pathname length
      NPATH = NPATH + ILEN + 1
C
      ELSE
C
C     Extreemly rare to execute the following code.
C     If we have reached maximum length, finish slashes
      JLEN = LEN(CPATH)
      IF (JLEN.GT.MAXLEN) JLEN = MAXLEN
      CPATH(NPATH+1:JLEN) = CPART(I)(IBEG:)
C     Add ending slashmarks
      DO 20 J=1,7-I
      CPATH(JLEN:JLEN) = '/'
      JLEN = JLEN - 1
 20   CONTINUE
      NPATH = MAXLEN
      GO TO 60
      END IF
C
 40   CONTINUE
C
C     Add final slashmark
      CPATH(NPATH:NPATH)= '/'
C
 60   CONTINUE
      RETURN
C
      END
