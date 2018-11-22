package ne.wsdlparser.lib.xsd;

import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;

public class XSDChoice extends XSDComplexElement {

    public XSDChoice(WSDLManagerRetrieval manager, Node node)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node);
    }

    @Override
    public String getNodeHelp() {
        return (this.children.size() <= 1) ? null :
         String.format(Locale.getDefault(), "You have a choice of the following %s parameters:",
                this.children.size());
    }

    @Override
    protected Boolean isESQLPrintable() {
        return true;
    }

    @Override
    protected void loadChildren()
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        Node child = Utils.getFirstXMLChild(this.node);
        int i = 1;
        while (child != null) {
            child.setUserData("tns", this.node.getUserData("tns"), null);
            XSDElement element = XSDElement.getInstance(this.manager, child);
            element.setNodeHelp(String.format(Locale.getDefault(), "Choice (%s) --------------", i));
            i++;
            this.children.add(element);
            child = Utils.getNextXMLSibling(child);
        }
    }
}