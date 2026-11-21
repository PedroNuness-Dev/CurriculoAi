package br.com.CurriculoAi.client.github;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "github", url = "https://api.github.com")
public interface GitHubClient {
    @GetMapping("/users/{username}")
    String profile(@PathVariable String username);

    @GetMapping("/users/{username}/repos?per_page=30&sort=updated&type=owner")
    String repositories(@PathVariable String username, @RequestParam int page);

    @GetMapping("/repos/{username}/{repository}")
    String repository(@PathVariable String username, @PathVariable String repository);

    @GetMapping("/repos/{username}/{repository}/git/trees/{ref}?recursive=1")
    String tree(@PathVariable String username, @PathVariable String repository, @PathVariable String ref);

    @GetMapping("/repos/{username}/{repository}/git/blobs/{sha}")
    String blob(@PathVariable String username, @PathVariable String repository, @PathVariable String sha);
}
