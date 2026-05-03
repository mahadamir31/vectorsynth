package synth;
/**
   An oscillator which outputs a Band-Limited Triangle Wave by integrating over a Blit Square.
   Alpha is updated as 1.0 - 0.1 * min(freq/1000, 1) to eliminate most pops.  The output is then
   scaled by 4 and offset by 0.5.
*/

public class BlitTriangle extends BlitSquare
    {
    // This might prove useful
    double prev = 0;

    protected double blitTriangle(long tickCount, double freq)
        {
        /// IMPLEMENT ME
        /// Be sure to check for NaN before you return the value, and if so, return 0
        double alpha = 1.0-0.1 * Math.min(freq/1000.0, 1.0);
        double square = blitSquare(tickCount);
        double triangle = square+alpha *prev;
        prev=triangle;
        if(Double.isNaN(triangle)){
            prev = 0;
            return 0;
        }
        return triangle;
        }

    public double tick(long tickCount)
        {
        /// IMPLEMENT ME
        /// Be sure to grab the current phase
        /// Also be sure to scale to the right values, see book
        double freq = valueToHz(getFrequencyUnit().getValue());
        if(freq==0){
            return getValue();
        }
        double triangle= blitTriangle(tickCount, freq);
        return triangle * 4.0 +0.5;
        }
    }
