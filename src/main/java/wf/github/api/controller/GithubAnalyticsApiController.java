package wf.github.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import wf.github.api.model.Contributor;
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

	@Operation(operationId = "searchRpository", 
		tags = "WF Exam", 
		summary = "Retrieve search result from Github API.",
		description = "Retrieve search result from Github API.")
	@GetMapping("/repos/search")
	public ResponseEntity<List<Repository>> repositories(
		@RequestParam(name = "q", required = true) final String q) {
		return ResponseEntity.ok(service.searchRepositories(q));
	}

	@Operation(operationId = "retrieveCommitters",
		tags = "WF Exam", 
		summary = "Retrieve Github committers from a given project by owner and repo name.",
		description = "Retrieve Github committers from a given project by owner and repo name.")
	@GetMapping("/committers/{owner}/{repoName}")
	public ResponseEntity<List<Contributor>> fetchContributors(
		@PathVariable(name = "owner", required = true) String owner,
		@PathVariable(name = "repoName", required = true) String repoName) {
		OwnerRepo ownerRepo = new OwnerRepo();
		ownerRepo.setOwner(owner);
		ownerRepo.setRepoName(repoName);
		return ResponseEntity.ok(service.contributors(ownerRepo));
	}

	@Operation(operationId = "retrieveProjectionData",
		tags = "WF Exam",
		summary = "Retrieve Github commits by contibutors on a repo name as projection data.",
		description = "Retrieve Github commits by contibutors on a repo name as projection data.")
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
