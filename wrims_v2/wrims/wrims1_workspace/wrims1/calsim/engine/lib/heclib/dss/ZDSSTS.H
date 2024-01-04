C     ---------------------------------------
C
C     DSS Time-Series Buffer Common Block
      PARAMETER (KLBUFF=12000,NIBUFF=300)
      PARAMETER (KSBUFF=(KLBUFF/2))
C
      REAL BUFF(KLBUFF)
      REAL SBUFF1(KSBUFF), SBUFF2(KSBUFF)
      INTEGER ILBUFF(KLBUFF), INTBUF(NIBUFF)
      EQUIVALENCE (ILBUFF,BUFF)
C     Be sure BUFF and SBUFF1 are equivalenced as follows!!
      EQUIVALENCE (BUFF(1), SBUFF1), (BUFF(KSBUFF+1), SBUFF2)
      COMMON /ZDSSTS/ BUFF
      COMMON /ZDSSTI/ INTBUF
C
C     ---------------------------------------
C
