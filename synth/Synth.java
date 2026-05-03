package synth;

// Copyright 2018 by George Mason University


import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.*;

/**
   The Synthesizer unit.  In setup(), you create the various units, in order, and add them to the synth.
   Call setOuput() to specify which unit has its output handed to audio.  
   Then Synth calls updateValue() on each in turn and generates sound. 
*/

public class Synth 
    {
/*
 * DO NOT EDIT THIS SECTION BELOW (SEE BOTTOM OF FILES FOR WHERE TO UPDATE)
 * ==============================
 * VVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
 **/



    /**
     * Size of the Java SourceDataLine audio buffer.
     * Larger and we have more latency.  Smaller and the system can't handle it.
     * It appears &geq 1024 is required on the Mac for 44100 KHz
     */
    public static final int BATCH_SIZE = 32;
    Midi midi;
    // The current Ouptut Mixer
    javax.sound.sampled.Mixer.Info mixer;
    // The current Input Mixer
    javax.sound.sampled.Mixer.Info input;
    // The Audio Format
    AudioFormat audioFormat;
    // The audio output
    SourceDataLine sdl;
    // The audio input
    TargetDataLine tdl;
    // current write position in buffer
    int buffpos = 0;
    // Audio buffer, which the audio output drains.
    byte[] audioBuffer = new byte[BATCH_SIZE];
    /*
      Random Number Generation

      Each Sound has its own random number generator.
      You can get a new, more or less statistically  independent generator from this method.
    */
    Object randomLock = new Object[0];
    long randomSeed;
    protected Random rnd = getNewRandom();
    protected ArrayList<Unit> units = new ArrayList<Unit>();
    Unit outputUnit = null;
    // Global tickcount
    long tickCount = 0;

    public Synth() 
        {
        randomSeed = System.currentTimeMillis();
        }

    public void setOutput(Unit outputUnit) { this.outputUnit = outputUnit; }
    public Unit getOutput() { return outputUnit; }
        
    public void setMidi(Midi midi) { this.midi = midi; }
    public Midi getMidi() { return midi; }

    public static int getInt(String s) 
        {
        try {
            return (Integer.parseInt(s));
            } catch (NumberFormatException ex) 
            {
            return -1;
            }
        }

    public static void showDevices(Midi midi, javax.sound.sampled.Mixer.Info[] mixers, javax.sound.sampled.Mixer.Info[] inputs, boolean includeInput)
        {
        midi.displayDevices();
        System.err.println("\nAUDIO DEVICES:");
        for (int i = 0; i < mixers.length; i++)
            System.err.println("" + i + ":\t" + mixers[i].getName());
        if (includeInput)
            {
            System.err.println("\nINPUT DEVICES:");
            for (int i = 0; i < inputs.length; i++)
                System.err.println("" + i + ":\t" + inputs[i].getName());
            }

        System.err.println("\nFormat:\n\tjava Synth\t\t\t[displays available devices]\n\tjava Synth [midi] [audio]\t[runs synth with the given device numbers]");
        }

    public static void run(Synth synth, String[] args) 
        {
        run(synth, args, false);
        }

    public static void run(Synth synth, String[] args, boolean includeInput) 
        {
        Midi midi = new Midi();
        synth.audioFormat = new AudioFormat((float)Config.SAMPLING_RATE, 16, 1, true, false);
        javax.sound.sampled.Mixer.Info[] mixers = synth.getSupportedMixers();
        javax.sound.sampled.Mixer.Info[] inputs = synth.getSupportedInputs();

        if (args.length == 0) 
            {
            showDevices(midi, mixers, inputs, includeInput);
            }
        else if ((args.length == 3 && includeInput) || (args.length == 2 && !includeInput))
            {
            int x = getInt(args[0]);
            ArrayList<Midi.MidiDeviceWrapper> in = midi.getInDevices();
            if (x >= 0 && x < in.size()) 
                {
                midi.setInDevice(in.get(x));
                System.err.println("MIDI: " + in.get(x));

                if (mixers == null)
                    {
                    System.err.println("No output found which supports the desired sampling rate and bit depth\n");
                    showDevices(midi, mixers, inputs, includeInput);
                    System.exit(1);
                    }
                else
                    {
                    x = getInt(args[1]);
                    if (x >= 0 && x < mixers.length)
                        {
                        synth.setMixer(mixers[x]);
                        System.err.println("Audio: " + mixers[x].getName());  
                        
                        if (includeInput)
                            {
                            if (inputs == null)
                                {
                                System.err.println("No input found which supports the desired sampling rate and bit depth\n");
                                showDevices(midi, inputs, inputs, includeInput);
                                System.exit(1);
                                }
                            else
                                {
                                x = getInt(args[2]);
                                if (x >= 0 && x < inputs.length)
                                    {
                                    synth.setInput(inputs[x]);
                                    System.err.println("Input: " + inputs[x].getName());
                                    }
                                }  
                            }                               
                                                         
                        synth.setMidi(midi);
                        synth.setup();
                        synth.go();
                        synth.sdl.drain();
                        synth.sdl.close();
                        
                        if (includeInput)
                            {
                            synth.tdl.drain();
                            synth.tdl.close();
                            }
                        }
                    else
                        {
                        System.err.println("Invalid Audio number " + args[1] + "\n");
                        showDevices(midi, mixers, inputs, includeInput);
                        System.exit(1);
                        }
                    }
                }
            else 
                {
                System.err.println("Invalid MIDI number " + args[0] + "\n");
                showDevices(midi, mixers, inputs, includeInput);
                System.exit(1);
                }
            }
        else
            {
            System.err.println("Invalid argument format\n");
            showDevices(midi, mixers, inputs, includeInput);
            System.exit(1);
            }
        }

    /**
     * Returns the currently used Mixer
     */
    public javax.sound.sampled.Mixer.Info getMixer() 
        {
        return mixer;
        }

    /**
     * Sets the currently used Mixer
     */
    public void setMixer(javax.sound.sampled.Mixer.Info mixer) 
        {
        try 
            {
            if (sdl != null)
                sdl.stop();
            if (mixer == null) 
                {
                javax.sound.sampled.Mixer.Info[] m = getSupportedMixers();
                if (m.length > 0)
                    mixer = m[0];
                }
            if (mixer == null)
                sdl = AudioSystem.getSourceDataLine(audioFormat);
            else
                sdl = AudioSystem.getSourceDataLine(audioFormat, mixer);
            sdl.open(audioFormat, bufferSize);
            sdl.start();
            this.mixer = mixer;
            } 
        catch (LineUnavailableException ex) 
            {
            throw new RuntimeException(ex);
            }
        }

    /**
     * Sets the currently used Mixer
     */
    public void setInput(javax.sound.sampled.Mixer.Info input) 
        {
        try 
            {
            if (tdl != null)
                tdl.stop();
            if (input == null) 
                {
                javax.sound.sampled.Mixer.Info[] m = getSupportedMixers();
                if (m.length > 0)
                    input = m[0];
                }
            if (input == null)
                tdl = AudioSystem.getTargetDataLine(audioFormat);
            else
                tdl = AudioSystem.getTargetDataLine(audioFormat, input);
            tdl.open(audioFormat, bufferSize);
            tdl.start();
            this.input = input;
            } 
        catch (LineUnavailableException ex) 
            {
            throw new RuntimeException(ex);
            }
        }

    /**
     * Returns the available mixers which support the given audio format.
     */
    public javax.sound.sampled.Mixer.Info[] getSupportedInputs() 
        {
        DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        javax.sound.sampled.Mixer.Info[] info = AudioSystem.getMixerInfo();
        int count = 0;
        for (int i = 0; i < info.length; i++) 
            {
            javax.sound.sampled.Mixer m = AudioSystem.getMixer(info[i]);
            if (m.isLineSupported(lineInfo)) 
                {
                count++;
                }
            }

        javax.sound.sampled.Mixer.Info[] options = new javax.sound.sampled.Mixer.Info[count];
        count = 0;
        for (int i = 0; i < info.length; i++) 
            {
            javax.sound.sampled.Mixer m = AudioSystem.getMixer(info[i]);
            if (m.isLineSupported(lineInfo))
                options[count++] = info[i];
            }
        return options;
        }

    /**
     * Returns the available mixers which support the given audio format.
     */
    public javax.sound.sampled.Mixer.Info[] getSupportedMixers() 
        {
        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        javax.sound.sampled.Mixer.Info[] info = AudioSystem.getMixerInfo();
        int count = 0;
        for (int i = 0; i < info.length; i++) 
            {
            javax.sound.sampled.Mixer m = AudioSystem.getMixer(info[i]);
            if (m.isLineSupported(lineInfo)) 
                {
                count++;
                }
            }

        javax.sound.sampled.Mixer.Info[] options = new javax.sound.sampled.Mixer.Info[count];
        count = 0;
        for (int i = 0; i < info.length; i++) 
            {
            javax.sound.sampled.Mixer m = AudioSystem.getMixer(info[i]);
            if (m.isLineSupported(lineInfo))
                options[count++] = info[i];
            }
        return options;
        }

    public static final int RANDOM_INCREASE = 17;
    Random getNewRandom() 
        {
        synchronized (randomLock) 
            {
            randomSeed += RANDOM_INCREASE;  // or whatever
            return new Random(randomSeed);
            }
        }

    /**
     * Sample ranges from 0 to 1
     */
    public void emitSample(double d) 
        {
        int val = 0;
        if (d > 1.0) d = 1.0;
        if (d < 0.0) d = 0.0;
        d -= 0.5;

        val = (int) (d * 65536);
        if (val > 32767) val = 32767;
        if (val < -32768) val = -32768; 

        audioBuffer[buffpos] = (byte) (val & 255);
        audioBuffer[buffpos + 1] = (byte) ((val >> 8) & 255);
        buffpos += 2;
        if (buffpos + 1 >= BATCH_SIZE) 
            {
            sdl.write(audioBuffer, 0, buffpos);
            buffpos = 0;
            }
        }

    void go() 
        {
        if (this.outputUnit == null) 
            {
            System.err.println("No output unit defined: exiting");
            return;
            }
        while (true) 
            {
            for (Unit m : this.units) 
                {
                m.doUpdate(tickCount);
                }
            emitSample(this.outputUnit.getValue());
            tickCount++;
            }
        }

    /**
       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
       ==============================
       DO NOT EDIT THIS SECTION ABOVE
    **/


    /** ADJUST THIS VALUE IF YOU GET A LOT OF GLITCHY SOUND */
    public static int numSamples = 2048;

    // DON'T ADJUST THIS VALUE
    static int bufferSize = numSamples * 2;

    /**
     * MAKE YOUR EDITS TO THIS METHOD
     **/
    public void setup() 
        {
        }
        
    }

