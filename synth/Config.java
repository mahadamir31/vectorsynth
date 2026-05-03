package synth;

/**
   Configuration parameters. 
*/

public class Config 
    {
    /** Sampling rate of the sound. */
    public static final double SAMPLING_RATE = 48000.0;
    
    /** Inverse sampling rate of the sound. */
    public static final double INV_SAMPLING_RATE = 1.0 / SAMPLING_RATE;
    
    /** Nyquist limit frequency of the sound (half the sampling rate). */
    public static final double NYQUIST_LIMIT = SAMPLING_RATE / 2.0;
    
    /** Inverse Nyquist limit frequency of the sound. */
    public static final double INV_NYQUIST_LIMIT = 1.0 / NYQUIST_LIMIT;
    }
