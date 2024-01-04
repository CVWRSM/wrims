C -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
C
C
      INTEGER    IMXMCP
      INTEGER    IFUNLN
C     PARAMETER (IMXMCP = 300, IFUNLN=80, MAXCHR=200, MAXFUN=50)        HL
C     PARAMETER (IMXMCP = 100, IFUNLN=50, MAXCHR=133, MAXFUN=40)        u
      PARAMETER (IMXMCP = 50, IFUNLN=25, MAXCHR=133, MAXFUN=20)         M
C
      CHARACTER*133 C133
      CHARACTER*90  CLBUFM(IMXMCP+2)
      CHARACTER*(MAXCHR)  CLINE, CKBLIN, CLINSV
      CHARACTER CFUNCT(MAXFUN)*(IFUNLN), CKEY(MAXFUN)
      CHARACTER*4   CSPL
      CHARACTER*60  CPROMT
C
      COMMON /PCHAR/ CLBUFM, CKEY,   CFUNCT, CLINE, CSPL, C133,
     +               CPROMT, CLINSV, CKBLIN
C
C
C -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
