package wrims.schematic;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import wrims.dss.DssViewer;

public abstract class DssFrameRelatedPopup {

	public DssFrameRelatedPopup() {
	}

	public abstract JPopupMenu getPopup();

	/**
	 * CB - added to ease maintenance
	 * 
	 * @param text
	 * @return
	 */
	protected JMenu createSubmenu(String text, int keyCode) {
		JMenu submenu = new JMenu(text);
		submenu.setMnemonic(keyCode);
		if (!plotAction.canAct())
			submenu.setEnabled(false);
		// JMenuItem menuItem = new JMenuItem(plotAction);
		JMenu plotMenu = new JMenu("Plot Type");
		JMenuItem menuItem = new JMenuItem(plotAction);
		if (!plotAction.canAct())
			menuItem.setEnabled(false);
		menuItem.setMnemonic(KeyEvent.VK_P);
		// submenu.add(menuItem);
		plotMenu.add(menuItem);

		// CB added more plot options
		menuItem = new JMenuItem(exceedenceAction);
		if (!plotAction.canAct())
			menuItem.setEnabled(false);
		menuItem.setMnemonic(KeyEvent.VK_E);
		plotMenu.add(menuItem);
		menuItem = new JMenuItem(annualTotAction);
		if (!plotAction.canAct())
			menuItem.setEnabled(false);
		menuItem.setMnemonic(KeyEvent.VK_N);
		plotMenu.add(menuItem);
		menuItem = new JMenuItem(annualTotExceedAction);
		if (!plotAction.canAct())
			menuItem.setEnabled(false);
		menuItem.setMnemonic(KeyEvent.VK_X);
		plotMenu.add(menuItem);
		menuItem = new JMenuItem(monthlyAverageAction);
		if (!plotAction.canAct())
			menuItem.setEnabled(false);
		menuItem.setMnemonic(KeyEvent.VK_V);
		plotMenu.add(menuItem);

		submenu.add(plotMenu);
		menuItem = new JMenuItem(monthlyAction);
		if (!monthlyAction.canAct())
			menuItem.setEnabled(false);
		menuItem.setMnemonic(KeyEvent.VK_M);
		submenu.add(menuItem);
		menuItem = new JMenuItem(tableAction);
		if (!tableAction.canAct())
			menuItem.setEnabled(false);
		menuItem.setMnemonic(KeyEvent.VK_T);
		submenu.add(menuItem);
		menuItem = new JMenuItem(allAction);
		if (!allAction.canAct())
			menuItem.setEnabled(false);
		menuItem.setMnemonic(KeyEvent.VK_A);
		submenu.add(menuItem);
		return submenu;
	}

	// *********************************************************************************************
	// CB TO DO: create a DssFrameRelatedPopupAction class so that the below
	// objects don't have so
	// many lines of redundant code
	// *********************************************************************************************

	DssFrameRelatedAction plotAction = null; // extends AbstractAction
	{
		// Icon icon = iconImage("images/Copy24.gif");
		plotAction = new DssFrameRelatedAction(DssViewer.PLOT_STRING,
				SchematicUtils.schematic) {
			public void actionPerformed(ActionEvent e) {
				if (canAct()) {
					String outputType = "";
					MainFrame s = (MainFrame) getApp();
					if (s._DssFrame.getFP().filterByBpart(s.getSelectedNames())) {
						String variableType = "";
						if (e.getSource() instanceof JMenuItem) {
							outputType = ((JMenuItem) e.getSource()).getText();
							if (((JMenuItem) e.getSource()).getParent() instanceof JPopupMenu) {
								Component invoker = ((JPopupMenu) ((JMenuItem) e
										.getSource()).getParent()).getInvoker();
								if (invoker instanceof JMenu) {
									if (((JMenu) invoker).getText()
											.toLowerCase().indexOf("plot") == -1) {// CB
										// had
										// to
										// add
										// this
										// because
										// i
										// put
										// 3
										// actions
										// in
										// one
										// plot
										// type
										// submenu
										variableType = ((JMenu) invoker)
												.getText(); // CB now it is
										// "Plot Type"
									} else { // CB had to add this because i put
										// 3 actions in one plot type
										// submenu
										if (((JMenu) invoker).getParent() instanceof JPopupMenu) {
											invoker = ((JPopupMenu) ((JMenu) invoker)
													.getParent()).getInvoker();
											if (invoker instanceof JMenu) {
												variableType = ((JMenu) invoker)
														.getText();
											}
										}
									}
								}
							}
						}
						actionPerfomedHelper(outputType, variableType);
					} else { // CB added else block
						JOptionPane
								.showMessageDialog(
										((MainFrame) getApp())._DssFrame,
										"No variables in dss files that match any of the selected items",
										"Match Not Found",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			public boolean canAct() {
				return getApp() != null && getApp() instanceof MainFrame
						&& ((MainFrame) getApp())._DssFrame != null;
			}
		}; // doesn't depend on a view
	}

	DssFrameRelatedAction exceedenceAction = null; // extends AbstractAction
	{
		// Icon icon = iconImage("images/Copy24.gif");
		exceedenceAction = new DssFrameRelatedAction(
				DssViewer.EXCEEDENCE_STRING, SchematicUtils.schematic) {
			public void actionPerformed(ActionEvent e) {
				if (canAct()) {
					String outputType = "";
					MainFrame s = (MainFrame) getApp();
					if (s._DssFrame.getFP().filterByBpart(s.getSelectedNames())) {
						String variableType = "";
						if (e.getSource() instanceof JMenuItem) {
							outputType = ((JMenuItem) e.getSource()).getText();
							if (((JMenuItem) e.getSource()).getParent() instanceof JPopupMenu) {
								Component invoker = ((JPopupMenu) ((JMenuItem) e
										.getSource()).getParent()).getInvoker();
								if (invoker instanceof JMenu) {
									if (((JMenu) invoker).getText()
											.toLowerCase().indexOf("plot") == -1) {// CB
										// had
										// to
										// add
										// this
										// because
										// i
										// put
										// 3
										// actions
										// in
										// one
										// plot
										// type
										// submenu
										variableType = ((JMenu) invoker)
												.getText(); // CB now is
										// "Plot Type"
									} else { // CB had to add this because i put
										// 3 actions in one plot type
										// submenu
										if (((JMenu) invoker).getParent() instanceof JPopupMenu) {
											invoker = ((JPopupMenu) ((JMenu) invoker)
													.getParent()).getInvoker();
											if (invoker instanceof JMenu) {
												variableType = ((JMenu) invoker)
														.getText();
											}
										}
									}
								}
							}
						}
						actionPerfomedHelper(outputType, variableType);
					} else { // CB added else block
						JOptionPane
								.showMessageDialog(
										((MainFrame) getApp())._DssFrame,
										"No variables in dss files that match any of the selected items",
										"Match Not Found",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			public boolean canAct() {
				return getApp() != null && getApp() instanceof MainFrame
						&& ((MainFrame) getApp())._DssFrame != null;
			}
		}; // doesn't depend on a view
	}

	DssFrameRelatedAction annualTotAction = null; // extends AbstractAction
	{
		// Icon icon = iconImage("images/Copy24.gif");
		annualTotAction = new DssFrameRelatedAction(
				DssViewer.ANNUAL_TOTAL_STRING, SchematicUtils.schematic) {
			public void actionPerformed(ActionEvent e) {
				if (canAct()) {
					String outputType = "";
					MainFrame s = (MainFrame) getApp();
					if (s._DssFrame.getFP().filterByBpart(s.getSelectedNames())) {
						String variableType = "";
						if (e.getSource() instanceof JMenuItem) {
							outputType = ((JMenuItem) e.getSource()).getText();
							if (((JMenuItem) e.getSource()).getParent() instanceof JPopupMenu) {
								Component invoker = ((JPopupMenu) ((JMenuItem) e
										.getSource()).getParent()).getInvoker();
								if (invoker instanceof JMenu) {
									if (((JMenu) invoker).getText()
											.toLowerCase().indexOf("plot") == -1) {// CB
										// had
										// to
										// add
										// this
										// because
										// i
										// put
										// 3
										// actions
										// in
										// one
										// plot
										// type
										// submenu
										variableType = ((JMenu) invoker)
												.getText(); // CB now is
										// "Plot Type"
									} else { // CB had to add this because i put
										// 3 actions in one plot type
										// submenu
										if (((JMenu) invoker).getParent() instanceof JPopupMenu) {
											invoker = ((JPopupMenu) ((JMenu) invoker)
													.getParent()).getInvoker();
											if (invoker instanceof JMenu) {
												variableType = ((JMenu) invoker)
														.getText();
											}
										}
									}
								}
							}
						}
						actionPerfomedHelper(outputType, variableType);
					} else { // CB added else block
						JOptionPane
								.showMessageDialog(
										((MainFrame) getApp())._DssFrame,
										"No variables in dss files that match any of the selected items",
										"Match Not Found",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			public boolean canAct() {
				return getApp() != null && getApp() instanceof MainFrame
						&& ((MainFrame) getApp())._DssFrame != null;
			}
		}; // doesn't depend on a view
	}

	DssFrameRelatedAction annualTotExceedAction = null; // extends
	// AbstractAction
	{
		// Icon icon = iconImage("images/Copy24.gif");
		annualTotExceedAction = new DssFrameRelatedAction(
				DssViewer.ANNUAL_TOTAL_EXCEEDENCE_STRING,
				SchematicUtils.schematic) {
			public void actionPerformed(ActionEvent e) {
				if (canAct()) {
					String outputType = "";
					MainFrame s = (MainFrame) getApp();
					if (s._DssFrame.getFP().filterByBpart(s.getSelectedNames())) {
						String variableType = "";
						if (e.getSource() instanceof JMenuItem) {
							outputType = ((JMenuItem) e.getSource()).getText();
							if (((JMenuItem) e.getSource()).getParent() instanceof JPopupMenu) {
								Component invoker = ((JPopupMenu) ((JMenuItem) e
										.getSource()).getParent()).getInvoker();
								if (invoker instanceof JMenu) {
									if (((JMenu) invoker).getText()
											.toLowerCase().indexOf("plot") == -1) {
										variableType = ((JMenu) invoker)
												.getText(); // CB now is
										// "Plot Type"
									} else { // CB had to add this because i put
										// 3 actions in one plot type
										// submenu
										if (((JMenu) invoker).getParent() instanceof JPopupMenu) {
											invoker = ((JPopupMenu) ((JMenu) invoker)
													.getParent()).getInvoker();
											if (invoker instanceof JMenu) {
												variableType = ((JMenu) invoker)
														.getText();
											}
										}
									}
								}
							}
						}
						actionPerfomedHelper(outputType, variableType);
					} else { // CB added else block
						JOptionPane
								.showMessageDialog(
										((MainFrame) getApp())._DssFrame,
										"No variables in dss files that match any of the selected items",
										"Match Not Found",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			public boolean canAct() {
				return getApp() != null && getApp() instanceof MainFrame
						&& ((MainFrame) getApp())._DssFrame != null;
			}
		}; // doesn't depend on a view
	}

	/**
	 * CB added
	 */
	DssFrameRelatedAction monthlyAverageAction = null; // extends AbstractAction
	{
		// Icon icon = iconImage("images/Copy24.gif");
		monthlyAverageAction = new DssFrameRelatedAction(
				DssViewer.MONTHLY_AVERAGE_STRING, SchematicUtils.schematic) {
			public void actionPerformed(ActionEvent e) {
				if (canAct()) {
					String outputType = "";
					MainFrame s = (MainFrame) getApp();
					if (s._DssFrame.getFP().filterByBpart(s.getSelectedNames())) {
						String variableType = "";
						if (e.getSource() instanceof JMenuItem) {
							outputType = ((JMenuItem) e.getSource()).getText();
							if (((JMenuItem) e.getSource()).getParent() instanceof JPopupMenu) {
								Component invoker = ((JPopupMenu) ((JMenuItem) e
										.getSource()).getParent()).getInvoker();
								if (invoker instanceof JMenu) {
									if (((JMenu) invoker).getText()
											.toLowerCase().indexOf("plot") == -1) {// CB
										// had
										// to
										// add
										// this
										// because
										// i
										// put
										// 3
										// actions
										// in
										// one
										// plot
										// type
										// submenu
										variableType = ((JMenu) invoker)
												.getText(); // CB now is
										// "Plot Type"
									} else { // CB had to add this because i put
										// 3 actions in one plot type
										// submenu
										if (((JMenu) invoker).getParent() instanceof JPopupMenu) {
											invoker = ((JPopupMenu) ((JMenu) invoker)
													.getParent()).getInvoker();
											if (invoker instanceof JMenu) {
												variableType = ((JMenu) invoker)
														.getText();
											}
										}
									}
								}
							}
						}
						actionPerfomedHelper(outputType, variableType);
					} else { // CB added else block
						JOptionPane
								.showMessageDialog(
										((MainFrame) getApp())._DssFrame,
										"No variables in dss files that match any of the selected items",
										"Match Not Found",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			public boolean canAct() {
				return getApp() != null && getApp() instanceof MainFrame
						&& ((MainFrame) getApp())._DssFrame != null;
			}
		}; // doesn't depend on a view
	}

	DssFrameRelatedAction monthlyAction = null; // extends AbstractAction
	{
		// Icon icon = iconImage("images/Copy24.gif");
		monthlyAction = new DssFrameRelatedAction(DssViewer.MONTHLY_STRING,
				SchematicUtils.schematic) {
			public void actionPerformed(ActionEvent e) {
				if (canAct()) {
					MainFrame s = (MainFrame) getApp();
					if (s._DssFrame.getFP().filterByBpart(s.getSelectedNames())) {
						String outputType = "";
						String variableType = "";
						if (e.getSource() instanceof JMenuItem) {
							outputType = ((JMenuItem) e.getSource()).getText();
							if (((JMenuItem) e.getSource()).getParent() instanceof JPopupMenu) {
								Component invoker = ((JPopupMenu) ((JMenuItem) e
										.getSource()).getParent()).getInvoker();
								if (invoker instanceof JMenu) {
									variableType = ((JMenu) invoker).getText(); // e.e.,
									// like
									// "Storage (EOP)"
									// or
									// "Inflows"
								}
							}
						}
						actionPerfomedHelper(outputType, variableType);
					} else { // CB added else block
						JOptionPane
								.showMessageDialog(
										((MainFrame) getApp())._DssFrame,
										"No variables in dss files that match any of the selected items",
										"Match Not Found",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			public boolean canAct() {
				return getApp() != null && getApp() instanceof MainFrame
						&& ((MainFrame) getApp())._DssFrame != null;
			}
		}; // doesn't depend on a view
	}

	DssFrameRelatedAction tableAction = null; // extends AbstractAction
	{
		// Icon icon = iconImage("images/Copy24.gif");
		tableAction = new DssFrameRelatedAction(DssViewer.TABLE_STRING,
				SchematicUtils.schematic) {
			public void actionPerformed(ActionEvent e) {
				// System.out.println("entering actionPerformed of DssFrameRelatedAction tableAction");
				if (canAct()) {
					MainFrame s = (MainFrame) getApp();
					if (s._DssFrame.getFP().filterByBpart(s.getSelectedNames())) {
						String outputType = "";
						String variableType = "";
						if (e.getSource() instanceof JMenuItem) {
							outputType = ((JMenuItem) e.getSource()).getText();
							if (((JMenuItem) e.getSource()).getParent() instanceof JPopupMenu) {
								Component invoker = ((JPopupMenu) ((JMenuItem) e
										.getSource()).getParent()).getInvoker();
								if (invoker instanceof JMenu)
									variableType = ((JMenu) invoker).getText();
							}
						}
						actionPerfomedHelper(outputType, variableType);
					} else { // CB added else block
						JOptionPane
								.showMessageDialog(
										((MainFrame) getApp())._DssFrame,
										"No variables in dss files that match any of the selected items",
										"Match Not Found",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			public boolean canAct() {
				return getApp() != null && getApp() instanceof MainFrame
						&& ((MainFrame) getApp())._DssFrame != null;
			}
		}; // doesn't depend on a view
	}

	DssFrameRelatedAction allAction = null; // extends AbstractAction
	{
		// Icon icon = iconImage("images/Copy24.gif");
		allAction = new DssFrameRelatedAction(DssViewer.ALL_STRING,
				SchematicUtils.schematic) {
			public void actionPerformed(ActionEvent e) {
				if (canAct()) {
					MainFrame s = (MainFrame) getApp();
					if (s._DssFrame.getFP().filterByBpart(s.getSelectedNames())) {
						String outputType = "";
						String variableType = "";
						if (e.getSource() instanceof JMenuItem) {
							outputType = ((JMenuItem) e.getSource()).getText();
							if (((JMenuItem) e.getSource()).getParent() instanceof JPopupMenu) {
								Component invoker = ((JPopupMenu) ((JMenuItem) e
										.getSource()).getParent()).getInvoker();
								if (invoker instanceof JMenu)
									variableType = ((JMenu) invoker).getText();
							}
						}
						actionPerfomedHelper(outputType, variableType);
					} else { // CB added else block
						JOptionPane
								.showMessageDialog(
										((MainFrame) getApp())._DssFrame,
										"No variables in dss files that match any of the selected items",
										"Match Not Found",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			public boolean canAct() {
				return getApp() != null && getApp() instanceof MainFrame
						&& ((MainFrame) getApp())._DssFrame != null;
			}
		}; // doesn't depend on a view
	}
}
