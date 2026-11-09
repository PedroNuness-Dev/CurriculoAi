package br.com.CurriculoAi.models;

import br.com.CurriculoAi.enums.careergoal.GoalType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "career_goal")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CareerGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_goal_id")
    private Long careerGoalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false, length = 50)
    private GoalType goalType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @Column(name = "description", length = 1000)
    private String description;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}