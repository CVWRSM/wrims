#include <io.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
 
/* open a file using "c" i/o, typically from fortran
   set iaccess = 10 (decimal) for most apps.
   istat is returned -1 if error, otherwise 0 */

void __declspec(dllexport)  __stdcall
openfc (char *cname, int *iaccess, int *ihandl, int *istat, int len)
{
      int acc = 0;
      if ((*iaccess & 0) != 0) {
            acc |= O_RDONLY;
      }
      if ((*iaccess & 1) != 0) {
            acc |= O_WRONLY;
      }
      if ((*iaccess & 2) != 0) {
            acc |= O_RDWR;
      }
      if ((*iaccess & 4) != 0) {

      }
      if ((*iaccess & 8) != 0) {
            acc |= O_CREAT;
	  }
	  acc |= _O_BINARY;
	*ihandl = _open (cname, acc, _S_IWRITE);
	*istat  = ((*ihandl != -1) ? 0 : 1);
}
