package it.snowdays.snowdays23.util.platform;

public class NfcUtils {

    public static String toHexString(byte[] arr) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : arr) {
            builder.append(String.format("%02x", b)).append(":");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString().replace(":", "");
    }

}
