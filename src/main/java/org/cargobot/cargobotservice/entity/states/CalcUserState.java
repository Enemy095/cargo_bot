package org.cargobot.cargobotservice.entity.states;

public enum CalcUserState {
    NONE,
    AWAIT_LENGTH,
    AWAIT_WIDTH,
    AWAIT_HEIGHT,
    AWAIT_WEIGHT,
    AWAIT_BOXES,
    CHECK_FRAGILITY,
    CHECK_URGENCY,
}
