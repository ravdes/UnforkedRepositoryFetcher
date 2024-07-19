package com.ravdes.unforkedrepositories.repofetcher;

import com.ravdes.unforkedrepositories.repofetcher.dto.GetRepoRequest;
import com.ravdes.unforkedrepositories.repofetcher.dto.GetRepoResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("github")
class RepoFetcherController {
	private final RepoFetcherService repoFetcherService;

	public RepoFetcherController(RepoFetcherService repoFetcherService) {
		this.repoFetcherService = repoFetcherService;
	}

	@PostMapping("getRepos")
	public Mono<List<GetRepoResponse>> getAllRepos(@RequestBody GetRepoRequest request) {
		return repoFetcherService.getProjects(request);

	}
}
