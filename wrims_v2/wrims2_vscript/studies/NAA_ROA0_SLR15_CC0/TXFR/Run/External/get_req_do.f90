!     Last change:  AM   10 Jan 2000    4:53 pm
!

   FUNCTION get_req_do (G_o,ECstd,So,Sb,galpha,gbeta,n) RESULT (Qreq)

   IMPLICIT NONE
   REAL,INTENT(IN)  :: G_o,ECstd,So,Sb,galpha,gbeta,n
   REAL             :: Qreq
   REAL             :: arg2,gmtol,gmdT,Gavg,YY,DYDQ,QOLD,QNEW,diff,G99avg
   INTEGER          :: nit

   gmtol=1E-08
   gmdT=0.08333
! **********************************************************************************
!  Calculate required average G
!
!  check log argument so that it is greater than tolerance, otherwise use tolerance

   arg2=(ECstd-Sb)/(So-Sb)
   IF (arg2 < gmtol) THEN
       arg2=gmtol
   END IF
   Gavg=(-LOG(arg2)/galpha)**(1.0/n)
! *********************************************************************************
!  Use Newton-Raphson to calculate required Q given Gavg
!    where   Gavg = Q + gbeta/gmdT * LN((1 + (Q/G_o-1)*EXP(-Q*gmdT/gbeta))/(Q/G_o))
!
!    Newton-Raphson uses   Q new = Q old - Y / (dY/dQ)
!    where  Y = Q/G_o * EXP(-gmdT*(Q-Gavg)/gbeta)  -  Q/G_o * EXP(-gmdT*Q/gbeta)
!               +  EXP(-gmdT*Q/gbeta)  -  1.0

   QOLD=(gbeta*(Gavg-G_o)/(G_o*gmdT))+.5*(Gavg+G_o)  ! initial guess
   IF (QOLD < 500) THEN
       QOLD=Gavg                !  In case initial guess of Q < 0
   END IF

!  Check the value of  G99AVE that results from a very low Q of 99 cfs.
!  	if 	Go > Gavg and G99avg > Gavg,
!       then 	set Qreq = 99, and skip Newton-Raphson step.

   Qreq=0.0
   QNEW=99.0                             !  Nominal very low outflow
   IF (G_o > Gavg) THEN
       G99avg=QNEW + (gbeta/gmdT)*LOG((1.0+(QNEW/G_o-1.0)*&
&      EXP(-QNEW*gmdT/gbeta))/(QNEW/G_o))
       IF (G99avg >= Gavg) THEN
          Qreq=99.0
       END IF
   END IF
   IF (Qreq < 0.1) THEN
      diff=gmtol + 1
      nit=0
      DO WHILE (diff > gmtol)
         nit=nit+1
         IF (nit > 50) THEN
!            WRITE(12,*) 'newton-raphson error -  exceeded 50 iterations'
            EXIT
         END IF
         YY= QOLD*EXP(-gmdT*(QOLD-Gavg)/gbeta)/G_o    &
&               - (QOLD/G_o-1.0)*EXP(-gmdT*QOLD/gbeta) - 1.0
         DYDQ= (1.0/G_o-QOLD*gmdT/(G_o*gbeta)) * EXP(-gmdT*(QOLD-Gavg)/gbeta) &
&               - (1.0/G_o-QOLD*gmdT/(G_o*gbeta) + gmdT/gbeta)*EXP(-gmdT*QOLD/gbeta)

         QNEW = QOLD - YY/DYDQ
         IF (QNEW < 0) then
             QNEW = 0
         END IF
         diff=ABS(QNEW-QOLD)
         IF (QNEW < 0 .and. QOLD < 0) THEN
             diff = 0
             QNEW = 0
         END IF
         QOLD=QNEW    ! Update QOLD value for next iteration
      END DO
      Qreq=QOLD
   END IF

   END FUNCTION GET_REQ_DO


