package com.ravdes.unforkedrepositories.repofetcher;

import com.ravdes.unforkedrepositories.repofetcher.dto.BranchData;
import com.ravdes.unforkedrepositories.repofetcher.dto.Commit;
import com.ravdes.unforkedrepositories.repofetcher.dto.GetRepoRequest;
import com.ravdes.unforkedrepositories.repofetcher.dto.GetRepoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(RepoFetcherController.class)
class RepoFetcherControllerIntegrationTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private RepoFetcherService repoFetcherService;

	@Test
	void getAllReposSuccess() {

		List<BranchData> testBranchData = new ArrayList<>();
		Commit commit = new Commit("asda12ecxsha");
		testBranchData.add(new BranchData("firstBranch", commit));
		GetRepoResponse mockResponse = new GetRepoResponse("nana", "login", testBranchData);
		when(repoFetcherService.getProjects(any(GetRepoRequest.class)))
				.thenReturn(Mono.just(List.of(mockResponse)));



		webTestClient.post().uri("/github/getRepos")
					 .contentType(MediaType.APPLICATION_JSON)
					 .bodyValue(new GetRepoRequest("ravdes"))
					 .exchange()
					 .expectStatus().isOk()
					 .expectBodyList(GetRepoResponse.class).hasSize(1);
	}

	@Test
	void getAllReposEmptyList() {
		when(repoFetcherService.getProjects(any(GetRepoRequest.class)))
				.thenReturn(Mono.just(Collections.emptyList()));

		webTestClient.post().uri("/github/getRepos")
					 .contentType(MediaType.APPLICATION_JSON)
					 .bodyValue(new GetRepoRequest("ravdes"))
					 .exchange()
					 .expectStatus().isOk()
					 .expectBodyList(GetRepoResponse.class).hasSize(0);
	}

}