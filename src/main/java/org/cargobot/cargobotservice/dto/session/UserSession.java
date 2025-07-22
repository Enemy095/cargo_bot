package org.cargobot.cargobotservice.dto.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cargobot.cargobotservice.entity.BaseEntity;
import org.cargobot.cargobotservice.entity.states.CalcUserState;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSession extends BaseEntity {
    private Long chatId;
    private Integer length;
    private Integer width;
    private Integer height;
    private Double weight;
    private Integer quantity;
    private boolean fragile;
    private boolean urgency;
    private CalcUserState state;
}
