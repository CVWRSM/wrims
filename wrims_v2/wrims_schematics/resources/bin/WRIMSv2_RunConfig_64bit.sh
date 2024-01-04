#!/bin/bash

# declare EXTERNALFOLDER variable
EXTERNALFOLDER="/home/hxie/fam/external"
MAINFILE="/home/hxie/fam/main_bo_fam.wresl"
SVFILE="/home/hxie/fam/dss/cl_future_bo_011012_sv.dss"
INITFILE="/home/hxie/fam/dss/callite2020d09einit.dss"
DVFILE="/home/hxie/fam/dss/testdv.dss"

PATH=/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit:$path
PATH=$EXTERNALFOLDER:$path
export PATH 

LD_LIBRARY_PATH=$EXTERNALFOLDER:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit
export LD_LIBRARY_PATH

/home/hxie/wrimsv2_SG_64bit/jre6/bin/java -Xmx1600m -Xss1024K -Duser.timezone=UTC -Djava.library.path=$EXTERNALFOLDER:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit -cp "$EXTERNALFOLDER:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/external:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/WRIMSv2.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/XAOptimizer.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/lpsolve55j.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/gurobi.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/heclib.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/jnios.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/jpy.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/misc.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/pd.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/commons-io-2.1.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/javatuples-1.2.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/guava-11.0.2.jar:/home/hxie/wrimsv2_SG_64bit/bin/../lib64bit/vista.jar" wrimsv2.components.ControllerBatch -config="/home/hxie/fam/fam_lpsolve.config"

exit
