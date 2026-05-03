// Copyright 2018 by George Mason University
// Licensed under the Apache 2.0 License

import synth.*;

/**
   A superclass for oscillators.  This basic class performs a simple ramp-style oscillation 
   from 0...1 through the period of the wave.  You could use the tick value to compute more
   useful oscillation functions in subclasses.
*/
 
public class LFO extends Osc 
    {
    public static final String[] TYPES = new String[] { "Sine", "Ramp", "Sawtooth", "Square", "Triangle"};
    public static final int LFO_TYPE_SINE = 0;
    public static final int LFO_TYPE_RAMP = 1;
    public static final int LFO_TYPE_SAW = 2;
    public static final int LFO_TYPE_SQUARE = 3;
    public static final int LFO_TYPE_TRIANGLE = 4;    
    int type = LFO_TYPE_SINE;

    public int getType()
        {
        return type;
        }
        
    public void setType(int type) 
        {
        this.type = type;
        }
    
    public Options getOptions()
        {
        return new Options("Types", TYPES, 0)
            {
            public void update(int val)
                {
                // this is a race condition but whatever
                setType(val);
                }
            };
        }
        
    public double tick(long tickCount) 
        {
        double phase = super.tick(tickCount);
        if (type == LFO_TYPE_SINE)
            {
            return (Math.sin(2.0*Math.PI*phase)+1.0) *0.5;
            }
        else if (type == LFO_TYPE_RAMP)
            {
            return phase;
            }
        else if (type == LFO_TYPE_SAW)
            {
            return 1.0- phase;
            }
        else if (type == LFO_TYPE_SQUARE)
            {
            return phase <0.5 ? 1.0 :0.0;
            }
        else if (type == LFO_TYPE_TRIANGLE)
            {
            return phase < 0.5 ? phase *2.0 : ((1.0 -phase) * 2.0);
            }
        else    // uh...
            {
            System.err.println("LFO Internal Error: this should be unreachable");
            return 0.5;
            }
        }
    
    }
