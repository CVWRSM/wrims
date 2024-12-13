/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import hec.heclib.dss.CondensedReference;
import hec.heclib.dss.DSSPathname;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import vista.db.dss.DSSUtil;
import vista.set.DataReference;
import vista.set.Group;
import vista.set.GroupProxy;
import vista.set.Pathname;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;

class DSSGroup extends GroupProxy {

    private final int dssIndex;
    private final boolean dv;

    public DSSGroup(int dssIndex, boolean dv) {
        this.dssIndex = dssIndex;
        this.dv = dv;
        super.setName("local::" + getFileName());
    }

    private String getFileName() {
        return dv ? DebugCorePlugin.studyDvFileNames[dssIndex] : DebugCorePlugin.studySvFileNames[dssIndex];
    }

    @Override
    protected Group getInitializedGroup() {
        String fileName = getFileName();
        if (fileName == null) {
            return null;
        } else if (!DSSUtil.isValidDSSFile(fileName)) {
            return null;
        } else {
            List<DataReference> array = new ArrayList<>();
            Vector paths = dv ? DebugCorePlugin.dvVector[dssIndex] : DebugCorePlugin.svVector[dssIndex];
            if (paths != null) {
                for (Object path : paths) {
                    if(path instanceof CondensedReference) {
                        String[] pathnameList = ((CondensedReference) path).getPathnameList();
                        if(pathnameList.length > 0) {
                            //Need the first block in order to correctly retrieve things like type
                            DSSPathname firstPathname = new DSSPathname(pathnameList[0]);
                            DSSPathname lastPathname = new DSSPathname(pathnameList[pathnameList.length - 1]);
                            firstPathname.setDPart(firstPathname.getDPart() + " - " + lastPathname.getDPart());

                            DataReference reference =
                                DSSUtil.createDataReference("local", fileName,
                                    Pathname.createPathname(firstPathname.getPathname()));
                            array.add(reference);
                        }
                    }
                }
            }
            return Group.createGroup(getFileName(), array);
        }
    }
}
