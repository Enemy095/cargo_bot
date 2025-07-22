package org.cargobot.cargobotservice.dto.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cargobot.cargobotservice.entity.states.DeleteTariffState;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TariffDeleteSession {
    private Long chatId;
    private UUID tariffId;
    private String tariffName;
    private DeleteTariffState deleteState;
}
