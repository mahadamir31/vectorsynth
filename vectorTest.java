import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import synth.*;

public class vectorTest extends Synth implements ItemListener{

    private String[] choices = {"Sine", "Triangle", "Square", "Blit", "Saw"};
    private JLabel osc1label = new JLabel("Oscillator 1:");
    private JLabel osc2label = new JLabel("Oscillator 2:");
    private JLabel osc3label = new JLabel("Oscillator 3:");
    private JLabel osc4label = new JLabel("Oscillator 4:");
    final JComboBox<String> cb1 = new JComboBox<String>(choices);
    final JComboBox<String> cb2 = new JComboBox<String>(choices);
    final JComboBox<String> cb3 = new JComboBox<String>(choices);
    final JComboBox<String> cb4 = new JComboBox<String>(choices);
    private VectorOscillator vectorOsc = new VectorOscillator();

    private MidiUnit midi;
    private Osc osc1;
    private Osc osc2;
    private Osc osc3;
    private Osc osc4;
    boolean setUp = false;

    private JFrame frame;

    public static void main(String[] args) {
        Synth.run(new vectorTest(), args);
    }

    public void setup(){
        cb1.addItemListener(this);
        cb2.addItemListener(this);
        cb3.addItemListener(this);
        cb4.addItemListener(this);
        setup(null, null, null, null);
    }

    public void setup(Osc newOsc1, Osc newOsc2, Osc newOsc3, Osc newOsc4) {
        units.clear();
        midi = new MidiUnit(getMidi());
        units.add(midi);
        Gate gate = new Gate(midi);
        units.add(gate);

        if (frame != null){frame.dispose();}
        frame = new JFrame("Vector Synth");
        frame.setLayout(new BorderLayout());
        Box outerBox = Box.createHorizontalBox();
        frame.add(outerBox, BorderLayout.CENTER);

        if (!setUp || newOsc1 == null){
            osc1 = new BlitSaw();
            osc1.setFrequencyUnit(midi);
            units.add(osc1);

            BlitSquare square = new BlitSquare();
            square.setFrequencyUnit(midi);
            square.setPhaseUnit(new Constant(0.5));
            osc2 = square;
            units.add(osc2);

            BlitTriangle triangle = new BlitTriangle();
            triangle.setFrequencyUnit(midi);
            triangle.setPhaseUnit(new Constant(0.5));
            osc3 = triangle;
            units.add(osc3);

            osc4 = new Blit();
            osc4.setFrequencyUnit(midi);
            units.add(osc4);
            setUp = true;
        }
        else{
            osc1 = (newOsc1 != null) ? newOsc1 : osc1;
            osc2 = (newOsc2 != null) ? newOsc2 : osc2;
            osc3 = (newOsc3 != null) ? newOsc3 : osc3;
            osc4 = (newOsc4 != null) ? newOsc4 : osc4;
        }

        // Vector oscillator blends the four
        vectorOsc.setInput0(osc1);
        vectorOsc.setInput1(osc2);
        vectorOsc.setInput2(osc3);
        vectorOsc.setInput3(osc4);
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
        // buttons below pad
        JButton recBtn   = new JButton("Rec");
        JButton playBtn  = new JButton("Play");
        JButton clearBtn = new JButton("Clear");
        recBtn.addActionListener(e   -> pad.startRecording());
        playBtn.addActionListener(e  -> pad.startPlayback());
        clearBtn.addActionListener(e -> pad.clearTrajectory());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(recBtn);
        btnPanel.add(playBtn);
        btnPanel.add(clearBtn);
        padWrapper.add(btnPanel, BorderLayout.SOUTH);

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

        //OSC choice
        osc1label.setVisible(true);
        outerBox.add(osc1label);
        outerBox.add(cb1);

        osc2label.setVisible(true);
        outerBox.add(osc2label);
        outerBox.add(cb2);

        osc3label.setVisible(true);
        outerBox.add(osc3label);
        outerBox.add(cb3);

        osc4label.setVisible(true);
        outerBox.add(osc4label);
        outerBox.add(cb4);

        frame.pack();
        frame.setVisible(true);
    }
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        JComboBox<String> src = (JComboBox<String>) e.getSource();
        Osc newOsc = buildOsc((String) src.getSelectedItem());
        if (newOsc == null) return;

        if (e.getSource() == cb1) { units.remove(osc1); osc1 = newOsc; units.add(osc1); vectorOsc.setInput0(osc1); }
        else if (e.getSource() == cb2) { units.remove(osc2); osc2 = newOsc; units.add(osc2); vectorOsc.setInput1(osc2); }
        else if (e.getSource() == cb3) { units.remove(osc3); osc3 = newOsc; units.add(osc3); vectorOsc.setInput2(osc3); }
        else if (e.getSource() == cb4) { units.remove(osc4); osc4 = newOsc; units.add(osc4); vectorOsc.setInput3(osc4); }
}
    // public void itemStateChanged(ItemEvent e)
    // {
    //     if (e.getStateChange() != ItemEvent.SELECTED) {return;}
    //     Osc newOsc = new Sine();
    //     JComboBox<String> src = (JComboBox<String>)e.getSource();
    //     Object item = src.getSelectedItem();
    //     System.out.println(item.toString());
    //     if (item.equals("Sine")){
    //         newOsc = new Sine();
    //         newOsc.setFrequencyUnit(midi);
    //     }
    //     else if(item.equals("Saw")){
    //         newOsc = new BlitSaw();
    //         newOsc.setFrequencyUnit(midi);
    //     }
    //     else if(item.equals("Square")){
    //         BlitSquare square = new BlitSquare();
    //         square.setFrequencyUnit(midi);
    //         square.setPhaseUnit(new Constant(0.5));
    //         newOsc = square;
    //     }
    //     else if(item.equals("Triangle")){
    //         BlitTriangle tri = new BlitTriangle();
    //         tri.setFrequencyUnit(midi);
    //         tri.setPhaseUnit(new Constant(0.5));
    //         newOsc = tri;
    //     }
    //     else if(item.equals("Blit")){
    //         BPBlit blit = new BPBlit();
    //         blit.setFrequencyUnit(midi);
    //         newOsc = blit;
    //     }
    //     // if the state combobox is changed
    //     if (e.getSource() == cb1){
    //         setup(newOsc, osc2, osc3, osc4);
    //     }
    //     if (e.getSource() == cb2){
    //         setup(osc1, newOsc, osc3, osc4);
    //     }
    //     if (e.getSource() == cb3){
    //         setup(osc1, osc2, newOsc, osc4);
    //     }
    //     if (e.getSource() == cb4){
    //         setup(osc1, osc2, osc3, newOsc);
    //     }
    // }

    private Osc buildOsc(String type) {
    switch (type) {
        case "Sine":     Sine s = new Sine(); s.setFrequencyUnit(midi); return s;
        case "Saw":      BlitSaw saw = new BlitSaw(); saw.setFrequencyUnit(midi); return saw;
        case "Square":   BlitSquare sq = new BlitSquare(); sq.setFrequencyUnit(midi); sq.setPhaseUnit(new Constant(0.5)); return sq;
        case "Triangle": BlitTriangle tri = new BlitTriangle(); tri.setFrequencyUnit(midi); tri.setPhaseUnit(new Constant(0.5)); return tri;
        case "Blit":     BPBlit b = new BPBlit(); b.setFrequencyUnit(midi); return b;
        default: return null;
    }
}
}