package Manager;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class CryptoManager {

    private static final String ALGORITHM = "AES";

    /**
     * Generates a secret key that can be use for AES encryption. The key is insecure as normally a random salt should be used.
     * @param password password from which the key will be derived
     * @return the key for the encryption
     */
    public static SecretKey getKeyFromPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), " ".getBytes(), 65536, 128);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(),"AES");
    }

    /**
     * Encrypts input file into targer file
     * @param key key to use for the encryption
     * @param inputFile file to encrypt
     * @param outputFile file where the encrypted result is written
     */
    public static void encryptFile(SecretKey key, File inputFile, File outputFile) throws
    InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, 
    NoSuchAlgorithmException,BadPaddingException, InvalidKeyException {
        cryptographyOnFile(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

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
     * Applies a cryptographic operation (encryption or decryption) to a file with a second file as target output
     * @param cipherMode the cryptographic operation to perform
     * @param key the key to use during the cryptographic operation
     * @param inputFile file on which the cryptographic must be performed
     * @param outputFile file where the result is written
     */
    private static void cryptographyOnFile(int cipherMode, SecretKey key, File inputFile, File outputFile) 
    throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, 
    BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(cipherMode, key);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        // A buffer is used for IO operations so that the whole file does not need to be put in memory which can be
        // problematic if the file is too big
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) > -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }
}
