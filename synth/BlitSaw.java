package synth;

/**
   An oscillator which outputs a Band-Limited Sawtooth Wave.  This is computed by integrating
   across a BLIT over time.  The output is then scaled by 0.8 and offset by 0.47.
*/

public class BlitSaw extends Blit
    {
    double prev = 0;

    protected double blitSaw(long tickCount, double freq)
        {
        double period = Config.SAMPLING_RATE/freq;
        double leak = 1.0-(2.0 * freq/Config.SAMPLING_RATE);
        double blitVal = blit(tickCount, 0);
        double dc = 1.0/period;
        double curr = blitVal -dc + leak * prev;
        prev = curr;
        return curr;
        }

    public double tick(long tickCount)
        {
        /// IMPLEMENT ME
        /// Also be sure to scale to the right values, see book
        double freq = valueToHz(getFrequencyUnit().getValue());
        if(freq == 0){
            return getValue();
        }
        double saw= blitSaw(tickCount, freq);
        return saw * 0.8 + 0.47;
        }
    }
