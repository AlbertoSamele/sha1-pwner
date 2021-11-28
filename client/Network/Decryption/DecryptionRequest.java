package Network.Decryption;
// Request model for DecryptionHandler
public class DecryptionRequest {

    // The hashed password of the file to be decrypted
    public final byte[] hashedPassword;
    // The decrypted password's length
    public final int passwordLength;
    // The encrypted file's length
    public final long fileLength;


    public DecryptionRequest(byte[] hashedPassword, int passwordLength, long fileLength){
        this.hashedPassword = hashedPassword;
        this.passwordLength= passwordLength;
        this.fileLength = fileLength;
    }
    
}
