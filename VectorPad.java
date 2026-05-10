import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class VectorPad extends JPanel{
    private final VectorOscillator oscillator;
    private double alpha = 0.5;
    private double beta = 0.5;

    // trajectory
    private static class Frame{
        double alpha;
        double beta;
        long timeMs;
        Frame(double a, double b, long t){
            alpha=a;
            beta=b;
            timeMs= t;
        }
    }
    private java.util.ArrayList<Frame> frames=new java.util.ArrayList<>();
    private boolean rec = false;
    private boolean play = false;
    private long recStart= 0;
    private javax.swing.Timer playTimer;

    public VectorPad(VectorOscillator osc){
        this.oscillator=osc;
        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                update(e);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent e){
                update(e);
            }
        });

        //buttons
        JPanel btnPanel= new JPanel();
        JButton recordBtn = new JButton("record");
        JButton playBtn = new JButton("play");
        JButton clearbtn = new JButton("clear");
        btnPanel.add(recordBtn);
        btnPanel.add(playBtn);
        btnPanel.add(clearbtn);
        setLayout(new BorderLayout());
        add(btnPanel,BorderLayout.SOUTH);

        recordBtn.addActionListener(e ->{
            frames.clear();
            rec =true;
            play= true;
            recStart= System.currentTimeMillis();
        });
        playBtn.addActionListener(e ->{
            if(frames.isEmpty()){
                return;
            }
            rec = false;
            play= true;
            playTimer.start();
        });
        clearbtn.addActionListener(e ->{
            frames.clear();
            rec = false;
            play=false;
            if(playTimer!=null) playTimer.stop();
        });
        playTimer=new javax.swing.Timer(16,e ->{
            if(frames.isEmpty()){
                return;
            }
            long total=frames.get(frames.size()-1).timeMs;
            long elapsed=(System.currentTimeMillis()-recStart)% total;
            Frame prev=frames.get(0), next= frames.get(0);
            for(int i=1;i<frames.size();i++){
                if (frames.get(i).timeMs>= elapsed){
                    prev=frames.get(i-1);
                    next=frames.get(i);
                    break;
                }
                prev=next = frames.get(i);
            }
            double t=(next.timeMs==prev.timeMs)?0 : (double)(elapsed - prev.timeMs) / (next.timeMs - prev.timeMs);
            alpha=prev.alpha+t*(next.alpha-prev.alpha);
            beta=prev.beta+t* (next.beta - prev.beta);
            oscillator.setPosition(alpha,beta);
            repaint();
        });
    }

    private void update(MouseEvent e){
        int w=getWidth();
        int h =getHeight();
        alpha =Math.max(0.0, Math.min(1.0,(double) e.getX()/w));
        beta= Math.max(0.0, Math.min(1.0,(double) e.getY()/h));
        //System.err.println("w=" + w + " h=" + h + " x=" + e.getX() + " y=" + e.getY() + " alpha=" + alpha + " beta=" + beta);
        oscillator.setPosition(alpha, beta);
        if(rec){
            frames.add(new Frame(alpha,beta,System.currentTimeMillis()-recStart));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w=getWidth();
        int h=getHeight();
        // background
        g2.setColor(new Color(20,20,30));
        g2.fillRect(0,0,w,h);
        // crosshair
        g2.setColor(new Color(50,50,70));
        g2.drawLine(w/2,0,w/2, h);
        g2.drawLine(0,h/2,w, h/2);

        // border
        g2.setColor(new Color(80,80,120));
        g2.drawRect(0,0,w-1,h-1);

        // corner labels
        g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,11));
        g2.setColor(new Color(140,140,200));
        g2.drawString("1",6,16);
        g2.drawString("2",w-52, 16);
        g2.drawString("3",6,h - 6);
        g2.drawString("4",w - 30,h - 6);

        // dot shadow
        int dotX=(int)(alpha*w);
        int dotY=(int)(beta* h);
        g2.setColor(new Color(0,0,0,100));
        g2.fillOval(dotX-7, dotY -6, 14,14);

        // dot
        g2.setColor(Color.WHITE);
        g2.fillOval(dotX-7, dotY-7,14,14);
        g2.setColor(new Color(120,180,255));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(dotX-7,dotY-7,14,14);
    }

    @Override public Dimension getPreferredSize(){
        return new Dimension(210,240);
    }
    @Override public Dimension getMinimumSize(){
        return new Dimension(210,240);
    }
    @Override public Dimension getMaximumSize(){
        return new Dimension(210,240);
    }
}

