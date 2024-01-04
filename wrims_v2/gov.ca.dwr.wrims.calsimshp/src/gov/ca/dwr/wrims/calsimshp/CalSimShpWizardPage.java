package gov.ca.dwr.wrims.calsimshp;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ui.AbstractUDIGImportPage;
import org.locationtech.udig.catalog.ui.UDIGConnectionPage;
import org.locationtech.udig.catalog.ui.workflow.EndConnectionState;
import org.locationtech.udig.core.RecentHistory;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Based on the WMSWizardPage this WizardPage provides an interface 
 * to load NASA WorldWind configuration files by an URL or
 * from a local file. 
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class CalSimShpWizardPage extends AbstractUDIGImportPage implements ModifyListener, UDIGConnectionPage {

    private static final String CALSIM_WIZARD = "CALSIM_WIZARD"; //$NON-NLS-1$
    private static final String CALSIM_RECENT = "CALSIM_RECENT"; //$NON-NLS-1$
    private static final String CALSIM_PATH = "CALSIM_PATH"; //$NON-NLS-1$

    private IDialogSettings settings;

    private Text txtFile;
    private Button btnOpenFileDialog;
    private Combo typeCombo;
    private Combo idFieldCombo;
    private Combo typeFieldCombo;
    private Combo subTypeFieldCombo;
    
    private String url = ""; //$NON-NLS-1$    

    public CalSimShpWizardPage() {
        super("CalSim Shapefile"); 

        settings = CalSimShpPlugin.getDefault().getDialogSettings().getSection(CALSIM_WIZARD);
        if (settings == null) {
            settings = CalSimShpPlugin.getDefault().getDialogSettings().addNewSection(CALSIM_WIZARD);
        }
    }

    public String getId() {
        return "gov.ca.dwr.wrims.calsimshp"; //$NON-NLS-1$
    }
    
    public void createControl(Composite parent) {
        Composite grid = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        grid.setLayout(gridLayout);
        
        Label descLabel = new Label(parent, SWT.NONE);
        descLabel.setText("Please select the arc or node CalSim shapefile.");
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        descLabel.setLayoutData(gd);

        Label fileLabel = new Label(grid, SWT.NONE);
        fileLabel.setText("File");
        
        txtFile = new Text (grid, SWT.BORDER);
        txtFile.setLayoutData(new GridData(350, 20));
        
        btnOpenFileDialog = new Button (grid, SWT.PUSH);
        btnOpenFileDialog.setText("Select File");

        final Shell shell = parent.getShell();
        btnOpenFileDialog.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText("Select File");
                setLastUsedPath(fd);
                
                // Set filter on XML-files
                String[] filterExtensions = {"*.shp"};  //$NON-NLS-1$
                String[] filterNames = {"CalSim Shapefile (*.shp)"}; //$NON-NLS-1$
                fd.setFilterExtensions(filterExtensions);                
                fd.setFilterNames(filterNames);
                
                // Open the dialog
                String selectedFile = fd.open();
                
                if (selectedFile != null) {
                    txtFile.setText(selectedFile);                    
                    saveLastUsedPath(fd.getFilterPath());
                        
                    modifyText(null);
                }
            }

            private void saveLastUsedPath(String path) {
                if (settings != null && path != null) {
                    settings.put(CALSIM_PATH, path);
                }
            }

            private void setLastUsedPath(FileDialog fd) {
                if (settings != null) {
                    fd.setFilterPath(settings.get(CALSIM_PATH));
                }
            }
        });

        Label fileTypeLabel = new Label(grid, SWT.NONE);
        fileTypeLabel.setText("File Type");

        typeCombo = new Combo(grid, SWT.NONE);
        typeCombo.setItems(new String[] {"Arc", "Node"});
        new Label(grid, SWT.NONE).setText("");
        
        Label idFieldLabel = new Label(grid, SWT.NONE);
        idFieldLabel.setText("Id Field");

        idFieldCombo = new Combo(grid, SWT.NONE);
        new Label(grid, SWT.NONE).setText("");
        
        Label typeFieldLabel = new Label(grid, SWT.NONE);
        typeFieldLabel.setText("Type Field");

        typeFieldCombo = new Combo(grid, SWT.NONE);
        new Label(grid, SWT.NONE).setText("");

        Label subTypeFieldLabel = new Label(grid, SWT.NONE);
        subTypeFieldLabel.setText("Sub-Type Field");

        subTypeFieldCombo = new Combo(grid, SWT.NONE);
        new Label(grid, SWT.NONE).setText("");

        setControl(grid);
        getWizard().getContainer().updateButtons();
        
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                EndConnectionState currentState = getState();
                Map<IService, Throwable> errors = currentState.getErrors();
                if( errors!=null && !errors.isEmpty()){
                    for (Map.Entry<IService, Throwable> entry : errors.entrySet()) {
                        if( entry.getKey() instanceof CalSimShpServiceImpl ){
                            Throwable value = entry.getValue();
                            String message = "Problem connecting:" + value.getLocalizedMessage();
                            setErrorMessage(message);                            
                        }
                    }
                }

            }
        });
    }
    
    @Override
    public void setErrorMessage(String newMessage) {
        WizardPage page=(WizardPage) getContainer().getCurrentPage();
        page.setErrorMessage(newMessage);
        
        setPageComplete(newMessage == null);
        getWizard().getContainer().updateButtons();
    }

    @Override
    public void setMessage(String newMessage) {
        WizardPage page=(WizardPage) getContainer().getCurrentPage();
        page.setMessage(newMessage);
    }
    
    @Override
    public void setMessage(String newMessage, int messageType) {
        WizardPage page=(WizardPage) getContainer().getCurrentPage();
        page.setMessage(newMessage, messageType);
    }

    public EndConnectionState getState() {
        return (EndConnectionState) super.getState();
    }
    

    /**
     * Double click in list, or return from url control.
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    public void widgetDefaultSelected( SelectionEvent e ) {
        e.getClass();// kill warning
        if (getWizard().canFinish()) {
            getWizard().performFinish();
        }
    }

    
    
    /**
     * This should be called using the Wizard .. job when next/finish is pressed.
     * (never called?)
     */
    public List<IService> getResources( IProgressMonitor monitor ) throws Exception {
        URL location = new URL(url);
        
        CalSimShpServiceExtension creator = new CalSimShpServiceExtension();

        Map<String, Serializable> params = creator.createParams(location);
        IService service = creator.createService(location, params);
        service.getInfo(monitor); // load it

        List<IService> servers = new ArrayList<IService>();
        servers.add(service);

        /*
         * Success! Store the URL in history.
         */
        saveWidgetValues();

        return servers;
    }
    
    /**
     * When "Next" is pressed, check if we can load the file.
     */
    public boolean leavingPage() {  
        Collection<IService> services = getServices();
        
        for (IService service : services) {
            final IService runService = service;
            
            try {
                getContainer().run(false, true, 
                        new IRunnableWithProgress(){

                            public void run(IProgressMonitor monitor) 
                                    throws InvocationTargetException, InterruptedException {
                                monitor.beginTask("Trying to open the file", IProgressMonitor.UNKNOWN);
                
                                // try to load the file
                                try {
                                    runService.members(monitor);
                                } catch (IOException e) {
                                    throw (InvocationTargetException) new InvocationTargetException(e, e.getLocalizedMessage());
                                    
                                }
                                
                                monitor.done();
                            }});
            } catch(Exception exc) {
                // no, this is not going to work, cancel
                setErrorMessage("Error opening the file"); 
                setPageComplete(false);
                getWizard().getContainer().updateButtons();
                
                return false;
            }
        }
        
        // everything worked fine        
        saveWidgetValues();
        
        return super.leavingPage();
    }
    

    public void modifyText(ModifyEvent e) {
    	String errorMsg = "";
        try {
            getState().getErrors().clear();
            
            // confirm that the file path is valid by converting to a URL
            String filePath = txtFile.getText().trim();
            File file = new File(filePath);
            if(!file.isFile() || !filePath.toLowerCase().endsWith(".shp")) {
            	errorMsg += "You must select a valid shapefile.";
            }
            URL url = file.toURI().toURL();
            
            // open the file to get the list of field names
            ShapefileDataStore shpds = new ShapefileDataStore(url);
	    	SimpleFeatureType shpType = shpds.getSchema();
	    	String[] fieldNames = new String[shpType.getAttributeCount()];
	    	for(int index = 0; index < fieldNames.length; index++) {
	    		fieldNames[index] = shpType.getDescriptor(index).getLocalName();
	    	}
	    	idFieldCombo.setItems(fieldNames);
	    	typeFieldCombo.setItems(fieldNames);
	    	subTypeFieldCombo.setItems(fieldNames);
	    	
	    	for(String fieldName : fieldNames) {
	    		if(fieldName.equals("Arc_ID")) {
	    			idFieldCombo.setText(fieldName);
	    		}else if(fieldName.equals("CalSim_ID")) {
	    			idFieldCombo.setText(fieldName);
	    		}
	    		if(fieldName.equals("Type")) {
	    			typeFieldCombo.setText(fieldName);
	    		}
	    		if(fieldName.equals("Sub_Type")) {
	    			subTypeFieldCombo.setText(fieldName);
	    		}	    				
	    	}
	    	
	    	if(filePath.toLowerCase().contains("arc")) {
	    		typeCombo.setText("Arc");
	    	}
	    	if(filePath.toLowerCase().contains("node")) {
	    		typeCombo.setText("Node");
	    	}
	    	
            if(errorMsg.equals("")) {
            	setErrorMessage(null);
            	setPageComplete(true);
            } else {
            	setErrorMessage(errorMsg);
            	setPageComplete(false);
            }
        } catch (IOException exception) {
            setErrorMessage("You must select a valid shapefile."); 
            setPageComplete(false);
        }
        
        getWizard().getContainer().updateButtons();
    }

    /**
     * Saves the widget values
     */
    private void saveWidgetValues() {
        if (settings != null) {
            RecentHistory<String> recent =
                    new RecentHistory<String>( settings.getArray(CALSIM_RECENT) );
            recent.add( url );
            settings.put(CALSIM_RECENT, recent.toArray(new String[recent.size()]));
        }
    }

    public Map<String, Serializable> getParams() {
    	try {
	    	Map<String,Serializable> params = new HashMap<String,Serializable>();
	    	params.put(CalSimShpServiceExtension.FILE_KEY, new File(txtFile.getText()).toURI().toURL());
	    	params.put(CalSimShpServiceExtension.TYPE_KEY, typeCombo.getText());
	    	params.put(CalSimShpServiceExtension.ID_FIELD_NAME_KEY, idFieldCombo.getText());
	    	params.put(CalSimShpServiceExtension.TYPE_FIELD_NAME_KEY, typeFieldCombo.getText());
	    	params.put(CalSimShpServiceExtension.SUB_TYPE_FIELD_NAME_KEY, subTypeFieldCombo.getText());
	    	return params;
    	} catch(MalformedURLException mue) {
    		mue.printStackTrace();
    	}
    	return null;
    }

    public List<URL> getURLs() {
    	ArrayList<URL> list = new ArrayList<URL>();
        list.add(CalSimShpServiceExtension.createUrl(getParams()));
        return list;
    }

}

