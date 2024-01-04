#include <windows.h>
#include <stdlib.h>

void
isonlocaldrive_ (char *pathName, int *isLocal, int len_pathName) 
{
	
	char absPath[_MAX_PATH];
	char *filePart;
	GetFullPathName(pathName, _MAX_PATH, absPath, &filePart);
	absPath[3] = '\0';
	switch (GetDriveType(absPath)) {
		case DRIVE_FIXED     :
		case DRIVE_REMOVABLE :
		case DRIVE_CDROM     :
		case DRIVE_RAMDISK   :
		  /* return TRUE; */
			*isLocal = 1;
			return;
		default :
		  /* return FALSE; */
			*isLocal = 0;
			return;

	}
}

