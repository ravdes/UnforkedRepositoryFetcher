package com.ravdes.unforkedrepositories.repofetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ravdes.unforkedrepositories.exceptions.UserNotFoundException;
import com.ravdes.unforkedrepositories.repofetcher.dto.GetRepoRequest;
import com.ravdes.unforkedrepositories.repofetcher.dto.GetRepoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@WireMockTest(httpPort = 8181)
class RepoFetcherServiceIntegrationTest {
	@Autowired
	private RepoFetcherService repoFetcherService;

	@Autowired
	private WebClient.Builder webClientBuilder;


	private final ObjectMapper objectMapper = new ObjectMapper();


	@BeforeEach
	void setUp() {
		WebClient webClient = webClientBuilder.baseUrl("http://localhost:8181").build();
		repoFetcherService = new RepoFetcherService(webClient);
	}


	@Test
	void shouldGetNonForkedRepos() {
		String userReposResponse = "[{\"name\":\"testRepo\",\"fork\":false,\"owner\":{\"login\":\"testuser\"}}]";
		String branchesResponse = "[{\"name\":\"main\",\"commit\":{\"sha\":\"asdcz123\"}}]";

		stubFor(get(urlEqualTo("/users/testuser/repos"))
				.willReturn(okJson(userReposResponse)));

		stubFor(get(urlEqualTo("/repos/testuser/testRepo/branches"))
				.willReturn(okJson(branchesResponse)));

		GetRepoRequest request = new GetRepoRequest("testuser");

		Mono<List<GetRepoResponse>> actualResponse = repoFetcherService.getProjects(request);

		List<GetRepoResponse> parsedResponse = actualResponse.block();

		assertThat(parsedResponse).isNotNull();
		assertThat(parsedResponse.size()).isEqualTo(1);
		assertThat(parsedResponse.get(0).ownerLogin()).isEqualTo("testuser");

		verify(getRequestedFor(urlEqualTo("/users/testuser/repos")));
		verify(getRequestedFor(urlEqualTo("/repos/testuser/testRepo/branches")));

	}

	@Test
	void shouldThrowUserNotFound() {
		stubFor(get(urlEqualTo("/users/unknownuser/repos"))
				.willReturn(notFound()));

		GetRepoRequest request = new GetRepoRequest("unknownuser");


		UserNotFoundException exception = assertThrows(UserNotFoundException.class,
				() -> repoFetcherService.getProjects(request).block());
		assertThrows(UserNotFoundException.class, () -> repoFetcherService.getProjects(request).block());
		assertThat(exception.getMessage()).isEqualTo("User unknownuser doesn't exist");
		verify(getRequestedFor(urlEqualTo("/users/unknownuser/repos")));

	}



	@Test
	void shouldFilterOutForkedRepos() {
		stubFor(get(urlEqualTo("/users/testuser/repos"))
				.willReturn(
						okJson("[{\"name\":\"nonForkedRepo\",\"fork\":false,\"owner\":{\"login\":\"testuser\"}},{\"name\":\"forkedRepo\",\"fork\":true,\"owner\":{\"login\":\"testuser\"}}]")));

		stubFor(get(urlEqualTo("/repos/testuser/nonForkedRepo/branches"))
				.willReturn(okJson("[{\"name\":\"main\",\"commit\":{\"sha\":\"abcd1234\"}}]")));


		GetRepoRequest request = new GetRepoRequest("testuser");

		List<GetRepoResponse> parsedResponse = repoFetcherService.getProjects(request).block();


		assertThat(parsedResponse).isNotNull();
		assertThat(parsedResponse.size()).isEqualTo(1);
		assertThat(parsedResponse.getFirst().repositoryName()).isEqualTo("nonForkedRepo");
		verify(getRequestedFor(urlEqualTo("/users/testuser/repos")));
		verify(getRequestedFor(urlEqualTo("/repos/testuser/nonForkedRepo/branches")));


	}
}