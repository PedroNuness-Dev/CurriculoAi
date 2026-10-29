package br.com.CurriculoAi.utils.services;

import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.repositories.UsuarioCadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UsuarioUtils {

    private final UsuarioCadRepository usuarioCadRepository;

    //Busca o usuário através do JWT
    public User me(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null){

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticadp");
        }

        Object principal = authentication.getPrincipal();
        User userAutenticado;

        if (principal instanceof User){
            userAutenticado = (User) principal;
        }
        else if (principal instanceof UserDetails){

            String email = ((UserDetails) principal).getUsername();

            userAutenticado = usuarioCadRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        User userBuscado = usuarioCadRepository.findById(userAutenticado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return userBuscado;
    }
}
