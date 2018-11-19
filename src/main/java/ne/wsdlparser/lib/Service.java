package ne.wsdlparser.lib;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Service {
    private String name;
    private WSDLManagerRetrieval manager;
    private ArrayList<Port> ports;
    private Node node;

    public Service(WSDLManagerRetrieval manager, Node node) throws XPathExpressionException {
        this.name = Utils.getAttrValueFromNode(node, "name");
        this.manager = manager;
        this.node = node;
//        this.loadPorts();
    }
    
    public ArrayList<Port> loadPorts() throws WSDLException  {
        try {
            final NodeList ports = (NodeList) this.manager.getXPath().compile("port").evaluate(this.node,
                    XPathConstants.NODESET);
            this.ports = new ArrayList<Port>() {
                {
                    for (int i = 0; i < ports.getLength(); i++) {
                        Node portNode = ports.item(i);
                        Port port = new Port(Service.this.manager, portNode);
                        add(port);
                    }
                }
            };
            return this.ports;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }
    };
    public Port loadPort(String portName) throws WSDLException {
        try {
            final Node portNode = (Node) this.manager.getXPath().compile(String.format("port[@name='%s']", portName)).evaluate(this.node,
                    XPathConstants.NODE);
            if (portNode == null){
                throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION, "No port found with this name!");
            }
            return new Port(Service.this.manager, portNode);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }
        
    }

    public String getName() {
        return this.name;
    }
}