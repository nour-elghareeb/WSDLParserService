package ne.wsdlparser.lib.esql;

import java.util.Locale;

import ne.wsdlparser.lib.Utils;
import ne.wsdlparser.lib.utility.ConsoleStyle;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

public class ESQLSetterLine extends ESQLLine {
    private XSDSimpleElementType xsdType;
    private String xPath;
    private String defaultValue;

    public ESQLSetterLine(String xPath, XSDSimpleElementType xsdType, String defaultValue) {
        super();
        this.xPath = xPath;
        this.xsdType = xsdType;
        if (defaultValue == null)
            this.defaultValue = "''";
        else
            this.defaultValue = defaultValue;
    }

    @Override
    String generate(boolean useColors) {
        switch (this.source) {
        case OUTPUT:
            return this.generateOutputBlock(useColors);
        case INPUT:
            return this.generateInputSetters(useColors);
        }
        return null;
    }

    private String generateInputSetters(boolean useColors) {
        String pathWithoutPrefix = Utils.replacePrefixesWithAsterisk(this.xPath);
        String varname = Utils.splitPrefixes(this.xPath.substring(xPath.lastIndexOf(".") + 1))[1];
        String placeholder = "DECLARE %s %s %s.XMLNSC.%s; %s";
        
        String esqlType = this.useReference ? "REFERENCE TO"
                            : this.xsdType.equals(XSDSimpleElementType.UNION_CHILDREN) ? "REFERENCE TO"
                                    : this.xsdType.getESQLDataType().getValue();
        if (useColors)
            // TODO: add coloring
            return String.format(Locale.getDefault(), placeholder,
                    ConsoleStyle.addTextColor(varname, ConsoleStyle.Color.YELLOW),
                    ConsoleStyle.addTextColor(esqlType,
                            ConsoleStyle.Color.PURPLE),
                    ConsoleStyle.addTextColor(this.source.get(), ConsoleStyle.Color.GREEN),
                    ConsoleStyle.addTextColor(pathWithoutPrefix, ConsoleStyle.Color.BLUE),
                    ConsoleStyle.addTextColor("-- " + this.xsdType.getDesc(), ConsoleStyle.Color.LIGHT_GRAY));
        else
            return String.format(Locale.getDefault(), placeholder, varname, esqlType, this.source.get(),
                    pathWithoutPrefix, "-- " + this.xsdType.getDesc());
    }

    // private String generateInputUsingRef(boolean useColors) {

    // String pathWithoutPrefix = Utils.replacePrefixesWithAsterisk(this.xPath);
    // String varname =
    // Utils.splitPrefixes(this.xPath.substring(xPath.lastIndexOf(".") + 1))[1];
    // String placeholder = "SET %s %s %s.XMLNSC.%s; %s";
    // if (useColors)
    // // TODO: add coloring
    // return String.format(Locale.getDefault(), placeholder,
    // ConsoleStyle.addTextColor(varname, ConsoleStyle.Color.YELLOW),
    // ConsoleStyle.addTextColor("REFERENCE TO", ConsoleStyle.Color.PURPLE),
    // ConsoleStyle.addTextColor(this.source.get(), ConsoleStyle.Color.GREEN),
    // ConsoleStyle.addTextColor(pathWithoutPrefix, ConsoleStyle.Color.BLUE),
    // ConsoleStyle.addTextColor("-- " + this.xsdType.getDesc(),
    // ConsoleStyle.Color.LIGHT_GRAY));
    // else
    // return String.format(Locale.getDefault(), placeholder, varname, "REFERENCE
    // TO", this.source.get(),
    // pathWithoutPrefix, "-- " + this.xsdType.getDesc());
    // }

    private String generateOutputBlock(boolean useColors) {
        String placeholder = "SET %s.XMLNSC.%s = %s; %s";
        if (useColors) {
            // TODO: add color
            return String.format(Locale.getDefault(), placeholder,
                    ConsoleStyle.addTextColor(this.source.get(), ConsoleStyle.Color.GREEN),
                    ConsoleStyle.addTextColor(this.xPath, ConsoleStyle.Color.BLUE),
                    ConsoleStyle.addTextColor(this.defaultValue, ConsoleStyle.Color.YELLOW),
                    ConsoleStyle.addTextColor("-- " + this.xsdType.getDesc(), ConsoleStyle.Color.LIGHT_GRAY));
        } else
            return String.format(Locale.getDefault(), placeholder, this.source.get(), this.xPath, this.defaultValue,
                    " -- " + this.xsdType.getDesc());
    }

    @Override
    public void print() {
        System.out.println(this.generate(true));
    };

}