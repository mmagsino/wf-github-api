package wf.github.api.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import wf.github.api.UrlConfig;
import wf.github.api.model.Commit;
import wf.github.api.model.Committer;
import wf.github.api.model.OwnerRepo;
import wf.github.api.model.Projection;
import wf.github.api.model.Repository;

@Service
@Slf4j
public class GithubAnalyticsServiceImpl implements GithubAnalyticsService {
	private static final String REPO_QUERY_FMT = "%s?q=%s&sort=stars&order=desc";
	private static final int MILLISECOND = 1000;
	private final ObjectMapper mapper;
	private final UrlConfig config;
	private final RestTemplate restTemplate;

	@Autowired
	public GithubAnalyticsServiceImpl(final ObjectMapper mapper, final UrlConfig config, final RestTemplate restTemplate) {
		this.mapper = mapper;
		this.config = config;
		this.restTemplate = restTemplate;
	}

	@Override
	public Set<String> suggests(String query) {
		String content = restTemplate.getForObject(String.format(REPO_QUERY_FMT,config.githubApiRepoEndpoint(), query), String.class);
		Set<String> suggestions = Sets.newTreeSet();
		try {
			JsonNode rootNode = mapper.readTree(content);
			JsonNode items = rootNode.path("items");
			Iterator<JsonNode> nodes = items.elements();
			while (nodes.hasNext()) {
				JsonNode childNode = nodes.next();
				JsonNode name = childNode.path("name");
				suggestions.add(name.asText());
			}	
		} catch (final Exception e) {
			log.error("Error while giving repo suggestion. {}", e);
		}
		return suggestions;
	}

	@Override
	public List<Repository> searchRepositories(String query) {
		String content = restTemplate.getForObject(String.format(REPO_QUERY_FMT,config.githubApiRepoEndpoint(), query), String.class);
		List<Repository> repositories = Lists.newArrayList();
		try {
			JsonNode rootNode = mapper.readTree(content);
			JsonNode items = rootNode.path("items");
			Iterator<JsonNode> nodes = items.elements();
			while (nodes.hasNext()) {
				Repository repo = new Repository();
				JsonNode childNode = nodes.next();
				repo.setName(childNode.path("name").asText());
				repo.setFullName(childNode.path("full_name").asText());
				repo.setDescription(childNode.path("description").asText());
				repo.setUrl(childNode.path("html_url").asText());
				repo.setOwner(childNode.path("owner").path("login").asText());
				repositories.add(repo);
			}
		} catch (final Exception e) {
			log.error("Error while searching repo. {}", e);
		}
		log.info("{}", repositories);
		return repositories;
	}

	@Override
	public Set<String> contributors(OwnerRepo ownerRepo) {
		Set<String> contributors = Sets.newTreeSet();
		String content = restTemplate.getForObject(String.format("%srepos/%s/%s/contributors", 
			config.getGithubApi(),
			ownerRepo.getOwner(),
			ownerRepo.getRepoName()), String.class);
		try {
			final JsonNode rootNode = mapper.readTree(content);
			final Iterator<JsonNode> commiters = rootNode.iterator();
			while (commiters.hasNext()) {
				JsonNode commiter = commiters.next();
				contributors.add(commiter.path("login").asText());
			}
		} catch (final Exception e) {
			log.error("An error occured while fetching commiters. {}", e);
		}
		return contributors;
	}

	@Override
	public Projection latestProjection(OwnerRepo ownerRepo) {
		Multimap<String, Commit> groupings = groupByCommitters(ownerRepo);
		if (groupings.isEmpty()) {
			return Projection.empty();
		} else {
			List<Committer> commiters = Lists.newArrayList();
			groupings.keySet().forEach(key -> {
				Collection<Commit> commits = groupings.get(key);
				Committer committer = new Committer();
				committer.setName(key);
				committer.setNumOfCommits(commits.size());
				commiters.add(committer);
			});
			Collections.sort(commiters, Comparator
				.comparing(Committer::getNumOfCommits)
				.reversed());
			Projection projection = new Projection();
			projection.setContributorCommits(commiters);
			projection.setTimelineCommits(groupings.asMap());
			return projection;
		}
	}

	private Multimap<String, Commit> groupByCommitters(OwnerRepo ownerRepo) {
		Multimap<String, Commit> groupings = ArrayListMultimap.create();
		String content = restTemplate.getForObject(String.format("%srepos/%s/%s/commits?per_page=%d", 
			config.getGithubApi(),
			ownerRepo.getOwner(),
			ownerRepo.getRepoName(),
			config.getNumOfCommits()), String.class);
		try {
			JsonNode rootNode = mapper.readTree(content);
			Iterator<JsonNode> commits = rootNode.iterator();
			while (commits.hasNext()) {
				JsonNode node = commits.next();
				JsonNode sha = node.path("sha");
				JsonNode committer = node.path("author").path("login");
				JsonNode date = node.path("commit").path("committer").path("date");
				Commit commit = new Commit();
				commit.setDate(new Date(LocalDateTime
					.parse(date.asText(), DateTimeFormatter.ISO_DATE_TIME)
					.atZone(ZoneId.systemDefault())
					.toEpochSecond() * MILLISECOND));
				commit.setSha(sha.asText());
				groupings.put(committer.asText(), commit);
			}
		} catch (final Exception e) {
			log.error("An error occured while building projection. {}", e);
		}
		return groupings;
	}
}
