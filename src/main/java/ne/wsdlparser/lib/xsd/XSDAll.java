package ne.wsdlparser.lib.xsd;

import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;

public class XSDAll extends XSDComplexElement {

    public XSDAll(WSDLManagerRetrieval manager, Node node)
            throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, WSDLException {
        super(manager, node, XSDAll.class);
    }

    @Override
    public String getNodeHelp() {
        return String.format(Locale.getDefault(),
                "The following % children can appear in any order and each of them can appear once or not at all",
                this.children.size());
    }

    @Override
    protected Boolean isESQLPrintable() {
        return true;
    }

}