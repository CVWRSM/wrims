      PROGRAM DSS_IO
      
      !use MatData
      use dss_utility
      use string_utility

      IMPLICIT NONE

! test Kevin
      integer*4 nvals_to_read
      real*4    values_dss(9000) ! data values
      character date_begin_read*10, time_begin_read*10
      character date_begin_write*10, time_begin_write*10
      character*32 Api,Bpi,Cpi,Epi,Fpi
      character*32 Apo,Bpo,Cpo,Epo,Fpo      
      
      character*130,dimension(2) :: infilenames_dss
      character*130,dimension(2) :: outfilenames_dss = ' '
      
      integer, dimension(600,2)  :: ifltab_in_dss           ! DSS table for each input file
      integer, dimension(600,2) :: ifltab_out_dss          ! DSS table for each output file
      character*80 ::  file_out
      integer  :: ii

      logical :: sameFile = 0
      integer*4 istat
      
      



!      real*8 ::  temp(10,1),threeD(10,10,10)
!
!      temp = 567.
!      threeD = 989
!
!      call matwrite('result2.mat','Temperature',temp,'w') 
!      call matwrite('3D2.mat','threeDimension',threeD,'w') 
      
      

         open(unit = 1, file = "input.txt")

         read(1, *)
         read(1, *) infilenames_dss(1)
         read(1, *)
         read(1, *) Api,Bpi,Cpi,Epi,Fpi
         read(1, *)
         read(1, *) date_begin_read, time_begin_read
         read(1, *)
         read(1, *) nvals_to_read
         read(1, *) 
         read(1, *) outfilenames_dss(1)
         read(1, *) 
         read(1, *) Apo,Bpo,Cpo,Epo,Fpo
         read(1, *)
         read(1, *) date_begin_write, time_begin_write
         read(1, *)
         read(1, *) file_out
         

       if (StrLowCase(trim(infilenames_dss(1))) .eq. StrLowCase(trim(outfilenames_dss(1))) ) then
         sameFile = 1
       else
         sameFile = 0
       end if                                     


 
       
       call zopen (ifltab_in_dss(:,1), infilenames_dss(1), istat)
       
       !call ZOPCAT ( CDSSFI, CATFIL, ICUNIT, LOPEN, LCATLG, LCREAT, NREC)
          
       call dss_read(values_dss,             &            
     &                ifltab_in_dss(:,1), Api,Bpi,Cpi,Epi,Fpi, &   
     &                date_begin_read, time_begin_read, nvals_to_read)

       
     
!========================================================
       open(unit = 2, file = file_out)     
       write(2,"(f8.4)") values_dss(1:nvals_to_read)
       
       do ii = 1, nvals_to_read
        write(*,"(i,f8.4)") ii,values_dss(ii)
       end do
!========================================================       
       
       if (sameFile) then
         ifltab_out_dss(:,1) = ifltab_in_dss(:,1)
       else
         call zopen (ifltab_out_dss(:,1), outfilenames_dss(1), istat)
       end if      
       
       
       
       call dss_write(values_dss, ifltab_out_dss(:,1),  &
                      Apo,Bpo,Cpo,Epo,Fpo,              &
                      date_begin_write, time_begin_write, & 
                      nvals_to_read)
       



       if  (sameFile) then
         call zclose (ifltab_in_dss(:,1))
       else
         call zclose (ifltab_in_dss(:,1))
         call zclose (ifltab_out_dss(:,1))
       end if    
       
       pause
END

