      SUBROUTINE CHRLNB (CLINE,ILAST)                                   MLup
C
C     FINDS THE LAST NON-BLANK CHARACTER IN CHARACTER ARRAY CLINE
C
      CHARACTER CLINE*(*)                                               MLup
C
C     ILEN = LEN(CLINE)                                                 M
C     ILAST = NINDXC ( CLINE, ILEN, 1, ' ')                             M
C
      ILAST = NINDXR (CLINE, ' ')                                       Lup
C
      RETURN                                                            MLup
      END                                                               MLup
