package ne.wsdlparser.lib.xsd;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

public class XSDAny extends XSDSimpleElement {

    public XSDAny(WSDLManagerRetrieval manager, Node node){

        super(manager, node, XSDSimpleElementType.ANY);
    }

    @Override
    public String getNodeHelp() {
        return "You can add any custom element at this level";
    }

    @Override
    protected Boolean isESQLPrintable() {
        return false;
    }
    @Override
    public void toESQL() throws WSDLException {
        addHelpComments();
        this.manager.getESQLManager().addParam(this.prefix, "", XSDSimpleElementType.ANY, null);
    }
}