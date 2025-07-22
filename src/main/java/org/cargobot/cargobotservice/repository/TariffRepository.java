package org.cargobot.cargobotservice.repository;

import org.cargobot.cargobotservice.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, UUID> {
    Tariff findByName(String name);

    List<Tariff> findAllByActive(Boolean active);

    void deleteTariffByName(String name);
}
