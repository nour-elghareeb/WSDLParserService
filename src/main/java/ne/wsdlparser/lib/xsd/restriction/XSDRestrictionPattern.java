package ne.wsdlparser.lib.xsd.restriction;

public class XSDRestrictionPattern extends XSDRestrictionParam {
    private String value;

    public XSDRestrictionPattern() {
        super();

    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getHelp() {
        return "Value must match this pattern: " + this.value;
    }

}