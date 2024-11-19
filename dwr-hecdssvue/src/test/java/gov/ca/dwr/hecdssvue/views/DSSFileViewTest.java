/*
 * Copyright (c) 2024
 * United States Army Corps of Engineers - Hydrologic Engineering Center (USACE/HEC)
 * All Rights Reserved.  USACE PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval from HEC
 */

package gov.ca.dwr.hecdssvue.views;

import hec.heclib.dss.CombinedDataManager;
import hec.heclib.dss.HecDss;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;

final class DSSFileViewTest {

    @Test
    void testSlowOpenDssFiles() {
        DebugCorePlugin.selectedStudies[0] = true;
        DebugCorePlugin.studyDvFileNames[0] = "J:\\DWR\\BigFiles\\SampleDSScycleoutput\\2020D09EDV_cycle_dp_7.dss";
        CombinedDataManager manager = new CombinedDataManager(false);
        manager.setDSSFileName("J:\\DWR\\_SampleDSS_CalSimII\\2020D09EDV_cycle_dp_7.dss");
        DebugCorePlugin.dvDss[0] = new HecDss(manager);
        DebugCorePlugin.studySvFileNames[0] = "J:\\DWR\\_SampleDSS_CalSimII\\CSII_DCR2015_Base_SV.dss";
        manager = new CombinedDataManager(false);
        manager.setDSSFileName("J:\\DWR\\_SampleDSS_CalSimII\\CSII_DCR2015_Base_SV.dss");
        DebugCorePlugin.svDss[0] = new HecDss(manager);
        DSSFileView view = new DSSFileView();
        Instant start = Instant.now();
        view.openDssFiles();
        Duration between = Duration.between(start, Instant.now());
        System.out.println(between);
    }
}
