package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "user_social_network")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSocialNetwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_social_network_id")
    private Long userSocialNetworkId;

    private String link;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "social_network_id", nullable = false)
    private SocialNetwork socialNetwork;
}
