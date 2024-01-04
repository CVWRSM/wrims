package wvscript.gui;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/** @see http://stackoverflow.com/questions/4053090 */
public class ImageDisplay extends JFrame {

    private static final String title = "Select a file";
    private MyImagePanel imagePanel = new MyImagePanel();
    private JLabel result = new JLabel(title, JLabel.CENTER);
    private MyChooser fileChooser = new MyChooser();

    public ImageDisplay(String name) {
        super(name);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWidgets();
        this.pack();
        this.setVisible(true);
    }

    private void addWidgets() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Images", "jpg", "JPG", "GIF", "gif", "JPEG", "png", "PNG");
        fileChooser.setFileFilter(filter);
        this.add(fileChooser, BorderLayout.WEST);
        this.add(new JScrollPane(imagePanel), BorderLayout.CENTER);
        this.add(result, BorderLayout.SOUTH);
    }

    class MyChooser extends JFileChooser {

        @Override
        public void approveSelection() {
            File f = fileChooser.getSelectedFile();
            if (imagePanel.setImage(f)) {
                result.setText(f.getName());
            } else {
                result.setText(title);
            }
        }

        @Override
        public void cancelSelection() {
            imagePanel.setImage(null);
            result.setText(title);
        }
    }

    class MyImagePanel extends JPanel {

        private BufferedImage bi;

        public MyImagePanel() {
            this.setPreferredSize(new Dimension(500, 700));
        }

        /** Return true if read() succeeded. */
        public boolean setImage(File f) {
            try {
                bi = ImageIO.read(f);
            } catch (Exception e) {
                bi = null;
            }
            if (bi != null) {
                setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
            }
            this.revalidate();
            this.repaint();
            return bi != null;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bi, 0, 0, null);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ImageDisplay("Image Demo").setVisible(true);
            }
        });
    }
}