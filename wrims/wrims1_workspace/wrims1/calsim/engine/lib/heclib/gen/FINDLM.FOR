      SUBROUTINE FINDLM (CA,KA,NA,NF,IBF,ILF,IDT,IDP,ITBL)
C
C      FIND DELIMITER SEARCHES THROUGH A CHARACTER STRING
C     AND LOCATES THE POSITIONS AND LENGTHS OF FIELDS ENDED BY GIVEN
C     DELIMITERS AND SPECIFIES THE ENDING DELIMITER.  DELIMITERS ARE
C     SPECIFIED BY CALLING SETDLM.  DEFAULT VALUES ARE SET IF SETDLM
C     NOT CALLED.
C
C             CA -  ARRAY CONTAINING CHARACTER STRING TO BE SEARCHED.
C             KA -  CHARACTER POSITION IN IA AT WHICH TO BEGIN.
C             NA -  NUMBER OF CHARACTERS TO BE SEARCHED.
C             NF -  VARRIABLE RETURNING WITH THE NUMBER OF FIELDS FOUND.
C             IBF - ARRAY RETURNED WITH THE CHARACTER POSITION OF THE
C                   BEGINNING OF FIELD I (I=1,NF).
C             ILF -  ARRAY RETURNED WITH THE LENGTH OF FIELD I.
C             IDT - ARRAY RETURNED WITH THE ENDING DELIMITER TYPE.
C             IDP - ARRAY RETURNED WITH THE ENDING DELIMITER POSITION.
C
C     REMARKS - USER MUST DIMENSION ARRAYS IBF, ILF, IDT, AND IDP
C               TO THE MAX NUMBER OF FIELDS EXPECTED, AND CA
C               TO THE STRING LENGTH.
C
      CHARACTER CA*(*)
      INTEGER IBF(*), ILF(*), IDT(*), IDP(*)
      INTEGER ITBL(-1:126)
C
C     IF SETDLM HAS NOT BEEN CALLED PREVIOUSLY, SET DEFAULT VALUES
C
      IF (ITBL(-1).NE.-7456) CALL SETDLM (0,CA,I,I,ITBL)
C
C     INITALIZE VARIABLES
C
      IF (NF.LT.0) THEN
      MAXF = IABS(NF)
      ELSE
      MAXF = 500
      ENDIF
C
      IEND = 0
      NF = 0
      IL = KA + NA
      K = KA - 1
C
C      LOOK FOR FIRST NON-DELIMITER IN FIELD
C
 10   K = K + 1
      IF (K.EQ.IL) RETURN
 11   ICH = ICHAR (CA(K:K))
C     ICH = IEB2AS (ICH)
      IVAL = ITBL(ICH)
      ITEMP = (IVAL/100) + 1
      GO TO (30,20,10,70) ITEMP
C
C     DELIMETER SHOWS FIELD LENGTH = 0
C
 20   NF = NF + 1
      IF (NF.GT.MAXF) GO TO 900
      ILF(NF) = 0
      IBF(NF) = K
      IDT(NF) = 1
      IVAL = ITBL(ICH)
      IDP(NF) = IVAL - 100
      GO TO 10
C
C     FIRST CHARACTER ENCOUNTERED IS A NON DELIMITER
C     START FIELD, AND FIND END
C
 30   NF = NF + 1
      IF (NF.GT.MAXF) GO TO 900
      IBF(NF) = K
 31   K = K + 1
      IF (K.EQ.IL) GO TO 101
      ICH = ICHAR (CA(K:K))
C     ICH = IEB2AS (ICH)
      IVAL = ITBL(ICH)
      ITEMP = (IVAL/100) + 1
      GO TO (31,40,50,40) ITEMP
      RETURN
C
C     FOUND TYPE 1 OR 3 DEL, END FIELD, FIND NEXT FIELD
C
 40   ILF(NF) = K - IBF(NF)
      IVAL = ITBL(ICH)
      IDT(NF) = IVAL/100
      IDP(NF) = MOD(IVAL,100)
      GO TO (10,90,70) IDT(NF)
C
C     FOUND A BLANK OR THE END OF A STRING, FIND ENDING DELIM
C
 50   ILF(NF) = K - IBF(NF)
 51   ILND = ICH
      K = K + 1
      IF (K.EQ.IL) GO TO 90
      ICH = ICHAR (CA(K:K))
C     ICH = IEB2AS (ICH)
      IVAL = ITBL(ICH)
      ITEMP = IVAL/100 + 1
      GO TO (60,61,51,61) ITEMP
C
C     FOUND END,SPECIFY DELIM TYPE
C
 60   ICH = ILND
 61   CONTINUE
      IVAL = ITBL(ICH)
      IDT(NF) = IVAL/100
      IDP(NF) = MOD(IVAL,100)
      GO TO (10,30,11) IDT(NF)
C
C     FOUND A STRING DELIM, SEARCH FOR THE END OF STRING
C
 70   ISNG = ICH
      NF = NF + 1
      IBF(NF) = K + 1
 80   K = K + 1
      IF (K.EQ.IL) GO TO 100
      ICH = ICHAR (CA(K:K))
C     ICH = IEB2AS (ICH)
      IF (ICH.NE.ISNG) GO TO 80
      GO TO 50
C
C     90 IS NORMAL RETURN DURING A TYPE 2 DELIM (BLANK)
C
 90   CONTINUE
      JVAL = ITBL(ILND)
      IDT(NF) = JVAL/100
      IVAL = ITBL(ICH)
      IDP(NF) = MOD(IVAL,100)
      RETURN
C
C
C     100 IS A RETURN WHEN IN A TYPE 3 (STRING) FIELD
C     101 IS A RETURN WHEN LAST CHAR IS A NON-DELIMITER
C
 100  IEND = 1
 101  IDT(NF) = 4 + IEND
      IDP(NF) = 1
      ILF(NF) = K - IBF(NF)
      RETURN
C
C     MAX FIELDS REACHED
 900  CONTINUE
      NF = MAXF
      RETURN
C
      END
