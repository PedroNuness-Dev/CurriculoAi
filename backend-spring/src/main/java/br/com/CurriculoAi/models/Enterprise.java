package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enterprise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enterprise_id")
    private Long enterpriseId;

    @Column(name = "name", nullable = false)
    private String name;
}
