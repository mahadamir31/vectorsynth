/*
  Copyright 2023 by Sean Luke and Vi Hoyle
  Please see us for licensing beyond use in CS 499 / 695
*/

package synth;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.border.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.locks.*;


/**
   A special unit which presents itself as a GUI dial widget.  Changing the dial with your 
   mouse will change the output of the underlying Unit.  You create the Dial first,
   then call getUnit() to extract the unit that it will control. 
*/


public class Dial extends JPanel
    {
    // The dial width
    public static final int DIAL_WIDTH = 20;
    // The thickness of the thicker dial stroke.  The thinner dial stroke will be half this width.
    public static final float STROKE_WIDTH = 4.0f;
    // Ths thickness of the thinner dial stroke.
    public static final BasicStroke THIN_STROKE = new BasicStroke(STROKE_WIDTH / 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    // Ths thickness of the thicker dial stroke.
    public static final BasicStroke THICK_STROKE = new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    // The color of the thin portion of the dial
    public static final Color THIN_COLOR = Color.BLACK;
    // The color of the thick portion of the dial when being changed in real time
    public static final Color DYNAMIC_COLOR = Color.RED;
    // The default color of the thick portion of the dial when not being changed in real time
    public static final Color DEFAULT_STATIC_COLOR = Color.BLUE;
    // The label font
    public static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    // The leaky integrator amount used to gradually move the reported value towards the desired value indicated by the user 
    public static final double DEFAULT_ALPHA = 0.99;
    // The distance the mouse travels to go 0...1
    public static final int SCALE = 256;

    // The color of the thick portion of the dial when not being changed
    Color staticColor = DEFAULT_STATIC_COLOR;
    // Is the user currently setting the dial?
    boolean dynamicallyChanging = false;
    // The value when the mouse was pressed 
    double startValue;
    // The Y mouse position when the mouse was pressed 
    int startY;
    // Is the mouse pressed?  This is part of a mechanism for dealing with
    // a stupidity in Java: if you PRESS in a widget, it'll be told. But if
    // you then drag elsewhere and RELEASE, the widget is never told.
    boolean mouseDown;
    // The Dial's title.  This is null until a LabelledDial is generated.
    JLabel title = null;
    // The Dial's displayed value.   This is null until a LabelledDial is generated.
    JLabel data = null;
    // The Dial's MidiUnit, if a CC parameter has been set, else null.
    MidiUnit midiUnit = null;
    // The Dial's CC parameter value, if a CC parameter has been set, else null.
    int ccParam;
    // The Dial's internal Unit
    DialUnit dialUnit = new DialUnit();
    // The Dial's alpha value
    double alpha = DEFAULT_ALPHA;
    
    /** Returns the Dial's internal Unit */
    public Unit getUnit() { return dialUnit; }
    
    /** Returns the color of the thick portion of the dial when not being changed. */
    public Color getStaticColor() { return staticColor; }
    
    /** Sets the color of the thick portion of the dial when not being changed.  
        If null, then the color is set to DEFAULT_STATIC_COLOR. */
    public void setStaticColor(Color color) { if (color == null) staticColor = DEFAULT_STATIC_COLOR; else staticColor = color; }
    
    /** Assigns the Dial to a CC paramter.  This must be a value 0...127. If midiUnit is null, then the Dial is unassignd. */
    public void setCC(MidiUnit midiUnit, int ccParam)
        {
        this.ccParam = ccParam;
        this.midiUnit = midiUnit;
        }
    
    /** Returns the preferred size of the Dial */
    public Dimension getPreferredSize() { return new Dimension(DIAL_WIDTH, DIAL_WIDTH); }
    
    /** Sets the preferred size of the Dial */
    public Dimension getMinimumSize() { return new Dimension(DIAL_WIDTH, DIAL_WIDTH); }
        
    
    /** The Unit which is updated in response to changing the Dial.
        When a user changes the Dial, the Unit's getValue() value gradually and smoothly
        moves towards the user's desired value, getting closer each time getValue() is called. 
        The DialUnit also handles incoming CC data in the same way. 
    */ 
        
    public class DialUnit extends Unit
        {
        // The internal value reported by getValue()
        double internalValue = 0;
        ReentrantLock lock = new ReentrantLock();

        // Sets the desired value but doesn't repaint
        double setValueNoRepaint(double value) 
            {
            lock.lock();
            try
                {
                double val = super.getValue();
                super.setValue(value);
                return val;
                }
            finally
                {
                lock.unlock();
                }
            }
                        
        /** Sets the desired value and repaints */
        public void setValue(double value) 
            {
            double val = setValueNoRepaint(value);
            if (val != value) repaint();
            }
                
        // Returns the desired value.
        double getDesiredValue()
            {
            lock.lock();
            try
                {
                return super.getValue();
                }
            finally
                {
                lock.unlock();
                }
            }

        /** Returns the current value, which gradually moves closer to the desiredValue on successive calls */
        public double getValue()
            {
            lock.lock();
            try
                {
                return internalValue;
                }
            finally
                {
                lock.unlock();
                }
            }

        public double tick(long tickCount) 
            {
            if (midiUnit != null)
                {
                if (midiUnit.isCCNew(ccParam))   // something new came in
                    {
                    setValue(midiUnit.getCC(ccParam));
                    }
                }

            // update internal value only
            lock.lock();
            try
                {
                internalValue = alpha * internalValue + (1.0 - alpha) * super.getValue();
                }
            finally
                {
                lock.unlock();
                }
            return super.getValue();
            }
        };

    /** Updates the dial to a new value */
    public void update(double val) 
        { 
        if (val < 0) val = 0; 
        if (val > 1) val = 1; 
        dialUnit.setValueNoRepaint(val);

        if (data != null)
            data.setText(" " + map(val));
        repaint();
        }

    /** Returns the String to be displayed for the given data value. */
    public String map(double val)
        { 
        return String.format("%.4f", val); 
        }
                                
    /** Returns the actual square within which the Dial's circle is drawn. */
    Rectangle getDrawSquare()
        {
        Insets insets = getInsets();
        Dimension size = getSize();
        int width = size.width - insets.left - insets.right;
        int height = size.height - insets.top - insets.bottom;
                
        // How big do we draw our circle?
        if (width > height)
            {
            // base it on height
            int h = height;
            int w = h;
            int y = insets.top;
            int x = insets.left + (width - w) / 2;
            return new Rectangle(x, y, w, h);
            }
        else
            {
            // base it on width
            int w = width;
            int h = w;
            int x = insets.left;
            int y = insets.top + (height - h) / 2;
            return new Rectangle(x, y, w, h);
            }
        }

                        
    // Fixes a bad bug where released isn't sent to the proper widget.  See below.
    AWTEventListener releaseListener = null;
    void mouseReleased(MouseEvent e)
        {          
        if (mouseDown)
            {
            mouseDown = false;
            dynamicallyChanging = false;
            repaint();
            if (releaseListener != null)
                Toolkit.getDefaultToolkit().removeAWTEventListener(releaseListener);
            }
        }
 
    /** Returns the Labelled version of this Dial. */
    public JPanel getLabelledDial(String label)
        {
        JPanel panel = new JPanel();
        JPanel subpanel = new JPanel();
        JPanel superpanel = new JPanel();
                        
        title = new JLabel(" " + label);
        title.setFont(FONT);
                
        data = new JLabel(" " + map(dialUnit.getDesiredValue()));
        data.setFont(FONT);
                        
        panel.setLayout(new BorderLayout());
        subpanel.setLayout(new BorderLayout());
        superpanel.setLayout(new BorderLayout());
                        
        panel.add(this, BorderLayout.WEST);
        subpanel.add(title, BorderLayout.NORTH);
        subpanel.add(data, BorderLayout.CENTER);
        panel.add(subpanel, BorderLayout.CENTER);
        superpanel.add(panel, BorderLayout.NORTH);
        return superpanel;
        }

    public Dial(double initialValue)
        {
        this(initialValue, DEFAULT_ALPHA);
        }
                
    public Dial(double initialValue, double alpha)
        {
        if (alpha <= 0) alpha = DEFAULT_ALPHA;
        else if (alpha > 1) alpha = DEFAULT_ALPHA;
        this.alpha = alpha;
        
        if (initialValue < 0) initialValue = 0;
        if (initialValue > 1) initialValue = 1;
        dialUnit.internalValue = initialValue;
        dialUnit.setValueNoRepaint(initialValue);
            
        addMouseListener(new MouseAdapter()
            {                        
            public void mousePressed(MouseEvent e)
                {                        
                mouseDown = true;
                startY = e.getY();
                startValue = dialUnit.getDesiredValue();
                dynamicallyChanging = true;
                repaint();

                if (releaseListener != null)
                    Toolkit.getDefaultToolkit().removeAWTEventListener(releaseListener);

                // This gunk fixes a BAD MISFEATURE in Java: mouseReleased isn't sent to the
                // same component that received mouseClicked.  What the ... ? Asinine.
                // So we create a global event listener which checks for mouseReleased and
                // calls our own function.  EVERYONE is going to do this.
                                
                Toolkit.getDefaultToolkit().addAWTEventListener( releaseListener = new AWTEventListener()
                    {
                    public void eventDispatched(AWTEvent e)
                        {
                        if (e instanceof MouseEvent && e.getID() == MouseEvent.MOUSE_RELEASED)
                            {
                            Dial.this.mouseReleased((MouseEvent)e);
                            }
                        }
                    }, AWTEvent.MOUSE_EVENT_MASK);
                }
                        
            MouseEvent lastRelease;
            public void mouseReleased(MouseEvent e)
                {
                if (e == lastRelease) // we just had this event because we're in the AWT Event Listener.  So we ignore it
                    return;
                    
                dynamicallyChanging = false;
                repaint();
                if (releaseListener != null)
                    Toolkit.getDefaultToolkit().removeAWTEventListener(releaseListener);
                lastRelease = e;
                }
            });
                        
        addMouseMotionListener(new MouseMotionAdapter()
            {
            public void mouseDragged(MouseEvent e)
                {
                // Propose a value based on Y
                
                int py = e.getY();                                
                int y = -(py - startY);
                int min = 0;
                int max = 1;
                double range = (max - min);
                double multiplicand = SCALE / range;
                double proposedValue = startValue + y / multiplicand;
                if (proposedValue < 0) proposedValue = 0;
                if (proposedValue > 1) proposedValue = 1;
                            
                // update                  
                if (dialUnit.getDesiredValue() != proposedValue)
                    {
                    update(proposedValue);
                    }
                }
            });

        repaint();
        }
        
        
    /** Returns the actual square within which the Dial's circle is drawn. */
    public void paintComponent(Graphics g)
        {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

                
        Rectangle rect = getBounds();
        rect.x = 0;
        rect.y = 0;
        graphics.setPaint(new JLabel("").getBackground());
        graphics.fill(rect);
        rect = getDrawSquare();

        graphics.setPaint(THIN_COLOR);
        graphics.setStroke(THIN_STROKE);
        Arc2D.Double arc = new Arc2D.Double();
        
        double startAngle = 90 + (270 / 2);
        double interval = -270;
                
        arc.setArc(rect.getX() + STROKE_WIDTH / 2, rect.getY() + STROKE_WIDTH/2, rect.getWidth() - STROKE_WIDTH, rect.getHeight() - STROKE_WIDTH, startAngle, interval, Arc2D.OPEN);

        graphics.draw(arc);
        graphics.setStroke(THICK_STROKE);
        arc = new Arc2D.Double();
                
        double value = dialUnit.getDesiredValue();
        double min = 0;
        double max = 1;
        interval = -((value - min) / (double)(max - min) * 265) - 5;

        if (dynamicallyChanging)
            {
            graphics.setPaint(DYNAMIC_COLOR);
            if (value == min)
                {
                interval = -5;
                }
            }
        else
            {
            graphics.setPaint(getStaticColor());
            if (value == min)
                {
                interval = 0;
                }
            }

        arc.setArc(rect.getX() + STROKE_WIDTH / 2, rect.getY() + STROKE_WIDTH/2, rect.getWidth() - STROKE_WIDTH, rect.getHeight() - STROKE_WIDTH, startAngle, interval, Arc2D.OPEN);            
        graphics.draw(arc);
        }
    }
