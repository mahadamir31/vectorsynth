package synth;
/**
   An oscillator which outputs a Band-Limited Pulse Wave.  This is computed by integrating
   across a BP-BLIT over time.  The output is then scaled by 0.7 and offset by 0.15.
*/

public class BlitSquare extends BPBlit
    {
    public static final double ALPHA = 0.001;

    // This might prove useful
    double prev = 0;

    protected double blitSquare(long tickCount)
        {
        /// IMPLEMENT ME
        /// Be sure to check for NaN before you return the value, and if so, return 0
        double square = bpblit(tickCount) + (1.0- ALPHA) * prev;
        prev = square;
        if(Double.isNaN(square)){
            prev = 0;
            return 0;
        }
        return square;
        }

    public double tick(long tickCount)
        {
        /// IMPLEMENT ME
        /// Be sure to grab the current phase
        /// Also be sure to scale to the right values, see book
        double freq = valueToHz((getFrequencyUnit().getValue()));
        double phase = getPhaseUnit().getValue();
        if(freq == 0){
            return getValue();
        }
        double square = blitSquare(tickCount);
        return square * 0.7 +0.15;
        }
    }
