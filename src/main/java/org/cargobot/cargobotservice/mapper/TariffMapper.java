package org.cargobot.cargobotservice.mapper;

import org.cargobot.cargobotservice.dto.TariffDto;
import org.cargobot.cargobotservice.entity.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

    TariffDto tariffToDto(Tariff tariff);

    Tariff tariffDtoToEntity(TariffDto tariffDto);

    List<TariffDto> entityToTariffDto(List<Tariff> tariffs);

}
