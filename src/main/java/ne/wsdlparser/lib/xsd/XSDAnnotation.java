package ne.wsdlparser.lib.xsd;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

public class XSDAnnotation extends XSDComplexElement {

    public XSDAnnotation(WSDLManagerRetrieval manager, Node node)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node);
    }

    @Override
    public String getNodeHelp() {
        return this.nodeHelp;
    }

    @Override
    protected boolean validateChild(Node child, XSDElement element)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        // return super.validateChild(child, element);
        XSDSimpleElement simpleElement = (XSDSimpleElement) element;
        XSDSimpleElementType elementType = simpleElement.getSimpleType();
        switch (elementType) {
        case DOCUMENTATION:
            this.nodeHelp = child.getTextContent();
            return true;
        }
        return false;
    }

    @Override
    protected Boolean isESQLPrintable() {
        return false;
    }
}