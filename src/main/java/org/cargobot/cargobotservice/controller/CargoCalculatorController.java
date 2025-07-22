package org.cargobot.cargobotservice.controller;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.dto.CargoCalculationRequest;
import org.cargobot.cargobotservice.service.CalculationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/calculator")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CargoCalculatorController {

    private final CalculationService calculationService;

    @PostMapping("/calculate")
    public String createOrder(@RequestBody CargoCalculationRequest cargoCalculationRequest) {

        calculationService.calculatePrice(cargoCalculationRequest);
        return "Success!!";
    }

}
