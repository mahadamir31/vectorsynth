import synth.*;

public class Sine extends Osc 
    {
    public double tick(long tickCount) 
        {
        // IMPLEMENT ME
        // Remember that super.tick(tickCount) will return a value between 0.0 and 1.0
        // Map that into a sine wave. The sine wave should range from 0.0 to 1.0
        // rather than from -1 to +1.
        double value = super.tick(tickCount);
        double sine = Math.sin(2 * Math.PI * value);
        return (sine+1)/ 2;

        }
    }
