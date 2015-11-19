package jmx.javachallenge.builder;

import eu.loxon.centralcontrol.WsCoordinate;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by joci on 11/19/15.
 * A defenzív algoritmussal szemben itt távoli pontok lesznek kijelölve,
 * azokhoz közelít a builder, ha eléri, csinál valamit,
 * esetleg spéci null értéket ad vissza, jelezve a buildernek,
 * hogy váltson másik stratégiára (defenzív?),
 * vagy az explorer kijelölhet egy újabb távoli pontot (pókháló jellegű bejárás)
 */
//TODO statikus listában tárolni a többi explorer strategy célpontját
//és az új célpontokat úgy kéne kikalkulálni, hogy az előző pontokhoz
// képest a távolságuk maximális legyen
public class ExplorerStrategy implements Strategy {
    @Override
    public WsCoordinate nextCoordinate() {
        return null;
    }
}
