package ne.wsdlparser.lib.xsd;


import org.w3c.dom.Node;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.esql.constant.ESQLDataType;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;
/**
 * An implementation for any XSD element that cannot have children
 * @author nour
 */
public class XSDSimpleElement extends XSDElement {
    private final XSDSimpleElementType simpleType;

    public XSDSimpleElement(WSDLManagerRetrieval manager, Node node, XSDSimpleElementType type) {
        super(manager, node);
        this.simpleType = type;
        this.setDefaultValue(Utils.getAttrValueFromNode(this.node, "default"));
        this.setFixedValue(Utils.getAttrValueFromNode(this.node, "fixed"));
    }


    @Override
    public final void setDefaultValue(String value) {
        this.defaultValue = this.prepareElementValue(value);
    }
    
    @Override
    public void toESQL() throws WSDLException{
        super.toESQL();
        
        if (this.maxOccurs != 0) {
            String val = this.fixedValue == null ? this.defaultValue : this.fixedValue;
            this.manager.getESQLManager().addParam(getPrintablePrefix(), this.name, this.simpleType, val);
        }
    }

    @Override
    protected Boolean isESQLPrintable() {
        return true;
    }

    public XSDSimpleElementType getSimpleType() {
        return this.simpleType;
    }

    @Override
    protected boolean hasPrintable() {
        return true;
    }

    @Override
    protected final void setFixedValue(String fixedValue) {
        if (fixedValue != null)
            this.fixedValue = this.prepareElementValue(String.valueOf(fixedValue));
    }

    public String prepareElementValue(String value) {
        if (this.simpleType == null || value == null) return null;
        ESQLDataType type = this.simpleType.getESQLDataType();
        if (type == null) return null;
        switch (type) {
        case BOOLEAN:
            return value.toUpperCase();
        case CHARACTER:
            return String.format("'%s'", value);
        case BIT:
            return String.format("B'%s'", value);
        case BLOB:
            return String.format("X'%s'", value);
        case DATE:
        case TIME:
        case GMTTIME:
        case TIMESTAMP:
        case GMTTIMESTAMP:
            return String.format("%s '%s'", this.simpleType.getESQLDataType().getValue(), value);
        case INTERVAL:
            return String.format("%s '%s'", this.simpleType.getESQLDataType().getValue(), value);
        case DECIMAL:
        case FLOAT:
        case INTEGER:
            return value;
        case NULL:
            return "NULL";
        default:
            return null;
        }
    }
}