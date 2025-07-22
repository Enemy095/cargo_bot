package org.cargobot.cargobotservice.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cargobot.cargobotservice.entity.states.UserState;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserSession extends BaseEntity {
    private Long chatId;
    private Integer length;
    private Integer width;
    private Integer height;
    private Double weight;
    private Integer quantity;
    private boolean fragile;
    private boolean urgency;
    private UserState state;
}
