package ne.wsdlparser.lib.xsd.restriction;

public class XSDRestrictionMinLength extends XSDRestrictionParam {
    private String value;

    public XSDRestrictionMinLength() {
        super();

    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getHelp() {
        return "Minimum lengh allowed is: " + this.value;
    }

}