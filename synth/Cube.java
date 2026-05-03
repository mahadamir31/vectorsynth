/*
  Copyright 2023 by Sean Luke and Vi Hoyle
  Please see us for licensing beyond use in CS 499 / 695
*/

package synth;

/**
   A simple class which takes an input X and returns X^3.
*/

public class Cube extends Unit 
    {
    private Unit input = new Constant(1.0);

    public Unit getInput() { return input; }
    public void setInput(Unit input) { this.input = input; }

    public double tick(long tickCount) 
        {
        double val = input.getValue();
        return val * val * val;
        }
    }
