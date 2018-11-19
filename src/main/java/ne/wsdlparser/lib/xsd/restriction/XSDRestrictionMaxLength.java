package ne.wsdlparser.lib.xsd.restriction;

public class XSDRestrictionMaxLength extends XSDRestrictionParam {
    private String value;

    public XSDRestrictionMaxLength() {
        super();

    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getHelp() {
        return "Maximum lengh allowed is: " + this.value;
    }

}