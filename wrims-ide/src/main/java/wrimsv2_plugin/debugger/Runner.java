package wrimsv2_plugin.debugger;

import java.io.IOException;


public class Runner extends Thread {
	private ContactDebugger cd;
	
	public Runner(ContactDebugger cd){
		this.cd=cd;
	}
	
	@Override
	public void run() {
		for (cd.i=0; cd.i<=10000; cd.i++){
			if (cd.i==cd.pauseIndex){
				try {
					cd.sendEvent("suspended");
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.suspend();
			}else{
				String currIndex=Integer.toString(cd.i);
				if (cd.breakIndex.contains(currIndex)) {
					int j=cd.breakIndex.indexOf(currIndex);
					try {
						cd.sendEvent("suspended:"+cd.i+":"+cd.breakFile.get(j));
					} catch (IOException e) {
						e.printStackTrace();
					}
					this.suspend();
				}
			}
		}
	}

}
