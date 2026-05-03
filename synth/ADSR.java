// Copyright 2018 by George Mason University
// Licensed under the Apache 2.0 License

package synth;

/**
    A TIME-based ADSR.  Make this linear.
*/

public class ADSR extends Unit
    {
    static final int START = 0;
    static final int ATTACK = 1;
    static final int DECAY = 2;
    static final int SUSTAIN = 3;
    static final int RELEASE = 4;
    int state = START;

    Unit attackLevel = new Constant(1.0);
    Unit attackTime = new Constant(0.01);
    Unit decayTime = new Constant(0.5);
    Unit sustainLevel = new Constant(1.0);
    Unit releaseTime = new Constant(0.01);
    Unit gate = new Constant(0);

    // You should find these handy
    double startTime = 0;
    double endTime = 0;
    double startLevel = 0;
    double endLevel = 0;

    static final double TIME_MULTIPLIER = Config.SAMPLING_RATE;

    // Yeah yeah, these are bad names given the get() methods below, so sue me...
    public void setAttackTime(Unit attackTime) { this.attackTime = attackTime; }
    public void setAttackLevel(Unit attackLevel) { this.attackLevel = attackLevel; }
    public void setDecayTime(Unit decayTime) { this.decayTime = decayTime; }
    public void setSustainLevel(Unit sustainLevel) { this.sustainLevel = sustainLevel; }
    public void setReleaseTime(Unit releaseTime) { this.releaseTime = releaseTime; }
    public void setGate(Unit gate) { this.gate = gate; }

    double getAttackTime() { return attackTime.getValue() * TIME_MULTIPLIER; }
    double getAttackLevel() { return attackLevel.getValue(); }
    double getDecayTime() { return decayTime.getValue() * TIME_MULTIPLIER; }
    double getSustainLevel() { return sustainLevel.getValue(); }
    double getReleaseTime() { return releaseTime.getValue() * TIME_MULTIPLIER; }
    double getGate() { return gate.getValue(); }

    public double tick(long tickCount)
        {
        switch(state)
            {
            case START:
                // If it's time to attack, set up the attack parameters and change the state
                // Otherwise return 0
                if(getGate() > 0){
                    startTime = tickCount;
                    endTime= tickCount + getAttackTime();
                    startLevel =0;
                    endLevel = getAttackLevel();
                    state= ATTACK;
                } else{
                    return 0.0;
                }
                break;
            case ATTACK:
                // If it's time to release, set up the release parameters and change the state
                // Else if it's time to decay, set up the decay parameters and change the state
                if(getGate() <=0){
                    double currLevel = (endTime>startTime) ? startLevel+(endLevel - startLevel) * (tickCount-startTime)/(endTime-startTime) : endLevel;
                    startTime = tickCount;
                    endTime = tickCount + getReleaseTime();
                    startLevel = currLevel;
                    endLevel = 0;
                    state = RELEASE;
                } else if (tickCount >=endTime){
                    startTime=tickCount;
                    endTime = tickCount +getDecayTime();
                    startLevel=getAttackLevel();
                    endLevel=getSustainLevel();
                    state=DECAY;
                }
                break;
            case DECAY:
                // If it's time to release, set up the release parameters and change the state
                // Else if it's time to sustain, change the state and return the sustain level
                if(getGate()<=0){
                    double currLevel = (endTime>startTime) ? startLevel+(endLevel - startLevel) * (tickCount-startTime)/(endTime-startTime) : endLevel;
                    startTime= tickCount;
                    endTime= tickCount + getReleaseTime();
                    startLevel=currLevel;
                    endLevel=0;
                    state=RELEASE;
                }else if(tickCount >=endTime){
                    state= SUSTAIN;
                    return getSustainLevel();
                }
                break;
            case SUSTAIN:
                // If it's time to release, set up the release parameters and change the state
                // Else return the sustain level
                if(getGate() <=0){
                    startTime = tickCount;
                    endTime = tickCount+getReleaseTime();
                    startLevel = getSustainLevel();
                    endLevel = 0;
                    state=RELEASE;
                }else{
                    return getSustainLevel();
                }
                break;
            case RELEASE:
                // If it's time to attack, set up the attack parameters and change the state
                // Else if it's time to start [again], change the state and return 0
                if(getGate()>0){
                    double currLevel = (endTime>startTime) ? startLevel+(endLevel - startLevel) * (tickCount-startTime)/(endTime-startTime) : endLevel;
                    startTime =tickCount;
                    endTime= tickCount+getAttackTime();
                    startLevel=currLevel;
                    endLevel=getAttackLevel();
                    state=ATTACK;
                }else if(tickCount >=endTime){
                    state=START;
                    return  0.0;
                }
                break;
            default:                // uh...
                System.err.println("ADSR Internal Error: this should be unreachable");
                return 0.0;
            }

        // If we have 0 time (startTime is >= endTime) return endLevel
        // Otherwise interpolate the level between start and end level using the tick count between start and end time
        // and return the interpolation
        if(startTime>=endTime){
            return endLevel;
        }else{
            return startLevel+(endLevel-startLevel) * (tickCount - startTime)/(endTime-startTime);
        }
        }
    }

