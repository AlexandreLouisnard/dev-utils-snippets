public class BinHexUtils {
    /**
     * Converts byte to hexadecimal {@link String}.
     *
     * @param b
     * @return the binary {@link String} representation
     */
    public static String toBinaryString(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    /**
     * Converts int to hexadecimal {@link String}.
     *
     * @param i
     * @return the binary {@link String} representation
     */
    public static String toBinaryString(int i) {
        return String.format("%16s", Integer.toBinaryString(i)).replace(' ', '0');
    }

    /**
     * Converts byte[] to hexadecimal {@link String}.
     *
     * @param bytes
     * @param separator the separator between each byte.<br />
     *                  Pass <b>null</b> to ignore this parameter.
     * @param maxLength the max length of the {@link String}. If exceeded, the
     *                  resulting {@link String} will be truncated.<br />
     *                  Pass <b></b>0 or negative value</b> to ignore this
     *                  parameter.
     * @param reverse   <b>true</b> to reverse the order of bytes.
     * @return
     */
    public static String toHexaString(byte[] bytes, String separator, int maxLength, boolean reverse) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            if (reverse) {
                sb.insert(0, String.format("%1$02X%2$s", b, separator));

            } else {
                sb.append(String.format("%1$02X%2$s", b, separator));
            }
            if (maxLength > 0 && sb.length() >= maxLength) {
                break;
            }
        }
        return sb.toString();
    }

    public static String toHexaString(byte[] bytes, String separator) {
        return toHexaString(bytes, separator, -1, false);
    }

    public static String toHexaString(byte[] bytes, boolean reverse) {
        return toHexaString(bytes, "", -1, reverse);
    }

    public static String toHexaString(byte[] bytes) {
        return toHexaString(bytes, "", -1, false);
    }

    public static @Nullable Bitmap encodeAsQRCode(String str, int size) {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, size, size, null);
        } catch (IllegalArgumentException | WriterException e) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, size, 0, 0, w, h);
        return bitmap;
    }

    /**
     * Returns the digit of the given number at the given position (starting from
     * 0).
     *
     * @param number
     * @param position
     * @return
     * @throws IndexOutOfBoundsException
     */
    public static byte getDigit(int number, int position) throws IndexOutOfBoundsException {
        return Byte.parseByte(Integer.toString(number).substring(position, position + 1));
    }

    /**
     * For the given {@code number}, returns the bit value at {@code bitIndex}.
     *
     * @param number
     * @param bitIndex
     * @return the bit value (0 or 1).
     */
    public static int getBit(int number, int bitIndex) {
        return (number >> bitIndex) & 1;
    }

    /**
     * For the given {@code number}, sets the bit at {@code bitIndex} to
     * {@code bitValue}, and returns the new number value.
     *
     * @param number
     * @param bitIndex 0 (LSB) to n (MSB)
     * @param bitValue 0 or 1
     * @return the new number value
     */
    public static int setBit(int number, int bitIndex, int bitValue) {
        switch (bitValue) {
            case 0:
                return number & ~(1 << bitIndex);
            case 1:
                return number | 1 << bitIndex;
            default:
                return number;
        }
    }

    /**
     * For the given {@code number}, toggles the bit value at {@code bitIndex}, and
     * returns the new number value.
     *
     * @param number
     * @param bitIndex
     * @return the new number value
     */
    public static int toggleBit(int number, int bitIndex) {
        return number ^ 1 << bitIndex;
    }
}