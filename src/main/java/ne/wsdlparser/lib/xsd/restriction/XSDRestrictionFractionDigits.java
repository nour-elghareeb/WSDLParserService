package ne.wsdlparser.lib.xsd.restriction;

public class XSDRestrictionFractionDigits extends XSDRestrictionParam {
    private String value;

    public XSDRestrictionFractionDigits() {
        super();

    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getHelp() {
        return "Maximum number of decimal places allowed is " + this.value;
    }

}