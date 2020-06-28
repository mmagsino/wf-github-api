package wf.github.api.service;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import wf.github.api.model.Commit;
import wf.github.api.model.Committer;
import wf.github.api.model.Contributor;
import wf.github.api.model.OwnerRepo;
import wf.github.api.model.Projection;
import wf.github.api.model.Repository;

@SpringBootTest
public class GithubAnalyticsServiceIntTest {

	@Autowired
	private GithubAnalyticsService service;
	
	@Test
	public void shouldSearchForGithubPublicProjects() {
		List<Repository> repositories = service.searchRepositories("spring");
		assertFalse(repositories.isEmpty());
	}

	@Test
	public void shouldFetchContributorsOfAGivenProject() {
		List<Contributor> contributors = service.contributors(createOwnerRepo());
		assertFalse(contributors.isEmpty());
	}

	@Test
	public void shouldCreateBasicAnalyticsForAGivenProject() {
		Projection projection = service.latestProjection(createOwnerRepo());
		List<Committer> committers = projection.getContributorCommits();
		Map<String, Collection<Commit>> timelineCommits = projection.getTimelineCommits();
		assertFalse(committers.isEmpty());
		assertFalse(timelineCommits.isEmpty());
	}

	@Test
	public void shouldSuggestAProjectBasedOnAQuery() {
		Set<String> suggestions = service.suggests("spring");
		assertFalse(suggestions.isEmpty());
	}

	private OwnerRepo createOwnerRepo() {
		OwnerRepo ownerRepo = new OwnerRepo();
		ownerRepo.setOwner("spring-projects");
		ownerRepo.setRepoName("spring-boot");
		return ownerRepo;
	}
}
