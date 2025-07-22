package org.cargobot.cargobotservice.service;

import lombok.RequiredArgsConstructor;
import org.cargobot.cargobotservice.dto.TariffDto;
import org.cargobot.cargobotservice.entity.Tariff;
import org.cargobot.cargobotservice.mapper.TariffMapper;
import org.cargobot.cargobotservice.repository.TariffRepository;
import org.cargobot.cargobotservice.repository.cash.Cash;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffMapper tariffMapper;
    private final TariffRepository tariffRepository;
    private final Cash tariffCash;

    public String createTariff(TariffDto tariff) {
        Tariff tariffEntity = tariffMapper.tariffDtoToEntity(tariff);
        Tariff saveTariff = tariffRepository.save(tariffEntity);
        tariffCash.tariffInit();
        return convertTariffIntoFormat(saveTariff);
    }

    public List<Tariff> getTariffsActive() {
        return tariffRepository.findAllByActive(true);
    }

    public Tariff getTariffByName(String name) {
        return tariffRepository.findByName(name);
    }

    public void deleteTariffByName(String name) {
        tariffRepository.deleteTariffByName(name);
    }


    public String formatTariffs(List<Tariff> tariffs) {
        if (tariffs.isEmpty()) {
            return "⛔ Активные тарифы отсутствуют";
        }

        StringBuilder sb = new StringBuilder("📋 *Список активных тарифов:*\n\n");
        for (Tariff tariff : tariffs) {
            sb.append(convertTariffIntoFormat(tariff));
        }
        return sb.toString();
    }

    public String convertTariffIntoFormat(Tariff tariff) {
        return String.format("""
                        
                        🏷️ *%s*
                        ▫️ Статус: %S
                        ▫️ Ставка за кг: %.2f USD 💵
                        ▫️ Мин. цена: %.2f USD 💵
                        ▫️ Конверсия объема: %.2f
                        ▫️ Надбавка за хрупкость: %.2f %% 🍸
                        ▫️ Надбавка за срочность: %.2f %% 🏎
                        ➖➖➖➖➖➖➖➖➖➖
                        """,
                tariff.getName(),
                tariff.isActive() ? "✅ Активен" : "⛔ Неактивен",
                tariff.getKgRate(),
                tariff.getMinPrice(),
                tariff.getCubicConversion(),
                tariff.getFragility(),  // Преобразуем в проценты
                tariff.getUrgency());
    }

//    public void createTariff(TariffDto tariffDto){
//        Tariff tariff = tariffMapper.tariffToEntity(tariffDto);
//        tariffRepository.save(tariff);
//    }
//
//    public void deleteTariff(TariffDto tariffDto){
//        Tariff tariff = tariffMapper.tariffToEntity(tariffDto);
//        tariffRepository.delete(tariff);
//    }
//
//    public void changeActive(TariffDto tariffDto){
//        Tariff tariff = tariffRepository.findByName(tariffDto.getTariffName());
//        tariff.setActive(tariffDto.isActive());
//    }
}
