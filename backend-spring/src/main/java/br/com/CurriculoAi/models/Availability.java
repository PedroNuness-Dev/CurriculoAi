package br.com.CurriculoAi.models;

import br.com.CurriculoAi.enums.availability.WorkModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "availability")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private Long availabilityId;

    @Column(name = "start_availability")
    private LocalDate startAvailability;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_model", nullable = false)
    private WorkModel workModel;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
