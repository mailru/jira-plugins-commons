package ru.mail.jira.plugins.commons;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class LocalUtils {
    private static final String ZERO_CAPTION = "ноль";
    private static final String MINUS_CAPTION = "минус";

    private static final String[][] MAGNITUDE_CAPTIONS = new String[][] {
            { null,     null,       null,        null         },
            { "FEMALE", "тысяча",   "тысячи",    "тысяч"      },
            { null,     "миллион",  "миллиона",  "миллионов"  },
            { null,     "миллиард", "миллиарда", "миллиардов" }
    };
    private static final int SEX_MAGNITUDE = 0;
    private static final int ONE_MAGNITUDE = 1;
    private static final int FOUR_MAGNITUDE = 2;
    private static final int MANY_MAGNITUDE = 3;

    private static final String[][] DIGIT_CAPTIONS = new String[][] {
            { null,     null,     "десять",       null,          null        },
            { "один",   "одна",   "одиннадцать",  "десять",      "сто"       },
            { "два",    "две",    "двенадцать",   "двадцать",    "двести"    },
            { "три",    "три",    "тринадцать",   "тридцать",    "триста"    },
            { "четыре", "четыре", "четырнадцать", "сорок",       "четыреста" },
            { "пять",   "пять",   "пятнадцать",   "пятьдесят",   "пятьсот"   },
            { "шесть",  "шесть",  "шестнадцать",  "шестьдесят",  "шестьсот"  },
            { "семь",   "семь",   "семнадцать",   "семьдесят",   "семьсот"   },
            { "восемь", "восемь", "восемнадцать", "восемьдесят", "восемьсот" },
            { "девять", "девять", "девятнадцать", "девяносто",   "девятьсот" }
    };
    private static final int MALE_DIGIT = 0;
    private static final int FEMALE_DIGIT = 1;
    private static final int TEEN_DIGIT = 2;
    private static final int DECADE_DIGIT = 3;
    private static final int HUNDRED_DIGIT = 4;

    public static String numberToCaption(int number) {
        if (number == 0)
            return ZERO_CAPTION;

        List<String> result = new ArrayList<String>();

        if (number < 0) {
            result.add(MINUS_CAPTION);
            number = -number;
        }

        for (int magnitude = MAGNITUDE_CAPTIONS.length - 1; magnitude >= 0; magnitude--) {
            int magnitudeMultiplier = (int) Math.pow(1000, magnitude);
            int part = number / magnitudeMultiplier;
            number %= magnitudeMultiplier;

            if (part > 0) {
                if (part >= 100) {
                    result.add(DIGIT_CAPTIONS[part / 100][HUNDRED_DIGIT]);
                    part %= 100;
                }
                if (part >= 10 && part < 20) {
                    result.add(DIGIT_CAPTIONS[part - 10][TEEN_DIGIT]);
                    part = 0;
                }
                if (part >= 20) {
                    result.add(DIGIT_CAPTIONS[part / 10][DECADE_DIGIT]);
                    part %= 10;
                }
                if (part > 0) {
                    boolean female = MAGNITUDE_CAPTIONS[magnitude][SEX_MAGNITUDE] != null;
                    result.add(DIGIT_CAPTIONS[part][female ? FEMALE_DIGIT : MALE_DIGIT]);
                }

                if (magnitude > 0)
                    switch (part) {
                        case 1:
                            result.add(MAGNITUDE_CAPTIONS[magnitude][ONE_MAGNITUDE]);
                            break;
                        case 2: case 3: case 4:
                            result.add(MAGNITUDE_CAPTIONS[magnitude][FOUR_MAGNITUDE]);
                            break;
                        default:
                            result.add(MAGNITUDE_CAPTIONS[magnitude][MANY_MAGNITUDE]);
                            break;
                    }
            }
        }

        return StringUtils.join(result, " ");
    }
}
