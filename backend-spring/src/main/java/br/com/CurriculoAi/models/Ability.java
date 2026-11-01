package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "ability")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ability_id")
    private Long abilityId;

    @Column(name = "name", nullable = false)
    private String name;
}
