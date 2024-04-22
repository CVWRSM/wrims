To upload a CalSim or CalLite watershed for use in comparisons, run gradlew with a publish task in this sub-project.
This can be done from this directory by launching gradlew in the parent directory, or by launching gradlew from
this directory with the following command:
```shell
../gradlew publish 
  -PsourceDir=<path to the CalSim or CalLite watershed>
  -PzipName=<name of the zip file to be created>
  -PpostVersion=<version of the zip file>
  -Puser-id=<username>
  -Ppassword=<password>
    
```

To execute the test model, run gradlew with a test task in this sub-project.
This can be done from this directory by launching gradlew in the parent directory, or by launching gradlew from
this directory with the following command:
```shell
../gradlew testExecute 
 
```

The testExec task will download a test watershed as a zip artifact from GitHub packages, and run WRIMS in 
build/testProjects/CalLite4.1_TR with run controls set in the file Test_01.config.

When downloads are required to install or update libraries or model data sets, you must supply the command-line
parameters (-P) for username and password. Use your GitHub user ID and a personal access token.

The CalLite test model has been tested successfully running with Java 8 build 212. Newer versions of Java may
may not run the model successfully.
