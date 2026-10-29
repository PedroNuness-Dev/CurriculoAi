package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "ability")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ability_id")
    private Long abilityId;

    @Column(name = "name", nullable = false)
    private String name;
}
