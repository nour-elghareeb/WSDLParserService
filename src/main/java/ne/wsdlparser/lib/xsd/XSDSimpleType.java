package ne.wsdlparser.lib.xsd;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;
/**
 * Simple type XSD implementation.
 * @author nour
 */
public class XSDSimpleType extends XSDComplexElement {
    private XSDSimpleElementType simpleType;
    private boolean hasList;
    private boolean hasUnion;
    private XSDUnion union;

    public XSDSimpleType(WSDLManagerRetrieval manager, Node node)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node);
    }

    @Override
    protected Boolean isESQLPrintable() {
        return true;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    protected boolean validateChild(Node child, XSDElement element)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        // return super.validateChild(child, element);
        // child is restriction
        if (element instanceof XSDRestriction) {
            this.simpleType = ((XSDRestriction) element).getSimpleType();
            this.restriction = (XSDRestriction) element;
            this.hasRestriction = true;
        } else if (element instanceof XSDList) {
            this.hasList = true;
            this.simpleType = ((XSDList) element).getSimpleType();
        } else if (element instanceof XSDUnion) {
            this.hasUnion = true;
            // element.setName(Utils.getParamWithPrefix(this.prefix, this.name));
            this.simpleType = XSDSimpleElementType.UNION_CHILDREN;
            this.union = (XSDUnion) element;
        }
        return true;

    }

    protected boolean handleList() {
        // if (this.hasList)
        // TODO: fix list
        return this.hasRestriction;
    }

    @Override
    public void toESQL() throws WSDLException{
        this.handleList();
        super.toESQL();
        // this.addHelpComments();
        String val = this.fixedValue == null ? this.defaultValue : this.fixedValue;
        this.manager.getESQLManager().addParam(getPrintablePrefix(), this.name, this.simpleType, val);
    }

    @Override
    protected boolean hasPrintable() {
        return false;
    }

    public XSDSimpleElementType getSimpleType() {
        return this.simpleType;
    }

    public boolean hasRestriction() {
        return this.hasRestriction;
    }

    public boolean hasList() {
        return this.hasList;
    }
}