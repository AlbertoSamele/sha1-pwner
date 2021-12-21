package Util;

import java.util.Random;

public class Exponential {

    private double avg;
    private Random rand;

    /** Creates a variable with a given mean. */
    public Exponential(double avg) {
        this.avg = avg;
        rand = new Random();
    }

    public double next() {
        return -Math.log(1.0 - rand.nextDouble()) / avg;
    }
}