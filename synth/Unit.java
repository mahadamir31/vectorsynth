package synth;

/**
   A top-level superclass for all Units.  Each unit overrides a method called tick()
   to return the current value of the Unit for the given tick count.  The public method doUpdate(...)
   calls tick(...) and then stores the value such that it can be retrieved with getValue().
*/
 

public abstract class Unit 
    {
    /**
     * Maps linearly from domain [0.0, Config.NYQUIST_LIMIT] to range [0.0, 1.0]
     **/
    public static final double hzToValue(double hz) 
        {
        if (hz > Config.SAMPLING_RATE / 2.0)
            {
            return 1.0;
            }
        if (hz < 0) 
            {
            return 0.0;
            }
        return hz * Config.INV_NYQUIST_LIMIT;
        }

    /**
     * Maps linearly from domain [0.0, 1.0] to range [0.0, Config.NYQUIST_LIMIT]
     **/
    public static final double valueToHz(double val)
        {
        if (val <= 0)
            {
            return 0;
            }
        if (val > Config.NYQUIST_LIMIT)
            {
            return Config.NYQUIST_LIMIT;
            }
        return val * Config.NYQUIST_LIMIT;
        }

    double value = 0.0;

    /** Returns the current value of the Unit.  This is normally updated during doUpdate()
        except for Constants where it may be set once and left like that. */
    public double getValue() { return value; }

    /** Sets the current value of the Unit.  This is only exposed so that Dials and Constants
        can call it.  Otherwise you probably shouldn't touch it. */
    public void setValue(double value) { this.value = value; }

    /** Override this method to return the current value associated with the given tick.
        This method will be called by doUpdate to update the Unit's value returned by getValue(). */
    protected abstract double tick(long tickCount);

    /** Updates the current value returned by getValue() by calling tick(...) */
    public void doUpdate(long tickCount) 
        {
        setValue(tick(tickCount));
        }
    }
