import synth.*;

/* 
 * Utility unit that provides some common parameters for all units inteded to be oscillators
 */
public class PM extends Osc 
    {
    public static final double MAX_RELATIVE_FREQUENCY = 10;  // The DX7 uses 32
    Unit relativeFrequency = new Constant(1.0);
    public void setRelativeFrequency(Unit p) { relativeFrequency = p; }
    public Unit getRelativeFrequency() { return relativeFrequency; }
    
    // This is the Modulator M
    Unit phaseModulator = new Constant(0.0);
    public void setPhaseModulator(Unit p) { phaseModulator = p; }
    public Unit getPhaseModulator() { return phaseModulator; }
    
    // This is Beta
    public static final double MAX_PHASE_AMPLIFICATION = 4;
    Unit phaseAmplifier = new Constant(1.0);
    public void setPhaseAmplifier(Unit p) { phaseAmplifier = p; }
    public Unit getPhaseAmplifier() { return phaseAmplifier; }
    
    // This is Alpha
    Unit outputAmplitude = new Constant(1.0);
    public void setOutputAmplitude(Unit p) { outputAmplitude = p; }
    public Unit getOutputAmplitude() { return outputAmplitude; }

    double state = 0;
    
    public double tick(long tickCount) 
        {
        // First we have to override Oscillator.tick to include just one minor item: the relative 
        // frequency in addition to the absolute frequency.  We could do this with elaborate combinations
        // of Mul and Add and stuff, but instead it's probably just easier to override it below.
        double hz = valueToHz(getFrequencyUnit().getValue() * getRelativeFrequency().getValue() * MAX_RELATIVE_FREQUENCY);
        state += hz * Config.INV_SAMPLING_RATE;
        
        if (state >= 1)
            {
            state -= 1;
            reset();
            }
            
        /// IMPLEMENT ME BELOW

        // okay, now state contains our instantaneous phase.  We need to compute our modulation amount (since Beta
        // only goes 0...1, multiply the whole thing by MAX_PHASE_AMPLIFICATION).  Make certain that it's centered
        // around zero.  Then include it with the instantaneous phase and push it through the equations.
        // Make sure the result then goes 0...1.

        double mod= (phaseModulator.getValue()-0.5)* 2.0 * phaseAmplifier.getValue() * MAX_PHASE_AMPLIFICATION;
        double output= Math.sin(2.0*Math.PI *(state+mod));
        return (output*outputAmplitude.getValue())/2.0+0.5;
        }
    
    }
