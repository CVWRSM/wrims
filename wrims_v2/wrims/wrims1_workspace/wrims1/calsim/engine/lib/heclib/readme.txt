Go to each subdirectory and Automake there.
Note that mixed\automake.fig is different.  It needs to access HECNT.DLL 
Then move all object files obj.  In Unix, one would say "mv */*.obj obj"
  but that doesn't work in messy-dos.
Finally, type makelib.

*******
1.  delete all *.obj
2.  am in each subdirectory (no errors)
3.  copy all *.obj to \obj directory
4.  run makelib.bat to make heclib43.lib
*******