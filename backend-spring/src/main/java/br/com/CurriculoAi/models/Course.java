package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "name", nullable = false)
    private String name;
}
