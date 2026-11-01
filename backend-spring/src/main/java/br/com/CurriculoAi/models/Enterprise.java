package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enterprise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enterprise_id")
    private Long enterpriseId;

    @Column(name = "name", nullable = false)
    private String name;
}
