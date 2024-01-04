package wrimsv2.external;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

import wrimsv2.components.ControlData;
import wrimsv2.components.FilePaths;

public class Functionshortwarning extends ExternalFunction{
	
	private String line1=" WARNING:\n";
	private String line2="AD_TERM SHORTAGES and/or RELAXATIONS larger than 0.01 OCCUR!\n";
	private String[] header=new String[]{"YYYY", "MM", "Kswck","RedBlf","Wilkns","SacFea","SacAme","YoloBP","Therm","YubFea","Nimbus","Hst","ANN_RL","MRDO_RL","PosSum","NegSum"};
	private ArrayList<Integer> wYears=new ArrayList<Integer>() ;
	private ArrayList<Integer> wMonths=new ArrayList<Integer>();
	private ArrayList<Double[]> shortages=new ArrayList<Double[]>(); 
	
	public Functionshortwarning(){

	}

	public void execute(Stack stack) {

		//values in reverse order:
		Object param14 = stack.pop();
		Object param13 = stack.pop();
		Object param12 = stack.pop();
		Object param11 = stack.pop();
		Object param10 = stack.pop();
		Object param9 = stack.pop();
		Object param8 = stack.pop();
		Object param7 = stack.pop();
		Object param6 = stack.pop();
		Object param5 = stack.pop();
		Object param4 = stack.pop();
		Object param3 = stack.pop();
		Object param2 = stack.pop();
		Object param1 = stack.pop();

		//cast params to correct types:
		double mrdo_relax = ((Number) param14).doubleValue();
		double ann_relax = ((Number) param13).doubleValue();
		double short_AD_Hst = ((Number) param12).doubleValue();
		double short_AD_Nimbus = ((Number) param11).doubleValue();
		double short_AD_YubFea = ((Number) param10).doubleValue();
		double short_AD_Therm = ((Number) param9).doubleValue();
		double short_AD_YoloBP = ((Number) param8).doubleValue();
		double short_AD_SacAme = ((Number) param7).doubleValue();
		double short_AD_SacFea = ((Number) param6).doubleValue();
		double short_AD_Wilkns = ((Number) param5).doubleValue();
		double short_AD_RedBlf = ((Number) param4).doubleValue();
		double short_AD_kswck = ((Number) param3).doubleValue();
		int wMonth = ((Number) param2).intValue();
		int wYear = ((Number) param1).intValue();

		shortWarning(wYear, wMonth, short_AD_kswck,short_AD_RedBlf,short_AD_Wilkns,short_AD_SacFea, 
                short_AD_SacAme,short_AD_YoloBP,short_AD_Therm,short_AD_YubFea, 
                short_AD_Nimbus,short_AD_Hst,ann_relax,mrdo_relax);

		// push the result on the Stack
		stack.push(1.0);
	}

	public void shortWarning(int wYear, int wMonth, double short_AD_kswck, double short_AD_RedBlf, double short_AD_Wilkns, double short_AD_SacFea,
            double short_AD_SacAme, double short_AD_YoloBP, double short_AD_Therm, double short_AD_YubFea, 
            double short_AD_Nimbus, double short_AD_Hst, double ann_relax, double mrdo_relax){
		
	    Double[] shortsInAYear=new Double[14];
		
		shortsInAYear[0] = short_AD_kswck; 
		shortsInAYear[1]= short_AD_RedBlf; 
		shortsInAYear[2] = short_AD_Wilkns; 
		shortsInAYear[3] = short_AD_SacFea;  
		shortsInAYear[4]= short_AD_SacAme; 
		shortsInAYear[5]= short_AD_YoloBP; 
		shortsInAYear[6]= short_AD_Therm; 
		shortsInAYear[7]= short_AD_YubFea;   
		shortsInAYear[8]= short_AD_Nimbus;
		shortsInAYear[9] = short_AD_Hst;
		shortsInAYear[10] = ann_relax; 
		shortsInAYear[11]= mrdo_relax;
		
		boolean isShort=false;
		for (int i=0; i<=11; i++){
			if (Math.abs(shortsInAYear[i])>=0.01) isShort=true;
		}
		if (isShort){
			shortsInAYear[12]=0.0;
			shortsInAYear[13]=0.0;
			for (int j=0; j<=11; j++){
				if (shortsInAYear[j]>0.01){
					shortsInAYear[12]=shortsInAYear[12]+shortsInAYear[j];
				}else if (shortsInAYear[j]<0.01){
					shortsInAYear[13]=shortsInAYear[13]+shortsInAYear[j];
				}
			}
			shortages.add(shortsInAYear);
			wYears.add(wYear);
			wMonths.add(wMonth);
		}
		
		if (ControlData.currYear==ControlData.endYear && ControlData.currMonth==ControlData.endMonth){
			writeOpenWarningFile();	
		}
	}
	
	public void writeOpenWarningFile(){
		String warningFileFullPath=FilePaths.mainDirectory+"shorts.txt";
		try{
			FileWriter warningFile = new FileWriter(warningFileFullPath);
			PrintWriter out = new PrintWriter(warningFile);

			out.println(line1);
			out.println(line2);
			String line3="";
			for (int i=0; i<=15; i++){
				line3=line3+header[i]+"\t";
			}
			out.println(line3);
			for (int j=0; j<wYears.size();j++){
				String dataLine=wYears.get(j)+"\t"+wMonths.get(j)+"\t";
				Double[] shortsInAYear=shortages.get(j);
				for (int i=0; i<=13;i++){
					dataLine=dataLine+Math.floor(shortsInAYear[i] * 100 +.5)/100+"\t";
				}
				out.println(dataLine);
			}
			
			out.close();

			Desktop.getDesktop().open(new File(warningFileFullPath));
			
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
