package org.cargobot.cargobotservice.dto;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CargoCalculationResult {
    private Double totalWeight;
    private Double totalVolume;
    private Double cost;
}
