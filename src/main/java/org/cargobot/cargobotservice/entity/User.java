package org.cargobot.cargobotservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cargobot.cargobotservice.entity.states.CalcUserState;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
@Entity
public class User extends BaseEntity {

    private String chatId;

//    private String role; // "ADMIN" или "USER"
}


