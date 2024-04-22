To upload a CalSim or CalLite watershed for use in comparison or regression tests, run gradlew with a publish task in this sub-project.
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
