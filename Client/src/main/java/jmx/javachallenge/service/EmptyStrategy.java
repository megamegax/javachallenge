package jmx.javachallenge.service;

import eu.loxon.centralcontrol.WsCoordinate;
import jmx.javachallenge.builder.JMXBuilder;
import jmx.javachallenge.builder.Strategy;

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
