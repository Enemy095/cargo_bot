package org.cargobot.cargobotservice.entity.states;


public enum AdminTariffState {
    NONE,
    AWAIT_TARIFF_NAME,
    AWAIT_KG_RATE,
    AWAIT_MIN_PRICE,
    AWAIT_CUBIC_CONVERSION,
    AWAIT_FRAGILITY,
    AWAIT_URGENCY,
    AWAIT_ACTIVE,
}
