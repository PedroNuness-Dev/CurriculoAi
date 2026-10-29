package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projeto")
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;
    @Column(name = "descricao", nullable = false)
    private String descricao;
    @Column(name = "link_repositorio", nullable = false)
    private String link;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
