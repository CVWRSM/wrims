!  demo.f90 
!
!  FUNCTIONS/SUBROUTINES exported from demo.dll:
!  demoFORTRAN - subroutine 
!
SUBROUTINE demoFORTRAN(s,sArray)

! sample for calling CVF6.6c-DLLs from
! vb/vba/python with simple structure

!!DEC$ ATTRIBUTES DLLEXPORT::DEMOFORTRAN


!DEC$ ATTRIBUTES DLLEXPORT, STDCALL, REFERENCE, MIXED_STR_LEN_ARG, ALIAS:"DEMOFORTRAN" :: demoFORTRAN

TYPE rcFORTRAN
    INTEGER :: a
    REAL :: b
    CHARACTER*(9) :: c
END TYPE

TYPE ArrayFORTRAN
    REAL*8 :: b(10)
END TYPE

TYPE(rcFORTRAN) :: s
TYPE(ArrayFORTRAN) ::sArray

print *, "============================================="
WRITE(*,*) "In Fortran......."
WRITE(*,*) " "
WRITE(*,*) "a, b, c"
WRITE(*,*) s%a, s%b, s%c
WRITE(*,*) "array"
WRITE(*,*) sArray%b
! sample calculation:
s%a = s%a*10
s%b = s%b**2.3
s%c = 'Sample'
sArray%b = sArray%b*10
END
