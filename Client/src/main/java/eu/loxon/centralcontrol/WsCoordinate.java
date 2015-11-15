
package eu.loxon.centralcontrol;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wsCoordinate complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="wsCoordinate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="x" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="y" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsCoordinate", propOrder = {
        "x",
        "y"
})
public class WsCoordinate {

    protected int x;
    protected int y;

    public WsCoordinate() {
    }

    public WsCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the value of the x property.
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     */
    public void setX(int value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     */
    public void setY(int value) {
        this.y = value;
    }

    @Override
    public String toString() {
        return "WsCoordinate{" +
                "x=" + (x+1) +
                ", y=" + (y+1) +
                '}';
    }
}
