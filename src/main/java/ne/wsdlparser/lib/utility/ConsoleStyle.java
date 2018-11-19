package ne.wsdlparser.lib.utility;

import java.util.Locale;

/**
 * Created by nour on 5/10/17.
 */

public class ConsoleStyle {
    public final static String RESET_VALUE = "\033[0m";

    public enum Color {
        GREEN("\033[42;1m", "\033[32;1m"), YELLOW("\033[43;1m", "\033[33;1m"), RED("\033[41;1m", "\033[31;1m"),
        BLUE("\033[44;1m", "\033[34;1m"), WHITE("\033[47;1m", "\033[38;1m"), PURPLE("\033[47;1m", "\033[35;1m"),
        LIGHT_GRAY("\033[47;1m", "\033[37;1m"), DARK_GRAY("\033[100;1m", "\037[90;1m"),
        DEFAULT("\033[42;1m", "\033[39;1m");
        private String bgColor;
        private String textColor;

        Color(String bgColor, String textColor) {
            this.bgColor = bgColor;
            this.textColor = textColor;
        }

        public String getBGColor() {
            return this.bgColor;
        }

        public String getTextColor() {
            return this.textColor;
        }
    }

    public enum Style {
        DIM("\033[2m", "\033[22m"), BOLD("\033[1m", "\033[21m"), UNDERLINED("\033[4m", "\033[24m"),
        BLINK("\033[5m", "\033[25m"), INVERTED("\033[7m", "\033[27m");

        private String styleValue;
        private String unStyleValue;

        Style(String styleValue, String unStyleValue) {
            this.styleValue = styleValue;
            this.unStyleValue = unStyleValue;
        }

        public String getStyleValue() {
            return this.styleValue;
        }

        public String getUnStyleValue() {
            return this.unStyleValue;
        }
    }

    public static String style(String value, Style... styles) {
        String styled = "%s";
        for (Style style : styles) {
            styled = style.getStyleValue() + styled + style.getUnStyleValue();
        }
        return String.format(Locale.getDefault(), styled, value);
    }

    public static String addTextColor(String value, Color color) {
        return color.getTextColor() + value + Color.DEFAULT.getTextColor();
    }

    public static String addBGColor(String value, Color color) {
        return color.getBGColor() + value + Color.DEFAULT.getBGColor();
    }
    // Setting title style..
    // public static void setTitle(String title, Color bgColor, Color textColor) {
    // setBackground(bgColor);
    // setColor(textColor);
    // System.out.println();
    // String sign = "";
    // for (int i = 0; i < 205; i++) {
    // sign += "=";
    // }
    // System.out.println(sign);
    // System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t");
    // reset();
    // setBold();
    // setColor(textColor);
    // System.out.print(title);
    // reset();
    // setBackground(bgColor);
    // setColor(textColor);
    // System.out.println();
    // System.out.print(sign);
    // reset();
    // System.out.println();
    // System.out.println();
    // }

}