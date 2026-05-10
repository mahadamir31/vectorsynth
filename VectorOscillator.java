import synth.*;

// Implements the books vector synthesis equation
// m(t) = alpha * f1(t ) + (1-alpha) * f2(t) + beta * f3(t) + (1-beta) * f4(t)

public class VectorOscillator extends Unit{
    // the four waveform inputs one per corner of the vector pad
    private Unit input0=new Constant(0.5); // top left corner
    private Unit input1=new Constant(0.5); // top right corner
    private Unit input2=new Constant(0.5); // bottom left corner
    private Unit input3=new Constant(0.5); // bottom right corner

    // alpha = X position on pad (0.0 = left, 1.0 = right)
    // beta  = Y position on pad (0.0 = top,  1.0 = bottom)
    public volatile double alpha=0.5;
    public volatile double beta=0.5;

    // input setters 
    public void setInput0(Unit u){
        input0 =u;
    }
    public void setInput1(Unit u){
        input1=u;
    }
    public void setInput2(Unit u){
        input2=u;
    }
    public void setInput3(Unit u){
        input3=u;
    }

    public Unit getInput0(){
        return input0;
    }
    public Unit getInput1(){
        return input1;
    }
    public Unit getInput2(){
        return input2;
    }
    public Unit getInput3(){
        return input3;
    }

    public void setPosition(double a, double b){
        alpha=a;
        beta=b;
    }
    @Override
    public double tick(long tickCount){
        double a =alpha;
        double b=beta;

        // center each input from [0..1] to [-1..+1]
        double c0=(input0.getValue()-0.47) /0.8; // BlitSaw: * 0.8 + 0.47
        double c1=(input1.getValue()-0.15)/0.7; // BlitSquare: * 0.7 + 0.15
        double c2=(input2.getValue()-0.5)/4.0; // BlitTriangle: * 4.0 + 0.5
        double c3=(input3.getValue()-0.5)/0.5; // Blit: * 0.5 + 0.5

        // book equation: alpha*f1 + (1-alpha)*f2 + beta*f3 + (1-beta)*f4
        // Max weight sum = 2.0, so divide by 2 to keep output in [-1..+1]
        //double mixed = (a*c0+(1.0-a)*c1+b*c2+(1.0 -b) * c3)/2.0;

        // bilinear interpolation, each corner is purely at its position
        double top= (1.0-a)*c0+a *c1; // blend top 2
        double bottom=(1.0-a) *c2+a*c3; // blend bottom 2
        double mixed = (1.0-b) * top + b*bottom; // blend top/bottom

        // clamp and return to [0..1]
        double out=mixed *0.5 +0.5;
        if (out< 0.0) out= 0.0;
        if (out > 1.0) out=1.0;
        return out;
    }
}
