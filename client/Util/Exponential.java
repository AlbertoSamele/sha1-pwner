package Util;

import java.util.Random;

public class Exponential {

    private double lambda;
    private Random rand;

    /** Creates a variable with a given mean. */
    public Exponential(double lambda) {
        this.lambda = lambda;
        rand = new Random();
    }

    public double next() {
        return -Math.log(1.0 - rand.nextDouble()) / lambda;
    }
}