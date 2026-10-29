package br.com.CurriculoAi.models;

import br.com.CurriculoAi.enums.language.LanguageEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "language")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    private Long languageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private LanguageEnum name;
}
