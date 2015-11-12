
package eu.loxon.centralcontrol;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for objectType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="objectType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Tunnel"/>
 *     &lt;enumeration value="Shuttle"/>
 *     &lt;enumeration value="BuilderUnit"/>
 *     &lt;enumeration value="Rock"/>
 *     &lt;enumeration value="Granite"/>
 *     &lt;enumeration value="Obsidian"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "objectType")
@XmlEnum
public enum ObjectType {

    @XmlEnumValue("Tunnel")
    TUNNEL("Tunnel"),
    @XmlEnumValue("Shuttle")
    SHUTTLE("Shuttle"),
    @XmlEnumValue("BuilderUnit")
    BUILDER_UNIT("BuilderUnit"),
    @XmlEnumValue("Rock")
    ROCK("Rock"),
    @XmlEnumValue("Granite")
    GRANITE("Granite"),
    @XmlEnumValue("Obsidian")
    OBSIDIAN("Obsidian");
    private final String value;

    ObjectType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ObjectType fromValue(String v) {
        for (ObjectType c: ObjectType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
