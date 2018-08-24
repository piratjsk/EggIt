package net.piratjsk.eggit;

import org.bukkit.ChatColor;

public class Util {

    public static String encodeAsColors(final double num) {
        final String doubleString = String.valueOf(num);
        final StringBuilder encodedStringBuilder = new StringBuilder();
        for (final char c : doubleString.toCharArray()) {
            if (c == '.')
                encodedStringBuilder.append(ChatColor.ITALIC);
            else if (c == '-')
                encodedStringBuilder.append(ChatColor.STRIKETHROUGH);
            else
                encodedStringBuilder.append(ChatColor.getByChar(c));
        }
        return encodedStringBuilder.toString();
    }

    public static double decodeFromColors(final String code) {
        final StringBuilder doubleStringBuilder = new StringBuilder();
        for (final char c : code.toCharArray()) {
            if (c == ChatColor.COLOR_CHAR) continue;
            if (c == ChatColor.ITALIC.getChar())
                doubleStringBuilder.append('.');
            else if (c == ChatColor.STRIKETHROUGH.getChar())
                doubleStringBuilder.append('-');
            else
                doubleStringBuilder.append(c);
        }
        return Double.valueOf(doubleStringBuilder.toString());
    }

    public static String colorize(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String decolorize(final String text) {
        return ChatColor.stripColor(text);
    }

}
