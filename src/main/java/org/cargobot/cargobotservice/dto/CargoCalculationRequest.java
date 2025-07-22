package org.cargobot.cargobotservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cargobot.cargobotservice.entity.Tariff;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CargoCalculationRequest {
    private Integer length;
    private Integer width;
    private Integer height;
    private Integer distance;
    private Double weight;
    private Double ratio;
    private Double volume;
    private Integer boxCount;
    @JsonProperty("isFragile")
    private boolean isFragile;
    @JsonProperty("withFridge")
    private boolean withFridge;
    private boolean urgency;
    private Tariff tariff;
    private String formula;
}
