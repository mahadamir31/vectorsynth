import synth.*;

/**
   An oscillator which outputs a Band-Limited Impulse Train (or BLIT).
*/


public class Blit extends Osc 
    {
    public double sincm(double x, double m)
        {
        double denominator = m*Math.sin(Math.PI * x/m);
        if(Math.abs(denominator) < 1e-9){
            return 1.0;
        }else{
            return Math.sin(Math.PI * x) /denominator;
        }
        }

    /// HINT: d goes from 0...1.  The purpose of d is to allow an offset in phase.
    /// What might be able to take advantage of this function?
        
    protected double blit(long tickCount, double d) 
        {
        double phase = super.tick(tickCount);
        double freq = valueToHz(getFrequencyUnit().getValue());
        if (freq == 0) 
            { 
            return getValue(); 
            }
        else
            {
            double p = Config.SAMPLING_RATE / freq;
            double m = Math.floor(p / 2.0) * 2.0 + 1.0;
            return (m / p) * sincm((phase - d) * m, m);
            }
        }

    public double tick(long tickCount)
        {
        if (valueToHz(getFrequencyUnit().getValue()) == 0)
            {
            return getValue();
            }    
        else
            {
            return blit(tickCount, 0) * 0.5 + 0.5;
            }
        }
    }
