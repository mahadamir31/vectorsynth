import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class VectorPad extends JPanel {
    private static final int PAD  = 180;
    private static final int BORDER = 15;

    private final VectorOscillator oscillator;
    private int dotX = BORDER + PAD/2;
    private int dotY = BORDER + PAD/2;

    public VectorPad(VectorOscillator osc) {
        this.oscillator = osc;
        setPreferredSize(new Dimension(PAD + BORDER*2, PAD + BORDER*2));
        setBackground(new Color(30, 30, 40));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { move(e); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { move(e); }
        });
    }

    private void move(MouseEvent e) {
        dotX = Math.max(BORDER, Math.min(BORDER + PAD, e.getX()));
        dotY = Math.max(BORDER, Math.min(BORDER + PAD, e.getY()));
        double alpha = (double)(dotX - BORDER) / PAD;
        double beta  = (double)(dotY - BORDER) / PAD;
        oscillator.setPosition(alpha, beta);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // grid
        g2.setColor(new Color(70, 70, 90));
        g2.drawRect(BORDER, BORDER, PAD, PAD);
        g2.setColor(new Color(50, 50, 70));
        g2.drawLine(BORDER + PAD/2, BORDER, BORDER + PAD/2, BORDER + PAD);
        g2.drawLine(BORDER, BORDER + PAD/2, BORDER + PAD, BORDER + PAD/2);

        // corner labels
        g2.setColor(new Color(160, 160, 200));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        g2.drawString("Saw",      BORDER + 3,       BORDER + 12);
        g2.drawString("Square",   BORDER + PAD - 42, BORDER + 12);
        g2.drawString("Triangle", BORDER + 3,       BORDER + PAD - 4);
        g2.drawString("Blit",     BORDER + PAD - 24, BORDER + PAD - 4);

        // dot
        g2.setColor(Color.WHITE);
        g2.fillOval(dotX - 6, dotY - 6, 12, 12);
        g2.setColor(new Color(150, 200, 255));
        g2.drawOval(dotX - 6, dotY - 6, 12, 12);
    }
}