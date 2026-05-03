package synth;

/**
   A unit which aways returns a constant value, set during the constructor.
   You can change this value with setValue() if absolutely needed, but it's bad style.
*/

public class Constant extends Unit 
    {
    public Constant(double value) 
        {
        setValue(value);
        }

    public double tick(long tickCount) 
        {
        return getValue();
        }
    }
