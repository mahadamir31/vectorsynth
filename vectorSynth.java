import synth.*;

import javax.swing.*;
import java.awt.*;


public class vectorSynth extends Synth
    {
    public static void main(String[] args)
        {
            Synth.run(new vectorSynth(), args);
        }

    public void setup()
        {
            MidiUnit midiUnit = new MidiUnit(getMidi());
            units.add(midiUnit);

            //create osc. Each array of items goes: up down right left
            Sine upOsc = new Sine();
            Sine downOsc = new Sine();
            Sine rightOsc = new Sine();
            Sine leftOsc = new Sine();

            Sine[] vectorOsc = {upOsc, downOsc, rightOsc, leftOsc};

            for (Sine osc: vectorOsc){
                osc.setFrequencyUnit(midiUnit);
            }

            //create gate
            Gate gate = new Gate(midiUnit);

            //create Dials for each LFO
            Dial upLFODial = new Dial(0);
            Dial downLFODial = new Dial(0);
            Dial rightLFODial = new Dial(0);
            Dial leftLFODial = new Dial(0);

            Dial[] LFODials = {upLFODial, downLFODial, rightLFODial, leftLFODial};

            //create Mul units
            Mul upLFOMul = new Mul();
            Mul downLFOMul = new Mul();
            Mul rightLFOMul = new Mul();
            Mul leftLFOMul = new Mul();

            Mul[] LFOMul = {upLFOMul, downLFOMul, rightLFOMul, leftLFOMul};

            //set the Mul to the dial input
            for (int i = 0; i < LFODials.length; i ++){
                LFOMul[i].setInput(LFODials[i].getUnit());
                LFOMul[i].setValue(0.001);
            }

            //create the LFO and assign them a Mul
            LFO upLFO = new LFO();
            LFO downLFO = new LFO();
            LFO rightLFO = new LFO();
            LFO leftLFO = new LFO();

            LFO[] vectorLFOs = {upLFO, downLFO, rightLFO, leftLFO};

            for (int i = 0; i < vectorLFOs.length; i ++){
                vectorLFOs[i].setFrequencyUnit(LFOMul[i]);
            }

            Dial upVibrato = new Dial(0.1);
            Dial downVibrato = new Dial(0.1);
            Dial rightVibrato = new Dial(0.1);
            Dial leftVibrato = new Dial(0.1);

            Dial[] vibratoDials = {upVibrato, downVibrato, rightVibrato, leftVibrato};

            Mul upPitch = new Mul();
            Mul downPitch = new Mul();
            Mul rightPitch = new Mul();
            Mul leftPitch = new Mul();

            Mul[] pitchMuls = {upPitch, downPitch, rightPitch, leftPitch};

            for (int i = 0; i < pitchMuls.length; i ++){
                pitchMuls[i].setInput(vectorLFOs[i]);
                pitchMuls[i].setMultiplier(vibratoDials[i].getUnit());
                pitchMuls[i].setValue(0.001);
            }

            Amplifier upGainAmp = new Amplifier();
            Amplifier downGainAmp = new Amplifier();
            Amplifier rightGainAmp = new Amplifier();
            Amplifier leftGainAmp = new Amplifier();

            Amplifier[] gainAmps = {upGainAmp, downGainAmp, rightGainAmp, leftGainAmp};

            for (int i = 0; i < gainAmps.length; i ++){
                Dial dial = new Dial(1.0);
                upGainAmp.setAmplitudeUnit(dial.getUnit());
            }

            ADSR upAdsr

            // for (Amplifier amp: gainAmps){

            // }
        }
    }