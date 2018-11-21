package ne.wsdlparser.lib;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.constant.WSDLProperty;
import com.sun.istack.NotNull;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

import org.w3c.dom.Node;
/**
 * A WSDL WSDLBinding implementation
 * @author nour
 */
public class WSDLBinding {

    private final String name;
    private final WSDLPort port;
    private final WSDLManagerRetrieval manager;
    private Node node;
    private WSDLProperty globalStyle;
    /**
     * Constructor for WSDL Binding instance
     * @param manager an WSDLManager instance injection.
     * @param name a binding name
     * @param port
     * @throws WSDLException 
     */
    public WSDLBinding(@NotNull WSDLManagerRetrieval manager,@NotNull String name, @NotNull WSDLPort port) throws WSDLException {
        this.name = Utils.splitPrefixes(name)[1];
        this.manager = manager;
        this.port = port;
        loadBinding();
    }

    /**
     * Port getter
     * @return Port associated with this binding
     */
    public WSDLPort getPort() {
        return this.port;
    }

    /**
     * Load binding information including Port Type associated associated with this binding.
     * Load global operation style if found.
     * @throws WSDLException if any error occur
     */
    private void loadBinding() throws WSDLException {
        try {
            // loading node of binding
            this.node = (Node) this.manager.getXPath()
                    .compile(String.format(Locale.getDefault(), "/definitions/binding[@name='%s']", this.name))
                    .evaluate(this.manager.getWSDLFile(), XPathConstants.NODE);
            // setting port type...

            this.port.setType(Utils.getAttrValueFromNode(node, "type"));

            Node soapBinding = (Node) this.manager.getXPath().compile("binding").evaluate(this.node,
                    XPathConstants.NODE);
            if (soapBinding != null) {
                this.setGlobalStyle(Utils.getAttrValueFromNode(soapBinding, "style"));
            }
            return;

        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLBinding.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID, "Could not load binding!");
    }
    /**
     * Binding name getter
     * @return name of this Binding
     */
    public String getName() {
        return this.name;
    }
    /**
     * Binding XML node getter.
     * @return XML node associated with this Binding
     */
    public Node getNode() {
        return this.node;
    }

    /**
     * @return WSDLManagerRetrieval return the manager
     */
    public WSDLManagerRetrieval getManager() {
        return manager;
    }
    
    /**
     * @return String return the globalStyle
     */
    public WSDLProperty getGlobalStyle() {
        return this.globalStyle;
    }

    /**
     * sets global style for operations under this binding.
     * @param value accepted values are document|rpc
     */
    public void setGlobalStyle(String value) {
        if (value == null) {
            return;
        }
        if (value.toLowerCase().trim().equals("document")) {
            this.globalStyle = WSDLProperty.DOCUMENT;
        } else if (value.toLowerCase().trim().equals("rpc")) {
            this.globalStyle = WSDLProperty.RPC;
        }
    }

}
