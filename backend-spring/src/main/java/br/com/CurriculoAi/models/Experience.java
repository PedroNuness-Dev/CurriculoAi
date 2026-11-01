package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "experience")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experience_id")
    private Long experienceId;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private Enterprise enterprise;

    @Column(name= "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "finish_date")
    private LocalDate finishDate;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Builder.Default
    @Column(name = "work_current")
    private Boolean workCurrent = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
