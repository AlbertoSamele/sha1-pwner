package Util;

public class CharsetIterable {
    
    public enum Charset {
        ASCII;

        /**
         * @return all character elements available in a charset
         */
        public char[] elements() {
            switch(this) {
            case ASCII: 
                return new char[]{'!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~'};
            default:
                return new char[]{};
            }
        }
    }

    public static CharsetIterable ASCII = new CharsetIterable(Charset.ASCII);

    // The charset associated with the CharsetIterable instance
    public final Charset charset;


    private CharsetIterable(Charset charset) {
        this.charset = charset;
    }


    /**
     * @return the minimum lexicographic value in the charset
     */
    public char min() {
        return new String(charset.elements()).chars()
                .mapToObj(i->(char)i)
                .min( (o1, o2) -> o1 - o2)
                .orElse(charset.elements()[0]);
    }

    /**
     * @return the maxium lexicographic value in the charset
     */
    public char max() {
        return new String(charset.elements()).chars()
                .mapToObj(i->(char)i)
                .min( (o1, o2) -> o2 - o1)
                .orElse(charset.elements()[charset.elements().length - 1]);
    }

}
