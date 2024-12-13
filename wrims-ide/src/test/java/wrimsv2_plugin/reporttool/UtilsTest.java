/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package wrimsv2_plugin.reporttool;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hec.heclib.dss.HecTimeSeries;
import hec.heclib.util.HecTime;
import hec.heclib.util.HecTimeArray;
import hec.heclib.util.Heclib;
import hec.io.TimeSeriesContainer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

final class UtilsTest {

    @Test
    void testUtilsGetTSContainerDss6() throws IOException {
        Heclib.zset("ALLV", "", 6);
        Path wrimsTest = Files.createTempDirectory("WRIMS_TEST").resolve("testUtilsGetTSContainerDss6.dss");
        HecTimeSeries htsBase = new HecTimeSeries(wrimsTest.toString());
        TimeSeriesContainer write = new TimeSeriesContainer();
        String dss6Path = "/CALSIM/A12/SURFACE-AREA//1MON/2020D09E/";
        write.setName(dss6Path);
        HecTimeArray hecTimes = new HecTimeArray(1);
        hecTimes.setElementAt(new HecTime("12Dec2024 0100"), 0);
        write.set(new double[]{5.0}, hecTimes);
        htsBase.write(write);
        TimeSeriesContainer read = Utils.getTSContainer(htsBase, dss6Path, false);
        assertNotNull(read);
        assertTrue(read.getNumberValues() > 0);
        String dss7Path = "/CALSIM/A12/SURFACE-AREA//1Month/2020D09E/";
        read = Utils.getTSContainer(htsBase, dss7Path, false);
        assertNotNull(read);
        assertTrue(read.getNumberValues() > 0);
    }

    @Test
    void testUtilsGetTSContainerDss7() throws IOException {
        Heclib.zset("ALLV", "", 7);
        Path wrimsTest = Files.createTempDirectory("WRIMS_TEST").resolve("testUtilsGetTSContainerDss6.dss");
        HecTimeSeries htsBase = new HecTimeSeries(wrimsTest.toString());
        TimeSeriesContainer write = new TimeSeriesContainer();
        String dss7Path = "/CALSIM/A12/SURFACE-AREA//1Month/2020D09E/";
        write.setName(dss7Path);
        HecTimeArray hecTimes = new HecTimeArray(1);
        hecTimes.setElementAt(new HecTime("12Dec2024 0100"), 0);
        write.set(new double[]{5.0}, hecTimes);
        htsBase.write(write);
        TimeSeriesContainer read = Utils.getTSContainer(htsBase, dss7Path, false);
        assertNotNull(read);
        assertTrue(read.getNumberValues() > 0);
        String dss6Path = "/CALSIM/A12/SURFACE-AREA//1MON/2020D09E/";
        htsBase.write(write);
        read = Utils.getTSContainer(htsBase, dss6Path, false);
        assertNotNull(read);
        assertTrue(read.getNumberValues() > 0);
    }

    @Test
    void testUtilsGetTSContainer1DayFail() throws IOException {
        Heclib.zset("ALLV", "", 7);
        Path wrimsTest = Files.createTempDirectory("WRIMS_TEST").resolve("testUtilsGetTSContainerDss6.dss");
        HecTimeSeries htsBase = new HecTimeSeries(wrimsTest.toString());
        TimeSeriesContainer write = new TimeSeriesContainer();
        String dss7Path = "/CALSIM/A12/SURFACE-AREA//1Day/2020D09E/";
        write.setName(dss7Path);
        HecTimeArray hecTimes = new HecTimeArray(1);
        hecTimes.setElementAt(new HecTime("12Dec2024 0100"), 0);
        write.set(new double[]{5.0}, hecTimes);
        htsBase.write(write);

        TimeSeriesContainer read = Utils.getTSContainer(htsBase, dss7Path, false);
        assertNotNull(read);
        assertTrue(read.getNumberValues() > 0);
        String dss6Path = "/CALSIM/A12/SURFACE-AREA//1MON/2020D09E/";
        htsBase.write(write);
        read = Utils.getTSContainer(htsBase, dss6Path, false);
        assertNull(read);
    }
}
