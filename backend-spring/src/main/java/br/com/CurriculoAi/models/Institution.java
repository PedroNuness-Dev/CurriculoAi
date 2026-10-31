package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "institution")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institution_id")
    private Long institutionId;

    @Column(name = "name", nullable = false)
    private String name;
}
