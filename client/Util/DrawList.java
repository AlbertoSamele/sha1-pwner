package Util;

import java.util.ArrayList;
import java.util.Random;


// A set which allows to draw random elements from its values
public class DrawList<T extends Object> extends ArrayList<T> {
    
    // The random numbers picker
    private final Random randomizer = new Random(System.currentTimeMillis());


    /**
     * Draws a random element from the set values
     * @return the random element
     * @throws IllegalStateException if a draw has been attempted when the set was empty
     */
    public T draw() throws IllegalStateException {
        int randomIndex = randomizer.nextInt(size());
        return get(randomIndex);
    }
}
