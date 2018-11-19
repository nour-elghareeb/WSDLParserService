
package wsdlparse.ne;

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
 *         &lt;element name="FileName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FileExtension" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MimeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FileContents" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="FileSize" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="Overwrite" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "fileName",
    "fileExtension",
    "mimeType",
    "fileContents",
    "fileSize",
    "overwrite"
})
@XmlRootElement(name = "UploadWSDLRequest")
public class UploadWSDLRequest {

    @XmlElement(name = "FileName", required = true)
    protected String fileName;
    @XmlElement(name = "FileExtension", required = true)
    protected String fileExtension;
    @XmlElement(name = "MimeType", required = true)
    protected String mimeType;
    @XmlElement(name = "FileContents", required = true)
    protected byte[] fileContents;
    @XmlElement(name = "FileSize")
    protected double fileSize;
    @XmlElement(name = "Overwrite", defaultValue = "false")
    protected boolean overwrite;

    /**
     * Gets the value of the fileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the fileExtension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Sets the value of the fileExtension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileExtension(String value) {
        this.fileExtension = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the fileContents property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFileContents() {
        return fileContents;
    }

    /**
     * Sets the value of the fileContents property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFileContents(byte[] value) {
        this.fileContents = value;
    }

    /**
     * Gets the value of the fileSize property.
     * 
     */
    public double getFileSize() {
        return fileSize;
    }

    /**
     * Sets the value of the fileSize property.
     * 
     */
    public void setFileSize(double value) {
        this.fileSize = value;
    }

    /**
     * Gets the value of the overwrite property.
     * 
     */
    public boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Sets the value of the overwrite property.
     * 
     */
    public void setOverwrite(boolean value) {
        this.overwrite = value;
    }

}
