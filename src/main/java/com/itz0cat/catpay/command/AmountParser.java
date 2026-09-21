package com.itz0cat.catpay.command;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmountParser {
    private static final Pattern SHORTHAND_PATTERN = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)\\s*([kmbKMB])?$");
    private static final NumberFormat COMMA_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    static {
        COMMA_FORMAT.setGroupingUsed(true);
    }

    /**
     * Parses an amount string (e.g. "500000", "10k", "1.5m", "2b") into a positive long.
     * @param input Raw amount string
     * @return Normalized long amount
     * @throws IllegalArgumentException if amount is invalid, non-positive, or exceeds safe limits
     */
    public static long parse(String input) throws IllegalArgumentException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be empty.");
        }

        String cleaned = input.trim().replace(",", "").replace("$", "");
        Matcher matcher = SHORTHAND_PATTERN.matcher(cleaned);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid amount format: '" + input + "'.");
        }

        try {
            double baseValue = Double.parseDouble(matcher.group(1));
            if (Double.isNaN(baseValue) || Double.isInfinite(baseValue) || baseValue <= 0) {
                throw new IllegalArgumentException("Amount must be a positive number.");
            }

            String unit = matcher.group(2);
            long multiplier = 1L;
            if (unit != null) {
                switch (unit.toLowerCase(Locale.ROOT)) {
                    case "k" -> multiplier = 1_000L;
                    case "m" -> multiplier = 1_000_000L;
                    case "b" -> multiplier = 1_000_000_000L;
                }
            }

            double total = baseValue * multiplier;
            if (total > Long.MAX_VALUE || total <= 0) {
                throw new IllegalArgumentException("Amount is too large or causes numeric overflow.");
            }

            return (long) total;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse numeric amount: " + e.getMessage());
        }
    }

    /**
     * Formats a long value with comma grouping (e.g. 500000000 -> "500,000,000").
     */
    public static String format(long amount) {
        return COMMA_FORMAT.format(amount);
    }
}
