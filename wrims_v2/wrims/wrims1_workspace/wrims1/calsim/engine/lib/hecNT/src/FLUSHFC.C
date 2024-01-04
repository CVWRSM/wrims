#include <io.h>
#include <errno.h>
 
void __declspec(dllexport) __stdcall
flushfc (int *ihandle, int *istat)
{
    *istat = _commit (*ihandle);
}
