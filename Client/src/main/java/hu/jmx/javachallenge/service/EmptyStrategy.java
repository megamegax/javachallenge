package hu.jmx.javachallenge.service;

import eu.loxon.centralcontrol.WsCoordinate;
import hu.jmx.javachallenge.builder.JMXBuilder;
import hu.jmx.javachallenge.builder.Strategy;

/**
 * Created by Márton on 2015. 11. 23..
 */
public class EmptyStrategy implements Strategy {
    private final JMXBuilder builderUnit;

    public EmptyStrategy(int unitID) {
        this.builderUnit = service.builderUnits.get(unitID);
    }

    @Override
    public WsCoordinate nextCoordinate() {
        return builderUnit.getCord();
    }
}
