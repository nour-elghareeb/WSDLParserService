package ne.wsdlparser.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ne.wsdlparser.lib.esql.ESQLBlock;
import ne.wsdlparser.lib.esql.ESQLManager;
import ne.wsdlparser.lib.exception.WSDLException;
import ne.wsdlparser.lib.exception.WSDLExceptionCode;
import ne.wsdlparser.lib.xsd.XSDManager;

public class WSDLManager implements WSDLManagerRetrieval {

    private Document wsdl;
    private ArrayList<Service> services;
    private HashMap<String, String> namespaces = new HashMap<String, String>();
    private HashMap<String, XSDManager> schemas = new HashMap<String, XSDManager>();
    private String targetNS;
    private String name;
    private XPath xPath;
    private String workingdir;
    private XSDManager xsdManager;
    private ESQLManager esqlManager = new ESQLManager(this);

    public WSDLManager(String path) throws WSDLException {

        try {
            this.namespaces.put("wsdl", "http://schemas.xmlsoap.org/wsdl/");
            this.namespaces.put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
            this.load(path);

        } catch (WSDLException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadNamespaces() throws XPathExpressionException {
        Node node = (Node) this.xPath.compile("/definitions").evaluate(this.wsdl, XPathConstants.NODE);
        NamedNodeMap attrMap = node.getAttributes();
        for (int i = 0; i < attrMap.getLength(); i++) {
            Node attrNode = attrMap.item(i);
            String nodeName = attrNode.getNodeName();
            String value = attrNode.getNodeValue();

            if (nodeName.startsWith("xmlns")) {
                this.namespaces.put(nodeName.replace("xmlns:", ""), value);
            } else if (nodeName.equals("targetNamespace")) {
                this.targetNS = value;
            } else if (nodeName.equals("name")) {
                this.name = value;
            }
        }
        this.xPath.setNamespaceContext(new NamespaceContext() {

            public Iterator getPrefixes(String namespaceURI) {
                return WSDLManager.this.namespaces.keySet().iterator();
            }

            public String getPrefix(String namespaceURI) {
                for (Entry entry : WSDLManager.this.namespaces.entrySet()) {
                    if (entry.getValue().equals(namespaceURI)) {
                        return (String) entry.getKey();
                    }
                }
                return null;
            }

            public String getNamespaceURI(String prefix) {
                return WSDLManager.this.namespaces.get(prefix);
            }
        });
    }

    private void load(String path) throws FileNotFoundException, SAXException, IOException,
            ParserConfigurationException, ClassNotFoundException, XPathException, XPathExpressionException, WSDLException {
        File file = new File(path);
        this.workingdir = file.getParent();
        this.wsdl = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(file));
        this.xPath = XPathFactory.newInstance().newXPath();
        this.loadNamespaces();
//        this.loadServices();
        System.out.println("Done");

    }

    private boolean loadSchema() throws WSDLException {

        try {
            NodeList types = (NodeList) this.xPath.compile("/definitions/types/schema").evaluate(this.wsdl,
                    XPathConstants.NODESET);
            this.xsdManager = new XSDManager(this, this.workingdir, types);
            
            return true;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new WSDLException(WSDLExceptionCode.XSD_SCHEMA_LOADING_ERROR);
    }

    public void loadServices() throws WSDLException {
        try {
            final NodeList services = (NodeList) this.getXPath().compile("/definitions/service").evaluate(this.getWSDLFile(),
                    XPathConstants.NODESET);
            this.services = new ArrayList<Service>() {
                {
                    for (int i = 0; i < services.getLength(); i++) {
                        Node serviceNode = services.item(i);
                        Service service = new Service(WSDLManager.this, serviceNode);
                        add(service);
                    }
                }
            };
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }
    }

    public Service loadService(String serviceName) throws WSDLException {
        try {
            final Node node = (Node) this.getXPath().compile(String.format(Locale.getDefault(), "/definitions/service[@name='%s']", serviceName)).evaluate(this.getWSDLFile(),
                    XPathConstants.NODE);
            if (node == null) {
                throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION, "No service found with this name!");
            }
            return new Service(WSDLManager.this, node);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WSDLManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new WSDLException(WSDLExceptionCode.WSDL_PARSING_EXCEPTION);
        }
    }

    public ArrayList<Service> getServices() {
        return this.services;
    }

    public Service getService(String serviceName) {
        for (Service service : this.services) {
            if (serviceName.equals(service.getName())) {
                return service;
            }
        }
        return null;
    }

    public Document getWSDLFile() {
        return this.wsdl;
    }

    /**
     *
     * @return @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws WSDLException
     */
    @Override
    public XSDManager getXSDManager() throws WSDLException {

        if (this.xsdManager == null) {
            this.loadSchema();
        }
        return this.xsdManager;

    }

    public XPath getXPath() {
        return this.xPath;
    }

    public String getTargetNameSpace() {
        return this.targetNS;
    }

    public String getNamespaceURI(String prefix) {
        String uri;
        uri = this.xPath.getNamespaceContext().getNamespaceURI(prefix);
        if (uri == null) {
            uri = this.xsdManager.getNamespaceURI(prefix);
        }
        return uri;
    }

    public ESQLManager getESQLManager() {
        return this.esqlManager;
    }

    public String getPrefix(String tns) {
        String prefix;
        prefix = this.xPath.getNamespaceContext().getPrefix(tns);
        if (prefix == null) {
            prefix = this.xsdManager.getPrefix(tns);
        }
        return prefix;
    }

}
