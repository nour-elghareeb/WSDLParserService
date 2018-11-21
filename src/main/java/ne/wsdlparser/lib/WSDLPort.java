package ne.wsdlparser.lib;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

import org.w3c.dom.Node;
/**
 * WSDL WSDLPort implementation
 * @author nour
 */
public class WSDLPort {

    private final String name;
    private WSDLManagerRetrieval manager;
    private WSDLPortType type;
    private WSDLBinding binding;
    private final Node node;
    /**
     * Constructor for a new WSDLPort
     * @param manager WSDLManager instance injection
     * @param node Port XML node
     * @throws WSDLException if loading port or binding or portType error occur
     */
    public WSDLPort(WSDLManagerRetrieval manager, Node node) throws WSDLException {
        this.node = node;
        this.name = Utils.getAttrValueFromNode(node, "name");
        if (this.name == null) {
            throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID, "Could not load one of the ports");
        }
        this.manager = manager;
        // load binding and adds a reference here in the port instance
        this.setBinding(Utils.getAttrValueFromNode(node, "binding"));
    }
    /**
     * Load binding instance based on this port by binding name
     * @param bindingName binding name from this port xml node
     * @throws WSDLException if binding loading failed
     */
    private void setBinding(String bindingName) throws WSDLException {
        this.binding = new WSDLBinding(this.manager, bindingName, this);
    }
    /**
     * Getter for port name
     * @return Port Name
     */
    public String getName() {
        return this.name;
    }
    /**
     * Loads a port type instance and add a reference to it in this port. 
     * This method is called from the WSDLBinding instance
     * @param typeName port type name from WSDLBinding xml node.
     */
    public void setType(String typeName) {
        this.type = new WSDLPortType(this.manager, this, typeName);
    }
    /**
     * Getter for port type instance
     * @return WSDLPortType instance for this Port
     */
    public WSDLPortType getType() {
        return this.type;
    }
    /**
     * Getter for WSDLBinding instance
     * @return WSDLBinding instance for this port
     */
    public WSDLBinding getBinding() {
        return this.binding;
    }
}
