package ne.wsdlparser.lib;

import java.util.Locale;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Binding {
    String name;
    Port port;
    private WSDLManagerRetrieval manager;
    private Node node;
    private WSDLProperty globalStyle;

    public Binding(WSDLManagerRetrieval manager, String name, Port port) throws XPathExpressionException {
        this.setName(name);
        this.manager = manager;
        this.port = port;
        loadBinding();

    }

    public void setPort(Port Port) {
        this.port = Port;
    }

    public Port getPort() {
        return this.port;
    }

    public void setName(String name) {
        if (name.contains(":")) {
            name = Utils.splitPrefixes(name)[1];
        }
        this.name = name;
    }

    private void loadBinding() throws XPathExpressionException {
        // loading node of binding
        this.node = (Node) this.manager.getXPath()
                .compile(String.format(Locale.getDefault(), "/definitions/binding[@name='%s']", this.name))
                .evaluate(this.manager.getWSDLFile(), XPathConstants.NODE);
        // setting port type...

        this.port.setType(Utils.getAttrValueFromNode(node, "type"));
        try {
            Node soapBinding = (Node) this.manager.getXPath().compile("binding").evaluate(this.node,
                    XPathConstants.NODE);
            if (soapBinding != null) {
                this.setGlobalStyle(Utils.getAttrValueFromNode(soapBinding, "style"));
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

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
     * @param manager the manager to set
     */
    public void setManager(WSDLManagerRetrieval manager) {
        this.manager = manager;
    }

    /**
     * @param node the node to set
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * @return String return the globalStyle
     */
    public WSDLProperty getGlobalStyle() {
        return this.globalStyle;
    }

    /**
     * @param globalStyle the globalStyle to set
     */
    public void setGlobalStyle(String value) {
        if (value == null) return;
        if (value.toLowerCase().trim().equals("document"))
            this.globalStyle = WSDLProperty.DOCUMENT;
        else if (value.toLowerCase().trim().equals("rpc"))
            this.globalStyle = WSDLProperty.RPC;
    }

}