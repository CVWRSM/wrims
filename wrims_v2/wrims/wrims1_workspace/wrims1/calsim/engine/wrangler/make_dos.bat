del *.mod
del *.obj
del wrangler.lib
del wrangler.dll
del AUTOMAKE.DEP
set path=..\..\..\..\3rd_party\libstuff;%path%;
call am
