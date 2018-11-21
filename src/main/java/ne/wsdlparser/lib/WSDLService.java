package ne.wsdlparser.lib;

import ne.wsdlparser.lib.utility.Utils;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * A WSDL Service
 * @author nour
 */
public class WSDLService {
    private final String name;
    private final WSDLManagerRetrieval manager;
    private ArrayList<WSDLPort> ports;
    private final Node node;
    /**
     * Create a new WSDL Service instance
     * @param manager A WSDLManager instance
     * @param node WSDL Service XML node     
     * @throws WSDLException if could not retrieve service name
     */
    public WSDLService(WSDLManagerRetrieval manager, Node node) throws WSDLException {
        this.name = Utils.getAttrValueFromNode(node, "name");
        if (this.name == null) throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID, "Could not load one of the services");
        this.manager = manager;
        this.node = node;
    }
    /**
     * Load all ports under this service
     * @return an ArrayList of port instances or throws an exception
     * @throws WSDLException 
     */
    @NotNull
    public ArrayList<WSDLPort> loadPorts() throws WSDLException  {
        try {
            final NodeList portNodes = (NodeList) this.manager.getXPath().compile("port").evaluate(this.node,
                    XPathConstants.NODESET);
            this.ports = new ArrayList<WSDLPort>() {
                {
                    for (int i = 0; i < portNodes.getLength(); i++) {
                        Node portNode = portNodes.item(i);
                        WSDLPort port = new WSDLPort(WSDLService.this.manager, portNode);
                        add(port);
                    }
                }
            };
            return this.ports;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLService.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }
    };
    /**
     * Load specific port by name
     * @param portName
     * @return loaded port instance or throws an exception
     * @throws WSDLException 
     */
    public WSDLPort loadPort(String portName) throws WSDLException {
        try {
            final Node portNode = (Node) this.manager.getXPath().compile(String.format("port[@name='%s']", portName)).evaluate(this.node,
                    XPathConstants.NODE);
            if (portNode == null){
                throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION, "No port found with this name!");
            }
            return new WSDLPort(WSDLService.this.manager, portNode);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLService.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }
        
    }
    /**
     * Get service name
     * @return 
     */
    @NotNull
    public String getName() {
        return this.name;
    }
}