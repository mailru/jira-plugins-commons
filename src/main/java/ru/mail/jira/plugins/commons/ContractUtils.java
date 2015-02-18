package ru.mail.jira.plugins.commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedDeclaration")
public class ContractUtils {
    private final static Pattern INN_PATTERN = Pattern.compile("\\d{10}|\\d{12}");
    private final static Pattern SNILS_WITH_CHECKSUM_PATTERN = Pattern.compile("(\\d{3}-\\d{3}-\\d{3}) (\\d{2})");
    private final static Pattern SNILS_WITHOUT_CHECKSUM_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{3}");
    private final static Pattern EXTRA_SNILS_VALIDATE_PATTERN = Pattern.compile("0{3}|1{3}|2{3}|3{3}|4{3}|5{3}|6{3}|7{3}|8{3}|9{3}");

    public static boolean isValidSnils(String snils) {
        Matcher snilsWithCsMatcher = SNILS_WITH_CHECKSUM_PATTERN.matcher(snils);
        if (snilsWithCsMatcher.matches()) {
            String joinedPartBeforeSpace = snilsWithCsMatcher.group(1).replaceAll("-", "");
            int checksum = Integer.valueOf(snilsWithCsMatcher.group(2));

            if (EXTRA_SNILS_VALIDATE_PATTERN.matcher(joinedPartBeforeSpace).find())
                return false;

            int sum = 0;
            for (int i = 0, j = 9; i < 9; i++, j--)
                sum += Character.getNumericValue(joinedPartBeforeSpace.charAt(i)) * j;

            if (sum < 100)
                return checksum == sum;
            else if (sum == 100 || sum == 101)
                return checksum == 0;
            else
                return (checksum == sum % 101) || (checksum == 0 && sum % 101 == 100);
        }

        if (SNILS_WITHOUT_CHECKSUM_PATTERN.matcher(snils).matches()) {
            String joinedPartBeforeSpace = snils.replaceAll("-", "");
            return !EXTRA_SNILS_VALIDATE_PATTERN.matcher(joinedPartBeforeSpace).find() && Integer.valueOf(joinedPartBeforeSpace) <= 1001998;
        }

        return false;
    }

    public static boolean isValidInn(String inn) {
        return INN_PATTERN.matcher(inn).matches();
    }
}
