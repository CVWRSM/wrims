package wrimsv2_plugin.debugger.toolbaritem;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.internal.provisional.action.ToolBarContributionItem2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.view.WPPVariableView;
import wrimsv2_plugin.tools.TimeOperation;

public class DebugSet extends WorkbenchWindowControlContribution{
	private Combo _year;
	private Combo _month;
	private Combo _day;
	private Combo _cycle;
	private Slider timeSlider;
	private int _intMonth = 9;
	private int _intYear = 2009;
	private int _intDay = 30;
	private int totalMonth;
	private MouseListener tl;
	private DayItemListener dl=new DayItemListener();
	private MYItemListener ml=new MYItemListener(), yl=new MYItemListener();
	private int startDebugYear=1921;
	private int startDebugMonth=10;
	private int endDebugYear=2009;
	private int endDebugMonth=9;
	
	@Override
    protected Control createControl(Composite parent) {
       	
		CoolBar coolbar=new CoolBar(parent, SWT.HORIZONTAL|SWT.FLAT);

		createTimeSlider(coolbar);
		CoolItem itemTime=new CoolItem(coolbar, SWT.NONE);
		itemTime.setControl(timeSlider);
		itemTime.setSize(300,20);
		
		createComboYear(coolbar);
		CoolItem itemYear = new CoolItem(coolbar, SWT.None); 
		itemYear.setControl(_year);
		//Point pt=comboYear.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		//itemYear.setSize(itemYear.computeSize(pt.x, pt.y));
		itemYear.setSize(100,20);
				
		createComboMonth(coolbar);
		CoolItem itemMonth = new CoolItem(coolbar, SWT.NONE);	
		itemMonth.setControl(_month);
		//pt=comboMonth.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		//itemMonth.setSize(itemMonth.computeSize(pt.x, pt.y));
		itemMonth.setSize(100,20);
		
		createComboDay(coolbar);
		CoolItem itemDay = new CoolItem(coolbar, SWT.NONE);	
		itemDay.setControl(_day);
		//pt=comboDay.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		//itemDay.setSize(itemDay.computeSize(pt.x, pt.y));
		itemDay.setSize(100,20);
		
		createComboCycle(coolbar);
		CoolItem itemCycle = new CoolItem(coolbar, SWT.NONE);	
		itemCycle.setControl(_cycle);
		//pt=comboCycle.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		//itemCycle.setSize(itemCycle.computeSize(pt.x, pt.y));
		itemCycle.setSize(100,20);
		
		DebugCorePlugin.debugSet=this;
		
        return coolbar;
	}

	public Combo getComboYear(){
		return _year;
	}
	
	public Combo getComboMonth(){
		return _month;
	}
	
	public Combo getComboDay(){
		return _day;
	}
	
	public Combo getComboCycle(){
		return _cycle;
	}
	
	public void createTimeSlider(Composite parent){
		timeSlider=new Slider(parent, SWT.HORIZONTAL);
		timeSlider.setToolTipText("Go To Year/Month:");
		totalMonth=TimeOperation.findMonthInBetween(startDebugYear, startDebugMonth, endDebugYear, endDebugMonth);
		timeSlider.setMaximum(totalMonth+9);
		timeSlider.setSelection(totalMonth+8);
		
		timeSlider.addMouseListener(tl=new MouseListener(){

			@Override
			public void mouseUp(MouseEvent e) {
				int selection = timeSlider.getSelection();
				int[] yearMonth=TimeOperation.searchYearMonth(selection,startDebugYear, startDebugMonth);
				////_year.removeSelectionListener(yl);
				////_month.removeSelectionListener(ml);
				_year.setText(String.valueOf(yearMonth[0]));
				_month.setText(String.valueOf(yearMonth[1]));
				////_year.addSelectionListener(yl);
				////_month.addSelectionListener(ml);
				adjustDayCombo();
				updateDebugTimeSet();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void createComboCycle(Composite parent){
		_cycle = new Combo(parent, SWT.DROP_DOWN);
		for (int i=1; i<=99; i++){
			_cycle.add(String.valueOf(i));
		}
     
		_cycle.select(98);
		_cycle.setToolTipText("Go To Cycle:");
		_cycle.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateDebugTimeSet();
			}
          });
	}
	
	public void createComboYear(Composite parent){
        _year = new Combo(parent, SWT.DROP_DOWN);
        for (int i=startDebugYear; i<=endDebugYear; i++){
        	_year.add(String.valueOf(i));
        }
        
        _year.select(88);
        _year.setToolTipText("Go To Year:");
        
        _year.addSelectionListener(yl);
	}
	
	public void createComboMonth(Composite parent){
        _month = new Combo(parent, SWT.DROP_DOWN);
        for (int i=1; i<=12; i++){
        	_month.add(String.valueOf(i));
        }
        
        _month.select(8);
        _month.setToolTipText("Go To Month:");
        
		_month.addSelectionListener(ml);

	}
	
	public void createComboDay(Composite parent){
        _day = new Combo(parent, SWT.DROP_DOWN);
        
        for (int i=1; i<=31; i++){
        	_day.add(String.valueOf(i));
        }
        _day.select(29);
        _day.setToolTipText("Go To Day:");
        
		_day.addSelectionListener(dl);
	}
	
	public void updateDebugTimeSet(){
		DebugCorePlugin.debugYear=Integer.parseInt(_year.getText());
		DebugCorePlugin.debugMonth=Integer.parseInt(_month.getText());
		DebugCorePlugin.debugDay=Integer.parseInt(_day.getText());
		DebugCorePlugin.debugCycle=Integer.parseInt(_cycle.getText());
		if (DebugCorePlugin.isDebugging){
			try {
				DebugCorePlugin.target.sendRequest("time:"+DebugCorePlugin.debugYear+"/"+DebugCorePlugin.debugMonth+"/"+DebugCorePlugin.debugDay+"/"+DebugCorePlugin.debugCycle);
			} catch (DebugException e) {
				WPPException.handleException(e);
			}
		}
	}
	
	public void reset(){
		
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				timeSlider.removeMouseListener(tl);
				totalMonth=TimeOperation.findMonthInBetween(DebugCorePlugin.startYear, DebugCorePlugin.startMonth, DebugCorePlugin.endYear, DebugCorePlugin.endMonth);
				timeSlider.setMaximum(totalMonth+9);
				timeSlider.setSelection(totalMonth+8);
				timeSlider.addMouseListener(tl);
				
				_year.removeAll();
				for (int i=DebugCorePlugin.startYear; i<=DebugCorePlugin.endYear; i++){
					_year.add(String.valueOf(i));
				}		
				
				Date startDate = new Date(DebugCorePlugin.startYear-1900, DebugCorePlugin.startMonth-1, DebugCorePlugin.startDay);
				Date endDate = new Date(DebugCorePlugin.endYear-1900, DebugCorePlugin.endMonth-1, DebugCorePlugin.endDay);
				Date debugDate = new Date (DebugCorePlugin.debugYear-1900, DebugCorePlugin.debugMonth-1, DebugCorePlugin.debugDay);
				
				////_year.removeSelectionListener(yl);
				////_month.removeSelectionListener(ml);
				////_day.removeSelectionListener(dl);
				if (debugDate.after(endDate) || debugDate.before(startDate)){
					_year.setText(String.valueOf(DebugCorePlugin.endYear));
					_month.setText(String.valueOf(DebugCorePlugin.endMonth));
					_day.setText(String.valueOf(DebugCorePlugin.endDay));
					DebugCorePlugin.debugYear=DebugCorePlugin.endYear;
					DebugCorePlugin.debugMonth=DebugCorePlugin.endMonth;
					DebugCorePlugin.debugDay=DebugCorePlugin.endDay;
				}else{
					_month.setText(String.valueOf(DebugCorePlugin.debugMonth));
					_year.setText(String.valueOf(DebugCorePlugin.debugYear));
					_day.setText(String.valueOf(DebugCorePlugin.debugDay));
				}
				adjustDayCombo();
				moveSliderBar();
				////_year.addSelectionListener(yl);
				////_month.addSelectionListener(ml);
				////_day.addSelectionListener(dl);
			}
		});
	}
	
	public void nextTimeStep(){
		if (TimeOperation.isMonthlyInterval(DebugCorePlugin.timeStep)){
			timeSlider.setSelection(timeSlider.getSelection()+1);
			int selection = timeSlider.getSelection();
			int[] yearMonth=TimeOperation.searchYearMonth(selection,startDebugYear, startDebugMonth);
			_month.setText(String.valueOf(yearMonth[1]));
			_year.setText(String.valueOf(yearMonth[0]));
		}else{
			Calendar cal1 = new GregorianCalendar();
	        Calendar cal2 = new GregorianCalendar();
			
			cal1.set(DebugCorePlugin.debugYear, DebugCorePlugin.debugMonth, DebugCorePlugin.debugDay);
			cal2.set(DebugCorePlugin.endYear, DebugCorePlugin.endMonth, DebugCorePlugin.endDay);
			
			Date endDate= cal2.getTime();
			Date debugDate = cal1.getTime();
			if (endDate.after(debugDate)){
				Calendar c = Calendar.getInstance();
		        c.setTime(debugDate);
		        c.add(Calendar.DATE, 1);
		        debugDate = c.getTime();
				_day.setText(String.valueOf(debugDate.getDate()));
				_month.setText(String.valueOf(debugDate.getMonth()+1));
				_year.setText(String.valueOf(debugDate.getYear()+1900));
			}
		}
		updateDebugTimeSet();
	}
	
	public void setDebugDate(String year, String month, String day){
		_year.setText(year);
		_month.setText(month);
		_day.setText(day);
		moveSliderBar();
		updateDebugTimeSet();
	}
	
	public void moveSliderBar(){
		startDebugYear=DebugCorePlugin.startYear;
		startDebugMonth=DebugCorePlugin.startMonth;
		endDebugYear=DebugCorePlugin.endYear;
		endDebugMonth=DebugCorePlugin.endMonth;
		int debugYear=Integer.valueOf(_year.getText());
		int debugMonth=Integer.valueOf(_month.getText());
		int selection=TimeOperation.findMonthInBetween(startDebugYear, startDebugMonth, debugYear, debugMonth)-1;
		timeSlider.setSelection(selection);
	}
	
	private void adjustDayCombo(){
		////_day.removeSelectionListener(dl);
		_intMonth = new Integer(_month.getText());
		_intYear = new Integer(_year.getText());
		int day=new Integer(_day.getText());
		int maxDay=TimeOperation.numberOfDays(
				_intMonth, _intYear);
		for (int d = _day.getItemCount(); d >maxDay ; d--) {
			_day.remove(d - 1);
		}
		for (int d = _day.getItemCount(); d < maxDay; d++) {
			_day.add(new Integer(d + 1).toString());
		}
		if (day>maxDay){
			_day.select(maxDay-1);
			_intDay = maxDay;
		}	
		////_day.addSelectionListener(dl);
	}
	
	private class DayItemListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			_intDay = new Integer(_day.getText()).intValue();
			updateDebugTimeSet();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}

	private class MYItemListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			adjustDayCombo();
			moveSliderBar();
			updateDebugTimeSet();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}

}
