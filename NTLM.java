import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.commons.httpclient.util.Base64;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 * Provides an implementation of the NTLM authentication protocol.
 * 

 * This class provides methods for generating authentication
 * challenge responses for the NTLM authentication protocol.  The NTLM
 * protocol is a proprietary Microsoft protocol and as such no RFC
 * exists for it.  This class is based upon the reverse engineering
 * efforts of a wide range of people.


 * 
 * 
Please note that an implementation of JCE must be correctly installed and configured when
 * using NTLM support.


 * 
 * 
This class should not be used externally to HttpClient as it's API is specifically
 * designed to work with HttpClient's use case, in particular it's connection management.


 *
 * @deprecated this class will be made package access for 2.0beta2
 *
 * @author Adrian Sutton
 * @author Jeff Dever
 * @author Mike Bowler
 *
 * @version $Revision: 1.12.2.2 $ $Date: 2004/02/22 18:21:13 $
 * @since 2.0alpha2
 */
public class NTLM {

    /** The current response */
    private byte[] currentResponse;

    /** The current position */
    private int currentPosition = 0;

    /** Log object for this class. */
   // private static final Log LOG = LogFactory.getLog(NTLM.class);

    /** Character encoding */
    public static final String DEFAULT_CHARSET = "ASCII";


    /**
     * Return the cipher for the specified key.
     * @param key The key.
     * @return Cipher The cipher.
     * @throws HttpException If the cipher cannot be retrieved.
     */
    private Cipher getCipher(byte[] key) {
        try {
            final Cipher ecipher = Cipher.getInstance("DES/ECB/NoPadding");
            key = setupKey(key);
            ecipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "DES"));
            return ecipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            Cipher empty = null;
            return empty;
            
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Cipher empty = null;
            return empty;
           
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            Cipher empty = null;
            return empty;
           
        }

         
    }

    /** 
     * Adds parity bits to the key.
     * @param key56 The key
     * @return The modified key.
     */
    private byte[] setupKey(byte[] key56) {
        byte[] key = new byte[8];
        key[0] = (byte) ((key56[0] >> 1) & 0xff);
        key[1] = (byte) ((((key56[0] & 0x01) << 6) 
            | (((key56[1] & 0xff) >> 2) & 0xff)) & 0xff);
        key[2] = (byte) ((((key56[1] & 0x03) << 5) 
            | (((key56[2] & 0xff) >> 3) & 0xff)) & 0xff);
        key[3] = (byte) ((((key56[2] & 0x07) << 4) 
            | (((key56[3] & 0xff) >> 4) & 0xff)) & 0xff);
        key[4] = (byte) ((((key56[3] & 0x0f) << 3) 
            | (((key56[4] & 0xff) >> 5) & 0xff)) & 0xff);
        key[5] = (byte) ((((key56[4] & 0x1f) << 2) 
            | (((key56[5] & 0xff) >> 6) & 0xff)) & 0xff);
        key[6] = (byte) ((((key56[5] & 0x3f) << 1) 
            | (((key56[6] & 0xff) >> 7) & 0xff)) & 0xff);
        key[7] = (byte) (key56[6] & 0x7f);
        
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) (key[i] << 1);
        }
        return key;
    }

    /**
     * Encrypt the data.
     * @param key The key.
     * @param bytes The data
     * @return byte[] The encrypted data
     * @throws HttpException If {@link Cipher.doFinal(byte[])} fails
     */
    private byte[] encrypt(byte[] key, byte[] bytes)
        {
        Cipher ecipher = getCipher(key);
        try {
            byte[] enc = ecipher.doFinal(bytes);
            return enc;
        } catch (IllegalBlockSizeException e) {
           
        } catch (BadPaddingException e) {
           
        }
        return new byte [2];
    }

    /** 
     * Prepares the object to create a response of the given length.
     * @param length the length of the response to prepare.
     */
    private void prepareResponse(int length) {
        currentResponse = new byte[length];
        currentPosition = 0;
    }

    /** 
     * Adds the given byte to the response.
     * @param b the byte to add.
     */
    private void addByte(byte b) {
        currentResponse[currentPosition] = b;
        currentPosition++;
    }

    /** 
     * Adds the given bytes to the response.
     * @param bytes the bytes to add.
     */
    private void addBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            currentResponse[currentPosition] = bytes[i];
            currentPosition++;
        }
    }

    
    




    /** 
     * Creates the LANManager and NT response for the given password using the
     * given nonce.
     * @param password the password to create a hash for.
     * @param nonce the nonce sent by the server.
     * @return The response.
     * @throws HttpException If {@link #encrypt(byte[],byte[])} fails.
     */
    private byte[] hashPassword(String password, byte[] nonce)
        {
        byte[] passw = getBytes(password.toUpperCase());
        byte[] lmPw1 = new byte[7];
        byte[] lmPw2 = new byte[7];

        int len = passw.length;
        if (len > 7) {
            len = 7;
        }

        int idx;
        for (idx = 0; idx < len; idx++) {
            lmPw1[idx] = passw[idx];
        }
        for (; idx < 7; idx++) {
            lmPw1[idx] = (byte) 0;
        }

        len = passw.length;
        if (len > 14) {
            len = 14;
        }
        for (idx = 7; idx < len; idx++) {
            lmPw2[idx - 7] = passw[idx];
        }
        for (; idx < 14; idx++) {
            lmPw2[idx - 7] = (byte) 0;
        }

        // Create LanManager hashed Password
        byte[] magic = {
            (byte) 0x4B, (byte) 0x47, (byte) 0x53, (byte) 0x21, 
            (byte) 0x40, (byte) 0x23, (byte) 0x24, (byte) 0x25
        };

        byte[] lmHpw1;
        lmHpw1 = encrypt(lmPw1, magic);

        byte[] lmHpw2 = encrypt(lmPw2, magic);

        byte[] lmHpw = new byte[21];
        for (int i = 0; i < lmHpw1.length; i++) {
            lmHpw[i] = lmHpw1[i];
        }
        for (int i = 0; i < lmHpw2.length; i++) {
            lmHpw[i + 8] = lmHpw2[i];
        }
        for (int i = 0; i < 5; i++) {
            lmHpw[i + 16] = (byte) 0;
        }

        // Create the responses.
        byte[] lmResp = new byte[24];
        calcResp(lmHpw, nonce, lmResp);

        return lmResp;
    }

    /** 
     * Takes a 21 byte array and treats it as 3 56-bit DES keys.  The 8 byte
     * plaintext is encrypted with each key and the resulting 24 bytes are
     * stored in the results array.
     * 
     * @param keys The keys.
     * @param plaintext The plain text to encrypt.
     * @param results Where the results are stored.
     * @throws HttpException If {@link #encrypt(byte[],byte[])} fails.
     */
    private void calcResp(byte[] keys, byte[] plaintext, byte[] results)
         {
        byte[] keys1 = new byte[7];
        byte[] keys2 = new byte[7];
        byte[] keys3 = new byte[7];
        for (int i = 0; i < 7; i++) {
            keys1[i] = keys[i];
        }

        for (int i = 0; i < 7; i++) {
            keys2[i] = keys[i + 7];
        }

        for (int i = 0; i < 7; i++) {
            keys3[i] = keys[i + 14];
        }
        byte[] results1 = encrypt(keys1, plaintext);

        byte[] results2 = encrypt(keys2, plaintext);

        byte[] results3 = encrypt(keys3, plaintext);

        for (int i = 0; i < 8; i++) {
            results[i] = results1[i];
        }
        for (int i = 0; i < 8; i++) {
            results[i + 8] = results2[i];
        }
        for (int i = 0; i < 8; i++) {
            results[i + 16] = results3[i];
        }
    }

    /** 
     * Converts a given number to a two byte array in little endian order.
     * @param num the number to convert.
     * @return The byte representation of num in little endian order.
     */
    private byte[] convertShort(int num) {
        byte[] val = new byte[2];
        String hex = Integer.toString(num, 16);
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        String low = hex.substring(2, 4);
        String high = hex.substring(0, 2);

        val[0] = (byte) Integer.parseInt(low, 16);
        val[1] = (byte) Integer.parseInt(high, 16);
        return val;
    }
    
    /**
     * Convert a string to a byte array.
     * @param s The string
     * @return byte[] The resulting byte array.
     */
    private static byte[] getBytes(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Parameter may not be null"); 
        }
        try {
            return s.getBytes(DEFAULT_CHARSET); 
        } catch (UnsupportedEncodingException unexpectedEncodingException) {
            throw new RuntimeException("NTLM requires ASCII support"); 
        }
    }
}