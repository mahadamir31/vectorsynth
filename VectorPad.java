import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class VectorPad extends JPanel {

    private final VectorOscillator oscillator;
    private double alpha = 0.5;
    private double beta  = 0.5;

    public VectorPad(VectorOscillator osc) {
        this.oscillator = osc;
        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.WHITE));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                update(e);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                update(e);
            }
        });
    }

    private void update(MouseEvent e) {
        int w = getWidth();
        int h = getHeight();
        alpha = Math.max(0.0, Math.min(1.0, (double) e.getX() / w));
        beta  = Math.max(0.0, Math.min(1.0, (double) e.getY() / h));
        //System.err.println("w=" + w + " h=" + h + " x=" + e.getX() + " y=" + e.getY() + " alpha=" + alpha + " beta=" + beta);
        oscillator.setPosition(alpha, beta);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // background
        g2.setColor(new Color(20, 20, 30));
        g2.fillRect(0, 0, w, h);

        // crosshair
        g2.setColor(new Color(50, 50, 70));
        g2.drawLine(w/2, 0, w/2, h);
        g2.drawLine(0, h/2, w, h/2);

        // border
        g2.setColor(new Color(80, 80, 120));
        g2.drawRect(0, 0, w-1, h-1);

        // corner labels
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        g2.setColor(new Color(140, 140, 200));
        g2.drawString("Saw",      6, 16);
        g2.drawString("Square",   w - 52, 16);
        g2.drawString("Triangle", 6, h - 6);
        g2.drawString("Blit",     w - 30, h - 6);

        // dot shadow
        int dotX = (int)(alpha * w);
        int dotY = (int)(beta  * h);
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillOval(dotX - 7, dotY - 6, 14, 14);

        // dot
        g2.setColor(Color.WHITE);
        g2.fillOval(dotX - 7, dotY - 7, 14, 14);
        g2.setColor(new Color(120, 180, 255));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(dotX - 7, dotY - 7, 14, 14);
    }


    @Override public Dimension getPreferredSize() { return new Dimension(210, 210); }
    @Override public Dimension getMinimumSize()   { return new Dimension(210, 210); }
    @Override public Dimension getMaximumSize()   { return new Dimension(210, 210); }
}

