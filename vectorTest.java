import java.awt.*;
import javax.swing.*;
import synth.*;

public class vectorTest extends Synth {

    public static void main(String[] args) {
        Synth.run(new vectorTest(), args);
    }

    public void setup() {
        MidiUnit midi = new MidiUnit(getMidi());
        units.add(midi);
        Gate gate = new Gate(midi);
        units.add(gate);

        JFrame frame = new JFrame("Vector Synth");
        frame.setLayout(new BorderLayout());
        Box outerBox = Box.createHorizontalBox();
        frame.add(outerBox, BorderLayout.CENTER);

        // Four oscillators — one per corner
        BlitSaw saw = new BlitSaw();
        saw.setFrequencyUnit(midi);
        units.add(saw);

        BlitSquare square = new BlitSquare();
        square.setFrequencyUnit(midi);
        square.setPhaseUnit(new Constant(0.5));
        units.add(square);

        BlitTriangle triangle = new BlitTriangle();
        triangle.setFrequencyUnit(midi);
        triangle.setPhaseUnit(new Constant(0.5));
        units.add(triangle);

        Blit blit = new Blit();
        blit.setFrequencyUnit(midi);
        units.add(blit);

        // Vector oscillator blends the four
        VectorOscillator vectorOsc = new VectorOscillator();
        vectorOsc.setInput0(saw);
        vectorOsc.setInput1(square);
        vectorOsc.setInput2(triangle);
        vectorOsc.setInput3(blit);
        units.add(vectorOsc);

        Dial LFOMulDial = new Dial(0.4);
        units.add(LFOMulDial.getUnit());
        outerBox.add(LFOMulDial.getLabelledDial("LFO MUL"));

        Mul LFOMul = new Mul();
        LFOMul.setInput(LFOMulDial.getUnit());
        LFOMul.setValue(0.001);
        units.add(LFOMul);
        LFO lfo = new LFO();
        lfo.setFrequencyUnit(LFOMul);
        units.add(lfo);

        //vibrato
        Dial vib = new Dial(0.1);
        units.add(vib.getUnit());
        outerBox.add(vib.getLabelledDial("vib"));

        Mul pitchMul = new Mul();
        pitchMul.setInput(lfo);
        pitchMul.setMultiplier(vib.getUnit());
        pitchMul.setValue(0.001);
        units.add(pitchMul);

        Add vibAdd = new Add();
        vibAdd.setAdder(pitchMul);
        vibAdd.setInput(vectorOsc);
        units.add(vibAdd);

        // Filter (same as Project4)
        Dial lpfCutoff = new Dial(0.5);
        units.add(lpfCutoff.getUnit());
        Dial lpfRes = new Dial(0.0);
        units.add(lpfRes.getUnit());

        Cube lpfCube = new Cube();
        lpfCube.setInput(lpfCutoff.getUnit());
        units.add(lpfCube);

        LPF lpf = new LPF();
        lpf.setInput(vibAdd);
        lpf.setFrequencyUnit(lpfCube);
        lpf.setResonanceUnit(lpfRes.getUnit());
        units.add(lpf);

        //LFO
        

        // Amp ADSR (same as Project4)
        Dial aAtt = new Dial(0.01); units.add(aAtt.getUnit());
        Dial aDec = new Dial(0.0);  units.add(aDec.getUnit());
        Dial aSus = new Dial(1.0);  units.add(aSus.getUnit());
        Dial aRel = new Dial(0.01); units.add(aRel.getUnit());

        ADSR ampADSR = new ADSR();
        ampADSR.setAttackTime(aAtt.getUnit());
        ampADSR.setAttackLevel(new Constant(1.0));
        ampADSR.setDecayTime(aDec.getUnit());
        ampADSR.setSustainLevel(aSus.getUnit());
        ampADSR.setReleaseTime(aRel.getUnit());
        ampADSR.setGate(gate);
        units.add(ampADSR);

        Amplifier amp = new Amplifier();
        amp.setInput(lpf);
        amp.setAmplitudeUnit(ampADSR);
        units.add(amp);

        Dial gain = new Dial(0.02);
        units.add(gain.getUnit());

        Amplifier gainAmp = new Amplifier();
        gainAmp.setInput(amp);
        gainAmp.setAmplitudeUnit(gain.getUnit());
        units.add(gainAmp);

        Oscilloscope osc = new Oscilloscope();
        osc.getUnit().setAmplitudeUnit(gainAmp);
        units.add(osc.getUnit());
        setOutput(gainAmp);

        // GUI
        JPanel padWrapper = new JPanel();
        padWrapper.setLayout(new BorderLayout());
        padWrapper.add(new JLabel("Vector"), BorderLayout.NORTH);
        VectorPad pad = new VectorPad(vectorOsc);
        padWrapper.add(pad, BorderLayout.CENTER);
        outerBox.add(padWrapper);

        Box filterCol = Box.createVerticalBox();
        filterCol.add(new JLabel("Filter"));
        filterCol.add(lpfCutoff.getLabelledDial("LPF Cutoff"));
        filterCol.add(lpfRes.getLabelledDial("LPF Res"));
        outerBox.add(filterCol);

        Box ampCol = Box.createVerticalBox();
        ampCol.add(new JLabel("Amp ADSR"));
        ampCol.add(aAtt.getLabelledDial("Attack"));
        ampCol.add(aDec.getLabelledDial("Decay"));
        ampCol.add(aSus.getLabelledDial("Sustain"));
        ampCol.add(aRel.getLabelledDial("Release"));
        outerBox.add(ampCol);

        Box outputCol = Box.createVerticalBox();
        outputCol.add(new JLabel("Output"));
        outputCol.add(gain.getLabelledDial("Gain"));
        outputCol.add(osc);
        outerBox.add(outputCol);

        frame.pack();
        frame.setVisible(true);
    }
}