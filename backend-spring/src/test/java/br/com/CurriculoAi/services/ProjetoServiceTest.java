package br.com.CurriculoAi.services;

import br.com.CurriculoAi.dto.request.ProjetoDtoRequest;
import br.com.CurriculoAi.dto.response.ProjetoDtoResponse;
import br.com.CurriculoAi.models.Projeto;
import br.com.CurriculoAi.models.Usuario;
import br.com.CurriculoAi.repositories.ProjetoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @InjectMocks
    private ProjetoService projetoService;

    @Test
    void deveLancarExcecaoQuandoListaForVazia(){

        List<ProjetoDtoRequest> projetosParaCadastro = List.of();

        Assertions.assertThrows(IllegalArgumentException.class, () -> projetoService.registerProjects(projetosParaCadastro));
    }

    @Test
    void deveCadastrarNovosProjeto(){

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("pedro@gmail.com");

        var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<ProjetoDtoRequest> projetosParaCadastro = List.of(
                new ProjetoDtoRequest("Java", "java completo", "https://"),
                new ProjetoDtoRequest("Angular", "angular completo", "https://"),
                new ProjetoDtoRequest("Docker", "docker completo", "https://")
        );


        List<Projeto> projetos = List.of(
                new Projeto(),
                new Projeto(),
                new Projeto()
        );

        Mockito.when(projetoRepository.saveAll(Mockito.anyList())).thenReturn(projetos);

        List<ProjetoDtoResponse> projetosSalvos = projetoService.registerProjects(projetosParaCadastro);

        //Verifica se foi executado pelo menos uma vez o salvamento
        Mockito.verify(projetoRepository, Mockito.times(1)).saveAll(Mockito.anyList());

        Assertions.assertEquals(projetosSalvos.size(), projetosParaCadastro.size());
    }
}
