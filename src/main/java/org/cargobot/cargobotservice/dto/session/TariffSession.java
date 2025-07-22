package org.cargobot.cargobotservice.dto.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cargobot.cargobotservice.entity.states.AdminTariffState;
import org.cargobot.cargobotservice.entity.states.DeleteTariffState;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TariffSession {

    private Long chatId;
    private UUID tariffId;
    private String tariffName;
    private Double kgRate;
    private Double minPrice;
    private Double cubicConversion;
    private Double pricePerCubicMeter;
    private Double fragility;
    private Double urgency;
    private AdminTariffState state;
    private boolean active;

}
