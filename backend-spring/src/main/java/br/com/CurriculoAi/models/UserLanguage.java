package br.com.CurriculoAi.models;

import br.com.CurriculoAi.enums.language.LanguageLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_language")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_language_id")
    private Long userLanguageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "level",nullable = false)
    private LanguageLevel level;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;
}
