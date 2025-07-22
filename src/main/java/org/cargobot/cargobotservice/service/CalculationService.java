package org.cargobot.cargobotservice.service;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.dto.CargoCalculationRequest;
import org.cargobot.cargobotservice.dto.CargoCalculationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculationService {
    private static final int CUBIC_METER_IN_CM = 1000000;
    private static final int PERCENT = 100;

    @Value("${factor.volumetric_weight}")
    private int factorVolumetricWeight;

    public CargoCalculationResult calculatePrice(CargoCalculationRequest cargo) {
//        double volume = cargo.getLengthCm() * cargo.getHeightCm() * cargo.getWidthCm();
//        double volumeM3 = volume / CUBIC_METER_IN_CM;
//
//        double totalVolume = volumeM3 * cargo.getBoxCount();
//        double totalWeight = cargo.getWeightKg() * cargo.getBoxCount();
//
//        double priceByVolume = totalVolume * cargo.getTariff().getCubicConversion();
//        double priceByWeight = totalWeight * cargo.getTariff().getKgRate();
//
//        double price = Math.max(priceByVolume, priceByWeight);
//
//        return CargoCalculationResult.builder()
//                .totalWeight(totalWeight)
//                .totalVolume(totalVolume)
//                .cost(Math.max(price, cargo.getTariff().getMinPrice()))
//                .build();


        double volume = cargo.getLength() * cargo.getHeight() * cargo.getWidth();
        double volumetricWeight = volume / cargo.getTariff().getCubicConversion();

        double volumeM3 = volume / CUBIC_METER_IN_CM;
        double totalVolume = volumeM3 + cargo.getBoxCount();

        double totalVolumetricWeight = volumetricWeight * cargo.getBoxCount();
        double totalWeight = cargo.getWeight() * cargo.getBoxCount();

        double priceByVolumetricWeight = totalVolumetricWeight * cargo.getTariff().getKgRate();
        double priceByWeight = totalWeight * cargo.getTariff().getKgRate();

        double price = Math.max(priceByVolumetricWeight, priceByWeight);
        double cost = Math.max(price, cargo.getTariff().getMinPrice());
        if (cargo.isFragile()) {
            cost = cost + (cost * 100 / cargo.getTariff().getFragility());
        }
        if (cargo.isUrgency()) {
             cost = cost + (cost * 100 / cargo.getTariff().getUrgency());
        }

        return CargoCalculationResult.builder()
                .totalWeight(totalWeight)
                .totalVolume(totalVolume)
                .cost(Math.max(cost, cargo.getTariff().getMinPrice()))
                .build();

        /**
         * double volume = cargo.getLengthCm() * cargo.getHeightCm() * cargo.getWidthCm();
         * double volumetricWeight = volume * factorVolumetricWeight;
         *
         * double totalVolumetricWeight = volumetricWeight * cargo.getBoxCount();
         * double totalWeight = cargo.getWeightKg() * cargo.getBoxCount();
         *
         * double priceByVolumetricWeight = totalVolume * tariff.getCubicConversion();
         * double priceByWeight = totalWeight * tariff.getKgRate();
         *
         * double price = Math.max(priceByVolume, priceByWeight);
         * return Math.max(price, tariff.getMinPrice());
         */
    }
}
