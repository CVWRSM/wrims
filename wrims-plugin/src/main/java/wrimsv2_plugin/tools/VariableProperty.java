package wrimsv2_plugin.tools;

public class VariableProperty {
	private String partC="";
	private String partE="";
	
	public VariableProperty(String partC, String partE){
		this.partC=partC;
		this.partE=partE;
	}
	
	public String getPartC(){
		return partC;
	}
	
	public String getPartE(){
		return partE;
	}
}
