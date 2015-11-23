package hu.jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;
import hu.jmx.javachallenge.service.Service;

/**
 * Created by joci on 11/19/15.
 */
public interface Strategy {
    Service service = Service.getInstance();
    WsCoordinate nextCoordinate();
}
