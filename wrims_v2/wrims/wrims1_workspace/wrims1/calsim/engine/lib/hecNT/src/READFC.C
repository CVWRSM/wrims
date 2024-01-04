#include <io.h>
 
void __declspec(dllexport)  __stdcall
readfc (int *ihandl, void *buff, int *nbytes, int *istat, int *ntrans)
{
    *ntrans = _read (*ihandl, buff, *nbytes);
    *istat  = ((*ntrans >= 0) ? 0 : -1);
}
