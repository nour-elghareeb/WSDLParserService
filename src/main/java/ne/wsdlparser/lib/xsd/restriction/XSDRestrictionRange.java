package ne.wsdlparser.lib.xsd.restriction;

import java.util.Locale;

public class XSDRestrictionRange extends XSDRestrictionParam {
    private String minValue;
    private String maxValue;

    public XSDRestrictionRange() {
        super();

    }

    public void setMaxValue(String value) {
        this.maxValue = value;
    }

    public void setMinValue(String value) {
        this.minValue = value;
    }

    @Override
    public String getHelp() {
        if (this.maxValue != null && this.minValue != null)
            return String.format(Locale.getDefault(), "Value must be greater than %s and lower than %s", this.minValue,
                    this.maxValue);
        else if (this.maxValue != null)
            return String.format(Locale.getDefault(), "Value must be lower than %s", this.maxValue);
        else if (this.minValue != null)
            return String.format(Locale.getDefault(), "Value must be greater than %s", this.minValue);
        else
            return "";
    }

}