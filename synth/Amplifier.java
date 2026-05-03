package synth;

/**
   A simple unit which multiplies its INPUT against an AMPLITUDE UNIT
   and a SCALE UNIT.  The INPUT is treated as SIGNED with no DC offset: 
   its zero point is centered at 0.5.  The AMPLITUDE UNIT is meant to run 
   between 0 and 1. The SCALE UNIT is typically set to a constant, by 
   default 1 but it can be higher.
*/
 
public class Amplifier extends Unit 
    {
    private Unit input = new Constant(1.0);
    private Unit amplitudeUnit = new Constant(1.0);
    private Unit scaleUnit = new Constant(1.0);
    
    public Unit getAmplitudeUnit() { return amplitudeUnit; }
    public void setAmplitudeUnit(Unit amplitudeUnit) { this.amplitudeUnit = amplitudeUnit; }

    public Unit getScaleUnit() { return scaleUnit; }
    public void setScaleUnit(Unit scaleUnit) { this.scaleUnit = scaleUnit; }

    public Unit getInput() { return input; }
    public void setInput(Unit input) { this.input = input; }

    public double tick(long tickCount) 
        {
        return (input.getValue() - 0.5) * amplitudeUnit.getValue() * scaleUnit.getValue() + 0.5;
        }
    }
