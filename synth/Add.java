/*
  Copyright 2023 by Sean Luke and Vi Hoyle
  Please see us for licensing beyond use in CS 499 / 695
*/

package synth;

/**
   A simple class which adds the value of an INPUT against an ADDER.
   That's all -- it's just adding, just which is the input and which is the adder
   is not consequential.  The two values are bounded to be between 0.0 and 1.0.
*/

public class Add extends Unit 
    {
    private Unit input = new Constant(1.0);
    private Unit adder = new Constant(0.0);

    public Unit getAdder() { return adder; }
    public void setAdder(Unit adder) { this.adder = adder; }

    public Unit getInput() { return input; }
    public void setInput(Unit input) { this.input = input; }

    public double tick(long tickCount) 
        {
        double val = input.getValue() + adder.getValue();
        if (val > 1) val = 1;
        return val;
        }
    }
