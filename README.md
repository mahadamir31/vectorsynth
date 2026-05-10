Vector Synthesizer
Mahad Amir, Jakob Elmore
 
Files
vectorTest.java — main entry point, wires everything together
VectorOscillator.java — blends four waveforms using bilinear interpolation
VectorPad.java — XY pad UI with trajectory record/playback
LFO.java — low frequency oscillator
ADSR.java, LPF.java, Mixer.java, Blit*.java, Filter.java — reused from previous projects
synth/` — course framework, do not modify

Build & Run
javac *.java
java vectorTest        # lists available devices
java vectorTest 0 1    # run with midi=0, audio=1 (or whatever yours may be)

 
Controls
Pad — drag dot to blend between the four corner waveforms
Rec/Play/Clear — record and replay a path through the pad
Dropdowns— swap the waveform at each corner
Dials — LFO, filter cutoff, resonance, ADSR, gain

