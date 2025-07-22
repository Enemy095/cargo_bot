package org.cargobot.cargobotservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TariffConfig {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private double kgRate;
    private double cubicConversion;
}
