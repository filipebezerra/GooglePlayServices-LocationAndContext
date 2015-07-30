package com.github.filipebezerra.baseplayservices.events;

import com.squareup.otto.Bus;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 29/07/2015
 * @since #
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
