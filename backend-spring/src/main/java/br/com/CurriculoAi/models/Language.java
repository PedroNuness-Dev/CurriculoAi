package br.com.CurriculoAi.models;

import br.com.CurriculoAi.enums.language.LanguageEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "language")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    private Long languageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private LanguageEnum name;
}
