package ne.wsdlparser.lib;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.XSDElement;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

public class FaultMessage extends WSDLMessage {
    public FaultMessage(WSDLManagerRetrieval manager, WSDLOperation operation, Node node)
            throws XPathExpressionException, WSDLException, SAXException, IOException, ParserConfigurationException {
        super(manager, operation, node);
    }

    @Override
    public void generateESQL() {
        this.manager.getESQLManager().clearTree();
        this.manager.getESQLManager().levelUp("soapenv", "Fault", true);
        this.manager.getESQLManager().addParam(null, "faultcode", XSDSimpleElementType.STRING, null);
        this.manager.getESQLManager().addParam(null, "faultstring   ", XSDSimpleElementType.STRING, null);
        this.manager.getESQLManager().addParam(null, "faultactor   ", XSDSimpleElementType.STRING, null);
        this.manager.getESQLManager().levelUp(null, "detail", this.parts.size() != 0);
        this.manager.getESQLManager().levelUp(this.prefix, this.name, this.parts.size() != 0);
        for (XSDElement element : this.parts) {
            element.toESQL();
        }
        this.manager.getESQLManager().levelDown(this.name, this.prefix, this.parts.size() != 0);
        this.manager.getESQLManager().levelDown(null, "detail", this.parts.size() != 0);
        this.manager.getESQLManager().levelDown("soapenv", "Fault", true);
    }
}