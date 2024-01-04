package calsim.msw;

import javax.swing.JOptionPane;

/**
 * @author Joel Fenolio
 * @author Clay Booher
 */
public class MSWThread extends Thread {
	private String _path;
	private MSWGui _gui;
	private MultiStudyWrapper _msw;    // old version
//	private MultiStudyWrapperNew _mswn; // CB new version
//	private boolean useNewVersion = true; // SWITCH BETWEEN NEW VERSION AND OLD

	public MSWThread(String path, MSWGui gui) {
		_path = path;
		_gui = gui;
	}

	public void run() {
		_gui.setWasStopped(false);
		try {
//			if (useNewVersion) {
//				if (_mswn != null) _mswn = null; // to prevent lockup from quick run click after previous run
//				_mswn = new MultiStudyWrapperNew(_path, _gui); // CB new version
//			} else {
				if (_msw != null) _msw = null; // to prevent lockup from quick run click after previous run
				_msw = new MultiStudyWrapper(_path, _gui);    // old version
//			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Exception: " + e, e.getMessage(),
				JOptionPane.ERROR_MESSAGE);
			_gui.setWasStopped(true);
			e.printStackTrace();
			_gui.println("Error occurred: run not started");
			return;
		}
		if (_gui.wasStopPushed()) {
			_gui.setWasStopped(true);
			return;
		}
		try {
//			if (useNewVersion) _mswn.run();
//			else _msw.run();
			_msw.run();
			String title;
			int messageType;
			if (_gui.wasRunSuccessful()) {
				_gui.setRunDoneMessage("Run completed successfully!");
				title = "Success!";
				messageType = JOptionPane.INFORMATION_MESSAGE;
			    JOptionPane.showMessageDialog(_gui, _gui.getRunDoneMessage(), title, messageType);
			} else {
//				title = "Problem During Run";
//				messageType = JOptionPane.ERROR_MESSAGE;
//			    JOptionPane.showMessageDialog(_gui, _gui.getRunDoneMessage(), title, messageType);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Exception: " + e, e.getMessage(),
				JOptionPane.ERROR_MESSAGE);
			_gui.setWasStopped(true);
			e.printStackTrace();
			_msw = null;
//			_mswn = null;
			_gui.println("Error occurred: error during the run");
		} finally {
			_gui.setWasStopped(true);
			_msw = null;
//			_mswn = null;
			_gui.setRunDone(true);
		}
	}
}

