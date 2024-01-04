
To compile on INTEL:
1. Navigate to the heclib directory.
2. Open the solution file heclib.sln in visual studio.
3. Select the configuration of choice (release_static, release_dll or debug_static).
4. Build the solution. This will build three lib files that will be combined in the next step. Don't move anything.
5. Get a command prompt in the heclib directory. Run combine_lib with the configuration as the only argument, e.g.:
heclib>combine_lib release_dll