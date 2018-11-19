
package wsdlparse.ne;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WSDLName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServiceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PortName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OperationName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MessageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ESQLVerboisty" maxOccurs="unbounded" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="STRUCTURE"/>
 *               &lt;enumeration value="VALUE_HELP"/>
 *               &lt;enumeration value="DOCUMENTATION"/>
 *               &lt;enumeration value="EMPTY"/>
 *               &lt;enumeration value="MULTIPLICITY"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ESQLSource">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="INPUT"/>
 *               &lt;enumeration value="OUTPUT"/>
 *               &lt;enumeration value="LOCAL_ENVIRONMENT"/>
 *               &lt;enumeration value="ENVIRONMENT"/>
 *               &lt;enumeration value="LOCAL_OUPUT_ENVIRONMENT"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="UseReferenceAsVariables" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "wsdlName",
    "serviceName",
    "portName",
    "operationName",
    "messageName",
    "esqlVerboisty",
    "esqlSource",
    "useReferenceAsVariables"
})
@XmlRootElement(name = "GenerateMessageESQLRequest")
public class GenerateMessageESQLRequest {

    @XmlElement(name = "WSDLName", required = true)
    protected String wsdlName;
    @XmlElement(name = "ServiceName", required = true)
    protected String serviceName;
    @XmlElement(name = "PortName", required = true)
    protected String portName;
    @XmlElement(name = "OperationName", required = true)
    protected String operationName;
    @XmlElement(name = "MessageName", required = true)
    protected String messageName;
    @XmlElement(name = "ESQLVerboisty")
    protected List<String> esqlVerboisty;
    @XmlElement(name = "ESQLSource", required = true)
    protected String esqlSource;
    @XmlElement(name = "UseReferenceAsVariables")
    protected Boolean useReferenceAsVariables;

    /**
     * Gets the value of the wsdlName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWSDLName() {
        return wsdlName;
    }

    /**
     * Sets the value of the wsdlName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWSDLName(String value) {
        this.wsdlName = value;
    }

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the portName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortName() {
        return portName;
    }

    /**
     * Sets the value of the portName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortName(String value) {
        this.portName = value;
    }

    /**
     * Gets the value of the operationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * Sets the value of the operationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationName(String value) {
        this.operationName = value;
    }

    /**
     * Gets the value of the messageName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageName() {
        return messageName;
    }

    /**
     * Sets the value of the messageName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageName(String value) {
        this.messageName = value;
    }

    /**
     * Gets the value of the esqlVerboisty property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the esqlVerboisty property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getESQLVerboisty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getESQLVerboisty() {
        if (esqlVerboisty == null) {
            esqlVerboisty = new ArrayList<String>();
        }
        return this.esqlVerboisty;
    }

    /**
     * Gets the value of the esqlSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getESQLSource() {
        return esqlSource;
    }

    /**
     * Sets the value of the esqlSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setESQLSource(String value) {
        this.esqlSource = value;
    }

    /**
     * Gets the value of the useReferenceAsVariables property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseReferenceAsVariables() {
        return useReferenceAsVariables;
    }

    /**
     * Sets the value of the useReferenceAsVariables property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseReferenceAsVariables(Boolean value) {
        this.useReferenceAsVariables = value;
    }

}
