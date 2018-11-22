package ne.wsdlparser.lib.esql;

import ne.wsdlparser.lib.constant.ESQLVerbosity;
import ne.wsdlparser.lib.utility.ConsoleStyle;

public class ESQLCommentLine extends ESQLLine {
    private String value;

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }
    private String title;
    private ESQLVerbosity verbosityType;

    ESQLCommentLine(ESQLVerbosity type, String title, String value) {
        super();
        this.value = value;
        this.title = title;
        this.verbosityType = type;
    }

    ESQLCommentLine(ESQLVerbosity type, String value) {
        super();
        this.value = value;
        this.verbosityType = type;
    }

    String generate(boolean useColors) {
        if (this.value == null)
            return "";
        return "-- ".concat(this.value).concat(";");
    }
    /**
     * print line. if value is null, it prints empty line.
     */
    @Override
    public void print() {
        if (this.value == null) {
            System.out.println();
            return;
        }
        if (this.title == null) {
            System.out.println(ConsoleStyle.addTextColor("-- " + this.value, ConsoleStyle.Color.LIGHT_GRAY));
            return;
        }
        System.out.println(ConsoleStyle.addTextColor("-- ", ConsoleStyle.Color.LIGHT_GRAY) + this.title
                + ConsoleStyle.addTextColor(this.value, ConsoleStyle.Color.LIGHT_GRAY));

    }

    public ESQLVerbosity getVerbosity() {
        return this.verbosityType;
    }
}