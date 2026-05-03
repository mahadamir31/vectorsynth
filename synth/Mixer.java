package synth;

/**
   A mixer of some N INPUTS, each with a gain defined by an AMPLITUDE MOD.
**/

public class Mixer extends Unit
    {
    Unit[] inputs;
    Unit[] amplitudeMods;

    public Mixer(int numInputs)
        {
        /// IMPLEMENT ME
        /// Set all the inputs and amplitude units to just a 0 Constant
        inputs= new Unit[numInputs];
        amplitudeMods= new Unit[numInputs];
        for(int i=0;i<numInputs;i++){
            inputs[i] = new Constant(0);
            amplitudeMods[i] = new Constant(0);
        }
        }

    public Mixer(Unit[] inputs, Unit[] amplitudeMods)
        {
        this.inputs = inputs;
        this.amplitudeMods = amplitudeMods;
        }

    public Unit getInput(int val) { return inputs[val]; }
    public void setInput(int val, Unit input) { inputs[val] = input; }

    public Unit getAmplitudeMod(int val) { return amplitudeMods[val]; }
    public void setAmplitudeMod(int val, Unit unit) { amplitudeMods[val] = unit; }

    public double tick(long tickCount)
        {
        /// IMPLEMENT ME
        /// Remember that the inputs go 0...1, centered at 0.5.  I would translate
        /// each of them to go from -1 to +1, then mix them (a weighted sum),
        /// then translate the result back from -1 ... +1 to 0...1
        double sum=0;
        for(int i=0;i<inputs.length; i++){
            double inputVal = inputs[i].getValue();
            double amplitudeVal = amplitudeMods[i].getValue();
            double centered = (inputVal-0.5)*2.0;
            sum += centered * amplitudeVal;
        }
        double output = (sum/2) + 0.5;
        if(output<0){
            output = 0;
        }else if(output>1){
            output =1;
        }
        return output;
        }
    }
