package wf.github.api.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import wf.github.api.UrlConfig;
import wf.github.api.model.Repository;

@Service
@Slf4j
public class GithubAnalyticsServiceImpl implements GithubAnalyticsService {
	private static final String REPO_QUERY_FMT = "%s?q=%s&sort=stars&order=desc";

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

}
