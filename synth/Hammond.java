package synth;

/**
   Hammond Organ Unit

   Keep in mind that this unit, when ticked, must also tick its underlying tonewheels,
   then compute the output from them and return that.
**/

public class Hammond extends Osc
    {
    public static final double[] TONEBAR_FREQUENCIES = new double[]
    {
    0.5,
    1.5,                // yes, they're flipped
    1,                  // yes, they're flipped
    2,
    3,
    4,
    5,
    6,
    8
    };

    // Number of amplitude units
    public static final int NUM_TONEWHEELS = TONEBAR_FREQUENCIES.length;

    // The amplifiers
    public Amplifier[] amplifiers = new Amplifier[NUM_TONEWHEELS];
    // The tone wheels (the actual oscillators which generate the sound)
    public Sine[] tonewheels = new Sine[NUM_TONEWHEELS];
    // These multiply the base frequency unit against constants which are fed into the actual frequencies of the tonewheels
    public Mul[] multipliers = new Mul[NUM_TONEWHEELS];
    // The options button
    public Options opt;

    public Hammond()
        {
        /// IMPLEMENT ME

        // Build the Multipliers.
        // Each multiplier is passed a different Constant
        for(int i=0; i<NUM_TONEWHEELS; i++){
            multipliers[i] = new Mul();
            multipliers[i].setMultiplier(new Constant(TONEBAR_FREQUENCIES[i]));

            // Build the Tonewheel Sines
            // Attach the Multipliers to them as frequency unit
            tonewheels[i] = new Sine();
            tonewheels[i].setFrequencyUnit(multipliers[i]);

            // Build the Amplifiers
            // Attach the Tonewheel Sines to them as input
            amplifiers[i] = new Amplifier();
            amplifiers[i].setInput(tonewheels[i]);
            amplifiers[i].setAmplitudeUnit(new Constant(0.0));
        }
        // I'll give you the presets options box
        String[] pre = new String[PRESETS.length];
        for(int i = 0; i < pre.length; i++)
            pre[i] = (String)PRESETS[i][0];
        opt = new Options("Presets", pre, 0)
            {
            public void update(int val)
                {
                // This is a race condition, but I'll just update the dials, it won't be too bad
                for(int i = 0; i < NUM_TONEWHEELS; i++)
                    {
                    int[] vals = (int[])(PRESETS[val][1]);
                    getAmplitudeUnit(i).setValue(vals[i] / 8.0);
                    }
                }
            };
        }

    public Options getOptions() { return opt; }

    public void setFrequencyUnit(Unit frequencyUnit)
        {
        super.setFrequencyUnit(frequencyUnit);

        // IMPLEMENT ME
        // All the Multipliers need to have their frequency unit set to this
        for(int i=0; i< NUM_TONEWHEELS;i++){
            multipliers[i].setInput(frequencyUnit);
        }
    }

    public void setAmplitudeUnit(int i, Unit amp)
        {
        // IMPLEMENT ME
        // The appropriate Amplifier should have its Amplifier Unit set to this
        amplifiers[i].setAmplitudeUnit(amp);
        }

    public Unit getAmplitudeUnit(int i)
        {
        // IMPLEMENT ME
        // Return the appropriate Amplifier's amplifier unit
        return amplifiers[i].getAmplitudeUnit();
        }

    public double tick(long tickCount)
        {
        // IMPLEMENT ME

        // Update all the multipliers, then all the tonewheels, then all the amplifiers
        // Be sure to call doUpdate(...), NOT tick(...)

        // Return the average of the output of the amplifiers
        for(int i=0; i< NUM_TONEWHEELS; i++){
            multipliers[i].doUpdate(tickCount);
        }
        for(int i=0; i<NUM_TONEWHEELS;i++){
            tonewheels[i].doUpdate(tickCount);
        }
        for(int i=0;i<NUM_TONEWHEELS;i++){
            amplifiers[i].doUpdate(tickCount);
        }

        double sum =0.0;
        for(int i=0;i<NUM_TONEWHEELS;i++){
            sum += (amplifiers[i].getValue()-0.5);
        }
        double avg = sum / NUM_TONEWHEELS;
        return avg + 0.5;
    }


    static final Object[][] PRESETS = new Object[][]
    {
    { "007740034 Alone in the City", new int[] { 0, 0, 7, 7, 4, 0, 0, 3, 4 } },
    { "887724110 America (Gospel) (U)", new int[] { 8, 8, 7, 7, 2, 4, 1, 1, 0 } },
    { "006606000 America (Gospel) (L)", new int[] { 0, 0, 6, 6, 0, 6, 0, 0, 0 } },
    { "885324588 Blues", new int[] { 8, 8, 5, 3, 2, 4, 5, 8, 8 } },
    { "888800000 Booker T. Jones 1", new int[] { 8, 8, 8, 8, 0, 0, 0, 0, 0 } },
    { "888630000 Booker T. Jones 2", new int[] { 8, 8, 8, 6, 3, 0, 0, 0, 0 } },
    { "888808008 Born to B3 (Gospel) (U)", new int[] { 8, 8, 8, 8, 0, 8, 0, 0, 8 } },
    { "007725400 Born to B3 (Gospel) (L)", new int[] { 0, 0, 7, 7, 2, 5, 4, 0, 0 } },
    { "888110000 Brian Auger 1", new int[] { 8, 8, 8, 1, 1, 0, 0, 0, 0 } },
    { "888805000 Brian Auger 2", new int[] { 8, 8, 8, 8, 0, 5, 0, 0, 0 } },
    { "878000456 Bright Comping", new int[] { 8, 7, 8, 0, 0, 0, 4, 5, 6 } },
    { "800000888 Brother Jack", new int[] { 8, 0, 0, 0, 0, 0, 8, 8, 8 } },
    { "843000000 Dark Comping", new int[] { 8, 4, 3, 0, 0, 0, 0, 0, 0 } },
    { "888888888 Dark Solo A (U)", new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8 } },
    { "662000000 Dark Solo A (L)", new int[] { 6, 6, 2, 0, 0, 0, 0, 0, 0 } },
    { "828200002 Dark Solo B (U)", new int[] { 8, 2, 8, 2, 0, 0, 0, 0, 2 } },
    { "606000000 Dark Solo B (L)", new int[] { 6, 0, 6, 0, 0, 0, 0, 0, 0 } },
    { "888000888 Fat", new int[] { 8, 8, 8, 0, 0, 0, 8, 8, 8 } },
    { "080080883 Fifth Organ (Gospel) (U)", new int[] { 0, 8, 0, 0, 8, 0, 8, 8, 3 } },
    { "008806000 Fifth Organ (Gospel) (L)", new int[] { 0, 0, 8, 8, 0, 6, 0, 0, 0 } },
    { "006802000 Flutes", new int[] { 0, 0, 6, 8, 0, 2, 0, 0, 0 } },
    { "888666888 Full and High", new int[] { 8, 8, 8, 6, 6, 6, 8, 8, 8 } },
    { "868868068 Full and Sweet", new int[] { 8, 6, 8, 8, 6, 8, 0, 6, 8 } },
    { "888888888 Full Organ", new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8 } },
    { "688600004 Funky Comping", new int[] { 6, 8, 8, 6, 0, 0, 0, 0, 4 } },
    { "888800000 Gimme Some Loving", new int[] { 8, 8, 8, 8, 0, 0, 0, 0, 0 } },
    { "808808008 Gospel 1", new int[] { 8, 0, 8, 8, 0, 8, 0, 0, 8 } },
    { "888000008 Gospel 2", new int[] { 8, 8, 8, 0, 0, 0, 0, 0, 8 } },
    { "868666568 Greg Allman 1", new int[] { 8, 6, 8, 6, 6, 6, 5, 6, 8 } },
    { "888600000 Greg Allman 2", new int[] { 8, 8, 8, 6, 0, 0, 0, 0, 0 } },
    { "886000040 Greg Allman 3", new int[] { 8, 8, 6, 0, 0, 0, 0, 4, 0 } },
    { "888800088 Greg Rolie 1", new int[] { 8, 8, 8, 8, 0, 0, 0, 8, 8 } },
    { "886400000 Greg Rolie 2", new int[] { 8, 8, 6, 4, 0, 0, 0, 0, 0 } },
    { "888886666 Greg Rolie 4", new int[] { 8, 8, 8, 8, 8, 6, 6, 6, 6 } },
    { "888420080 Groove Holmes (Gospel) (U)", new int[] { 8, 8, 8, 4, 2, 0, 0, 8, 0 } },
    { "000505000 Groove Holmes (Gospel) (L)", new int[] { 0, 0, 0, 5, 0, 5, 0, 0, 0 } },
    { "880000000 House Bass (Gospel) (U)", new int[] { 8, 8, 0, 0, 0, 0, 0, 0, 0 } },
    { "008080000 House Bass (Gospel) (L)", new int[] { 0, 0, 8, 0, 8, 0, 0, 0, 0 } },
    { "868600006 Jimmy McGriff 1", new int[] { 8, 6, 8, 6, 0, 0, 0, 0, 6 } },
    { "883200125 Jimmy McGriff 2 (Gospel) (U)", new int[] { 8, 8, 3, 2, 0, 0, 1, 2, 5 } },
    { "448650000 Jimmy McGriff 2 (Gospel) (L)", new int[] { 4, 4, 8, 6, 5, 0, 0, 0, 0 } },
    { "888888888 Jimmy Smith 1 (U)", new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8 } },
    { "007500000 Jimmy Smith 1 (L)", new int[] { 0, 0, 7, 5, 0, 0, 0, 0, 0 } },
    { "888000000 Jimmy Smith 2 (U)", new int[] { 8, 8, 8, 0, 0, 0, 0, 0, 0 } },
    { "838000000 Jimmy Smith 2 (L)", new int[] { 8, 3, 8, 0, 0, 0, 0, 0, 0 } },
    { "888000000 Jimmy Smith 3 (U)", new int[] { 8, 8, 8, 0, 0, 0, 0, 0, 0 } },
    { "808000000 Jimmy Smith 3 (L)", new int[] { 8, 0, 8, 0, 0, 0, 0, 0, 0 } },
    { "888400080 Joey DeFrancesco", new int[] { 8, 8, 8, 4, 0, 0, 0, 8, 0 } },
    { "884400000 Jon Lord", new int[] { 8, 8, 4, 4, 0, 0, 0, 0, 0 } },
    { "880060000 Latin (Gospel) (U)", new int[] { 8, 8, 0, 0, 6, 0, 0, 0, 0 } },
    { "006676000 Latin (Gospel) (L)", new int[] { 0, 0, 6, 6, 7, 6, 0, 0, 0 } },
    { "800808000 Matthew Fisher", new int[] { 8, 0, 0, 8, 0, 8, 0, 0, 0 } },
    { "868800004 Melvin Crispel", new int[] { 8, 6, 8, 8, 0, 0, 0, 0, 4 } },
    { "803600000 Mellow", new int[] { 8, 0, 3, 6, 0, 0, 0, 0, 0 } },
    { "007800453 Meditation Time (Gospel) (U)", new int[] { 0, 0, 7, 8, 0, 0, 4, 5, 3 } },
    { "006700540 Meditation Time (Gospel) (L)", new int[] { 0, 0, 6, 7, 0, 0, 5, 4, 0 } },
    { "886800300 Paul Shaffer 1", new int[] { 8, 8, 6, 8, 0, 0, 3, 0, 0 } },
    { "888768888 Paul Shaffer 2", new int[] { 8, 8, 8, 7, 6, 8, 8, 8, 8 } },
    { "888878678 Paul Shaffer 3", new int[] { 8, 8, 8, 8, 7, 8, 6, 7, 8 } },
    { "850005000 Pink Floyd", new int[] { 8, 5, 0, 0, 0, 5, 0, 0, 0 } },
    { "888800000 Power Chords", new int[] { 8, 8, 8, 8, 0, 0, 0, 0, 0 } },
    { "888800000 Progessive (Gospel) (U)", new int[] { 8, 8, 8, 8, 0, 0, 0, 0, 0 } },
    { "008884000 Progessive (Gospel) (L)", new int[] { 0, 0, 8, 8, 8, 4, 0, 0, 0 } },
    { "006876400 Ray Charles", new int[] { 0, 0, 6, 8, 7, 6, 4, 0, 0 } },
    { "808000008 Reggae", new int[] { 8, 0, 8, 0, 0, 0, 0, 0, 8 } },
    { "888800000 Rock, R&B (U)", new int[] { 8, 8, 8, 8, 0, 0, 0, 0, 0 } },
    { "848000000 Rock, R&B (L)", new int[] { 8, 4, 8, 0, 0, 0, 0, 0, 0 } },
    { "800388888 Screaming (Gospel) (U)", new int[] { 8, 0, 0, 3, 8, 8, 8, 8, 8 } },
    { "007033333 Screaming (Gospel) (L)", new int[] { 0, 0, 7, 0, 3, 3, 3, 3, 3 } },
    { "008888800 Shirley Scott", new int[] { 0, 0, 8, 8, 8, 8, 8, 0, 0 } },
    { "830000378 Simmering", new int[] { 8, 3, 0, 0, 0, 0, 3, 7, 8 } },
    { "876556788 Shouting 1", new int[] { 8, 7, 6, 5, 5, 6, 7, 8, 8 } },
    { "668848588 Shouting 2", new int[] { 6, 6, 8, 8, 4, 8, 5, 8, 8 } },
    { "878645466 Shouting 3 (Gospel) (U)", new int[] { 8, 7, 8, 6, 4, 5, 4, 6, 6 } },
    { "888800000 Shouting 3 (Gospel) (L)", new int[] { 8, 8, 8, 8, 0, 0, 0, 0, 0 } },
    { "008400000 Slow Balllad", new int[] { 0, 0, 8, 4, 0, 0, 0, 0, 0 } },
    { "068840003 Slowly", new int[] { 0, 6, 8, 8, 4, 0, 0, 0, 3 } },
    { "888700000 Soft Backing (Gospel) (U)", new int[] { 8, 8, 8, 7, 0, 0, 0, 0, 0 } },
    { "555400000 Soft Backing (Gospel) (L)", new int[] { 5, 5, 5, 4, 0, 0, 0, 0, 0 } },
    { "808400008 Soft Chords", new int[] { 8, 0, 8, 4, 0, 0, 0, 0, 8 } },
    { "678404231 Speaker Talking (Gospel) (U)", new int[] { 6, 7, 8, 4, 0, 4, 2, 3, 1 } },
    { "006602024 Speaker Talking (Gospel) (L)", new int[] { 0, 0, 6, 6, 0, 2, 0, 2, 4 } },
    { "888643200 Steppenwolf", new int[] { 8, 8, 8, 6, 4, 3, 2, 0, 0 } },
    { "888876788 Steve Winwood", new int[] { 8, 8, 8, 8, 7, 6, 7, 8, 8 } },
    { "876543211 Strings", new int[] { 8, 7, 6, 5, 4, 3, 2, 1, 1 } },
    { "008000000 Sweet", new int[] { 0, 0, 8, 0, 0, 0, 0, 0, 0 } },
    { "787746046 Testimony Service  (Gospel) (U)", new int[] { 7, 8, 7, 7, 4, 6, 0, 4, 6 } },
    { "008800673 Testimony Service  (Gospel) (L)", new int[] { 0, 0, 8, 8, 0, 0, 6, 7, 3 } },
    { "878656467 Theatre Organ (Gospel) (U)", new int[] { 8, 7, 8, 6, 5, 6, 4, 6, 7 } },
    { "008844000 Theatre Organ (Gospel) (L)", new int[] { 0, 0, 8, 8, 4, 4, 0, 0, 0 } },
    { "888800000 Tom Coster", new int[] { 8, 8, 8, 8, 0, 0, 0, 0, 0 } },
    { "800000008 Whistle 1", new int[] { 8, 0, 0, 0, 0, 0, 0, 0, 8 } },
    { "888000008 Whistle 2", new int[] { 8, 8, 8, 0, 0, 0, 0, 0, 8 } },
    { "688600000 Whiter Shade Of Pale 1 (U)", new int[] { 6, 8, 8, 6, 0, 0, 0, 0, 0 } },
    { "880070770 Whiter Shade Of Pale 1 (L)", new int[] { 8, 8, 0, 0, 7, 0, 7, 7, 0 } },
    { "888808006 Whiter Shade Of Pale 2 (U)", new int[] { 8, 8, 8, 8, 0, 8, 0, 0, 6 } },
    { "004440000 Whiter Shade Of Pale 2 (L)", new int[] { 0, 0, 4, 4, 4, 0, 0, 0, 0 } },
    { "866800000 Wide Leslie", new int[] { 8, 6, 6, 8, 0, 0, 0, 0, 0 } },
    };

    }
