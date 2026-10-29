package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_curriculum")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCurriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_curriculum_id")
    private Long userCurriculumId;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    //usar apenas no h2
    //@Column(name = "curriculum", nullable = false)
    // usar em prod
    @Column(name = "curriculum", nullable = false, columnDefinition = "OID")
    private byte[] curriculum;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}