!     Last change:  DM3  29 May 1998   11:57 am
!     Last change:  DM3  16 Jun 1998   11:20 am
!    Copyright (C) 1998 State of California, Department of Water
!    Resources.

!    This program is licensed to you under the terms of the GNU General
!    Public License, version 2, as published by the Free Software
!    Foundation.

!    You should have received a copy of the GNU General Public License
!    along with this program; if not, contact Dr. Francis Chung, below,
!    or the Free Software Foundation, 675 Mass Ave, Cambridge, MA
!    02139, USA.

!    THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
!    DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
!    EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
!    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
!    PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
!    DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
!    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
!    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
!    OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
!    BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
!    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
!    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
!    USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
!    DAMAGE.

!    For more information, contact:

!    Dr. Francis Chung
!    California Dept. of Water Resources
!    Division of Planning, Delta Modeling Section
!    1416 Ninth Street
!    Sacramento, CA  95814
!    916-653-5601


TYPE dwmy
    SEQUENCE       ! required because will be used in non-modules
    INTEGER     	:: day,month,wateryear ! ending point of current TS
    INTEGER     	:: week		       ! week number in current wateryear
    INTEGER     	:: deltaT_days	       ! Number of days in current TS
    CHARACTER(LEN=4)	:: timestep
END TYPE dwmy
