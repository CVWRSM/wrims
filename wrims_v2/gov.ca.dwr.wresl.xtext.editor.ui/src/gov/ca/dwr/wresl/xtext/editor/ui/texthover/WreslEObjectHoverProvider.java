package gov.ca.dwr.wresl.xtext.editor.ui.texthover;

import org.eclipse.swt.events.FocusListener;

import gov.ca.dwr.wresl.xtext.editor.ui.watch.AddWatch;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVar;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.AliasImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.ConstDefImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.DVarIntegerImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.DeclarationImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.DvarDefImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.ExternalDefImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.ExternalImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.IdentImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.SvarDefImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.TermImpl;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.UnaryImpl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.view.WPPWatchView;

public class WreslEObjectHoverProvider extends DefaultEObjectHoverProvider {
	
	@Override
	protected String getFirstLine(EObject o) {
		String hoverText;
		if (o instanceof DeclarationImpl){
			hoverText=((DeclarationImpl) o).getName();
			DebugCorePlugin.hoverText=hoverText;
			return hoverText;
		}else if (o instanceof AliasImpl){
			hoverText=((AliasImpl) o).getRef().getName();
			DebugCorePlugin.hoverText=hoverText;
			return hoverText;
		}else if (o instanceof GoalImpl){
			hoverText=((GoalImpl) o).getName();
			DebugCorePlugin.hoverText=hoverText;
			return hoverText;
		}else if (o instanceof IdentImpl){
			hoverText=((IdentImpl) o).getName();
			DebugCorePlugin.hoverText=hoverText;
			return hoverText;
		}else if (o instanceof SvarDefImpl){
			hoverText=((SvarDefImpl) o).getRef().getName();;
			DebugCorePlugin.hoverText=hoverText;
			return hoverText;
		}else if (o instanceof DvarDefImpl){
			hoverText=((DvarDefImpl) o).getRef().getName();
			DebugCorePlugin.hoverText=hoverText;
			return hoverText;
		}else if (o instanceof ConstDefImpl){
			hoverText=((ConstDefImpl) o).getRef().getName();
			DebugCorePlugin.hoverText=hoverText;
			return hoverText;
		}else if (o instanceof ExternalDefImpl){
			hoverText=((ExternalDefImpl) o).getRef().getName();
			return hoverText;
		}else{
			DebugCorePlugin.hoverText="";
			return o.toString();
		}
	}
}
