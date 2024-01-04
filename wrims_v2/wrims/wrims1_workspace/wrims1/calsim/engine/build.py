import os,subprocess

cwd = os.getcwd();
print(cwd)

newd = cwd+"\\wrangler";
print(newd);
os.chdir(newd)

subprocess.call(['clean.bat' ])
subprocess.call(['cmd','/c','am' ])
subprocess.call(['install.bat' ])

newd = cwd+"\\Simsolver";
print(newd);
os.chdir(newd)

subprocess.call(['clean.bat' ])
subprocess.call(['cmd','/c','am' ])
subprocess.call(['install.bat' ])

newd = cwd+"\\calsim_main";
print(newd);
os.chdir(newd)

subprocess.call(['clean.bat' ])
subprocess.call(['cmd','/c','am' ])
subprocess.call(['install.bat' ])

newd = cwd+"\\mkcode";
print(newd);
os.chdir(newd)

subprocess.call(['clean.bat' ])
subprocess.call(['cmd','/c','am' ])
subprocess.call(['install.bat' ])

cwd2 = os.getcwd();
print(cwd2);

