package Managers;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoManager {
    /**
     * Hashes a string with the SHA-1 algorithm
     * @param data the string to hash
     * @return A 20 bytes array representing the hash of the string
     */
    public static byte[] hashSHA1(String data) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return md.digest(data.getBytes());
    }

    /**
     * Reduces given hash into plaintext representable of the same length as the original hash
     * @param hash the hash to be reduced
     * @param j constant to lessen the chances of collisions during reduction
     * @param length the length of the reduced hash to be generated
     * @return
     */
    public static String reduceHash(byte[] hash, int j, int length) {
        String hashStringRepresentable = "";
        for (int i=0; i < hash.length; i++) {
            hashStringRepresentable += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
        }
        String stringifiedLong = hashStringRepresentable.replaceAll("[^0-9]", "");
        long hashLongRepresentable = Long.parseLong(stringifiedLong.substring(0, Math.min(stringifiedLong.length() - 1, 15)));
        // Reducing hash
        String reducedHash = "";
        long number = (long) ((hashLongRepresentable + j) % Math.pow(93,length));
        for(int i = 0; i < length; i++){
            int n = (int)(number % 93) + 33;
            char c = (char) n;
            number = number/93;
            reducedHash = c + reducedHash;
        }

        return reducedHash;
    }
}
