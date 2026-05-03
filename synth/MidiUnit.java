/*
  Copyright 2023 by Sean Luke and Vi Hoyle
  Please see us for licensing beyond use in CS 499 / 695
*/

package synth;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import java.util.ArrayList;

/**
 * 
 * This class is attached to Midi and updates a variety of MIDI state parameters.
 * It also serves as a unit itself, and outputs the current PITCH of the note
 * being played, as a value from 0...1.
 **/
 
public class MidiUnit extends Unit 
    {
    static final double BASE = Math.pow(2, 1 / 12.0);
    ProvidesMidi midi;
    int lastnote = 0;
    double pitch;
    double velocity;
    double bend = 1.0;
    double rawBend;
    double aftertouch = 0.5;
    double[] cc = new double[128];
    boolean[] newcc = new boolean[128];
    Voice[] voices;
        
    public MidiUnit(ProvidesMidi midi, Voice[] voices) 
        { 
        this.midi = midi; 
        this.voices = voices;
        if (voices != null)
            {
            for(int i = 0; i < voices.length; i++)
                voices[i].setMidiUnit(this);
            }
        }

    public MidiUnit(ProvidesMidi midi) { this(midi, null); }

    public double getBend() { return bend; }
        
    public double getPitch() { return pitch; }

    public double getVelocity() { return velocity; }

    public double getGate() { return velocity > 0 ? 1 : 0; }

    public double getAftertouch() { return aftertouch; }
        
    public double getCC(int param) { return cc[param]; }
        
    public boolean isCCNew(int param) { boolean val = newcc[param]; newcc[param] = false; return val; }

    // Processes a PITCH BEND message.
    void processPitchBend(ShortMessage sm) 
        {
        int lsb = sm.getData1();
        int msb = sm.getData2();

        // Linux Java distros have a bug: pitch bend data is treated
        // as a signed two's complement integer, which is wrong, wrong, wrong.
        // So we have to special-case it here. See:
        //
        // https://bugs.openjdk.java.net/browse/JDK-8075073
        // https://bugs.launchpad.net/ubuntu/+source/openjdk-8/+bug/1755640

        if (Utils.isUnix()) 
            {
            if (msb >= 64) 
                {
                msb = msb - 64;
                } 
            else 
                {
                msb = msb + 64;
                }
            }

        int rawBend = (lsb + msb * 128) - 8192;

        if (rawBend < -8191) 
            {
            rawBend = -8191;
            }

        // The 24 at the end is 2 octaves 
        this.rawBend = rawBend / 8191.0 * 2;
        bend = Utils.hybridpow(2.0, rawBend / 8191.0 * 2);
        }

    class Note 
        {
        public int note, vel;

        public Note(int note, int vel)
            {
            this.note = note;
            this.vel = vel;
            }

        }

    int stamp = 0;
    void allocate(int pitch, int vel)
        {
        if (voices == null) return;
        for(int i = 0; i < voices.length; i++)
            {
            if (!voices[i].isAllocated())
                {
                voices[i].allocate(pitch, vel, stamp++);
                return;
                }
            }
        // At this point nobody has been allocated so we have to reallocate.
        // We'll use last note priority
        int min = -1;
        int minVoice = 0;
        for(int i = 0; i < voices.length; i++)
            {
            if (voices[i].getStamp() < min || min == -1)
                {
                min = voices[i].getStamp();
                minVoice = i;
                }
            }
        voices[minVoice].allocate(pitch, vel, stamp++);
        }

    void deallocate(int pitch, int vel)
        {
        if (voices == null) return;
                
        int min = -1;
        int minVoice = 0;
        for(int i = 0; i < voices.length; i++)
            {
            if (voices[i].isAllocated() &&
                voices[i].getPitch() == pitch && 
                (voices[i].getStamp() < min || min == -1))
                {
                min = voices[i].getStamp();
                minVoice = i;
                }
            }
        if (min == -1) return; // no voice to deallocate

        voices[minVoice].deallocate(vel);
        }

    ArrayList<Note> notes = new ArrayList();

    void removeNote(int pitch)
        {
        for(Note n : notes)
            {
            if (n.note == pitch)
                {
                notes.remove(n);
                return;
                }
            }
        }
                
    void addNote(int pitch, int vel)
        {
        removeNote(pitch);
        if (vel != 0)
            notes.add(new Note(pitch, vel));
        }

    public double tick(long tickCount) 
        {
        MidiMessage[] messages = midi.getNextMessages();
        boolean changes = false;
        for (MidiMessage message : messages) 
            {
            if (message instanceof ShortMessage) 
                {
                changes = true;
                ShortMessage sm = (ShortMessage) message;
                switch (sm.getCommand()) 
                    {
                    case ShortMessage.NOTE_ON:
                        {
                        int pitch = sm.getData1();
                        int vel = sm.getData2();
                        addNote(pitch, vel);
                        deallocate(pitch, vel);
                        break;
                        }
                    case ShortMessage.NOTE_OFF:
                        {
                        int pitch = sm.getData1();
                        int vel = sm.getData2();
                        removeNote(pitch);
                        break;
                        }
                    case ShortMessage.PITCH_BEND:
                        {
                        processPitchBend(sm);
                        }
                    break;
                    case ShortMessage.CONTROL_CHANGE:
                        {
                        int param = sm.getData1();
                        int val = sm.getData2();
                        cc[param] = val / 127.0;        // so it ranges 0...1
                        newcc[param] = true;
                        }
                    break;
                    case ShortMessage.CHANNEL_PRESSURE:
                        {
                        aftertouch = sm.getData1() / 127.0;             // so it ranges 0...1
                        }
                    break;
                    default:
                        {
                        break;
                        }
                    }
                }
            }
            
        if (changes) 
            {
            if (notes.size() == 0) 
                {
                velocity = 0;
                } 
            else 
                {
                Note n = notes.get(notes.size()-1);
                int note = n.note;
                int vel = n.vel;
                velocity = vel / 127.0;
                pitch = hzToValue(440.0 * Math.pow(BASE, note - 69) * bend);
                }
            }

        return pitch;
        }

    
    }
