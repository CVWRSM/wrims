package wrimsv2.evaluator;

import hec.heclib.dss.*;
import hec.io.TimeSeriesContainer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.lang.IllegalArgumentException;

public class CondensedReferenceCacheAndRead {

    String[] strings = {
            "*",
            "/*/*/*/*/*/*/",
            "/*/*/ELEVATION/*/*/*/",
            "/CALSIM/*/*/*/1MON/L2020A/",
            "/CALSIM/*/ELEVATION/*/1MON/L2020A/",
            "/CALSIM/Z_PARDE_DLL/ELEVATION/*/1MON/L2020A/"
    };
    
    public void testCondensedReferenceCacheAndRead(String pathnameFilter) {
        //turn down the message level
        //HecTimeSeries.setMessageLevel(2);
        String filename = "D:\\Work\\taskorders\\Funded Jobs\\2021\\21-002 DWR EPPT\\2022-10-27 DWR Excel DSS download\\DWR Excel Tool update\\CS3\\DCRBL_DV_6.68.dss";
        HecDssCatalog catalog = new HecDssCatalog(filename);
        CondensedReferenceCache cache = createCondensedCache(filename, pathnameFilter);
        HecTimeSeries ts = new HecTimeSeries();
        int valuesRead = 0;

        List<String> pathsToRead = Arrays.asList("/CALSIM/Z_AMADR_DLL/ELEVATION//1MON/L2020A/",
                "/CALSIM/Z_CMCHE_DLL/ELEVATION//1MON/L2020A/",
                "/CALSIM/Z_MDSTO_DLL/ELEVATION//1MON/L2020A/",
                "/CALSIM/Z_PARDE_DLL/ELEVATION//1MON/L2020A/",
                "/CALSIM/Z_TRLCK_DLL/ELEVATION//1MON/L2020A/",
                "/CALSIM/Z_WDWRD_DLL/ELEVATION//1MON/L2020A/");
        for (String path : pathsToRead) {
            try {
                DSSPathname dssPathname = cache.getNominalPathname(path);
                TimeSeriesContainer tsc = new TimeSeriesContainer();
                tsc.fileName = filename;
                tsc.fullName = dssPathname.pathname();
                boolean removeMissing = false;
                ts.read(tsc, removeMissing);
                valuesRead += tsc.numberValues;
            } catch (IllegalArgumentException e) {
                System.out.println("File: " + filename + " does not have data. " + e.getMessage());
            }
        }
    }

    public static CondensedReferenceCache createCondensedCache(String filename, String pathnameFilter) {
        HecDssCatalog catalog = new HecDssCatalog(filename);
        CondensedReference[] condensedArray = catalog.getCondensedCatalog(pathnameFilter);
        Set<DSSPathname> condensedReferences = Arrays.stream(condensedArray)
                .map(CondensedReference::getNominalPathname)
                .map(DSSPathname::new)
                .collect(Collectors.toSet());
        return new CondensedReferenceCache(condensedReferences);
    }

    public static class CondensedReferenceCache {
        public final Set<DSSPathname> condensedReferences;

        private CondensedReferenceCache(Set<DSSPathname> condensedReferences) {
            this.condensedReferences = condensedReferences;
        }

        public DSSPathname getNominalPathname(String pathname) {
            return condensedReferences.stream()
                    .filter(c -> c.isSamePathname(pathname, false))
                    .findFirst()
                    .orElse(null);
        }
    }
}
