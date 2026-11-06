package br.com.CurriculoAi.models;

import br.com.CurriculoAi.enums.careergoal.GoalType;
import br.com.CurriculoAi.enums.evaluation.EvaluationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_evaluation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long evaluationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", length = 50)
    private GoalType goalType;

    @Column(name = "goal_fit_score", precision = 5, scale = 2)
    private BigDecimal goalFitScore;

    @Column(name = "overall_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "formation_score", precision = 5, scale = 2)
    private BigDecimal formationScore;

    @Column(name = "experience_score", precision = 5, scale = 2)
    private BigDecimal experienceScore;

    @Column(name = "ability_score", precision = 5, scale = 2)
    private BigDecimal abilityScore;

    @Column(name = "certification_score", precision = 5, scale = 2)
    private BigDecimal certificationScore;

    @Column(name = "project_score", precision = 5, scale = 2)
    private BigDecimal projectScore;

    @Column(name = "language_score", precision = 5, scale = 2)
    private BigDecimal languageScore;

    @Column(name = "completeness_score", precision = 5, scale = 2)
    private BigDecimal completenessScore;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "strengths", columnDefinition = "jsonb")
    private List<String> strengths;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "weaknesses", columnDefinition = "jsonb")
    private List<String> weaknesses;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "suggestions", columnDefinition = "jsonb")
    private List<String> suggestions;

    @Column(name = "ai_model", nullable = false, length = 100)
    private String aiModel;

    @Column(name = "rubric_version", nullable = false, length = 20)
    private String rubricVersion;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EvaluationStatus status;

    @Builder.Default
    @Column(name = "is_current", nullable = false)
    private Boolean current = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}