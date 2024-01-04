      SUBROUTINE VANSI(IDSP)
C      ************************************************************
C      *                                                          *
C      *    CONVERTED TO IBM PC:                                  *
C      *                                                          *
C      *      CONVERSION BY:  JIM FOSS                            *
C      *      DATE COMPLETED: MAY 6, 1987                         *
C      *                                                          *
C      ************************************************************
C     CHECK FOR TERMINAL TYPE
C     SETUP FOR ANSI IF POSSIBLE - ELSE USE DUMB EQUIVALENT
C
C     MODIFIED BY SPK - PUGNER 30SEP88 TO ALLOW FOR ALPHA TERMINALS
C     THAT DON'T WANT ALL THE PAGE CLEAR DASHED LINES OUTPUT
C
C
C     EXTERNAL CHAR                                                     H
C     CHARACTER*1 CHAR                                                  H
      LOGICAL LEXIT
      LOGICAL LANSI,LFIRST,LCRT
      INTEGER IVUNIT
      LOGICAL LPAD
      COMMON/SPKALF/LSPKAP
      LOGICAL LSPKAP
      COMMON/LANSI/LANSI,LFIRST,LCRT,IVUNIT
C
C
      CHARACTER CTRM*12, CANSI*26, CERASG*12,  CTEK*6
      CHARACTER*1 CEC, CGS, CFF, CCR, CNU, CV1*3, CV2*3
      CHARACTER CMODE*(6), CSPEC*(6), CTYP*3
C
      DATA LEXIT /.FALSE./
C
      IF ( LFIRST ) THEN
C     CALL VANBK                                                        H
      LFIRST = .FALSE.
      GO TO 1
      ELSE
      GO TO 2
      ENDIF
C
      ENTRY VINIT (JDSP)
      LSPKAP = .FALSE.
      LEXIT = .TRUE.
C
    1 CONTINUE
      CNU = CHAR(0)
      CEC = CHAR(27)
      CGS = CHAR(29)
      CFF = CHAR(12)
      CCR = CHAR(13)
      IF(LEXIT) THEN
      IF ( JDSP .GE. 0 ) IVUNIT = JDSP
      ELSE
      IF ( IDSP .GE. 0 ) IVUNIT = IDSP
      ENDIF
      IERASG = 0
      ITEK = 0
         LANSI = .TRUE.                                                 MLu
         IANSI = 0                                                      MLu
C       CALL GSTRRG ( 'STM', CTRM, NTRM, ISTAT)                         Hu
        LCRT = .FALSE.
C     WRITE(3,*)' STM,NTRM ',NTRM,CTRM,ISTAT
C       IF( CTRM(1:2) .EQ. '41' .OR. CTRM(1:2) .EQ. '42' ) THEN         Hu
C         LANSI= .TRUE.                                                 Hu
C         CALL TINT2C ( 49, CV1, N1 )                                   Hu
C         CV1(1:2) = 'C1'                                               Hu
C         N1 = 2                                                        Hu
C         CALL TINT2C ( 32, CV2, N2 )                                   Hu
C         CV2(1:2) = 'B0'                                               Hu
C         N2 = 2                                                        Hu
C         IF( CTRM(3:4) .EQ. '09' ) CALL TINT2C (30,CV2,N2)             Hu
C         IF ( CTRM(3:4) .EQ. '09' ) THEN                               Hu
C            CV2(1:2) = 'A>'                                            Hu
C            N2 = 2                                                     Hu
C         ENDIF                                                         Hu
C         CANSI(1:5)= CEC//'%!0'//CNU                                   Hu
C         CANSI(6:12)= CEC//'KA'//CEC//'LV1'                            Hu
C         CANSI(13:22)=CEC//'LB'//CV1(1:2)//CEC//'LL'//CV2(1:2)         Hu
C         CANSI(23:26)=CEC//'%!1'                                       Hu
C         IANSI= 26                                                     Hu
C         CTEK(1:4) = CANSI(1:4)                                        Hu
C         ITEK = 4                                                      Hu
C     IF(NTRM.GE.6.AND.(CTRM(5:6).EQ.'TG'.OR.CTRM(5:6).EQ.'VC')) THEN   Hu
C         CANSI(26:26) = '2'                                            Hu
C     ENDIF                                                             Hu
C         CERASG(1:4) = CANSI(1:4)                                      Hu
C         CERASG(5:11) = CEC//'SV!0'//CEC//CFF                           Hu
C         IERASG = 11                                                   Hu
C       ELSE IF(CTRM(1:4) .EQ. 'TABG') THEN                             Hu
C         LANSI = .TRUE.                                                Hu
C         CANSI(1:7)= CGS//CEC//'"0g'//CEC//'<'                         Hu
C         IANSI= 7                                                      Hu
C         CERASG(1:9)= CGS//CEC//CFF//CANSI(2:7)                        Hu
C         IERASG = 9                                                    Hu
C         CTEK(1:1) = CGS                                               Hu
C         ITEK = 1                                                      Hu
C       ELSE IF(CTRM(1:3) .EQ. 'TAB') THEN                              Hu
C         LANSI = .TRUE.                                                Hu
C         CANSI(1:2)= CEC//'<'                                          Hu
C         IANSI= 2                                                      Hu
C       ELSE IF(CTRM(1:3) .EQ. 'HDS') THEN                              Hu
C         LANSI= .TRUE.                                                 Hu
C         IANSI= 8                                                      Hu
C         CANSI(1:8)=  CCR//CEC//'[0+|'//CEC//'<'                       Hu
C         CERASG(1:12)= CEC//'[0+z'//CEC//'[0+|'//CEC//'<'              Hu
C         IERASG = 12                                                   Hu
C       ELSE IF (CTRM(1:2) .EQ. '86' ) THEN                             Hu
C         LCRT = .TRUE.                                                 Hu
C         LANSI = .FALSE.                                               Hu
C       ELSE IF (CTRM(1:4) .EQ. 'ALP2') THEN                            Hu
C         LCRT = .FALSE.                                                Hu
C         LANSI = .FALSE.                                               Hu
C         LSPKAP = .TRUE.                                               Hu
C       ELSE                                                            Hu
C         LANSI= .FALSE.                                                H
C     CALL TRMTYP ( IVUNIT, CTYP )                                      H
C     IF ( CTYP .EQ. 'CRT' ) LCRT = .TRUE.                              H
C       ENDIF                                                           Hu
      IF(LEXIT) RETURN
C
    2 IF(LANSI) THEN
C        CALL CHRWT (IVUNIT,CANSI,IANSI)                                Hu
CCC      CALL VATT(0)
      ENDIF
      RETURN
C
C
      ENTRY VERASG
C
C
      IF(LANSI) THEN
C     CALL CHRWT (IVUNIT, CERASG, IERASG)                               Hu
      CALL VERASE                                                       MLu
      ENDIF
      RETURN
C
C
      ENTRY VSET ( CMODE, CSPEC )
C
C     IF(LANSI) THEN                                                    Hu
C     IF(CMODE(1:3).EQ.'TEK') THEN                                      Hu
C     CALL CHRWT (IVUNIT, CTEK, ITEK )                                  Hu
C
C     ELSE IF ( CMODE(1:4) .EQ.'ANSI' ) THEN                            Hu
C     IF (  CTRM(1:2).EQ.'41'  .OR.  CTRM(1:2).EQ.'42' ) THEN           Hu
C     IF(CSPEC(1:4).EQ.'ANSI') THEN                                     Hu
C     CALL CHRWT (IVUNIT, CANSI, IANSI-1 )                              Hu
C     CALL CHRWT (IVUNIT, '1', 1 )                                      Hu
C     ELSE IF (CSPEC(1:4).EQ.'EDIT' ) THEN                              Hu
C     CALL CHRWT (IVUNIT, CANSI, IANSI-1 )                              Hu
C     CALL CHRWT (IVUNIT, '2', 1 )                                      Hu
C     ELSE                                                              Hu
C     CALL CHRWT ( IVUNIT, CANSI, IANSI )                               Hu
C     ENDIF                                                             Hu
C     ELSE                                                              Hu
C     CALL CHRWT ( IVUNIT, CANSI, IANSI )                               Hu
C     ENDIF                                                             Hu
C     ENDIF                                                             Hu
C
C
C     ENDIF                                                             Hu
      RETURN
      END
      BLOCK DATA VANBK                                                  MLu
C     SUBROUTINE VANBK                                                  H
C
C     Block data to set common block words.
C
      INTEGER IVUNIT
      LOGICAL LSPKAP, LFIRST, LCRT, LANSI
C
      COMMON/SPKALF/LSPKAP
      COMMON/LANSI/LANSI,LFIRST,LCRT,IVUNIT
C
      DATA IVUNIT /-1/
      DATA LFIRST /.TRUE./
      DATA LSPKAP /.FALSE./
C     RETURN                                                             H
      END
