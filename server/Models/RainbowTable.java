package Models;

import java.util.Map;

public class RainbowTable {
    // The length of a single table chain
    public final int chainLength;
    // The rainbow table, where each endpoint is mapped to its entrypoint
    public final Map<String, String> entries;


    /**
     * @param chainLength the length of a single table chain
     * @param entries the rainbow table, where each endpoint is mapped to its entrypoint
     */
    public RainbowTable(int chainLength, Map<String, String> entries) {
        this.chainLength = chainLength;
        this.entries = entries;
    }

}
