C     ---------------------------------------
C
C     Internal record buffers
      PARAMETER (MXBUFF=6)
      INTEGER IBUFF(NBSIZE,MXBUFF)
      COMMON /ZDSSBZ/ IBUFF, JCREC(MXBUFF), JBUNIT(MXBUFF),
     * JWRITE(MXBUFF), LSBUFF(MXBUFF), LOCKBF(MXBUFF), JBUFF,
     * JMXREC(MXBUFF)
      LOGICAL LSBUFF, LOCKBF
C
C     ---------------------------------------
