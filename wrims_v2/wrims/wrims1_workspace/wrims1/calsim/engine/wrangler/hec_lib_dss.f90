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

module HEC_LIB_DSS

  interface
     subroutine zfname(cnamin,cname,nname,lexist)
       CHARACTER(LEN=*),INTENT(IN) :: cnamin
       CHARACTER(LEN=64),INTENT(OUT):: cname
       INTEGER,INTENT(OUT)::nname
       LOGICAL,INTENT(OUT)::lexist
     end subroutine zfname

     subroutine zclose(ifltab)
       INTEGER,DIMENSION(600),INTENT(INOUT) :: ifltab
     END subroutine zclose

     subroutine zopen(ifltab,cname,iostat)
       INTEGER,DIMENSION(600),INTENT(INOUT) :: ifltab
       INTEGER,INTENT(OUT)::iostat
       CHARACTER(LEN=*) :: cname
     end subroutine zopen

     subroutine zset(citem,cstr,inumb)
       INTEGER :: inumb
       CHARACTER(LEN=*) :: citem, cstr
       INTENT(IN) :: inumb, citem, cstr
     end subroutine zset

     subroutine zdtype(ifltab,cpath,nsize,lexist,cdtype,idtype)
       INTEGER,DIMENSION(600),INTENT(INOUT) :: ifltab
       CHARACTER(LEN=*),INTENT(IN) :: cpath
       INTEGER,INTENT(OUT)::nsize,idtype
       LOGICAL,INTENT(OUT)::lexist
       CHARACTER(LEN=3),INTENT(OUT)::cdtype
     end subroutine zdtype

	!CB Retrieve Paired Data
     subroutine zrpd(ifltab, cpath, nord, ncurve, ihoriz, &
          c1unit, c1type, c2unit, c2type, values, kvals, nvals, &
          clabel, klabel, label, headu, kheadu, nheadu, istat)

       INTEGER,                INTENT(INOUT)            :: ifltab(600)
       INTEGER,                INTENT(OUT)              :: nord, ncurve, ihoriz, nvals
       INTEGER,                INTENT(OUT)              :: nheadu, istat
       INTEGER,                INTENT(IN)               :: kvals, klabel, kheadu
       REAL,                   INTENT(OUT)              :: values(*), headu(*)
       CHARACTER(LEN=80),      INTENT(IN)               :: cpath
       CHARACTER(LEN=klabel),  INTENT(OUT)              :: clabel(12)
       CHARACTER(LEN=8),       INTENT(OUT)              :: c1unit,c1type,c2unit,c2type
       LOGICAL, INTENT(OUT) :: label
     end subroutine zrpd

     subroutine zchkpn(cpath,npath,istat)
       CHARACTER(LEN=*)    :: cpath
       INTEGER, INTENT(IN) :: npath
       INTEGER, INTENT(OUT):: istat
     end subroutine zchkpn

     subroutine juldat(julian,istyle,cdate,ndate)
       INTEGER :: julian,istyle,ndate
       CHARACTER(LEN=20) :: cdate
     end subroutine juldat

     function inctim(intl, iflag, nper, juls, istime, jule, ietime)
       INTEGER, INTENT(IN) :: intl, iflag, nper, juls, istime
       INTEGER, INTENT(OUT) :: jule, ietime
     end function inctim

     subroutine zgintl(intl, ce, nvals, istat)
       INTEGER intl, nvals, istat
       CHARACTER ce*32
     end subroutine zgintl

     subroutine datjul(cdate,julian,ierror)
       CHARACTER*20 cdate
     end subroutine datjul

     subroutine zsrtsx(ifltab, cpath, cdate, ctime, nvals, values, &
          flags, lflags, cunits, ctype, headu, nheadu, iplan, &
          icomp, basev, lbasev, lhigh, iprec, istat)
       INTEGER ifltab(600)
       INTEGER nvals, nheadu, icomp, iprec, iplan, istat
       REAL values(*), flags(*), basev, headu(*)
       CHARACTER cpath*80, cdate*20, ctime*4, cunits*8, ctype*8
       LOGICAL lflags, lbasev, lhigh
       INTENT(OUT) :: istat
       INTENT(INOUT) :: ifltab
     end subroutine zsrtsx

     subroutine zsrts(ifltab, cpath, cdate, ctime, nvals, values, &
          cunits, ctype, iplan, istat)
       INTEGER ifltab(600)
       INTEGER nvals, iplan, istat
       REAL values(*)
       CHARACTER cpath*80, cdate*20, ctime*4, cunits*8, ctype*8
       INTENT(OUT) :: istat
       INTENT(INOUT) :: ifltab
     end subroutine zsrts

     subroutine zstfh(clabel, citem, nitem, headu, kheadu, nheadu, istat)
       INTEGER nitem, kheadu, nheadu, istat
       CHARACTER clabel(nitem)*(*), citem(nitem)*(*)
       REAL headu(kheadu)
       INTENT(IN) :: clabel, citem, nitem, kheadu
       intent(OUT) :: istat
       INTENT(INOUT) :: headu, nheadu
     end subroutine zstfh
  end interface

end module HEC_LIB_DSS
