package calsim.gym;
/**
  * The main class for running the generation of wresl
  * statements to allocate weights given priorities
  *
  * @author Nicky Sandhu
  * @version $Id: GenWresl.java,v 1.1.2.1 1999/02/11 17:50:45 nsandhu Exp $
  */
public class GenWresl{
  /**
    *
    */
  public static void main(String [] args){
    if ( args == null || args.length != 4 ) {
      printUsage();
      return;
    }
    String connectFile = args[0];
    String prFile = args[1];
    String consFile = args[2];
    String defsFile = args[3];
    try {
      GymUtils.genWRESL(connectFile,prFile,consFile,defsFile);
    }catch(java.io.IOException ioe){
      System.err.println("Exception: " + ioe.getMessage());
    }
  }
  /**
    *
    */
  public static void printUsage(){
    System.out.println("Usage: " + " gymmy connectivity.csv prioritity.list weights_constraints.wresl weights_defines.wresl");
  }
}
