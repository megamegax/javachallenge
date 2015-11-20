package jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;
import jmx.javachallenge.service.Service;

/**
 * Created by joci on 11/19/15.
 */
@FunctionalInterface
public interface Strategy {
    Service service = Service.getInstance();
    WsCoordinate nextCoordinate();

}
