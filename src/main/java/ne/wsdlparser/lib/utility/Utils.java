package ne.wsdlparser.lib.utility;

import com.sun.istack.Nullable;
import java.net.URL;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Few simple utils to read DOM. This is originally from the Jakarta Commons
 * Modeler.
 *
 * @author Costin Manolache
 */
public class Utils {

    private static final String XMLNAMESPACE = "xmlns";

    /**
     * Remove every prefix and replace it with a
     *
     *
     * @param value.
     * @return value after replacement
     */
    public static String replacePrefixesWithAsterisk(String value) {
        int startPoint = 0;

        while (startPoint != -1) {

            int end = value.indexOf(":", startPoint);
            if (end == -1) {
                return value;
            }
            int start = value.lastIndexOf(".", end);
            String sub = value.substring(start + 1, end);
            if (!sub.equals("*")) {
                value = value.replace(sub, "*");
            }
            startPoint = end + 1;

        }
        return value;
    }
    /**
     * Get next valid XML child ignoring #text and #comment
     * @param node valid XML node
     * @return 
     */
    public static Node getFirstXMLChild(Node node) {
        if (node == null) {
            return null;
        }
        node = node.getFirstChild();
        while (node != null && (node.getNodeName().equals("#text") || node.getNodeName().equals("#comment"))) {
            node = node.getNextSibling();
        }
        return node;
    }
    /**
     * Get next valid XML sibling ignoring #test and #comment
     * @param node valid XML sibling
     * @return 
     */
    public static Node getNextXMLSibling(Node node) {
        node = node.getNextSibling();
        while (node != null && (node.getNodeName().equals("#text") || node.getNodeName().equals("#comment"))) {
            node = node.getNextSibling();
        }
        return node;
    }
    /**
     * Validates if value is valid URI
     * @param uri
     * @return boolean
     */
    public static boolean validateURI(String uri) {
        final URL url;
        try {
            url = new URL(uri);
        } catch (Exception e1) {
            return false;
        }
        return "http".equals(url.getProtocol()) || "https".equals(url.getProtocol());
    }

    /**
     * Get all prefixes defined, up to the root, for a namespace URI.
     *
     * @param element
     * @param namespaceUri
     * @param prefixes
     */
    public static void getPrefixesRecursive(Element element, String namespaceUri, List<String> prefixes) {
        getPrefixes(element, namespaceUri, prefixes);
        Node parent = element.getParentNode();
        if (parent instanceof Element) {
            getPrefixesRecursive((Element) parent, namespaceUri, prefixes);
        }
    }

    /**
     * Get all prefixes defined on this element for the specified namespace.
     *
     * @param element
     * @param namespaceUri
     * @param prefixes
     */
    public static void getPrefixes(Element element, String namespaceUri, List<String> prefixes) {
        NamedNodeMap atts = element.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) {
            Node node = atts.item(i);
            String name = node.getNodeName();
            if (namespaceUri.equals(node.getNodeValue())
                    && (name != null && (XMLNAMESPACE.equals(name) || name.startsWith(XMLNAMESPACE + ":")))) {
                prefixes.add(node.getPrefix());
            }
        }
    }
    /**
     * Split a string with colon.
     * may return array[2] with null as values
     * @param value with :
     * @return an array with the value as first child, and prefix as the second.
     */
    public static String[] splitPrefixes(String value) {

        String[] splitArray = new String[2];
        if (value == null) {
            return splitArray;
        } else if (!value.contains(":")) {
            splitArray[0] = null;
            splitArray[1] = value;
            return splitArray;
        }
        return value.split(":");
    }
    /**
     * Joins param with prefix if exists. NullPointException safe.
     * @param prefix
     * @param name
     * @return prefix:param or param if prefix is null.
     */
    public static String getParamWithPrefix(String prefix, String name) {
        String val = "";
        if (prefix != null) {
            val += prefix + ":";
        }
        val += name;
        return val;
    }
    /**
     * Get specific attribute from Node or null.
     * @param node
     * @param attrName
     * @return attribute value or null
     */
    @Nullable
    public static String getAttrValueFromNode(Node node, String attrName) {
        if (node == null) {
            return null;
        }
        NamedNodeMap attrs = node.getAttributes();
        if (attrs == null) {
            return null;
        }
        Node attr = attrs.getNamedItem(attrName);
        if (attr == null) {
            return null;
        }

        return attr.getNodeValue();
    }
}
