package ne.wsdlparser.lib.xsd.restriction;

public class XSDRestrictionLength extends XSDRestrictionParam {
    private String value;

    public XSDRestrictionLength() {
        super();

    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getHelp() {
        return "Allowed length is: " + this.value;
    }

}