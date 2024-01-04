package gov.ca.dwr.wresl.xtext.editor.ui.refactoring;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static java.util.Collections.singletonList;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.ERROR;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.WARNING;
import static org.eclipse.xtext.util.Strings.equal;
import gov.ca.dwr.wresl.xtext.editor.AbstractWreslEditorRuntimeModule;
import gov.ca.dwr.wresl.xtext.editor.WreslTerminalConverters;
import gov.ca.dwr.wresl.xtext.editor.parser.antlr.WreslEditorAntlrTokenFileProvider;
import gov.ca.dwr.wresl.xtext.editor.services.WreslEditorGrammarAccess;
import gov.ca.dwr.wresl.xtext.editor.ui.internal.WreslEditorActivator;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.IdentImpl;

import java.net.URL;
import java.rmi.activation.Activator;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.ReferencedMetamodel;
import org.eclipse.xtext.TypeRef;
import org.eclipse.xtext.builder.builderState.IBuilderState;
import org.eclipse.xtext.builder.clustering.CurrentDescriptions.ResourceSetAware;
import org.eclipse.xtext.builder.impl.PersistentDataAwareDirtyResource;
import org.eclipse.xtext.common.services.TerminalsGrammarAccess;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.parser.antlr.AntlrTokenDefProvider;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;
import org.eclipse.xtext.parser.antlr.ITokenDefProvider;
import org.eclipse.xtext.resource.DefaultLocationInFileProvider;
import org.eclipse.xtext.resource.ExternalContentSupport;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IExternalContentSupport;
import org.eclipse.xtext.resource.IGlobalServiceProvider;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IGlobalServiceProvider.ResourceServiceProviderImpl;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.resource.impl.ResourceSetBasedResourceDescriptions;
import org.eclipse.xtext.ui.editor.DirtyStateManager;
import org.eclipse.xtext.ui.editor.IDirtyStateManager;
import org.eclipse.xtext.ui.refactoring.impl.AbstractProcessorBasedRenameParticipant;
import org.eclipse.xtext.ui.refactoring.impl.DefaultDependentElementsCalculator;
import org.eclipse.xtext.ui.refactoring.impl.DefaultRenameRefactoringProvider;
import org.eclipse.xtext.ui.refactoring.impl.DefaultRenameStrategy;
import org.eclipse.xtext.ui.refactoring.impl.ProjectUtil;
import org.eclipse.xtext.ui.refactoring.impl.RefactoringResourceSetProvider;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.ui.resource.IStorage2UriMapper;
import org.eclipse.xtext.ui.resource.IStorage2UriMapperJdtExtensions;
import org.eclipse.xtext.ui.resource.Storage2UriMapperImpl;
import org.eclipse.xtext.ui.resource.Storage2UriMapperJavaImpl;
import org.eclipse.xtext.ui.resource.XtextResourceSetProvider;
import org.eclipse.xtext.ui.shared.JdtHelper;
import org.eclipse.xtext.ui.util.IJdtHelper;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.ui.refactoring.IDependentElementsCalculator;
import org.eclipse.xtext.ui.refactoring.IRenameRefactoringProvider;
import org.eclipse.xtext.ui.refactoring.IRenameStrategy;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;


/**
 * Updates references to a LilyPond source file when it is renamed.
 */
@SuppressWarnings("restriction")
public class WreslEditorRenameElementParticipant extends AbstractProcessorBasedRenameParticipant {
	
	@Inject
	private IResourceDescriptions resourceDescriptions;
	
	public static final String ECORE_REFACTORING_PARTICIPANT_SHOW_WARNING_OPTION = "org.eclipse.xtext.xtext.ui.refactoring.EcoreRefactoringParticipant.show.warning.option";

	public WreslEditorRenameElementParticipant(){
		WreslEditorActivator activator = WreslEditorActivator.getInstance();
		activator.getInjector("gov.ca.dwr.wresl.xtext.editor.WreslEditor").injectMembers(this);
	}
	
	

	private ArrayList<URI> findPlatformResourceURI(QualifiedName name, EClass type) {
		ArrayList<URI> URIs=new ArrayList<URI>();
		for (IResourceDescription resourceDescription : resourceDescriptions.getAllResourceDescriptions()) {
			if (Strings.equal("wresl", resourceDescription.getURI().fileExtension())) {
				for (IEObjectDescription eObjectDescription : resourceDescription.getExportedObjectsByType(type)) {
					QualifiedName eObjectDescriptionName=eObjectDescription.getName();
					if (name.getLastSegment().equalsIgnoreCase(eObjectDescriptionName.getLastSegment())) {
						URIs.add(eObjectDescription.getEObjectURI());
					}
				}
			}
		}
		return URIs;
	}

	@Override
	protected List<EObject> getRenamedElementsOrProxies(EObject originalTarget) {
		/*
		if (originalTarget instanceof IdentImpl) {
			String name=((IdentImpl)originalTarget).getName();
			if (!Strings.isEmpty(name)) {
				String packageNsURI = ((IdentImpl)originalTarget).eClass().getEPackage().getNsURI();
				QualifiedName classifierQualifiedName = QualifiedName.create(packageNsURI, name);
				ArrayList<URI> platformResourceURIs = findPlatformResourceURI(classifierQualifiedName, ((IdentImpl)originalTarget).eClass());
				if (platformResourceURIs.size() == 0) {
					return null;
				} else {
					ArrayList<EObject> classifierProxyList=new ArrayList<EObject>();
					for (int i=0; i<platformResourceURIs.size(); i++){
						EObject classifierProxy = EcoreFactory.eINSTANCE.createEObject();
						((InternalEObject) classifierProxy).eSetProxyURI(platformResourceURIs.get(i));
						String optionFlag = FrameworkProperties.getProperty(ECORE_REFACTORING_PARTICIPANT_SHOW_WARNING_OPTION, "true");
						if(optionFlag == null || "true".equalsIgnoreCase(optionFlag))
							getStatus().add(WARNING, 
								"Renaming EClass '{0}' in ecore file. Please rerun the Ecore generator.", ((IdentImpl)originalTarget).eClass());
						classifierProxyList.add(classifierProxy);
					}
					return classifierProxyList;
				}
			}
		}
		*/
		return null;
	}
}
