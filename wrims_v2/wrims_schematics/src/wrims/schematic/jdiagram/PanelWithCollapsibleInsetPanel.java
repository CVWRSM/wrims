package wrims.schematic.jdiagram;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PanelWithCollapsibleInsetPanel extends JPanel {
	private JLayeredPane layeredPane;
	private JPanel mainPanel;
	private JPanel insetPanel;
	private JButton toggleButton;
	private Dimension insetPanelSize;

	public PanelWithCollapsibleInsetPanel(boolean collapsed) {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		layeredPane = new JLayeredPane();
		final Icon minusIcon = ImageUtil
				.createImageIcon("images/icon-collapse-minus.gif");
		final Icon plusIcon = ImageUtil
				.createImageIcon("images/icon-collapse-plus.gif");
		toggleButton = new JButton(minusIcon);
		toggleButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (toggleButton.getIcon().equals(plusIcon)) {
					toggleButton.setIcon(minusIcon);
					expand();
				} else {
					toggleButton.setIcon(plusIcon);
					collapse();
				}
			}
		});
		if (collapsed) {
			toggleButton.setIcon(plusIcon);
		}
		insetPanel = new JPanel();
		insetPanel.setPreferredSize(insetPanelSize = new Dimension(280, 400));
		insetPanel.setLayout(new BoxLayout(insetPanel, BoxLayout.PAGE_AXIS));
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		toggleButton.setPreferredSize(new Dimension(22, 22));
		layeredPane.add(toggleButton, new Integer(3));
		layeredPane.add(insetPanel, new Integer(2));
		layeredPane.add(mainPanel, new Integer(1));
		layeredPane.setPreferredSize(new Dimension(400, 400));
		insetPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		this.add(layeredPane);
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
	}

	@Override
	public void doLayout() {
		super.doLayout();
		Container parent = this;
		Dimension size = parent.getSize();
		Insets insets = parent.getInsets();
		Rectangle mainBounds = new Rectangle(0, 0, size.width - insets.left
				- insets.right, size.height - insets.top - insets.bottom);
		mainPanel.setBounds(mainBounds);
		Dimension insetSize = insetPanel.getPreferredSize();
		Rectangle insetBounds = new Rectangle(size.width - insets.left
				- insetSize.width, size.height - insets.bottom
				- insetSize.height, insetSize.width, insetSize.height);
		insetPanel.setBounds(insetBounds);
		Dimension buttonSize = toggleButton.getPreferredSize();
		Rectangle buttonBounds = new Rectangle(insetBounds.x, insetBounds.y,
				buttonSize.width, buttonSize.height);
		if (insetSize.width == 0) {
			buttonBounds.x = size.width - insets.left - buttonSize.width;
			buttonBounds.y = size.height - insets.bottom - buttonSize.height;
		}
		toggleButton.setBounds(buttonBounds);
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JPanel getInsetPanel() {
		return insetPanel;
	}

	public void expand() {
		insetPanel.setPreferredSize(insetPanelSize);
		this.doLayout();
		insetPanel.paintAll(insetPanel.getGraphics());
	}

	public void collapse() {
		insetPanel.setPreferredSize(new Dimension(0, 0));
		this.doLayout();
	}
}
