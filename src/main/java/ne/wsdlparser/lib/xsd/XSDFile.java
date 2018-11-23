package ne.wsdlparser.lib.xsd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import ne.wsdlparser.lib.WSDLManagerRetrieval;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.utility.Utils;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;

public class XSDFile {

    Document xsd;
    private XPath xPath;
    private HashMap<String, String> namespaces = new HashMap<String, String>();
    private String targetNS;
    private ArrayList<XSDFile> includes = new ArrayList<XSDFile>();
    private ArrayList<XSDFile> imports = new ArrayList<XSDFile>();
    private HashMap<String, Node> inlineImports = new HashMap<String, Node>();

    private final static String NEW_ROOT = "/root/";
    private Boolean isRootModified = false;
    private boolean qualified;
    private WSDLManagerRetrieval manager;

    /**
     * XSD file loading via file path/URI handling all the imports/includes
     * recursively. Checks if the path is a valid HTTP/HTTPS and if so, download
     * it to the wsdl directory. The download happens once. if any changes
     * happens to the remote file, you must delete WSDL and upload it again..
     *
     * @param manager WSDLManager instance injction.
     * @param filePath xsd file path or URI
     * @param namespace namespace for the file.
     * @throws WSDLException
     */
    public XSDFile(WSDLManagerRetrieval manager, String filePath, String namespace) throws WSDLException {
        this.manager = manager;
        File xsdFile;
        try {
            if (Utils.validateURI(filePath)) {
                String[] temp = filePath.split("/");
                xsdFile = new File(manager.getWSDLDirectory(), temp[temp.length - 1]);
                if (!xsdFile.exists()) {

                    BufferedInputStream in = new BufferedInputStream(new URL(filePath).openStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(xsdFile);
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }

                }
            } else {
                xsdFile = new File(filePath);
                xsdFile = new File(this.manager.getWSDLDirectory(), xsdFile.getName());
            }
            this.targetNS = namespace;

            this.xsd = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(xsdFile));
            this.load();
        } catch (FileNotFoundException e) {
            throw new WSDLException(WSDLExceptionCode.XSD_SCHEMA_FILE_NOT_FOUND, "Schema file '" + filePath + "' not found!");
        } catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException ex) {
            Logger.getLogger(XSDFile.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.XSD_SCHEMA_LOADING_ERROR);
        }

    }

    /**
     * Load inline xsd file located in WSDL handling all the imports/includes
     * recursively
     *
     * @param manager
     * @param schema
     * @throws WSDLException
     */
    public XSDFile(WSDLManagerRetrieval manager, NodeList schema) throws WSDLException {
        try {
            this.manager = manager;

            this.xsd = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element root = this.xsd.createElement("root");
            this.xsd.appendChild(root);
            this.targetNS = null;
            this.isRootModified = true;
            for (int i = 0; i < schema.getLength(); i++) {
                Node node = schema.item(i);
                node = this.xsd.importNode(node, true);
                root.appendChild(node);
            }
            this.load();
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException ex) {
            Logger.getLogger(XSDFile.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.XSD_SCHEMA_LOADING_ERROR);
        }

    }

    /**
     * import xsd file. <import> node handling..
     *
     * @param filePath
     * @param namespace
     * @throws FileNotFoundException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws WSDLException
     */
    private void _import(String filePath, String namespace) throws FileNotFoundException, XPathExpressionException,
            SAXException, IOException, ParserConfigurationException, WSDLException {
        this.imports.add(new XSDFile(this.manager, filePath, namespace));
    }

    /**
     * add xsd file to includes..
     *
     * @param filePath
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws WSDLException
     */
    private void include(String filePath) throws FileNotFoundException, SAXException, IOException,
            ParserConfigurationException, XPathExpressionException, WSDLException {
        this.includes.add(new XSDFile(this.manager, filePath, this.targetNS));
    }

    /**
     * Checks if elementFormDefault is present in current schema.
     *
     * @throws XPathExpressionException
     */
    private void setQualified() throws XPathExpressionException {
        Node schema = (Node) this.xPath.compile(this.prepareXPath("schema")).evaluate(this.xsd, XPathConstants.NODE);
        String temp = Utils.getAttrValueFromNode(schema, "elementFormDefault");
        this.qualified = temp != null && temp.toLowerCase().equals("qualified");
    }

    /**
     * Load XSD schema handling includes, imports..
     *
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws WSDLException
     */
    private void load() throws SAXException, IOException,
            ParserConfigurationException, XPathExpressionException, WSDLException {

        this.xPath = XPathFactory.newInstance().newXPath();

        this.loadNamespaces();
        // load includes if any

        setQualified();
        NodeList includeNodes = (NodeList) this.xPath.compile(this.prepareXPath("schema/include")).evaluate(this.xsd,
                XPathConstants.NODESET);
        for (int i = 0; i < includeNodes.getLength(); i++) {
            Node include = includeNodes.item(i);
            this.include(Utils.getAttrValueFromNode(include, "schemaLocation"));
        }
        // load imports if any
        NodeList importNodes = (NodeList) this.xPath.compile(this.prepareXPath("schema/import")).evaluate(this.xsd,
                XPathConstants.NODESET);
        if (importNodes != null) {
            this.imports = new ArrayList<>();
            for (int i = 0; i < importNodes.getLength(); i++) {
                Node _import = importNodes.item(i);
                // String ns = Utils.getAttrValueFromNode(_import, "namespace");
                // this._import(Utils.getAttrValueFromNode(_import, "schemaLocation"), ns);

                String ns = Utils.getAttrValueFromNode(_import, "namespace");
                String schemaLocation = Utils.getAttrValueFromNode(_import, "schemaLocation");
                if (schemaLocation == null) {
                    Node _schema = (Node) this.xPath.compile(
                            String.format(Locale.getDefault(), this.prepareXPath("schema[@targetNamespace='%s']"), ns))
                            .evaluate(this.xsd, XPathConstants.NODE);
                    this.inlineImports.put(ns, _schema);
                } else {
                    if (this.imports == null) {
                        this.imports = new ArrayList<>();
                    }
                    this._import(schemaLocation, ns);
                }
            }
        }

    }

    /**
     * handles XPath if a new root was added. workaround to make inline schema
     * works as an external file.
     *
     * @param xpath
     * @return
     */
    private String prepareXPath(String xpath) {
        if (!this.isRootModified) {
            if (xpath.startsWith(NEW_ROOT)) {
                xpath = xpath.replace(NEW_ROOT, "");
            }
            return xpath;
        }
        if (xpath.startsWith(NEW_ROOT)) {
            return xpath;
        }
        if (xpath.startsWith("/")) {
            return NEW_ROOT.concat(xpath.substring(1));
        } else {
            return NEW_ROOT.concat(xpath);
        }
    }

    /**
     * Load namespaces found in schema and add them to the XPath instance..
     *
     * @throws XPathExpressionException
     */
    private void loadNamespaces() throws XPathExpressionException {
        Node node = (Node) this.xPath.compile(this.prepareXPath("schema")).evaluate(this.xsd, XPathConstants.NODE);
        NamedNodeMap attrMap = node.getAttributes();
        for (int i = 0; i < attrMap.getLength(); i++) {
            Node attrNode = attrMap.item(i);
            String nodeName = attrNode.getNodeName();
            String value = attrNode.getNodeValue();

            if (nodeName.startsWith("xmlns")) {
                this.namespaces.put(nodeName.replace("xmlns:", ""), value);
            } else if (nodeName.equals("targetNamespace")) {
                this.targetNS = value;
            }
        }
        this.xPath.setNamespaceContext(new NamespaceContext() {

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return XSDFile.this.namespaces.keySet().iterator();
            }

            @Override
            public String getPrefix(String namespaceURI) {
                for (java.util.Map.Entry<String, String> entry : XSDFile.this.namespaces.entrySet()) {
                    if (entry.getValue().equals(namespaceURI)) {
                        return (String) entry.getKey();
                    }
                }
                return null;
            }

            @Override
            public String getNamespaceURI(String prefix) {
                return XSDFile.this.namespaces.get(prefix);
            }

        });
    }

    /**
     * Search passed source schema for the xpath given.
     *
     * @param xpath xpath to compile
     * @param source schema to search
     * @param returnType XPathConstant
     * @return node or null
     * @throws XPathExpressionException
     */
    public Object find(String xpath, Object source, QName returnType) throws XPathExpressionException {
        if (source == null) {
            return null;
        }
        return this.xPath.compile(xpath).evaluate(source, returnType);
    }
    /**
     * find a node in this schema or in their includes/imports handling their
     * form qualification and namespaces.
     * @param xpath
     * @param returnType
     * @return
     * @throws XPathExpressionException 
     */
    public Object find(String xpath, QName returnType) throws XPathExpressionException {
        String newxpath = this.prepareXPath(xpath);
        Object node = null;
        int i = 0;
        node = this.find(newxpath, this.xsd, returnType);

        if (node == null) {
            node = this.findInChildren(xpath, returnType);
        } else {
            ((Node) node).setUserData("qualified", isQualified(), null);
            if (isQualified()) {
                ((Node) node).setUserData("tns", this.targetNS, null);
            }
        }

        return node;
    }
    /**
     * search includes and imports for specific xpath
     * @param xpath
     * @param returnType
     * @return
     * @throws XPathExpressionException 
     */
    private Object findInChildren(String xpath, QName returnType) throws XPathExpressionException {
        Object node = null;
        for (XSDFile file : this.includes) {
            node = file.find(xpath, returnType);
            if (node != null) {
                if (node instanceof Node && ((Node) node).getUserData("tns") == null) {
                    ((Node) node).setUserData("tns", file.targetNS, null);
                    ((Node) node).setUserData("qualified", file.isQualified(), null);
                }
                return node;
            }
        }

        for (XSDFile file : this.imports) {
            node = file.find(xpath, returnType);
            if (node != null) {
                if (node instanceof Node && ((Node) node).getUserData("tns") == null) {
                    ((Node) node).setUserData("tns", file.targetNS, null);
                    ((Node) node).setUserData("qualified", file.isQualified(), null);
                }
                return node;
            }
        }
        for (Entry<String, Node> entry : this.inlineImports.entrySet()) {
            node = this.find(xpath, entry.getValue(), returnType);
            if (node != null) {
                if (node instanceof Node && ((Node) node).getUserData("tns") == null) {
                    ((Node) node).setUserData("tns", entry.getKey(), null);

                    Node nn = entry.getValue();
                    String elementFormDefault = Utils.getAttrValueFromNode(nn, "elementFormDefault");
                    ((Node) node).setUserData("qualified", elementFormDefault != null && elementFormDefault.toLowerCase().equals("qualified"), null);
                }
                return node;
            }
        }

        return node;

    }
    /**
     * search for specific NS by prefix in this file and its children
     * @param prefix
     * @return NS or null
     */
    public String getNamespaceURI(String prefix) {
        String ns = null;
        ns = this.xPath.getNamespaceContext().getNamespaceURI(prefix);
        if (ns != null) {
            return ns;
        }
        for (XSDFile file : this.includes) {
            ns = file.getNamespaceURI(prefix);
            if (ns != null) {
                return ns;
            }
        }
        for (XSDFile file : this.imports) {
            ns = file.getNamespaceURI(prefix);
            if (ns != null) {
                return ns;
            }
        }

        return null;
    }
    /**
     * Search for a specific prefix by NS in this file or its children
     * @param ns
     * @return prefix or null
     */
    public String getPrefix(String ns) {
        String prefix = null;
        prefix = this.xPath.getNamespaceContext().getPrefix(ns);
        if (prefix != null) {
            return prefix;
        }
        for (XSDFile file : this.includes) {
            prefix = file.getPrefix(ns);
            if (prefix != null) {
                return prefix;
            }
        }
        for (XSDFile file : this.imports) {
            prefix = file.getPrefix(ns);
            if (prefix != null) {
                return prefix;
            }
        }

        return null;
    }
    /**
     * Is this schema form qualified or unqualified
     * @return true if qualified
     */
    private boolean isQualified() {
        return this.qualified;
    }

}
