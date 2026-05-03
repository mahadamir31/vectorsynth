package synth;

public class LPF extends Filter
    {
    Unit frequencyUnit = new Constant(1.0);

    public void setFrequencyUnit(Unit frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
        }

    public Unit getFrequencyUnit() {
        return this.frequencyUnit;
        }

    Unit resonanceUnit = new Constant(0.0);

    public void setResonanceUnit(Unit resonanceUnit) {
        this.resonanceUnit = resonanceUnit;
        }

    public Unit getResonanceUnit() {
        return this.resonanceUnit;
        }

    void updateFilter(double cutoff, double Q)
        {
        double T = Config.INV_SAMPLING_RATE;
        /*
          double W = ... ;  IMPLEMENT ME
          double D = ... ;  IMPLEMENT ME
        */
       double w0 = 2.0*Math.PI * cutoff;
       double d = Math.cos(w0*T/2.0) /Math.sin(w0 *T/2.0);
       double d2 = d*d;
       double J= Q +d + d2*Q;

       b0 = Q/J;
       b[0] = 2.0 *Q /J;
       b[1] = Q/J;
       a[0]= (2.0*Q -2.0 *d2 *Q)/ J;
       a[1]=(Q-d+d2 *Q)/J;

        //// IMPLEMENT ME
        }

    public LPF()
        {
        super(2);
        }

    public static final double MIN_CUTOFF = 0.001;       // Not permitted to divide by zero
    public static final double MAX_CUTOFF = 0.999;       // Numerical instability at Nyquist
    double lastCutoff = Double.NaN;
    double lastQ = Double.NaN;
    public double tick(long tickCount)
        {
        double q = (resonanceUnit.getValue() * 10 + 1.0) / Math.sqrt(2.0);
        double cutoff = frequencyUnit.getValue();
        if (cutoff > MAX_CUTOFF) cutoff = MAX_CUTOFF;
        if (cutoff < MIN_CUTOFF) cutoff = MIN_CUTOFF;
        if (lastCutoff != cutoff || lastQ != q)
            {
            lastCutoff = cutoff;
            lastQ = q;
            updateFilter(valueToHz(cutoff), q);
            }
        return super.tick(tickCount);
        }
    }
