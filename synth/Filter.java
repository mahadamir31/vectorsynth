import synth.*;

/**
   A superclass of Filter units.  You provide the initial filter constants as the a array (representing
   coefficients a1 ... an), the b array (representing coefficients a1 ... an), and the b0 coefficient.
   These coefficients may be changed over time as necessary.  The current filter state is stored as
   x0 and as the x array (x1...xn) and y array (y1...yn), which are updated in Filter.tick().  You attach
   the INPUT to the filter.
**/

public class Filter extends Unit 
    {
    Unit input = new Constant(0.5);
    
    protected double b0;
    protected double[] b;
    protected double[] a;

    protected double x0;
    protected double[] x;
    protected double[] y;
        
    public Filter(int size)
        {
        this.a = new double[size];
        this.b = new double[size];
        this.x = new double[size];
        this.y = new double[size];
        }
        
    public Unit getInput() { return input; }
    public void setInput(Unit input) { this.input = input; }

    public double tick(long tickCount) 
        {
        double x0 = input.getValue() - 0.5;             // center it around 0
        
        // do sum
        double sum = b0*x0;
        for(int i=0;i<b.length;i++){
            sum += b[i] *x[i]-a[i]*y[i];
        }
        /// IMPLEMENT ME
        
        // do shifts
        for(int i=x.length-1; i>0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        if(x.length >0){
            x[0] = x0;
            y[0]=sum;
        }
        /// IMPLEMENT ME

        return sum + 0.5;                                               // recenter it around 0.5
        }
    }
