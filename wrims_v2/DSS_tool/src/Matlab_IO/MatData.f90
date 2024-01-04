! --------------------------------------------------------------------------
! - This module is used to read and write variables from and into Matlab   -
! - .mat format data file, which require linked with                       -
! - libmat.lib, libmat.dll, libmx.lib, libmx.dll and libut.dll             -
! - All these libraries are supported by Mathworks.inc Matlab 13           -
! -                                                                        -
! - Copyright (c) 2003-2004 by Dr. Chen Xianyao                            -
! - Key Lab. of Marine Science and Numerical Modelling                     -
! - First Institute of Oceanography                                        -
! - E-mail: chenxy@fio.org.cn, xianyaochen@yahoo.com.cn                    -
! -                                                                        -
! - All Rights Reserved.                                                   -
! - Disclosure without explicit written consent from the copyright owner   -
! - does not constitute publication.                                       -
! -                                                                        -
! - Matlab is one of products of Mathworks inc.                            -
! --------------------------------------------------------------------------


module MatData
implicit none

integer(kind=4), private :: mp, pa
integer, private :: sta

!interface write
!  module procedure  Put_Int2_0, Put_Int4_0, Put_Real4_0, Put_Real8_0, Put_Char_0, &
!                    Put_Int2_1, Put_Int4_1, Put_Real4_1, Put_Real8_1, Put_Char_1, &
!                    Put_Int2_2, Put_Int4_2, Put_Real4_2, Put_Real8_2,             &
!                    Put_Int2_3, Put_Int4_3, Put_Real4_3, Put_Real8_3,             &
!                    Put_Int2_4, Put_Int4_4, Put_Real4_4, Put_Real8_4             
!end interface

interface matwrite
  module procedure  Put_Int2_0, Put_Int4_0, Put_Real4_0, Put_Real8_0, Put_Char_0, &
                    Put_Int2_1, Put_Int4_1, Put_Real4_1, Put_Real8_1, Put_Char_1, &
                    Put_Int2_2, Put_Int4_2, Put_Real4_2, Put_Real8_2,             &
                    Put_Int2_3, Put_Int4_3, Put_Real4_3, Put_Real8_3,             &
                    Put_Int2_4, Put_Int4_4, Put_Real4_4, Put_Real8_4             
end interface

!interface read
!  module procedure  Get_Int2_0, Get_Int4_0, Get_Real4_0, Get_Real8_0, Get_Char_0, &
!                    Get_Int2_1, Get_Int4_1, Get_Real4_1, Get_Real8_1, Get_Char_1, &
!                    Get_Int2_2, Get_Int4_2, Get_Real4_2, Get_Real8_2,             &
!                    Get_Int2_3, Get_Int4_3, Get_Real4_3, Get_Real8_3,             &
!                    Get_Int2_4, Get_Int4_4, Get_Real4_4, Get_Real8_4             
!end interface

interface matread
  module procedure  Get_Int2_0, Get_Int4_0, Get_Real4_0, Get_Real8_0, Get_Char_0, &
                    Get_Int2_1, Get_Int4_1, Get_Real4_1, Get_Real8_1, Get_Char_1, &
                    Get_Int2_2, Get_Int4_2, Get_Real4_2, Get_Real8_2,             &
                    Get_Int2_3, Get_Int4_3, Get_Real4_3, Get_Real8_3,             &
                    Get_Int2_4, Get_Int4_4, Get_Real4_4, Get_Real8_4             
end interface

contains
! --- convert string into lowercase
function lowercase(s1) result(s2)
  character(len=*), intent(in) :: s1
  character(len=len(s1)) :: s2
  integer :: id,ich
  do id = 1,len(s1)
    ich = ichar(s1(id:id))
    if (ich >= 65 .and. ich<=90) then
      s2(id:id) = char(ich+32)
    else
      s2(id:id) = s1(id:id)    
    endif
  enddo
end function lowercase

! --- CheckFileName
function CheckFileName(filename) result(s)
  character(len=*), intent(in) :: filename
  character(len=len(filename)+4) :: s
  integer :: L,Ls
  L = len(filename)
  if (L-3 <=0) then
    Ls = L
  else
    Ls = L-3
  endif
  if (lowercase(filename(Ls:L)) /= '.mat') then
    s = filename//'.mat'
  else	  
    s = filename
  endif
end function CheckFileName

! --- CheckAction
function CheckAction(flnm,actionin) result(TrueAction)
  character(len=*), intent(in) :: flnm
  character(len=1), intent(in) :: actionin
  character(len=1) :: TrueAction
  logical :: ext
  
  TrueAction = lowercase(actionin)
  Inquire(file=flnm,exist=ext)
  if (TrueAction == 'a') then
    if (ext) then
      TrueAction = 'u'; return
    else
      TrueAction = 'w'; return
    endif
  endif
  if (TrueAction == 'r' .and. (.not. ext) ) then
    TrueAction = ''; return
  endif    
end function CheckAction

! --- Integer(kind=2) 
subroutine Put_Int2_0(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin,TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(1,1,mxClassIDFromClassName('int16'),0)
  ! --- Copy data to variable
  call mxCopyInteger2toPtr(var,mxGetPr(pa),1)
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int2_0


! --- Integer(kind=4) 
subroutine Put_Int4_0(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(1,1,mxClassIDFromClassName('int32'),0)
  ! --- Copy data to variable
  call mxCopyInteger4toPtr(var,mxGetPr(pa),1)
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int4_0

! --- real(kind=4) 
subroutine Put_Real4_0(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(1,1,mxClassIDFromClassName('single'),0)
  ! --- Copy data to variable
  call mxCopyReal4toPtr(var,mxGetPr(pa),1)
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real4_0

! --- real(kind=8) 
subroutine Put_Real8_0(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(1,1,mxClassIDFromClassName('double'),0)
  ! --- Copy data to variable
  call mxCopyReal8toPtr(var,mxGetPr(pa),1)
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real8_0

! --- Integer(kind=2), 1-dimensional array
subroutine Put_Int2_1(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), dimension(:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(1,size(var),mxClassIDFromClassName('int16'),0)
  ! --- Copy data to variable
  call mxCopyInteger2toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int2_1

! --- Integer(kind=4), 1-dimensional array
subroutine Put_Int4_1(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), dimension(:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(1,size(var),mxClassIDFromClassName('int32'),0)
  ! --- Copy data to variable
  call mxCopyInteger4toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int4_1

! --- real(kind=4), 1-dimensional array
subroutine Put_Real4_1(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), dimension(:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(1,size(var),mxClassIDFromClassName('single'),0)
  ! --- Copy data to variable
  call mxCopyReal4toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real4_1


! --- real(kind=8), 1-dimensional array
subroutine Put_Real8_1(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), dimension(:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(1,size(var),mxClassIDFromClassName('double'),0)
  ! --- Copy data to variable
  call mxCopyReal8toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real8_1

! --- Integer(kind=2), 2-dimensional array
subroutine Put_Int2_2(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), dimension(:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(size(var,1),size(var,2),mxClassIDFromClassName('int16'),0)
  ! --- Copy data to variable
  call mxCopyInteger2toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int2_2

! --- Integer(kind=4), 2-dimensional array
subroutine Put_Int4_2(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), dimension(:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(size(var,1),size(var,2),mxClassIDFromClassName('int32'),0)
  ! --- Copy data to variable
  call mxCopyInteger4toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int4_2

! --- real(kind=4), 2-dimensional array
subroutine Put_Real4_2(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), dimension(:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(size(var,1),size(var,2),mxClassIDFromClassName('single'),0)
  ! --- Copy data to variable
  call mxCopyReal4toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real4_2


! --- real(kind=8), 2-dimensional array
subroutine Put_Real8_2(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), dimension(:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericMatrix
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericMatrix(size(var,1),size(var,2),mxClassIDFromClassName('double'),0)
  ! --- Copy data to variable
  call mxCopyReal8toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real8_2


! --- Integer(kind=2), 3-dimensional array
subroutine Put_Int2_3(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), dimension(:,:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericArray
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericArray(3,shape(var),mxClassIDFromClassName('int16'),0)
  ! --- Copy data to variable
  call mxCopyInteger2toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int2_3


! --- Integer(kind=4), 3-dimensional array
subroutine Put_Int4_3(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), dimension(:,:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericArray
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericArray(3,shape(var),mxClassIDFromClassName('int32'),0)
  ! --- Copy data to variable
  call mxCopyInteger4toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int4_3

! --- real(kind=4), 3-dimensional array
subroutine Put_real4_3(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), dimension(:,:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericArray
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericArray(3,shape(var),mxClassIDFromClassName('single'),0)
  ! --- Copy data to variable
  call mxCopyReal4toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then   
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real4_3

! --- real(kind=8), 3-dimensional array
subroutine Put_Real8_3(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), dimension(:,:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericArray
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericArray(3,shape(var),mxClassIDFromClassName('double'),0)
  ! --- Copy data to variable
  call mxCopyReal8toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real8_3

! --- Integer(kind=2), 4-dimensional array
subroutine Put_Int2_4(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), dimension(:,:,:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericArray
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericArray(4,shape(var),mxClassIDFromClassName('int16'),0)
  ! --- Copy data to variable
  call mxCopyInteger2toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int2_4

! --- Integer(kind=4), 3-dimensional array
subroutine Put_Int4_4(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), dimension(:,:,:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericArray
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericArray(4,shape(var),mxClassIDFromClassName('int32'),0)
  ! --- Copy data to variable
  call mxCopyInteger4toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Int4_4

! --- real(kind=4), 3-dimensional array
subroutine Put_real4_4(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), dimension(:,:,:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericArray
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericArray(4,shape(var),mxClassIDFromClassName('single'),0)
  ! --- Copy data to variable
  call mxCopyReal4toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real4_4

! --- real(kind=8), 3-dimensional array
subroutine Put_Real8_4(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), dimension(:,:,:,:), intent(in)  :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateNumericArray
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateNumericArray(4,shape(var),mxClassIDFromClassName('double'),0)
  ! --- Copy data to variable
  call mxCopyReal8toPtr(var,mxGetPr(pa),size(var))
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Put_Real8_4

! ---------------------------------- 
! -         Put Character          -
! ----------------------------------

! --- Character 0
subroutine Put_Char_0(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  character(len=*), intent(in) :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxGetPr,mxClassIDFromClassName,mxCreateString
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Create variable
  pa = mxCreateString(var)
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)  
end subroutine Put_Char_0


! --- Character 1
subroutine Put_Char_1(filename,varname,var,action)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  character(len=*), dimension(:), intent(in) :: var
  character(len=1), intent(in), optional :: action

  character(len=len(filename)+4) :: flnm
  character(len=1) :: actionin, TrueAction

  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: mxCreateCharMatrixFromStrings
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  actionin = 'a'
  if (present(action)) actionin = action
  TrueAction = CheckAction(flnm,actionin)
  ! --- Open .mat file
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif

  ! --- Create character matrix from strings
  pa = mxCreateCharMatrixFromStrings(size(var),var)
  ! --- Put variable into .mat file with given name
  sta = matPutVariable(mp,varname,pa)
  if (sta /= 0) then
    write(*,'("Fail to write data ",a," to .mat file ",a)')varname,trim(flnm)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)  
end subroutine Put_Char_1


! --------------------------------------------------------------------------
! -                                                                        -
! -                          Read Subroutines                              -
! -                                                                        -
! --------------------------------------------------------------------------
! --- Integer(kind=2) 
subroutine Get_Int2_0(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger2(mxGetPr(pa),var,1)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int2_0

! --- Integer(kind=4) 
subroutine Get_Int4_0(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger4(mxGetPr(pa),var,1)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int4_0

! --- real(kind=4) 
subroutine Get_Real4_0(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal4(mxGetPr(pa),var,1)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real4_0


! --- real(kind=8) 
subroutine Get_Real8_0(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal8(mxGetPr(pa),var,1)
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real8_0

! --- Integer(kind=2) 1-dimension
subroutine Get_Int2_1(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), dimension(:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger2(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int2_1

! --- Integer(kind=4) 1-dimension
subroutine Get_Int4_1(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), dimension(:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger4(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int4_1

! --- real(kind=4) 1-dimension
subroutine Get_Real4_1(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), dimension(:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal4(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real4_1

! --- real(kind=8) 1-dimension
subroutine Get_Real8_1(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), dimension(:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal8(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real8_1

! --- Integer(kind=2) 2-dimension
subroutine Get_Int2_2(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), dimension(:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger2(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int2_2

! --- Integer(kind=4) 2-dimension
subroutine Get_Int4_2(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), dimension(:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger4(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int4_2

! --- real(kind=4) 2-dimension
subroutine Get_Real4_2(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), dimension(:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal4(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real4_2

! --- real(kind=8) 2-dimension
subroutine Get_Real8_2(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), dimension(:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal8(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real8_2

! --- Integer(kind=2) 3-dimension
subroutine Get_Int2_3(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), dimension(:,:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger2(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int2_3

! --- Integer(kind=4) 3-dimension
subroutine Get_Int4_3(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), dimension(:,:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger4(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int4_3

! --- real(kind=4) 3-dimension
subroutine Get_Real4_3(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), dimension(:,:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal4(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real4_3

! --- REAL(kind=8) 3-dimension
subroutine Get_Real8_3(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), dimension(:,:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal8(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real8_3

! --- Integer(kind=2) 4-dimension
subroutine Get_Int2_4(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=2), dimension(:,:,:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger2(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int2_4

! --- Integer(kind=4) 4-dimension
subroutine Get_Int4_4(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  integer(kind=4), dimension(:,:,:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToInteger4(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Int4_4

! --- real(kind=4) 4-dimension
subroutine Get_Real4_4(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=4), dimension(:,:,:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal4(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real4_4

! --- real(kind=8) 4-dimension
subroutine Get_Real8_4(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  real(kind=8), dimension(:,:,:,:), intent(out)  :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr
  
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    var = 0
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    ! --- Copy pointer to data variable
    call mxCopyPtrToReal8(mxGetPr(pa),var,size(var))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Real8_4

! --------------------------
! -     Get Character      -
! --------------------------
! --- character 0
subroutine Get_Char_0(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  character(len=*), intent(out)  :: var
 
  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer(kind=4) :: matClose, matGetVariable
  
  integer(kind=4) :: mxGetPr, mxGetString, mxGetM, mxGetN

  var = ''
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    return
  endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(flnm),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    !write(*,*)'pa',pa,mxGetN(pa)
    ! --- Copy pointer to data variable
    sta = mxGetString(pa,var,mxGetN(pa))
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Char_0

! - character 1
subroutine Get_Char_1(filename,varname,var)
  character(len=*), intent(in) :: filename
  character(len=*), intent(in) :: varname
  character(len=*), dimension(:), intent(out) :: var

  character(len=len(filename)+4) :: flnm
  character(len=1) :: TrueAction

  character(len=len(var)*size(var)) :: varget
  integer :: i,j,rows
  
  ! --- replace integer by integer(kind=8) on DEC alpha 64-bit platform
  integer(kind=4) :: matOpen
  integer :: matClose, matPutVariable
  
  integer(kind=4) :: matGetVariable,mxGetString,mxGetN,mxGetM
  
  var = ''
  ! --- check file name with .mat extension
  flnm = CheckFileName(filename)
  ! --- check file I/O action
  TrueAction = CheckAction(flnm,'r')
  if (TrueAction /= 'r') then
    write(*,'("Non-exist .mat file ",a)')flnm
    return
 endif
  
  ! --- Open .mat file for read
  mp = matOpen(trim(filename),TrueAction)
  if (mp == 0) then
    write(*,'("Fail to open .mat file ",a)')trim(flnm)
    return
  endif
  ! --- Get variable 
  pa = matGetVariable(mp,varname)
  if (pa == 0) then
    write(*,'("Non-exist variable ",a)')varname
  else
    rows = size(var)
    ! --- Copy pointer to data variable
    sta = mxGetString(pa,varget,min(mxGetM(pa)*mxGetN(pa),len(var)*size(var)))
    do j = 1,len(var)
      do i = 1,rows
        var(i) = trim(var(i))//varget( (j-1)*rows+i : (j-1)*rows+i )
      enddo  
    enddo
  endif
  sta = matClose(mp)  
  if (sta /= 0) then    
    write(*,'("Fail to close .mat file ",a)')trim(flnm)
  endif
  ! --- DestroyArray, Clean Memory
  call mxDestroyArray(pa)
end subroutine Get_Char_1

end module MatData
