package ne.wsdlparser.lib;

import java.util.ArrayList;
import java.util.Locale;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Port {
    private String name;
    private WSDLManagerRetrieval manager;
    private PortType type;
    private Binding binding;
    private Node node;

    public Port(WSDLManagerRetrieval manager, Node node) throws XPathExpressionException {
        this.node = node;
        this.name = Utils.getAttrValueFromNode(node, "name");
        this.manager = manager;
        this.setBinding(Utils.getAttrValueFromNode(node, "binding"));
    }

    private void setBinding(String bindingName) throws XPathExpressionException {
        this.binding = new Binding(this.manager, bindingName, this);
    }

    public String getName() {
        return this.name;
    }

    public void setType(String typeName) throws XPathExpressionException {
        this.type = new PortType(this.manager, this, typeName);
    }

    public PortType getType() {
        return this.type;
    }

    public Binding getBinding() {
        return this.binding;
    }
}