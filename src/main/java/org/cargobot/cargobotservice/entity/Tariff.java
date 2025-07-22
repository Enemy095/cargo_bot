package org.cargobot.cargobotservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tariff")
@Entity
public class Tariff extends BaseEntity{

    @Column(unique = true, nullable = false)
    private String name;
    private Double kgRate;
    private Double minPrice;
    private Double cubicConversion;
    private Double fragility;
    private Double urgency;

    @Builder.Default
    private boolean active = true;
}
