package br.com.CurriculoAi.models;

import br.com.CurriculoAi.enums.formation.FormationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "formation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "formation_id")
    private Long formationId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "formation_type", nullable = false, length = 150)
    private FormationType formationType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "finish_date")
    private LocalDate finishDate;

    @Builder.Default
    @Column(name = "in_progress")
    private Boolean inProgress = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

