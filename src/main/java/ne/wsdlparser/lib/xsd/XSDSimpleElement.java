package ne.wsdlparser.lib.xsd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ne.wsdlparser.lib.Utils;
import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.esql.ESQLLine;
import ne.wsdlparser.lib.esql.constant.ESQLDataType;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

public class XSDSimpleElement<T> extends XSDElement<T> {
    private XSDSimpleElementType simpleType;

    public XSDSimpleElement(WSDLManagerRetrieval manager, Node node, XSDSimpleElementType type) {
        super(manager, node, String.class);
        this.simpleType = type;
        this.setDefaultValue(Utils.getAttrValueFromNode(this.node, "default"));
        this.setFixedValue((T) Utils.getAttrValueFromNode(this.node, "fixed"));
    }


    @Override
    public void setDefaultValue(String value) {
        this.defaultValue = this.prepareElementValue(value);
    }

    @Override
    public void toESQL() throws WSDLException{
        super.toESQL();
        String prefix = this.prefix;
        if (this.prefix == null) {
            String ns = this.getExplicitlySetTargetTamespace();
            if (ns == null) {
                ns = this.getTargetTamespace();
            }
            if (!this.manager.getTargetNameSpace().equals(ns)) {
                prefix = this.manager.getPrefix(ns);
            }
        }
        if (this.maxOccurs != 0) {
            String val = this.fixedValue == null ? this.defaultValue : this.fixedValue;
            this.manager.getESQLManager().addParam(prefix, this.name, this.simpleType, val);
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
    protected void setFixedValue(T fixedValue) {
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