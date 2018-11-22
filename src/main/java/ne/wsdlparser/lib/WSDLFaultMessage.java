package ne.wsdlparser.lib;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.XSDElement;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

/**
 * Soap Fault message.
 *
 * @author nour
 */
public class WSDLFaultMessage extends WSDLMessage {

    /**
     *
     * @param manager WSDLManager instance injection
     * @param operation operation associated with the message
     * @param node XML node associated with the message.
     * @throws WSDLException
     */
    public WSDLFaultMessage(WSDLManagerRetrieval manager, WSDLOperation operation, Node node) throws WSDLException {
        super(manager, operation, node);
    }
    /**
     * Overrides super to implement special fault messages
     * @throws WSDLException     
     */
    @Override
    public void generateESQL() throws WSDLException {
        this.manager.getESQLManager().clearTree();
        this.manager.getESQLManager().levelUp("soapenv", "Fault", true);
        this.manager.getESQLManager().addParam(null, "faultcode", XSDSimpleElementType.INT, null);
        this.manager.getESQLManager().addParam(null, "faultstring   ", XSDSimpleElementType.STRING, null);
        this.manager.getESQLManager().addParam(null, "faultactor   ", XSDSimpleElementType.STRING, null);
        this.manager.getESQLManager().levelUp(null, "detail", !this.parts.isEmpty());
        this.manager.getESQLManager().levelUp(this.prefix, this.name, !this.parts.isEmpty());
        for (XSDElement element : this.parts) {
            element.toESQL();
        }
        this.manager.getESQLManager().levelDown(this.prefix, this.name, !this.parts.isEmpty());
        this.manager.getESQLManager().levelDown(null, "detail", !this.parts.isEmpty());
        this.manager.getESQLManager().levelDown("soapenv", "Fault", true);
    }
}
