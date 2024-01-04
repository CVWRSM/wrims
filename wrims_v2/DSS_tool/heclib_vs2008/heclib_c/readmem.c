#include <stdio.h>
#include <stdlib.h>

void
readmem_ (int *mbuff, int *wordpos, int *intbuff, int *nwords)
{
	int end;
	int i, k;
	int *membuff;

	membuff = (int *)*mbuff;
	end = wordpos[0] + nwords[0];
	k = 0;
	for (i=wordpos[0]; i<end; i++) {
		intbuff[k++] = membuff[i];
	}
}
