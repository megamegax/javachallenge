
package eu.loxon.centralcontrol;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{http://www.loxon.eu/CentralControl/}commonResp"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "result"
})
@XmlRootElement(name = "moveBuilderUnitResponse")
public class MoveBuilderUnitResponse {

    @XmlElement(required = true)
    protected CommonResp result;

    /**
     * Gets the value of the result property.
     *
     * @return possible object is
     * {@link CommonResp }
     */
    public CommonResp getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value allowed object is
     *              {@link CommonResp }
     */
    public void setResult(CommonResp value) {
        this.result = value;
    }

    @Override
    public String toString() {
        return "MoveBuilderUnitResponse{" +
                '}';
    }
}
