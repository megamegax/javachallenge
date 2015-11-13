
package eu.loxon.centralcontrol;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="scout" type="{http://www.loxon.eu/CentralControl/}scouting" maxOccurs="unbounded"/>
 *         &lt;element name="result" type="{http://www.loxon.eu/CentralControl/}commonResp"/>
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
    "scout",
    "result"
})
@XmlRootElement(name = "radarResponse")
public class RadarResponse {

    @XmlElement(required = true)
    protected List<Scouting> scout;
    @XmlElement(required = true)
    protected CommonResp result;

    /**
     * Gets the value of the scout property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the scout property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScout().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Scouting }
     * 
     * 
     */
    public List<Scouting> getScout() {
        if (scout == null) {
            scout = new ArrayList<Scouting>();
        }
        return this.scout;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link CommonResp }
     *     
     */
    public CommonResp getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommonResp }
     *     
     */
    public void setResult(CommonResp value) {
        this.result = value;
    }

    @Override
    public String toString() {
        return "RadarResponse{" +
                "scout=" + scout +
                ", result=" + result +
                '}';
    }
}
