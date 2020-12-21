import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    /**
     * Returns the formatted MAC doorId with the given {@code divisionChar}. For
     * example if divisionChar = ":", MAC = "01:AA:22:33:BB:44".
     *
     * @param rawMac       a raw MAC doorId such as 01AA2233BB44
     * @param divisionChar a division character such as ':', '.', '-', etc
     * @return the formatted MAC doorId {@link String} or <b>null</b>
     */
    @Nullable
    public static String formatMacAddress(@Nonnull String rawMac, char divisionChar) {
        if (rawMac.length() != 12) {
            return null;
        }
        String mac = rawMac.replaceAll("(.{2})", "$1" + divisionChar).substring(0, 17).toUpperCase();
        if (mac.length() != 17) {
            return null;
        }
        return mac;
    }

    /**
     * Returns the normalized MAC doorId {@link String} matching this format:
     * 01AA2233BB44.
     *
     * @param formattedMac a formatted MAC doorId such as 01:AA:22:33:BB:44,
     *                     01.AA.22.33.BB.44, 01aa2233bb44.
     * @return the normalized MAC doorId {@link String} or <b>null</b>
     */
    @Nullable
    public static String normalizeMacAddress(@Nonnull String formattedMac) {
        String mac = formattedMac.replaceAll("[^a-fA-F0-9]", "").toUpperCase();
        if (mac.length() != 12) {
            return null;
        }
        return mac;
    }

    /**
     * Indicates whether the given {@link String} is a valid MAC doorId.<br/>
     * Are considered valid: 01AA2233BB44, 01:AA:22:33:BB:44, 01.AA.22.33.BB.44,
     * 01aa2233bb44, etc.
     *
     * @param mac
     * @return
     */
    public static boolean checkMacAddressValidity(String mac) {
        if (mac.length() != 12 /* raw MAC */ && mac.length() != 17 /* MAC with separator */) {
            return false;
        }
        Pattern p = Pattern.compile("^([a-fA-F0-9]{2}[.:-]?){5}[a-fA-F0-9]{2}$");
        Matcher m = p.matcher(mac);
        return m.find();
    }
}