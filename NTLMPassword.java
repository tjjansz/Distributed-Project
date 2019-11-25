/**
 * This class encodes strings to NTLM Hex encoding.
 */

import jcifs.smb.NtlmPasswordAuthentication;
import java.io.FileWriter;
import java.io.File;

public class NTLMPassword {
    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public NTLMPassword() {
    }

    /**
     * 
     * @param value 
     * @return byte[] 
     */
    public static byte[] encodeBytes(String value){
        String s = (value != null) ? value : "";
        NtlmPasswordAuthentication temp = new NtlmPasswordAuthentication("");
        byte[] hash = temp.nTOWFv1(s);
        return hash;
    }

    /**
     * 
     * @param value
     * @return byte[]
     */
    public static String encode(String value) {
        String s = (value != null) ? value : "";
        NtlmPasswordAuthentication temp = new NtlmPasswordAuthentication("");
        byte[] hash = temp.nTOWFv1(s);
        return bytesToHex(hash).toUpperCase();
    }

    /**
     * 
     * @param bytes
     * @return String
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}