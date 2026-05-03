/*
  Copyright 2023 by Sean Luke and Vi Hoyle
  Please see us for licensing beyond use in CS 499 / 695
*/

package synth;

/**
   A simple class which multiplies the value of an INPUT against a MULTIPLIER and then against a SCALE.
   That's all -- it's just mutiplying, just which is the input and which is the multiplier an
   which is the scale is not consequential.  Don't use this for amplification -- use Amplifier
   instead, which treats the input as signed centered at 0.5.
*/

public class Mul extends Unit 
    {
    private Unit input = new Constant(1.0);
    private Unit multiplier = new Constant(1.0);
    private Unit scale = new Constant(1.0);

    public Unit getScale() { return scale; }
    public void setScale(Unit scale) { this.scale = scale; }

    public Unit getMultiplier() { return multiplier; }
    public void setMultiplier(Unit multiplier) { this.multiplier = multiplier; }

    public Unit getInput() { return input; }
    public void setInput(Unit input) { this.input = input; }

    public double tick(long tickCount) 
        {
        return input.getValue() * multiplier.getValue() * scale.getValue();
        }
    }
