
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
 *         &lt;element name="NSLine" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="prefix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="namespace" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ESQLLine" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="ESQLComment">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="ESQLSetter">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="XPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="ESQLSource" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="ValueHelp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="FieldType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="DefaultValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "nsLine",
    "esqlLine"
})
@XmlRootElement(name = "GenerateMessageESQLResponse")
public class GenerateMessageESQLResponse {

    @XmlElement(name = "NSLine")
    protected List<GenerateMessageESQLResponse.NSLine> nsLine;
    @XmlElement(name = "ESQLLine")
    protected List<GenerateMessageESQLResponse.ESQLLine> esqlLine;

    /**
     * Gets the value of the nsLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nsLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNSLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GenerateMessageESQLResponse.NSLine }
     * 
     * 
     */
    public List<GenerateMessageESQLResponse.NSLine> getNSLine() {
        if (nsLine == null) {
            nsLine = new ArrayList<GenerateMessageESQLResponse.NSLine>();
        }
        return this.nsLine;
    }

    /**
     * Gets the value of the esqlLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the esqlLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getESQLLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GenerateMessageESQLResponse.ESQLLine }
     * 
     * 
     */
    public List<GenerateMessageESQLResponse.ESQLLine> getESQLLine() {
        if (esqlLine == null) {
            esqlLine = new ArrayList<GenerateMessageESQLResponse.ESQLLine>();
        }
        return this.esqlLine;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;element name="ESQLComment">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="ESQLSetter">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="XPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="ESQLSource" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="ValueHelp" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="FieldType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="DefaultValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "esqlComment",
        "esqlSetter"
    })
    public static class ESQLLine {

        @XmlElement(name = "ESQLComment")
        protected GenerateMessageESQLResponse.ESQLLine.ESQLComment esqlComment;
        @XmlElement(name = "ESQLSetter")
        protected GenerateMessageESQLResponse.ESQLLine.ESQLSetter esqlSetter;

        /**
         * Gets the value of the esqlComment property.
         * 
         * @return
         *     possible object is
         *     {@link GenerateMessageESQLResponse.ESQLLine.ESQLComment }
         *     
         */
        public GenerateMessageESQLResponse.ESQLLine.ESQLComment getESQLComment() {
            return esqlComment;
        }

        /**
         * Sets the value of the esqlComment property.
         * 
         * @param value
         *     allowed object is
         *     {@link GenerateMessageESQLResponse.ESQLLine.ESQLComment }
         *     
         */
        public void setESQLComment(GenerateMessageESQLResponse.ESQLLine.ESQLComment value) {
            this.esqlComment = value;
        }

        /**
         * Gets the value of the esqlSetter property.
         * 
         * @return
         *     possible object is
         *     {@link GenerateMessageESQLResponse.ESQLLine.ESQLSetter }
         *     
         */
        public GenerateMessageESQLResponse.ESQLLine.ESQLSetter getESQLSetter() {
            return esqlSetter;
        }

        /**
         * Sets the value of the esqlSetter property.
         * 
         * @param value
         *     allowed object is
         *     {@link GenerateMessageESQLResponse.ESQLLine.ESQLSetter }
         *     
         */
        public void setESQLSetter(GenerateMessageESQLResponse.ESQLLine.ESQLSetter value) {
            this.esqlSetter = value;
        }


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
         *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "type",
            "comment"
        })
        public static class ESQLComment {

            @XmlElement(name = "Type", required = true)
            protected String type;
            @XmlElement(name = "Comment", required = true)
            protected String comment;

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setType(String value) {
                this.type = value;
            }

            /**
             * Gets the value of the comment property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getComment() {
                return comment;
            }

            /**
             * Sets the value of the comment property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setComment(String value) {
                this.comment = value;
            }

        }


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
         *         &lt;element name="XPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="ESQLSource" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="ValueHelp" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="FieldType" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="DefaultValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "xPath",
            "esqlSource",
            "valueHelp",
            "fieldType",
            "defaultValue"
        })
        public static class ESQLSetter {

            @XmlElement(name = "XPath", required = true)
            protected String xPath;
            @XmlElement(name = "ESQLSource", required = true)
            protected String esqlSource;
            @XmlElement(name = "ValueHelp", required = true)
            protected String valueHelp;
            @XmlElement(name = "FieldType", required = true)
            protected String fieldType;
            @XmlElement(name = "DefaultValue", required = true)
            protected String defaultValue;

            /**
             * Gets the value of the xPath property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getXPath() {
                return xPath;
            }

            /**
             * Sets the value of the xPath property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setXPath(String value) {
                this.xPath = value;
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
             * Gets the value of the valueHelp property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValueHelp() {
                return valueHelp;
            }

            /**
             * Sets the value of the valueHelp property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValueHelp(String value) {
                this.valueHelp = value;
            }

            /**
             * Gets the value of the fieldType property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFieldType() {
                return fieldType;
            }

            /**
             * Sets the value of the fieldType property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFieldType(String value) {
                this.fieldType = value;
            }

            /**
             * Gets the value of the defaultValue property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDefaultValue() {
                return defaultValue;
            }

            /**
             * Sets the value of the defaultValue property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDefaultValue(String value) {
                this.defaultValue = value;
            }

        }

    }


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
     *         &lt;element name="prefix" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="namespace" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "prefix",
        "namespace"
    })
    public static class NSLine {

        @XmlElement(required = true)
        protected String prefix;
        @XmlElement(required = true)
        protected String namespace;

        /**
         * Gets the value of the prefix property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * Sets the value of the prefix property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPrefix(String value) {
            this.prefix = value;
        }

        /**
         * Gets the value of the namespace property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNamespace() {
            return namespace;
        }

        /**
         * Sets the value of the namespace property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNamespace(String value) {
            this.namespace = value;
        }

    }

}
