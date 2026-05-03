package synth;
import javax.sound.midi.*;

public interface ProvidesMidi
    {
    public MidiMessage[] getNextMessages();
    }
