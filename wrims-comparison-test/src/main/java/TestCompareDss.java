import wrimsv2.components.ControllerBatch;
import hec.hecmath.DssCompare;
import hec.hecmath.DataSetResults;

public class TestCompareDss{
    static String fname_1 = "build\\testProjects\\CalLite4.1_TF\\DSS\\output\\Test_01.dss";
    static String fname_2 = "build\\testProjects\\CL_DV_4.1_TF_debug5_1921-22.dss";
    static String ofname =  "build\\testProjects\\CalLite4.1_TF\\DSS_compare.out.txt";
    static double[] tol = {0.1};
    static String [] filter = {"F=L2020A"};
    //public void testExistingFile() throws Exception {
    public static void main(){
        DssCompare compare = new DssCompare();
        DataSetResults results = compare.getDataSetResults(fname_1, fname_2, ofname, filter, tol, true, "\t", false);
    }

    public void runEngine(String controlFileName) throws Exception {
        String [] args = new String[1];
        args[0] = "-config=" + controlFileName;
        ControllerBatch controller = new ControllerBatch(args);

    }
}
