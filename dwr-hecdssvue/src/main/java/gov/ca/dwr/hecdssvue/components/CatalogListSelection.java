package gov.ca.dwr.hecdssvue.components;

import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import rma.util.PlugInLoader;
import rma.util.RMAIO;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import hec.heclib.dss.CombinedDataManager;
import hec.heclib.dss.DataReference;
import hec.heclib.dss.DataReferenceSet;
import hec.dssgui.DssVueProgressBar;
import hec.dssgui.ListSelection;
import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecDataManager;
import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.heclib.util.Heclib;
import hec.heclib.util.stringContainer;
import hec.io.DataContainer;
import hec.io.TimeSeriesContainer;
import hec.util.AnimatedStatusIconPanel;

public class CatalogListSelection extends ListSelection {

	public CatalogListSelection() {
		_mode = FULL_FUNCTION;
		setup(true);
	}

	protected void setup(boolean useTabbedPane)
	{
		String debug = System.getProperty("debug", "false");
		if (!debug.equalsIgnoreCase("false"))
			ListSelection.debug = true;
		setupParameters();
		_useTabbedPane = useTabbedPane;
		build();
		loadPlugins(this);
	}
	
	protected void build()
	{
		GridBagLayout gridBagLayout;
		gridBagLayout = new GridBagLayout();
		getContentPane().setLayout(gridBagLayout);

		buildToolBar();
		buildFilenamePanel();
		if (miScripts != null) {
			buildScriptButtons();
		}

		if (_titleName.length() == 0)
			setTitle(_programName);
	}
	
	private void loadPlugins(ListSelection selection)
	{
		// System.out.println("DssVue test mode only!!!");
		// plugins.Fix1MinPlugin x = new plugins.Fix1MinPlugin();
		// hec.dssgui.plugins.metaData.DssMetaDataPlugin x = new
		// hec.dssgui.plugins.metaData.DssMetaDataPlugin();
		// hec.dssgui.plugins.Excel x = new hec.dssgui.plugins.Excel();
		// hec.dssgui.plugins.TideMeans x = new hec.dssgui.plugins.TideMeans();
		// hec.dssgui.plugins.Peaks x = new hec.dssgui.plugins.Peaks();
		//hec.dssgui.plugins.Test x = new hec.dssgui.plugins.Test();
		 //hec.dssgui.plugins.usgs.UsgsControlPlugin y = new hec.dssgui.plugins.usgs.UsgsControlPlugin();
		// hec.dssgui.plugins.cdec.CdecControlPlugin x = new
		// hec.dssgui.plugins.cdec.CdecControlPlugin();
		 //hec.dssgui.plugins.NcdcImportPlugin y = new hec.dssgui.plugins.NcdcImportPlugin();
		 //hec.dssgui.plugins.DssutlImportPlugin x = new
		 //hec.dssgui.plugins.DssutlImportPlugin();
		 //hec.dssgui.plugins.WaterML x = new hec.dssgui.plugins.WaterML();
		 //hec.dssgui.plugins.riverGages.RiverGagesControlPlugin x = new hec.dssgui.plugins.riverGages.RiverGagesControlPlugin();
		 //Object j[] = new Object[1]; j[0] = selection; x.main(j);
		 //hec.dssgui.plugins.ImportWizardPlugin x = new
		//	 hec.dssgui.plugins.ImportWizardPlugin();
		 //Object j[] = new Object[1]; j[0] = selection; x.main(j);
		 //Object j1[] = new Object[1]; j1[0] = selection; y.main(j);
		 /***********************************************************************
		  * Object j[] = new Object[1]; j[0] = selection; x.main(j); /*
		 * hec.dssgui.plugins.Excel y = new hec.dssgui.plugins.Excel(); Object
		 * k[] = new Object[1]; k[0] = selection; y.main(k); /* /
		 * hec.dssgui.plugins.chart.ChartPlugin y = new
		 * hec.dssgui.plugins.chart.ChartPlugin(); Object k[] = new Object[1];
		 * k[0] = selection; y.main(k); /
		 **********************************************************************/

		PlugInLoader.loadPlugIns("HecDssVuePlugin", new Object[] {selection});
		addPluginMenus();
	}
	
	public void copyRecords(final boolean openFile){
		
		IWorkbench workbench=PlatformUI.getWorkbench();
		IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
		final DSSCatalogView dssCatalogView=(DSSCatalogView) workBenchPage.findView(DSSCatalogView.ID);

		if (dssCatalogView !=null){
			final Vector<String[]> selectedParts=dssCatalogView.getSelectedParts();			
			final int size = selectedParts.size();
			
			if (size == 0) {
				return;
			}

			final ListSelection ls=this;
			
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (openFile) {
						String openFileName = openDSSFileDialog(_remote,
							"Copy Records into HEC-DSS File", false);
						if (openFileName == null) {
							return;
						}
						DssPluginCore.lastCopiedDssFolder=openFileName.substring(0, openFileName.lastIndexOf(File.separator));
						_secondDataManagerName = openFileName;
						boolean opened = false;
						if (_secondDataManager != null) {
							if (_secondDataManager.DSSFileName().equals(
								_secondDataManagerName)) {
								opened = true;
							}
						}
						if (!opened){
							opened = openDSSFile(_secondDataManagerName, false, false);
						}
						if (!opened){
							String message = "Unable to access HEC-DSS file " +
								_secondDataManagerName;
							System.out.println(message);
							if (isInteractive()){
								JOptionPane.showMessageDialog(ls, message, _programName,
															  JOptionPane.WARNING_MESSAGE);
							}
							return;
						}
					}
					if (_secondDataManager.zinqir("READ") == 1) {
						String message =
							"You do not have permission to write to the HEC-DSS file " +
							_secondDataManagerName;
						System.out.println(message);
						if (isInteractive()){
							JOptionPane.showMessageDialog(ls, message, _programName,
														  JOptionPane.WARNING_MESSAGE);
						}
						_secondDataManager.close();
						return;
					}
					DataReferenceSet pathnames = new DataReferenceSet();
					
					int k=0;
					while (!DebugCorePlugin.selectedStudies[k] && k<4){
						k++;
					}
					
					if (k<4){
						HecDss dvDss=DebugCorePlugin.dvDss[k];
						HecDss svDss=DebugCorePlugin.svDss[k];
						
						if (svDss !=null){
							Vector<String> pathnameList = svDss.getPathnameList();
							String filename=svDss.getFilename();
							for (int j=0; j<pathnameList.size(); j++){
								String pathname=pathnameList.get(j);
								for (int i=0; i<size; i++){
									String[] parts = selectedParts.get(i);
									//String pathname = dssCatalogView.getPathname(parts);
									//Vector<DataContainer> dataVector_path = dssCatalogView.getData(pathname);
									//String filename=dataVector_path.get(0).fileName;
									if (DataOps.containParts(pathname, parts)){
										DataReference dr=new DataReference(pathname, filename);
										pathnames.add(dr);
									}
								}
							}
						}
						
						if (dvDss !=null){
							Vector<String> pathnameList = dvDss.getPathnameList();
							String filename=dvDss.getFilename();
							for (int j=0; j<pathnameList.size(); j++){
								String pathname=pathnameList.get(j);
								for (int i=0; i<size; i++){
									String[] parts = selectedParts.get(i);
									//String pathname = dssCatalogView.getPathname(parts);
									//Vector<DataContainer> dataVector_path = dssCatalogView.getData(pathname);
									//String filename=dataVector_path.get(0).fileName;
									if (DataOps.containParts(pathname, parts)){
										DataReference dr=new DataReference(pathname, filename);
										pathnames.add(dr);
									}
								}
							}
						}
						
						Vector newPathnames = pathnames.getPathnameSet(); // for																						// compatability															// w/
																											// checkOverwriteRecords
						if (pathnames.size() > 0){
							DataReference dr = pathnames.get(0);
							if (dr.isWaterYear()){
								pathnames = pathnames.expandWaterYear();
								newPathnames = pathnames.getPathnameSet(true);
							}
						}
						final DataReferenceSet paths = pathnames;
						final Vector newPaths = newPathnames;
						checkOverwriteRecords("CopyTo", _secondDataManager, paths, newPaths);
					}
				}
			});
		}
	}
	
	public boolean openDSSFile(String DSSFileName, boolean firstManager,
			   boolean mustExist){
		return openDSSFile(DSSFileName, firstManager,
		   _remote, mustExist);
	}

	public boolean openDSSFile(String DSSFileName, boolean firstManager,
			   boolean remote, boolean mustExist){
		if (remote)
			miImportShef.setVisible(false);
		CombinedDataManager dataManager = createDataManager(remote);
		stringContainer cleanName = new stringContainer();
		dataManager.cleanDSSFileName(DSSFileName, cleanName);
		DSSFileName = cleanName.string;
		boolean fileAlreadyExists = dataManager.doesDSSFileExist(DSSFileName);
		if (mustExist ) {
			if (!fileAlreadyExists) {
				String message = "The file " + DSSFileName + "\ndoes not exist.";
				System.out.println(message);
				if (isInteractive()){
					JOptionPane.showMessageDialog(this, message, _programName,
								  JOptionPane.WARNING_MESSAGE);
				}
				return false;
			}
		}

		if (firstManager ) {
			// dataManager.setUnit(71);
			miUndo.setEnabled(false);
		}
		int status = dataManager.setDSSFileName(DSSFileName, mustExist);
		if (status != 0) {
			String message = "Cannot Access File:\n" + DSSFileName;
			System.out.println(message);
			if (isInteractive()){
				JOptionPane.showMessageDialog(this, message, _programName,
							  JOptionPane.WARNING_MESSAGE);
			}
			_showErrorOnEmptyFile = false;
			return false;
		}
		if (!fileAlreadyExists) {
			String message = "Created file " + DSSFileName;
			System.out.println(message);
			if (isInteractive()){
				JOptionPane.showMessageDialog(this, message, _programName,
							  JOptionPane.INFORMATION_MESSAGE);
			}
			_showErrorOnEmptyFile = false;
		}
		else {
			_showErrorOnEmptyFile = true;
		}

		if (firstManager ) {
			if ( (_secondDataManager != null) &&
				(dataManager.DSSFileName().equals(_secondDataManager.
								  DSSFileName()))) {
				// If the one opened is the same as the second, don't close
				// the file, but make the second no longer active
				// _secondDataManager.done();
				_secondDataManager = null;
				miCopyRecordsTo.setVisible(false);
			}
			else {
				// _dataManager.close();
			}
			_dataManager = dataManager;
			HecDataManager.setDefaultDSSFileName(DSSFileName);
			if (_titleName.length() == 0)
			setTitle(RMAIO.getFileFromPath(DSSFileName) + " - " + _programName);
			if (!remote) {
				_currentDirectory = RMAIO.getDirectoryFromPath(_dataManager.
					DSSFileName());
			}
			newTableView(true, false, true);
			//updateFileStats();
			updateMenuItems(true);
		}
		else {
			if (_secondDataManager != null) {
				// If the second manager is opened, and is not the same file,
				// close it
				if (!_secondDataManager.DSSFileName().equals((dataManager.DSSFileName()))) {
					_secondDataManager.close();
				}
			}
			_secondDataManager = dataManager;
		}

		// Add file name to bottom of list, removing duplicate if there.
		String name = DSSFileName.replace('\\', '/');
		int found = -1;
		for (int i = 0; i < _pastFilesAccessed.size(); i++) {
			String pastName = (String) _pastFilesAccessed.elementAt(i);
			pastName = pastName.replace('\\', '/');
			if (pastName.equalsIgnoreCase(name)) {
				found = i;
				break;
			}
		}
		if (found > -1) {
			_pastFilesAccessed.removeElementAt(found);
		}
		_pastFilesAccessed.addElement(DSSFileName);

		return true;
	}

	public void checkOverwriteRecords(final String operationType, final CombinedDataManager dataManager,
			 final DataReferenceSet pathnames, final Vector newPathnames){
		setCursorWait("Checking if records exist in " + dataManager.DSSFileName());
		// exists = dataManager.recordsExist(newPathnames);
		SwingWorker ckExists = new SwingWorker<boolean[], boolean[]>() {
			public boolean[] doInBackground()
			{
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 1);
				boolean exists[] = dataManager.recordsExist(newPathnames);
				return exists;
			}

			protected void done()
			{
				boolean exists[];

				try{
					exists = (boolean[])get();
				}
				catch (Exception e){
					JOptionPane.showMessageDialog(null, e.toString());
					setCursorDefault();
					return;
				}
				setCursorDefault();

				Vector existingRecords = new Vector();
				for (int i = 0; i < exists.length; i++){
					if (exists[i]){
						existingRecords.add(newPathnames.get(i));
					}
				}
				if (existingRecords.size() > 0){
					System.out.println(existingRecords.size() +
							" records already exist:");
					int size = existingRecords.size();
					if (size > 10)
						size = 10;
					for (int i = 0; i < size; i++){
						System.out.println((String)existingRecords.get(i));
					}
					
					StringBuffer message = new StringBuffer();
					if (existingRecords.size() > 4){
						message.append(Integer.toString(existingRecords.size()));
						message.append(
								" records already exist.\nOverwrite existing records?");
					}
					else{
						message.append("Record(s) already exist:");
						for (int i = 0; i < existingRecords.size(); i++){
							message.append("\n");
							message.append((String)existingRecords.get(i));
						}
						message.append("\nOverwrite existing record(s)?");
					}
					int option = JOptionPane.showConfirmDialog(null, message,
									   "HecDssVue",
									   JOptionPane.YES_NO_CANCEL_OPTION);
					if (option == JOptionPane.CANCEL_OPTION){
						return;
					}
					if (option == JOptionPane.NO_OPTION){
						for (int i = (exists.length - 1); i > -1; i--){
							if (exists[i]){
								pathnames.removePathname(i);
								newPathnames.removeElementAt(i);
							}
						}
					}
				}
				if (newPathnames.size() == 0)
					return;
				// now process the remainder of the action
				if (operationType.equals("CopyTo")) {
					completeCopyRecords(pathnames);
				}
				else if (operationType.equals("CopyFrom")) {
					completeCopyFrom(pathnames, dataManager);
				}
				else if (operationType.equals("Rename")) {
					completeDupRename(pathnames, newPathnames, operationType, RENAME);
				}
				else if (operationType.equals("Duplicate")) {
					completeDupRename(pathnames, newPathnames, operationType, DUPLICATE);
				}
			}
		};
		ckExists.execute();
	}

	
	public void setCursorWait()
	{
		setCursorWait("Working...");
	}

	public void setCursorWait(String message)
	{
		// Updatable for fancy cursors
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		//updateMessageField(message);
		//_statusIcon.setState(AnimatedStatusIconPanel.WORKING_STATUS);
	}
	
	public void setCursorDefault()
	{
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		//updateMessageField();
		//_statusIcon.setState(AnimatedStatusIconPanel.NORMAL_STATUS);
		//_statusIcon.revalidate();
	}
	
	protected void completeCopyRecords(DataReferenceSet dataReferenceSet){
		String opNameVerb = "Copying records to " + _secondDataManagerName;
		setCursorWait(opNameVerb);
		Vector filenames = new Vector();
		final Vector pathnames = new Vector();
		DataReferenceSet drs = new DataReferenceSet();
		final DataReferenceSet timeSeries = new DataReferenceSet();
		final DataReferenceSet timeSeriesErrors = new DataReferenceSet();
		final Vector tsDataManagers = new Vector();
		for (int i=0; i<dataReferenceSet.size(); i++) {
			DataReference dr = dataReferenceSet.get(i);
			boolean useTimeWindow = false;
			if (dr.hasTimeWindow()) {
				DSSPathname path = new DSSPathname(dr.getPathname());
				if (path.isTimeSeries())
					useTimeWindow = true;
			}
			if (useTimeWindow) {
					timeSeries.add(dr);
					String fileName = dr.getFilename();
					//CombinedDataManager dataManager = _selectionAndFilterPanel.getDataManager(fileName);
					CombinedDataManager dataManager = new CombinedDataManager(false);
					dataManager.setDSSFileName(fileName);
					tsDataManagers.add(dataManager);
			}
			else {
				drs.add(dr);
			}

		}
		final int numberPaths = drs.getFilesAndPathnames(filenames, pathnames) + timeSeries.size();
		final Vector dataManagers = new Vector(filenames.size());
		for (int i=0; i<filenames.size(); i++) {
			String name = filenames.elementAt(i).toString();
			//CombinedDataManager dataManager = _selectionAndFilterPanel.getDataManager(name);
			CombinedDataManager dataManager = new CombinedDataManager(false);
			dataManager.setDSSFileName(name);
			dataManagers.add(dataManager);
		}
		_operationCanceled = false;
		boolean visible = true;
		if (dataReferenceSet.numberPathnames() < 1000)
			visible = false;
		//_dssVueProgressBar = new DssVueProgressBar(this, COPY, 1, opNameVerb, visible);
		//_dssVueProgressBar.setTask(1, 0, opNameVerb);
		SwingWorker copyRecs = new SwingWorker<Integer, Integer>()
		{
			public Integer doInBackground()
			{
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 1);
				for (int i = 0; i < dataManagers.size(); i++){
					CombinedDataManager dataManager = (CombinedDataManager)dataManagers.elementAt(i);
					int stat = dataManager.copyRecordsFrom(_secondDataManager,
														   _secondDataManagerName,
														   (Vector)pathnames.elementAt(i));
					if (stat < 0)
						return new Integer(stat);
				}
                for(int i = 0; i < timeSeries.size(); i++) {
					DataReference dr = timeSeries.get(i);
					CombinedDataManager dataManager = (CombinedDataManager)tsDataManagers.elementAt(i);
					TimeSeriesContainer tsc = new TimeSeriesContainer();
					int istat = dataManager.readData(dr, tsc, false);
					tsc.fileName = "";
					if (tsc.numberValues == 0){
						istat = -2;
					}
					else{
						// Look for all missing data sets - often the case for
						// duplicated data with a time window
						boolean allMissing = true;
						for (int j = 0; j < tsc.values.length; j++){
							if (tsc.values[j] != Heclib.UNDEFINED_DOUBLE){
								allMissing = false;
								break;
							}
						}
						if (allMissing)
							istat = -2;
					}
					if (istat >= -1){
						int stat = _secondDataManager.writeData(tsc);
						if (stat < 0)
							return new Integer(stat);
					}
					else {
						timeSeriesErrors.add(dr);
					}
				}
				return new Integer(0);
			}

			protected void done()
			{
				setCursorDefault();
				if (_secondDataManager.checkForSevereError()){
					//_dssVueProgressBar.done(1);
					_secondDataManager.done();
					return;
				}
				int stat = -1;
				try {
					Integer ist = get();
					stat = ist.intValue();
				}
				catch (Exception e) {}
				if (stat < 0){
					//_dssVueProgressBar.done(1);
					JOptionPane.showMessageDialog(null, "Record copy failed;  status: " + stat,
												  _programName,
												  JOptionPane.WARNING_MESSAGE);
					//_dssVueProgressBar.setVisible(false);
					//_dssVueProgressBar = null;
					_secondDataManager.done();
					return;
				}
				if (!_operationCanceled) {
					//_dssVueProgressBar.done(0);
					int number = numberPaths - timeSeriesErrors.size();
					String message;
					if(number == 1) {
                        message = number + "  record copied to " +
							_secondDataManagerName;
					}
					else {
                        message = number + "  records copied to " +
							_secondDataManagerName;
					}
                    if(timeSeries.size() > 0) {
                        // Indicate that a time window was used!
                        DataReference dr = timeSeries.get(0);
                        HecTime start = new HecTime();
                        HecTime end = new HecTime();
                        dr.getTimeWindow(start, end);
                        message += "\nUsing time window " + start.toString() + "   " + end.toString();
						if (timeSeriesErrors.size() > 0){
							message += "\nError reading " + timeSeriesErrors.size() + " data set(s)";
							if (timeSeriesErrors.size() < 5){
								for (int i = 0; i < timeSeriesErrors.size(); i++){
									message += "\n" + timeSeriesErrors.get(i).getDSSPathname();
								}
							}
						}
                    }

					JOptionPane.showMessageDialog(null, message, _programName,
												  JOptionPane.INFORMATION_MESSAGE);
				}
				//_dssVueProgressBar.setVisible(false);
				//_dssVueProgressBar = null;
				_secondDataManager.done();
			}
		};
		//_dssVueProgressBar.go();
		copyRecs.execute();
	}
}
