#include <stdio.h>
#include <windows.h>

void
seekf_ (int *ihandl, int *iorigin, long *iofset, int *ipos, int *istat)
{

	LONG lDistanceToMove;

	lDistanceToMove = (*iofset) + (*iorigin);
	
	*ipos = SetFilePointer((HANDLE)*ihandl, 
							lDistanceToMove,
							NULL,
							FILE_BEGIN);

	*istat = ((*ipos != lDistanceToMove) ? -1 : 0);
    //*istat = ((*ipos == INVALID_SET_FILE_POINTER) ? -1 : 0);
}
