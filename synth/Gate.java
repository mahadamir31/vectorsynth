/*
  Copyright 2023 by Sean Luke and Vi Hoyle
  Please see us for licensing beyond use in CS 499 / 695
*/

package synth;

/**
   A small unit which outputs 1 when a note is down and 0 when the note is up. 
*/

public class Gate extends Unit 
    {
    MidiUnit midiUnit;

    public Gate(MidiUnit midiUnit) { this.midiUnit = midiUnit; }

    public double tick(long tickCount) 
        {
        return this.midiUnit.getGate();
        }
    }
