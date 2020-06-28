package wf.github.api.service;

import java.util.List;
import java.util.Set;

import wf.github.api.model.OwnerRepo;
import wf.github.api.model.Projection;
import wf.github.api.model.Repository;

public interface GithubAnalyticsService {
	Set<String> suggests(String query);
	List<Repository> searchRepositories(String query);
	Set<String> contributors(OwnerRepo ownerRepo);
	Projection latestProjection(OwnerRepo ownerRepo);
}
