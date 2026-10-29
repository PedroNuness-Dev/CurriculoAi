package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "user_social_network")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
