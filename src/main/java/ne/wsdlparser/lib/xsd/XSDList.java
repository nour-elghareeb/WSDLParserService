package ne.wsdlparser.lib.xsd;

import com.sun.istack.logging.Logger;
import java.util.logging.Level;
import org.w3c.dom.Node;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.WSDLManagerRetrieval;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.xsd.constant.XSDSimpleElementType;

/**
 * XSD List implementation
 *
 * @author nour
 */
public class XSDList extends XSDSimpleElement {

    private XSDSimpleElementType itemType;

    public XSDList(WSDLManagerRetrieval manager, Node node) throws WSDLException {
        super(manager, node, XSDSimpleElementType.LIST);
        load(   );
    }

    @Override
    public String getNodeHelp() {
        return "List value is expected to be values seperated by a space, i.e. item1 item2 item3";
    }

    private void load() throws WSDLException {
        this.itemType = XSDSimpleElementType
                .parse(Utils.splitPrefixes(Utils.getAttrValueFromNode(this.node, "itemType"))[1]);

    }

    @Override
    public XSDSimpleElementType getSimpleType() {
        return this.itemType;
    }

}
