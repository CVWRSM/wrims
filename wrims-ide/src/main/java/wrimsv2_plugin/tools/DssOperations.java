package wrimsv2_plugin.tools;

import hec.heclib.util.Heclib;

import java.util.Vector;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

public class DssOperations {
	public static String matchPathName(Vector v, String partB, String partC, String partE){
		String pn=null;
		int i=0;
		int size=v.size();
		String partBString=partB.toUpperCase();
		String partCString=partC.toUpperCase();
		String partEString=partE.toUpperCase();
		while (pn == null && i<size){
			String pnv=v.get(i).toString();
			String[] parts=pnv.split("/");
			/*
			if (pnv.contains(partBString) && pnv.contains(partCString) && pnv.contains(partEString) ){
				pn=pnv;
			}
			*/
			if (parts[2].equals(partBString) && parts[3].equals(partCString) && parts[5].equals(partEString)){
				pn=pnv;
			}
			i++;
		}
		return pn;
	}
	
	public static String matchPathName(Vector v, String partABC, String partEF){
		String pn=null;
		int i=0;
		int size=v.size();
		partABC=partABC.toUpperCase();
		partEF=partEF.toUpperCase();
		while (pn == null && i<size){
			String pnv=v.get(i).toString();
			if (pnv.startsWith(partABC) && pnv.endsWith(partEF) ){
				pn=pnv;
			}
			i++;
		}
		return pn;
	}
	
	public static synchronized void waitForDSSOp(){
		try {
			while(DebugCorePlugin.isDssInOp){
				Thread.sleep(100);
			}
			DebugCorePlugin.isDssInOp=true;
		} catch (InterruptedException e) {
			WPPException.handleException(e);
		}
	}
}
