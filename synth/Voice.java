/*
  Copyright 2023 by Sean Luke and Vi Hoyle
  Please see us for licensing beyond use in CS 499 / 695
*/

package synth;

public class Voice extends MidiUnit
    {
    public static final int STATE_OFF = 0;
    public static final int STATE_ON = 1;
    public static final int STATE_RESET = 2;
    
    int state = STATE_OFF;
    int midiNote = 0;
    int midiVelocity = 0;
    double velocity = 0;
    int stamp = 0;
    boolean allocated = false;
    MidiUnit midiUnit;

    public Voice()
        {
        super(null, null);
        }

    public void setMidiUnit(MidiUnit midiUnit) { this.midiUnit = midiUnit; }
        
    public double getGate()
        {
        if (state == STATE_OFF || state == STATE_RESET) return 0;
        else return 1;
        }
                
    public double tick(long tickCount) 
        {
        if (state == STATE_RESET)
            {
            state = STATE_ON;
            }

        velocity = midiVelocity / 127.0;
        return hzToValue(440.0 * Math.pow(BASE, midiNote - 69) * midiUnit.getBend());
        }
    
    public boolean isAllocated()
        {
        return allocated;
        }
        
    public int getStamp()
        {
        return stamp;
        }
        
    public void allocate(int pitch, int vel, int stamp)
        {
        this.stamp = stamp;
        midiNote = pitch;
        midiVelocity = vel;
        allocated = true;
        if (state == STATE_ON)
            state = STATE_RESET;
        else
            state = STATE_ON;
        }
        
    public void deallocate(int vel)
        {
        stamp = -1;
        allocated = false;
        state = STATE_OFF;
        }
                
    public double getBend() { return midiUnit.getBend(); }
        
    public double getPitch() { return midiUnit.getPitch(); }

    public double getVelocity() { return velocity; }

    public double getAftertouch() { return midiUnit.getAftertouch(); }
        
    public double getCC(int param) { return midiUnit.getCC(param); }
        
    public boolean isCCNew(int param) { return midiUnit.isCCNew(param); }
    }
