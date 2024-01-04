#include <io.h>
#include <stdio.h>

void
seek64_ (int *ihandl, int *iorigin, _int64 *iofset, int *ipos, int *istat)
{
	_int64 jpos;
	_int64 jofset;

	jofset = *iofset;
    jpos = _lseeki64(*ihandl, jofset, *iorigin);
    *istat = ((jpos == -1) ? -1 : 0);
}
