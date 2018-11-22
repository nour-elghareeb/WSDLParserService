package ne.wsdlparser.lib.esql;

import java.util.Locale;

import ne.wsdlparser.lib.esql.constant.ESQLDataType;
import ne.wsdlparser.lib.utility.ConsoleStyle;
/**
 * ESQL declaration line implementation
 * @author nour
 */
public class ESQLDeclareLine extends ESQLLine {

    public String getParam() {
        return param;
    }

    public ESQLDataType getType() {
        return type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    private String param;
    private ESQLDataType type;
    private String defaultValue = "";

    ESQLDeclareLine(String paramName, ESQLDataType type, String defaultValue) {
        super();
        this.param = paramName;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    ESQLDeclareLine(String paramName, ESQLDataType type) {
        super();
        this.param = paramName;
        this.type = type;
    }

    @Override
    String generate(boolean useColors) {
        return String.format(Locale.getDefault(), "DECLARE %s %s '%s';", this.param, this.type.getValue(),
                this.defaultValue);
    }
    /**
     * print line with colors
     */
    @Override
    public void print() {
        String line = String.format(Locale.getDefault(), "%s %s %s '%s';",
                ConsoleStyle.addTextColor("DECLARE", ConsoleStyle.Color.YELLOW),
                ConsoleStyle.style(this.param, ConsoleStyle.Style.BOLD),
                ConsoleStyle.addTextColor(this.type.getValue(), ConsoleStyle.Color.BLUE), this.defaultValue);
        System.out.println(line);
    }
}