package br.com.CurriculoAi.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "certification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private Long certificationId;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "finish_date")
    private LocalDate finishDate;

    @Column(name = "certificate", columnDefinition = "BYTEA")
    private byte[] certificate;

    @Builder.Default
    @Column(name = "in_progress")
    private Boolean inProgress = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
