import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ClientManager {

    Crypto crypto = new Crypto();
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    SecretKey secretKey = keyGen.generateKey();
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

    public ClientManager() throws NoSuchAlgorithmException {

        keyGen.init(192, SecureRandom.getInstanceStrong());

    }

    public void hasBeenUsed() {
        // returns true if the program has been used before, checking if it's the user's first time using it.

    }

    public void encryptText(String text) throws Exception {
        // encrypting the text from the clipboard history

        byte[] inputBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedTextInBytes = crypto.encrypt(inputBytes, secretKeySpec);
        String encryptedText = new String(encryptedTextInBytes, StandardCharsets.UTF_8);
        System.out.println("This is the encrypted text: ");
        System.out.println(encryptedText);
        decryptText(encryptedTextInBytes);

    }

    public void decryptText(byte[] textToDecrypt) throws Exception {
        // decrypting the text from the clipboard history, received from the server

        byte[] encryptedTextInBytes = crypto.decrypt(textToDecrypt, secretKeySpec);
        String decryptedText = new String(encryptedTextInBytes, StandardCharsets.UTF_8);
        System.out.println("This is the decrypted text: ");
        System.out.println(decryptedText);


    }


}
