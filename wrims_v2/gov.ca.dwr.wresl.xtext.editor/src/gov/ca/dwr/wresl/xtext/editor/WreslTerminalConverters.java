package gov.ca.dwr.wresl.xtext.editor;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.impl.STRINGValueConverter;
import org.eclipse.xtext.nodemodel.INode;

public class WreslTerminalConverters extends DefaultTerminalConverters {

	@Override
	public IValueConverter<String> STRING() {
		return new STRINGValueConverter(){

			@Override
			protected String toEscapedString(String value) {
				if (value==null) return null;
				StringBuffer buf = new StringBuffer();
				int len = value.length();
				for(int i=0; i < len; i++){
					char c = value.charAt(i);
					if (c=='\\'){
						buf.append("\\\\");
					} else {
						buf.append(c);
					}
				}
				return buf.toString();
			}

			@Override
			public String toValue(String string, INode node) {
				return super.toValue(toEscapedString(string), node);
			}
			
		};
	}

}
