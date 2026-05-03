package synth;

/**
   An oscillator which outputs a Bipolar Band-Limited Impulse Train (or BP-BLIT).
   A BPBlit subtracts one off-phase BLIT from another.  The phase is determined
   by the PHASE MOD.
*/

public class BPBlit extends Blit
    {
    Blit offPhaseBlit = new Blit();
    Unit phaseUnit = new Constant(0.5);

    public void setPhaseUnit(Unit phaseUnit)
        {
        this.phaseUnit = phaseUnit;
        }

    public Unit getPhaseUnit() { return phaseUnit; }

    public void setFrequencyUnit(Unit frequencyUnit)
        {
        super.setFrequencyUnit(frequencyUnit);
        offPhaseBlit.setFrequencyUnit(frequencyUnit);
        }

    double bpblit(long tickCount)
        {
        double phase= phaseUnit.getValue();
        return blit(tickCount,0)- offPhaseBlit.blit(tickCount, phase);
        }

    public double tick(long tickCount)
        {
        if(valueToHz(getFrequencyUnit().getValue())==0){
            return getValue();
        }else{
            return bpblit(tickCount)* 0.5+0.5;
        }
        }
    }
