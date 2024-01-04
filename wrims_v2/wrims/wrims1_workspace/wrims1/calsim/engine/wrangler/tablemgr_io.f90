!     Last change:  AM    3 Dec 1999    4:56 pm
!     Copyright (C) 1998, 2000 State of California, Department of Water
!     Resources.

!     This program is licensed to you under the terms of the GNU General
!     Public License, version 2, as published by the Free Software
!     Foundation.

!     You should have received a copy of the GNU General Public License
!     along with this program; if not, contact Dr. Sushil Arora, below,
!     or the Free Software Foundation, 675 Mass Ave, Cambridge, MA
!     02139, USA.

!     THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
!     DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
!     EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
!     IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
!     PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
!     DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
!     ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
!     CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
!     OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
!     BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
!     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
!     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
!     USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
!     DAMAGE.

!     For more information, contact:

!     Dr. Sushil Arora
!     California Dept. of Water Resources
!     Division of Planning, Delta Modeling Section
!     1416 Ninth Street
!     Sacramento, CA  95814
!     916-653-7921
!     sushil@water.ca.gov

module tablemgr_io
  USE tablemgr_utils

  ! defines a Multivariate Table with relational database and interpolation features
  !
  ! Created by Dudley McFadden March, 1998
  !
  ! This is basically a way to look up functions of the form:
  !        y = f(x1,x2,x3,...,xn)
  ! Example Uses:
  !           Delivery and Cutback patterns
  !           Reservoir flood control plans
  !           Patterns of annual data indexed by month and control point number
  !


  IMPLICIT NONE
  public
  private copyInTable,addTableFromFile,existsTable,loadNewTable,newTable,openAndReadName
  PRIVATE td, ntables
  PRIVATE  initial_processing, file_exists, reload_tables, n_reload_tables

  CHARACTER(LEN=KIWI), DIMENSION(50) :: reload_tables

  ! This is the table database directory
  !CHARACTER(LEN=6), parameter :: tableDatabasePath = 'lookup'
  CHARACTER(LEN=200) :: tableDatabasePath, tableCommonPath
  
  ! This is the table database
  TYPE(tableType), DIMENSION(MAX_TABLES) :: td
  INTEGER                                :: ntables = 0
  INTEGER :: i, n_reload_tables
  LOGICAL :: file_exists
  LOGICAL :: initial_processing = .true.
    

contains

  ! A subroutine to fill up one table from a preopened text file
  ! Table name has already been read in
  ! goodsPointer is set to point to an allocated array containing the data
  ! You must be sure to deallocate it some time later.
  subroutine copyInTable(inputUnit, metadata, goods)
    implicit none
    TYPE(metaDataType), INTENT(OUT)            :: metadata
    REAL, DIMENSION(:,:), POINTER              :: goods, inputGoods
    INTEGER, INTENT(IN)                        :: inputUnit
    CHARACTER(LEN=KIWI), DIMENSION(MAX_COLS+1) :: colname       ! names of each column
    CHARACTER(LEN=200)                         :: inputLine
    CHARACTER(LEN=KIWI)                        :: token
    INTEGER                                    :: i,ncols, nrows
100 FORMAT(a)
101 FORMAT("Reading table ",a,": too many columns encountered (max is ",i2,")",a,">",a,"<")
102 FORMAT("Reading table ",a,": too few columns or rows")
103 FORMAT("Reading table ",a,": too many rows encountered (max is ",i6,")")
    ! Second line is a space-separated list of column names followed by the result column label
    READ(inputUnit,100) inputLine
    ncols=0
    DO
       token = nextToken(inputLine)
       if (token==' ') exit
       ncols = ncols+1
       if (ncols > MAX_COLS) then
          WRITE(msg,101) TRIM(metadata%tabname), MAX_COLS, CHAR(13), TRIM(inputLine)
          call stopWithError
       END if
       colname(ncols) = token
    END DO
    if (ncols<=1) then
       WRITE(msg,102) TRIM(metadata%tabname)
       call stopWithError
    END if
    metadata%colname(1:ncols) = colname(1:ncols)

    ! Subsequent lines are rows of data
    ALLOCATE( inputGoods( MAX_ENTRIES, ncols))
    do nrows=0, MAX_ENTRIES-1
       READ(inputUnit,100, END=500) inputLine
       READ(inputLine, *, END=500) (inputGoods(nrows+1,i),i=1,ncols)
    end do
    WRITE(msg,103) TRIM(metadata%tabname), MAX_ENTRIES
    call stopWithError

    ! done reading, we've got the goods, unless there was an end of file.
500 IF(nrows==0) then                          
       WRITE(msg,102) TRIM(metadata%tabname)
       call stopWithError
    END IF

    ! Finally, copy the input data into the table
    ALLOCATE( goods(nrows,ncols))
    goods = inputGoods(1:nrows,:)
    DEALLOCATE( inputGoods)
    return

  contains
    ! removes the next word from the beginning of the supplied string, and returns it uppercased
    function nextToken(a) RESULT(token)
      CHARACTER(LEN=2) :: whitespace = ' ' // CHAR(9)
      CHARACTER(LEN=*), INTENT(INOUT)  :: a
      CHARACTER(LEN=KIWI) :: token
      INTEGER :: j,k
      j = VERIFY(a,whitespace)
      IF  (j==0) then
         token=' '
      else
         a = a(j:)
         k = SCAN(a,whitespace)
         IF(k==0) then
            token = a(1:32)
            a=' '
         else
            token = a(1:k-1)
            a = a(k:)
         END if
      END if
      token=uc(token)
      return
    end function nextToken
  end subroutine copyInTable


  ! A subroutine that adds an unknown table from a text file whose filename is specified
  subroutine addTableFromFile(fname)
    CHARACTER(LEN=*),INTENT(IN) :: fname
    CHARACTER(LEN=kiwi)         :: tablename
100 FORMAT(a)
!    tablename = openAndReadName(10,fname)
    CALL newTable(tablename)
    td(ntables)%metadata%tabname = uc(tablename)
    call copyInTable( 10, td(ntables)%metadata, td(ntables)%goods)
    CLOSE(10)
    RETURN
  END subroutine addTableFromFile

  function existsTable(tablename)
    ! A subroutine that finds out if there is already a given table loaded
    implicit none
    INTEGER :: existsTable
    CHARACTER(LEN=KIWI), INTENT(IN)  :: tablename
    CHARACTER(LEN=kiwi)              :: fixed_tablename
    fixed_tablename = tablename
    call deNullify(fixed_tablename)
    existsTable = positionOf(fixed_tablename, td(1:ntables)%metadata%tabname)
  END function existsTable

  function loadNewTable(tablename)
    ! A subroutine that adds a new table file by forming a filename based on the table name
    INTEGER :: loadNewTable
    CHARACTER(LEN=KIWI), INTENT(IN) :: tablename
    CHARACTER(LEN=32)  :: fixed_tablename
    CHARACTER(LEN=255) :: filename  !shengjun revised
    CHARACTER(LEN=255) :: commonfile  !shengjun revised
    CHARACTER(LEN=KIWI):: tn_from_file
100 FORMAT(a)
101 FORMAT(a,'\',a,'.table')
102 FORMAT("Table name disagreement",a,"File: ",a,", first line: ",a)
    fixed_tablename = tablename        
    call deNullify(fixed_tablename)    
    WRITE(filename,101) TRIM(tableDatabasePath), TRIM(fixed_tablename)    
    WRITE(commonfile,101) TRIM(tableCommonPath), TRIM(fixed_tablename)
    tn_from_file = openAndReadName(10, filename, commonfile)
    if (uc(tablename) /= uc(tn_from_file)) then
       WRITE(msg,102) CHAR(13), filename, tn_from_file
       call stopWithError
    end if
    CALL newTable(tn_from_file)
    td(ntables)%metadata%tabname = tn_from_file
    call copyInTable( 10, td(ntables)%metadata, td(ntables)%goods)
    CLOSE(10)
    loadNewTable= ntables
    RETURN
  end function loadNewTable


  ! Increments number of tables in preparation for adding a new one
  subroutine newTable(tablename)
    CHARACTER(LEN=KIWI) :: tablename
100 FORMAT("The limit of",i5,"tables has been reached.")
101 FORMAT("Trying to re-read a table named '",a,"'")
    IF(ntables >= MAX_TABLES) then
       WRITE(msg,100) MAX_TABLES
       call stopWithError
    END if
    if (existsTable(tablename) > 0) then
       WRITE(msg,101) TRIM(tablename)
       call stopWithError
    end if
    ntables = ntables+1
  end subroutine newTable


  ! Opens a table data file and reads its first noncomment line
  function openAndReadName(unitNo,filename,commonfile) RESULT(tablename)
    INTEGER            :: unitNo, IS
    CHARACTER(LEN=kiwi):: tablename
    CHARACTER(LEN=*)   :: filename, commonfile
100 FORMAT(A32)
101 FORMAT(a,'/',a,'.table')
103 FORMAT(1x,"Can't open data file called '",a,"' due to error ",i8)
    OPEN(unitNo,FILE=TRIM(filename),STATUS='old',IOSTAT=IS,ACTION='READ')
    if (IS > 0) then
      OPEN(unitNo,FILE=TRIM(commonfile),STATUS='old',IOSTAT=IS,ERR=500,ACTION='READ')
      filename = commonfile
    end if	
    ! First line after comment lines is the table name
    do
       READ(unitNo,100) tablename
       if (tablename(1:1) /= '!') exit
    end do
    
    
    
!    write(*,*) 'Tablename = ', tablename    


    tablename=uc(tablename)
    return
500 WRITE(MSG,*) "Can not open file: ", TRIM(filename)
    call stopWithError
  END function openAndReadName


  ! Returns a table, loading it if necessary
  function getTable(tablename)
    TYPE(tableType) :: getTable
    CHARACTER(LEN=kiwi):: tablename
    INTEGER pos
    character(len=300) :: reloadTableConfigPath

    reloadTableConfigPath = TRIM(tableDatabasePath) // "\..\reload_tables.config"

    
    OPEN (7, FILE = 'log_new_tables.txt', ACCESS = 'APPEND')
    OPEN (8, FILE = 'log_existing_tables.txt', ACCESS = 'APPEND')
    OPEN (9, FILE = 'log_reload_tables.txt', ACCESS = 'APPEND')

    if (initial_processing) then
      INQUIRE(FILE = reloadTableConfigPath, EXIST=file_exists)
      if (file_exists) then
        OPEN (62, FILE = reloadTableConfigPath)
        READ (62,*) n_reload_tables
        
        DO i = 1, n_reload_tables
          READ (62,*) reload_tables(i)
        ENDDO  
      
        close(62)
      endif
      
      DO i = 1, n_reload_tables
        WRITE (9,*) reload_tables(i)
      ENDDO 
      
      initial_processing = .false.
    endif  
      
    pos = existsTable(tablename)
    
    if (pos==0) then
    
      pos = loadNewTable(tablename)
      WRITE(7,*) uc(tablename)
      
    elseif (pos>0) then

      if ( IsInCollection(tablename, reload_tables, n_reload_tables ) ) then
        
        WRITE(8,*) tablename, " this is in reload list "
        
        ! reload lookup table for each query
        if( loadOldTable(tablename, pos) ) then
            WRITE(8,*) tablename, " reloading successful "
        else
            WRITE(8,*) tablename, " reloading fail !!!!!!!!!!!! "
        endif    
                
      !else
      
        !WRITE(8,*) tablename
      
      endif
      
    endif
    
    getTable = td(pos)
    
  END function getTable

  function loadOldTable(tablename, positionOfTable)
    ! A subroutine that reloads a table file by forming a filename based on the table name
    LOGICAL :: loadOldTable
    INTEGER, INTENT(IN) :: positionOfTable
    CHARACTER(LEN=KIWI), INTENT(IN) :: tablename
    CHARACTER(LEN=32)  :: fixed_tablename
    CHARACTER(LEN=255) :: filename  !shengjun revised
    CHARACTER(LEN=255) :: commonfile  !shengjun revised
    CHARACTER(LEN=KIWI):: tn_from_file
100 FORMAT(a)
101 FORMAT(a,'\',a,'.table')
102 FORMAT("Table name disagreement",a,"File: ",a,", first line: ",a)

    loadOldTable = .false.

    fixed_tablename = tablename        
    call deNullify(fixed_tablename)    
    WRITE(filename,101) TRIM(tableDatabasePath), TRIM(fixed_tablename)    
    WRITE(commonfile,101) TRIM(tableCommonPath), TRIM(fixed_tablename)
    tn_from_file = openAndReadName(10, filename, commonfile)
    if (uc(tablename) /= uc(tn_from_file)) then
       WRITE(msg,102) CHAR(13), filename, tn_from_file
       call stopWithError
    end if

    ! td(positionOfTable)%metadata%tabname = tn_from_file
    call copyInTable( 10, td(positionOfTable)%metadata, td(positionOfTable)%goods)
    CLOSE(10)
    loadOldTable= .true.
    RETURN
  end function loadOldTable

  function IsInCollection(member, collection, n_size )
    implicit none
    LOGICAL :: IsInCollection 
    CHARACTER(LEN=KIWI), INTENT(IN)  :: member
    CHARACTER(LEN=KIWI), DIMENSION(50), INTENT(IN)  :: collection
    INTEGER, INTENT(IN)  :: n_size
    
    DO i = 1, n_size
      
      if ( uc(member) .eq. uc(collection(i)) ) then
        IsInCollection = .true.
        return
      endif
    ENDDO  
      
    IsInCollection = .false.
      
  END function IsInCollection
  
end module tablemgr_io
