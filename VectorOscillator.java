import synth.*;

public class VectorOscillator extends Unit {
    private Unit input0 = new Constant(0.5);
    private Unit input1 = new Constant(0.5);
    private Unit input2 = new Constant(0.5);
    private Unit input3 = new Constant(0.5);

    public volatile double alpha = 0.5;
    public volatile double beta  = 0.5;

    // running min/max for each input
    private double[] min = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private double[] max = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};

    // how quickly the tracked range decays back toward center (prevents stale ranges after osc swap)
    private static final double DECAY = 0.999995;

    public void setInput0(Unit u) { input0 = u; resetRange(0); }
    public void setInput1(Unit u) { input1 = u; resetRange(1); }
    public void setInput2(Unit u) { input2 = u; resetRange(2); }
    public void setInput3(Unit u) { input3 = u; resetRange(3); }

    public Unit getInput0() { return input0; }
    public Unit getInput1() { return input1; }
    public Unit getInput2() { return input2; }
    public Unit getInput3() { return input3; }

    public void setPosition(double a, double b) { alpha = a; beta = b; }

    private void resetRange(int i) {
        min[i] = Double.MAX_VALUE;
        max[i] = -Double.MAX_VALUE;
    }

    private double normalize(double val, int i) {
        // decay range slowly toward center to handle osc swaps
        min[i] = Math.min(min[i], val) * DECAY + 0.5 * (1.0 - DECAY);
        max[i] = Math.max(max[i], val) * DECAY + 0.5 * (1.0 - DECAY);

        double range = max[i] - min[i];
        if (range < 1e-6) return 0.0; // not enough data yet, output silence
        // map to [-1..+1]
        return ((val - min[i]) / range) * 2.0 - 1.0;
    }

    @Override
    public double tick(long tickCount) {
        double a = alpha;
        double b = beta;

        double c0 = normalize(input0.getValue(), 0);
        double c1 = normalize(input1.getValue(), 1);
        double c2 = normalize(input2.getValue(), 2);
        double c3 = normalize(input3.getValue(), 3);

        // bilinear interpolation
        double top    = (1.0 - a) * c0 + a * c1;
        double bottom = (1.0 - a) * c2 + a * c3;
        double mixed  = (1.0 - b) * top + b * bottom;

        // convert back to [0..1]
        double out = mixed * 0.5 + 0.5;
        if (out < 0.0) out = 0.0;
        if (out > 1.0) out = 1.0;
        return out;
    }
}