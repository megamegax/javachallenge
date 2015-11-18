
package eu.loxon.centralcontrol;

import jmx.javachallenge.helper.Logger;
import jmx.javachallenge.helper.Step;
import jmx.javachallenge.helper.Util;
import jmx.javachallenge.service.Service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Random;


/**
 * <p>Java class for wsBuilderunit complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="wsBuilderunit">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cord" type="{http://www.loxon.eu/CentralControl/}wsCoordinate"/>
 *         &lt;element name="unitid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsBuilderunit", propOrder = {
        "cord",
        "unitid"
})
public class WsBuilderunit {

    @XmlElement(required = true)
    protected WsCoordinate cord;
    protected int unitid;
    protected boolean ownWill = false;
    private Service service;

    public WsBuilderunit() {
        this.service = Service.getInstance();
    }

    /**
     * Gets the value of the cord property.
     *
     * @return possible object is
     * {@link WsCoordinate }
     */
    public WsCoordinate getCord() {
        return cord;
    }

    /**
     * Sets the value of the cord property.
     *
     * @param value allowed object is
     *              {@link WsCoordinate }
     */
    public void setCord(WsCoordinate value) {
        this.cord = value;
    }

    /**
     * Gets the value of the unitid property.
     */
    public int getUnitid() {
        return unitid;
    }

    /**
     * Sets the value of the unitid property.
     */
    public void setUnitid(int value) {
        this.unitid = value;
    }

    @Override
    public String toString() {
        return "WsBuilderunit{" +
                "cord=" + cord +
                ", unitid=" + unitid +
                '}';
    }

    public boolean hasOwnWill() {
        return ownWill;
    }

    public void setOwnWill(boolean will) {
        this.ownWill = will;
    }

    public void step() {
        Logger.log(unitid + ", has own will");
        //TODO építkezni, mozogni, nem visszalépni, figyelni mi merre van hajaj Marci alkoss valamit :D
        WsDirection direction = moveRandomly();
        service.watch(unitid);
        if (doMove(unitid, direction)) {
            doMove(unitid, direction);
        }
    }

    private boolean doMove(int unitID, WsDirection direction) {
        WsCoordinate simulatedCoordinate = Util.simulateMove(service.builderUnits.get(unitID), direction);
        Step step = Util.checkMovement(simulatedCoordinate);
        Logger.log(step);
        switch (step) {
            case BUILD:
                return service.structureTunnel(unitID, direction);

            case MOVE:
                return service.moveUnit(unitID, direction);

            case WATCH:
                return service.watch(unitID);

            case EXPLODE:
                return service.explode(unitID, direction);

            case STAY:
                return doMove(unitID, moveRandomly());

            case NO_POINTS:
                Logger.log("Elfogytak az elkölthető pontok");
                return false;
        }
        return false;
    }

    private WsDirection moveRandomly() {
        Random random = new Random();
        int r = random.nextInt(6) + 1;
        if (r == 1) {
            return WsDirection.RIGHT;
        } else if (r == 5) {
            return WsDirection.RIGHT;
        } else if (r == 2) {
            return WsDirection.LEFT;
        } else if (r == 3) {
            return WsDirection.UP;
        } else if (r == 6) {
            return WsDirection.DOWN;
        } else return WsDirection.DOWN;
    }

}
