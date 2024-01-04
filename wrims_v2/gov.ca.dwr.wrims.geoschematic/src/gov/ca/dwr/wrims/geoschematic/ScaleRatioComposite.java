package gov.ca.dwr.wrims.geoschematic;

import java.text.NumberFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.commands.SetScaleCommand;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.ViewportModelEvent;
import org.locationtech.udig.project.render.ViewportModelEvent.EventType;

/**
 * Adapted from org.locationtech.udig.project.ui.controls.ScaleRatioLabel
 * 
 * @author egouge
 *
 */
public class ScaleRatioComposite extends Composite implements FocusListener, KeyListener {

	private NumberFormat nf = NumberFormat.getIntegerInstance();
	private Combo combo;
	private IMap map;

	private boolean inDialog = false;
    /** Listens to viewport changes and updates the displayed scale accordingly */
    private IViewportModelListener listener = new IViewportModelListener(){
        public void changed( ViewportModelEvent event ) {
            if (event.getType() == EventType.CRS || event.getType() == EventType.BOUNDS) {
                Display display = PlatformUI.getWorkbench().getDisplay();
                if (display == null)
                    display = Display.getDefault();

                display.asyncExec(new Runnable(){

                    public void run() {
                        updateScale();
                    }
                });
            }
        }
    };    
    
	public ScaleRatioComposite(Composite parent, IMap map) {
		this(parent, map, false);
	}
	public ScaleRatioComposite(Composite parent, IMap map, boolean inDialog) {
		super(parent, SWT.NONE);
		this.map = map;
		this.inDialog = inDialog;
		map.getViewportModel().addViewportModelListener(listener);
		createContents();
	}

	@Override
	public void dispose() {
		if (combo != null)
			combo.dispose();
		map.getViewportModel().removeViewportModelListener(listener);

		super.dispose();
	}

	private void createContents( ) {
		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = gl.verticalSpacing = gl.marginBottom = gl.marginHeight = gl.marginLeft = gl.marginRight = gl.marginTop = gl.marginWidth = 0;
		setLayout(gl);
		
		combo = new Combo(this, SWT.BORDER|SWT.CENTER|SWT.DROP_DOWN);
        
        combo.addKeyListener(this);
        combo.addFocusListener(this);
        combo.addListener(SWT.MouseDown, new Listener(){
           public void handleEvent(Event e){
               if( combo.getText().contains(":") ) //$NON-NLS-1$
                   formatForEditing();
           }
        });
        combo.addSelectionListener( new SelectionListener(){            
            public void widgetSelected( SelectionEvent e ) {
                if( combo.getText().contains(":") ) //$NON-NLS-1$
                    formatForEditing();
                updateMap();
            }
            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });
        combo.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.character == SWT.Selection && e.getSource() == combo) {
					e.doit = false;
				}
			}
		});
        combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        updateScale();
	}
	
	
	@Override
	public void focusGained(FocusEvent e) {
		formatForEditing();
	}

	@Override
	public void focusLost(FocusEvent e) {
		updateScale();
	}
	 
	
	private void formatForEditing() {
		String text = combo.getText();
		int index = text.indexOf(':');
		if (index >= 0){
			//take the second part
			text = text.substring(index+1);
		}
		text= text.replaceAll(",", ""); //$NON-NLS-1$ //$NON-NLS-2$
		combo.setText(text);
		
		
		combo.setSelection(new Point(0, text.length()));
	}
	    
	
	private void updateScale() {
        if (combo == null || combo.isDisposed())
            return;

        if (map.getViewportModel() != null) {
            combo.removeAll();
            for( double scaleDenominator : map.getViewportModel().getPreferredScaleDenominators() ){
                String item = toLabel( scaleDenominator );
                combo.add( item );
            }
            try{
            	combo.setText( toLabel(map.getViewportModel().getScaleDenominator()));
            }catch (Throwable ex){
            	combo.setText("ERROR");
            }
            combo.setToolTipText(combo.getText());
        } else {
            combo.setText(""); //$NON-NLS-1$
        }
    }
	    
    private String toLabel( double scaleDenominator ){
        return "1:" + nf.format( scaleDenominator ); //$NON-NLS-1$
    }
    
    public void keyPressed(KeyEvent e) {
        if( combo.getText().contains(":") ) //$NON-NLS-1$
            formatForEditing();
        if( !isLegalKey(e) ){
            e.doit=false;
        }
    }
    
    public boolean isLegalKey(KeyEvent e){
        char c=e.character;
        
        if( c == '0' ||
                c == '1' ||
                c == '2' ||
                c == '3' ||
                c == '4' ||
                c == '5' ||
                c == '6' ||
                c == '7' ||
                c == '8' ||
                c == '9' ||
                c == SWT.DEL ||
                c == SWT.BS ){
            return true;
        }
        
        if( e.keyCode == SWT.ARROW_LEFT ||
                e.keyCode == SWT.ARROW_RIGHT ||
                e.keyCode == SWT.HOME ||
                e.keyCode == SWT.END ||
                e.keyCode == SWT.OK)
            return true;
        
        return false;
            
    }

    public void keyReleased(KeyEvent e) {
        if (e.character == SWT.Selection) {
            updateMap();
        } else if (e.character == SWT.ESC) {
            updateScale();
        }
        
    }
    private void updateMap() {
        String newScale=combo.getText().trim();
        try{
        	double d = nf.parse(newScale.replace(" ","")).doubleValue(); //$NON-NLS-1$ //$NON-NLS-2$
            SetScaleCommand command=new SetScaleCommand(d);
			if (inDialog) {
				//when running in dialog current must manuall refresh viewport
				command = new SetScaleCommand(d) {
					public void run(IProgressMonitor monitor) throws Exception {
						super.run(monitor);
						getMap().getRenderManager().refresh(null);
					}
				};
			}
            this.map.sendCommandASync(command);
        }catch(Exception e){
           
        }
    }
}
