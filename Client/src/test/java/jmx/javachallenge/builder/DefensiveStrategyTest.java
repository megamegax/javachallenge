/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;
import org.junit.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author joci
 */
public class DefensiveStrategyTest {
    private Strategy s;

    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        s = new DefensiveStrategy(0);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of nextCoordinate method, of class DefensiveStrategy.
     */
    @Test
    public void testCorrectOrder() {
        int until = 25;
        List<WsCoordinate> result = IntStream.range(0,until).mapToObj(i -> s.nextCoordinate()).collect(Collectors.toList());
        result.forEach(System.out::println);
    }
    
}
