package Manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoManager {

    private static final String ALGORITHM = "AES";


    /**
     * Generates a secret key that can be use for AES encryption. The key is insecure as normally a random salt should be used.
     * @param password the password from which the key will be derived from
     * @return the encryption key
     */
    public static SecretKey getKeyFromPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), " ".getBytes(), 65536, 128);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(),"AES");
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
     * Decryps given file
     * @param key Key to use for the decryption
     * @param inputFile File to decrypt
     * @param outputFile File where the decrypted result is written
     */
    public static void decryptFile(SecretKey key, File inputFile, File outputFile) 
    throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, 
    BadPaddingException, InvalidKeyException {
        cryptographyOnFile(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
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
