package com.ravdes.unforkedrepositories.repofetcher;

import com.ravdes.unforkedrepositories.exceptions.UserNotFoundException;
import com.ravdes.unforkedrepositories.repofetcher.dto.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
class RepoFetcherService {
	private static final Logger logger = LoggerFactory.getLogger(RepoFetcherService.class);

	private final WebClient webClient;

	public RepoFetcherService(WebClient webClient) {
		this.webClient = webClient;
	}


	public Mono<List<GetRepoResponse>> getProjects(GetRepoRequest request) {
		logger.info("Fetching non forked repositories for user : {}", request.nickname());

		return webClient.get()
						.uri(uriBuilder -> uriBuilder
								.path("/users/{username}/repos")
								.build(request.nickname()))
						.retrieve()
						.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
								Mono.error(new UserNotFoundException(String.format("User %s doesn't exist", request.nickname()))))
						.bodyToFlux(RepositoryData.class)
						.filter(repositoryData -> !repositoryData.fork())
						.flatMap(repositoryData -> getBranchesAndSHAs(repositoryData)
								.collectList()
								.map(branchDataList -> new GetRepoResponse(repositoryData.name(), repositoryData.owner().login(), branchDataList)))
						.collectList();



	}

	private Flux<BranchData> getBranchesAndSHAs(RepositoryData repositoryData) {
		logger.info("Fetching branches and SHA for repo : {}", repositoryData.name());
		Owner owner = repositoryData.owner();
		return webClient.get()
						.uri(uriBuilder -> uriBuilder
						.path("repos/{username}/{repo}/branches")
						.build(owner.login(), repositoryData.name()))
						.exchangeToFlux(res -> res.bodyToFlux(BranchData.class));


	}


}
