package br.com.CurriculoAi.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "usuario")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nome", nullable = false, length = 80)
    private String nome;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @ManyToOne
    @JoinColumn(name = "id_area")
    private Area area;

    @Builder.Default
    @OneToMany(mappedBy = "usuarioCad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FormacaoUser> formacoes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "usuario")
    private List<IdiomasUser> idiomas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "usuario")
    private List<UsuarioRedeSocial> redeSocials = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "usuario")
    private List<UsuarioHabilidade> habilidades = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_user",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_role")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "usuarioCad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<ExperienciaUser> experiencias = new ArrayList<>();

    @OneToMany(mappedBy = "usuarioCad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<CertificacaoUser> certificacoes = new ArrayList<>();

    @OneToOne(mappedBy = "usuarioCad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DisponibilidadeUser disponibilidade;

    @Builder.Default
    @OneToMany(mappedBy = "usuario", fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CurriculoUsuario> curriculos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Projeto> projetos = new ArrayList<>();

    //Verifica a quantidade de curriculos do usuário, se for igual a 3 (ou maior por precaução) retorna true
    //caso false, o usuário pode ter curriculos cadastrados
    public boolean verificarLimitadorDeCurriculos(){
        return curriculos.size() >= 3;
    }
}
