!     Last change:  R    21 Sep 2001   10:25 am
! Armin Munevar 11/22/98

! This program/function takes the flow requirements from all Delta standards
! and the associated dates, and computes the monthly controlling required
! Delta outflow.

! TO RUN AS STAND-ALONE, UNCOMMENT LINES BELOW
!program getfinalmrdo_prog
!REAL:: x
!x = getfinalmrdo(29200.0, 1, 7, &
!                  8627.0, 8,28, &
!                  5161.3,29,30, &
!                    99.0, 1,15, &
!                    99.0, 16,30, &
!                    99.0, 1,0, &
!                    99.0, 1,0, &
!                    99.0, 1,30, &
!                    99.0, 1,30, &
!                    99.0, 0,0, &
!                    99.0, 0,0, &
!                  2500.0, 1,30, &
!                  1922,7,30 )
!
!PRINT*, x

!contains

  function getfinalmrdo( &
  			Qroe  , bday_roe  , eday_roe  , &
  			Qchs  , bday_chs  , eday_chs  , &
  			Qcnf  , bday_cnf  , eday_cnf  , &
  			Qemt1 , bday_emt1 , eday_emt1 , &
  			Qemt2 , bday_emt2 , eday_emt2 , &
  			Qjpt1 , bday_jpt1 , eday_jpt1 , &
  			Qjpt2 , bday_jpt2 , eday_jpt2 , &
  			Qcol1 , bday_col1 , eday_col1 , &
  			Qcol2 , bday_col2 , eday_col2 , &
  			Qrsl1 , bday_rsl1 , eday_rsl1 , &
  			Qrsl2 , bday_rsl2 , eday_rsl2 , &
  			Qflw  , bday_flw  , eday_flw  , &
        wyear, month , daysin) RESULT (mrdo)

        IMPLICIT NONE
        REAL,INTENT(IN) :: Qroe,Qchs,Qcnf,Qflw
        REAL,INTENT(IN) :: Qemt1,Qjpt1,Qcol1,Qrsl1
        REAL,INTENT(IN) :: Qemt2,Qjpt2,Qcol2,Qrsl2
        INTEGER,INTENT(IN) :: bday_roe,eday_roe,bday_chs,eday_chs,bday_cnf,eday_cnf
        INTEGER,INTENT(IN) :: bday_emt1,eday_emt1,bday_jpt1,eday_jpt1,bday_col1,eday_col1,bday_rsl1,eday_rsl1
        INTEGER,INTENT(IN) :: bday_emt2,eday_emt2,bday_jpt2,eday_jpt2,bday_col2,eday_col2,bday_rsl2,eday_rsl2
        INTEGER,INTENT(IN) :: bday_flw,eday_flw
        INTEGER,INTENT(IN) :: wyear, month,daysin
        REAL,DIMENSION(daysin) :: Qout
        REAL :: mrdo
        INTEGER:: i
        CHARACTER(LEN=20),DIMENSION(daysin):: control
        CHARACTER(LEN=20):: Gcontrol
        INTEGER:: em,jp,co,rs,lastmonth

      if (wyear==1922 .and. month==1)  OPEN(17,FILE='delta-control.out',STATUS='new')
      if (wyear==1922 .and. month==1)  OPEN(18,FILE='g-control.out',STATUS='new')
      WRITE(17,100) 'YEAR = ',wyear, 'MONTH = ',month
      100 FORMAT(1x,a,i4,1x,a,i2)

        DO i=1,daysin
          Qout(i) = 0.0
        END DO

        DO i=1,daysin
          if (i >= bday_roe .and. i <= eday_roe .and. Qroe > Qout(i) ) then
             Qout(i) = Qroe
             control(i) = 'X2 Roe'
          end if
          if (i >= bday_chs .and. i <= eday_chs .and. Qchs > Qout(i) ) then
             Qout(i) = Qchs
             control(i) = 'X2 Chipps'
          end if
          if (i >= bday_cnf .and. i <= eday_cnf .and. Qcnf > Qout(i) ) then
             Qout(i) = Qcnf
             control(i) = 'X2 Confluence'
          end if
          if (i >= bday_emt1 .and. i <= eday_emt1 .and. Qemt1 > Qout(i) ) then
             Qout(i) = Qemt1
             control(i) = 'Emmaton'
          end if
          if (i >= bday_emt2 .and. i <= eday_emt2 .and. Qemt2 > Qout(i) ) then
             Qout(i) = Qemt2
             control(i) = 'Emmaton'
          end if
          if (i >= bday_jpt1 .and. i <= eday_jpt1 .and. Qjpt1 > Qout(i) ) then
             Qout(i) = Qjpt1
             control(i) = 'Jersey Point'
          end if
          if (i >= bday_jpt2 .and. i <= eday_jpt2 .and. Qjpt2 > Qout(i) ) then
             Qout(i) = Qjpt2
             control(i) = 'Jersey Point'
          end if
          if (i >= bday_col1 .and. i <= eday_col1 .and. Qcol1 > Qout(i) ) then
             Qout(i) = Qcol1
             control(i) = 'Collinsville'
          end if
          if (i >= bday_col2 .and. i <= eday_col2 .and. Qcol2 > Qout(i) ) then
             Qout(i) = Qcol2
             control(i) = 'Collinsville'
          end if
          if (i >= bday_rsl1 .and. i <= eday_rsl1 .and. Qrsl1 > Qout(i) ) then
             Qout(i) = Qrsl1
             control(i) = 'Rock Slough'
          end if
          if (i >= bday_rsl2 .and. i <= eday_rsl2 .and. Qrsl2 > Qout(i) ) then
             Qout(i) = Qrsl2
             control(i) = 'Rock Slough'
          end if
          if (i >= bday_flw .and. i <= eday_flw .and. Qflw > Qout(i) ) then
             Qout(i) = Qflw
             control(i) = 'Flow'
          end if
        WRITE(17,110)	'DAY = ',i,Qout(i),' control = ',control(i)
        110 FORMAT(1x,a,i2,2x,f16.2,2x,a,a)
        END DO

        !determine what G-model is controling for each month
        if (wyear==1922 .and. month==1) lastmonth = 12
        em = 0
        jp = 0
        co = 0
        rs = 0
        DO i=1,daysin
          if (control(i) == 'Emmaton') em = em + 1
          if (control(i) == 'Jersey Point') jp = jp + 1
          if (control(i) == 'Collinsville') co = co + 1
          if (control(i) == 'Rock Slough') rs = rs + 1
        END DO
        Gcontrol = 'none'
        IF(em >= 5) Gcontrol = 'Emmaton'
        IF(jp >= 5) Gcontrol = 'Jersey_Point'
        IF(co >= 5) Gcontrol = 'Collinsville'
        IF(rs >= 5) Gcontrol = 'Rock_Slough'
        IF(month /= lastmonth) WRITE(18,111) wyear,month,Gcontrol
        111 FORMAT(i4,x,i2,x,a)
        lastmonth = month

        mrdo = SUM(Qout)/daysin

      if (wyear==1994 .and. month==12) CLOSE(17)
      if (wyear==1994 .and. month==12) CLOSE(18)

  end function getfinalmrdo

!end program
