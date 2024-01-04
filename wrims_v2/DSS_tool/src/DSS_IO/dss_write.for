      subroutine dss_write(values_dss, ifltab_out_dss, Ap,Bp,Cp,Ep,Fp, date_begin, time_begin, nvals_to_read)

c-----Write out a block of data to DSS
      !use io_units
      !use runtime_data
      !use iopath_data
      implicit none

      !include 'dss.inc'
      !include 'writedss.inc'

!      logical,dimension(max_dssoutfiles) :: isopen = .false.
      character*32, intent(in) :: Ap,Bp,Cp,Ep,Fp
      character
     &      cunits*8            ! units (e.g. FEET, CFS)
     &     ,per_type*10         ! PER_MIN,PER_AVER 

      integer*4
     &     istat, iostat
     &     ,na,nb,nc,nd,ne,nf




!==================================================
! test Kevin
      integer
     &     nvals_to_read
      real*4 values_dss(90000) ! data values
      character date_begin*10, time_begin*10
      character*80 :: pathnames_dss
!      character*130,dimension(1)::outfilenames_dss = ' '
      integer, dimension(1200), intent(in) :: ifltab_out_dss          ! DSS table for each output file
      !integer ifltab_out_dss(1200,1)           ! DSS table for each input file
 
!==================================================





c--------Open the DSS file for writing
c--------exclusive write lock, works faster over NFS
c@@@         call zset('WLOCK','ON',0)
c--------set module name as data creator
!         call zset('PROGRAM',dsm2_name,0)
c--------preset for a very large .dss file
         call zset('SIZE', ' ', 1000000)

!==================================================
! test Kevin

!         outfilenames_dss(1) = 'sample_out.dss'
!         call zopen (ifltab_out_dss(:,1), outfilenames_dss(1)
!     &    , iostat)

!-----write the time block
       cunits='CFS'
       per_type='PER-AVER'
       !pathnames_dss = '/TESTA/TESTB/TESTC//1MON/TESTF/'
       pathnames_dss = "/"//trim(Ap)//"/"//trim(Bp)//"/"//trim(Cp)//"//"//trim(Ep)//"/"//trim(Fp)//"/"  
       
!       do i =1,900
!       values_dss(i) = 100*i
!       enddo

      call zsrts(ifltab_out_dss,
     &     pathnames_dss,
     &     date_begin , time_begin, nvals_to_read, values_dss, cunits,
     &     per_type, 0, istat)


!==================================================

      end
