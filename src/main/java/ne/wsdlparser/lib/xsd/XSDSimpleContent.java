package ne.wsdlparser.lib.xsd;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

public class XSDSimpleContent extends XSDComplexElement {
    private XSDSimpleElementType simpleType;

    public XSDSimpleContent(WSDLManagerRetrieval manager, Node node)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node);
    }

    @Override
    protected Boolean isESQLPrintable() {
        return true;
    }
  
    @Override
    protected boolean validateChild(Node child, XSDElement element)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {

        // child is extention..
        if (element instanceof XSDExtention) {
            
            this.simpleType = ((XSDExtention) element).getSimpleType();
            return false;
        }
        // child is restriction
        else if (element instanceof XSDRestriction) {
            this.simpleType = ((XSDRestriction) element).getSimpleType();
            this.restriction = (XSDRestriction) element;
            this.hasRestriction = true;
            return true;
        }
        return true;
    }

    @Override
    public void toESQL() {
    }


    public XSDSimpleElementType getSimpleType() {
        return this.simpleType;
    }

    public boolean hasRestriction() {
        return this.hasRestriction;
    }

}