package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "social_network")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialNetwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_network_id")
    private Long socialNetworkId;

    @Column(name = "name", nullable = false)
    private String name;
}
