package ne.wsdlparser.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.esql.ESQLManager;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;
import ne.wsdlparser.lib.xsd.XSDManager;

public class WSDLManager implements WSDLManagerRetrieval {

    private Document wsdl;
    private ArrayList<WSDLService> services;
    private final HashMap<String, String> namespaces;
    private String targetNS;
    private XPath xPath;
    private String workingdir;
    private XSDManager xsdManager;
    private ESQLManager esqlManager;

    /**
     * Load WSDL file from path successfully or throws and WSDLException
     *
     * @param path
     * @throws WSDLException
     */
    public WSDLManager(String path) throws WSDLException {
        this.namespaces = new HashMap<String, String>();

        //Add starting namespaces and prefixes for wsdl and soapenv
        this.namespaces.put("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        this.namespaces.put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        //load file
        this.load(path);
    }

    /**
     * Load WSDL file from path and creates XML document, creates new XPath
     * instance and proceeds to loading namespaces
     *
     * @param path
     * @throws WSDLException
     */
    private void load(String path) throws WSDLException {
        try {
            //load file from path
            File file = new File(path);
            if (!file.exists()) {
                throw new WSDLException(WSDLExceptionCode.WSDL_FILE_NOT_FOUND);
            }
            //get wsdl file directory to search for XSD files
            this.workingdir = file.getParent();
            this.wsdl = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(file));
            // Create new XPath object
            this.xPath = XPathFactory.newInstance().newXPath();
            // Load namespaces and prefixes from wsdl.
            this.loadNamespaces();
            return;

        } catch (SAXException | ParserConfigurationException | IOException ex) {
            Logger.getLogger(WSDLManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID);
    }

    /**
     * Load prefixes and NameSpaces from WSDL definition node and add them to
     * the XPath instance
     *
     * @throws XPathExpressionException
     * @throws WSDLException
     */
    private void loadNamespaces() throws WSDLException {
        try {
            // get definition node
            Node node = (Node) this.xPath.compile("/definitions").evaluate(this.wsdl, XPathConstants.NODE);
            if (node == null) {
                throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID);
            }
            //loop over xmlns to get prefixes and ns for this file
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
            // Add namespaces context to the XPath
            this.xPath.setNamespaceContext(new NamespaceContext() {

                @Override
                public Iterator getPrefixes(String namespaceURI) {
                    return WSDLManager.this.namespaces.keySet().iterator();
                }

                @Override
                public String getPrefix(String namespaceURI) {
                    for (Entry entry : WSDLManager.this.namespaces.entrySet()) {
                        if (entry.getValue().equals(namespaceURI)) {
                            return (String) entry.getKey();
                        }
                    }
                    return null;
                }

                @Override
                public String getNamespaceURI(String prefix) {
                    return WSDLManager.this.namespaces.get(prefix);
                }
            });

        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_NOT_VALID);
        }
    }

    /**
     * Load schema details from WSDL file and set up XSDManager instance
     *
     * @return true if succeeds| error if any error occur
     * @throws WSDLException if any error occur while load XSDs files
     */
    private boolean loadSchema() throws WSDLException {
        // load schema data from wsdl file
        try {
            NodeList types = (NodeList) this.xPath.compile("/definitions/types/schema").evaluate(this.wsdl,
                    XPathConstants.NODESET);
            this.xsdManager = new XSDManager(this, this.workingdir, types);

            return true;

        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.XSD_SCHEMA_LOADING_ERROR);
    }

    /**
     * Load all services from the WSDL file
     *
     * @return loaded services or throws exception
     * @throws WSDLException
     */
    public ArrayList<WSDLService> loadServices() throws WSDLException {
        if (this.services != null) {
            return this.services;
        }
        try {
            final NodeList serviceNodes = (NodeList) this.getXPath().compile("/definitions/service").evaluate(this.getWSDLFile(),
                    XPathConstants.NODESET);
            this.services = new ArrayList<WSDLService>() {
                {
                    for (int i = 0; i < serviceNodes.getLength(); i++) {
                        Node serviceNode = serviceNodes.item(i);
                        WSDLService service = new WSDLService(WSDLManager.this, serviceNode);
                        add(service);
                    }
                }
            };
            return this.services;

        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }
    }

    /**
     * Load service by name
     *
     * @param serviceName name of the service to load
     * @return WSDLService element or an exception
     * @throws WSDLException if any error occur
     */
    public WSDLService loadService(String serviceName) throws WSDLException {
        if (this.services != null) {
            for (WSDLService service : this.services) {
                if (service.getName().equals(serviceName)) {
                    return service;
                }
            }
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION, "No service found by this name!");
        }
        try {
            final Node node = (Node) this.getXPath().compile(String.format(Locale.getDefault(), "/definitions/service[@name='%s']", serviceName)).evaluate(this.getWSDLFile(),
                    XPathConstants.NODE);
            if (node == null) {
                throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION, "No service found by this name!");
            }
            return new WSDLService(WSDLManager.this, node);

        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }
    }

    @Override
    public Document getWSDLFile() {
        return this.wsdl;
    }

    @Override
    public XSDManager getXSDManager() throws WSDLException {
        
        if (this.xsdManager == null) {
            this.loadSchema();
        }
        return this.xsdManager;

    }

    @Override
    public XPath getXPath() {
        return this.xPath;
    }

    @Override
    public String getTargetNameSpace() {
        return this.targetNS;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        String uri;
        uri = this.xPath.getNamespaceContext().getNamespaceURI(prefix);
        if (uri == null) {
            uri = this.xsdManager.getNamespaceURI(prefix);
        }
        return uri;
    }

    @Override
    public ESQLManager getESQLManager() {
        if (this.esqlManager == null) this.esqlManager = new ESQLManager(this);
        return this.esqlManager;
    }

    @Override
    public String getPrefix(String tns) throws WSDLException {
        String prefix;
        prefix = this.xPath.getNamespaceContext().getPrefix(tns);

        if (prefix == null) {
            prefix = getXSDManager().getPrefix(tns);
        }
        return prefix;
    }

}
