package org.cargobot.cargobotservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TariffDto {

    private String name;
    private Double kgRate;
    private Double minPrice;
    private Double cubicConversion;
    private Double fragility;
    private Double urgency;
    private boolean active;
}
