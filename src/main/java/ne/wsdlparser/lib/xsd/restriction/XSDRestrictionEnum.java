package ne.wsdlparser.lib.xsd.restriction;

import java.util.ArrayList;

public class XSDRestrictionEnum extends XSDRestrictionParam {
    private ArrayList<String> values;

    public XSDRestrictionEnum() {
        super();
        this.values = new ArrayList<String>();
    }

    public void addValue(String value) {
        this.values.add(value);
    }

    @Override
    public String getHelp() {
        return "Value must be one of the following: " + String.join(", ", this.values);
    }

}