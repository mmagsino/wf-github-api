package wf.github.api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import wf.github.api.model.OwnerRepo;
import wf.github.api.model.Projection;
import wf.github.api.model.Repository;
import wf.github.api.service.GithubAnalyticsService;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class GithubAnalyticsApiController {
	private final GithubAnalyticsService service;

	@Autowired
	public GithubAnalyticsApiController(final GithubAnalyticsService service) {
		this.service = service;
	}

	@GetMapping("/repos/search")
	public ResponseEntity<List<Repository>> repositories(
		@RequestParam(name = "q", required = true) final String q) {
		return ResponseEntity.ok(service.searchRepositories(q));
	}

	@GetMapping("/committers/{owner}/{repoName}")
	public ResponseEntity<Set<String>> fetchContributors(
		@PathVariable(name = "owner", required = true) String owner,
		@PathVariable(name = "repoName", required = true) String repoName) {
		OwnerRepo ownerRepo = new OwnerRepo();
		ownerRepo.setOwner(owner);
		ownerRepo.setRepoName(repoName);
		return ResponseEntity.ok(service.contributors(ownerRepo));
	}

	@GetMapping("/committers/{owner}/{repoName}/projection")
	public ResponseEntity<Projection> projection(
		@PathVariable(name = "owner", required = true) String owner,
		@PathVariable(name = "repoName", required = true) String repoName) {
		OwnerRepo ownerRepo = new OwnerRepo();
		ownerRepo.setOwner(owner);
		ownerRepo.setRepoName(repoName);
		return ResponseEntity.ok(service.latestProjection(ownerRepo));
	}
}
