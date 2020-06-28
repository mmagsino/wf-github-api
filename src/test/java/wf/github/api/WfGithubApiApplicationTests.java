package wf.github.api;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import wf.github.api.model.Commit;

@SpringBootTest
class WfGithubApiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void searchPublicGithubRepositories() throws JsonMappingException, JsonProcessingException {
		final String searchEndpoint = "https://api.github.com/search/repositories?q=%s&sort=stars&order=desc";
		final String query = "spring";
		final RestTemplate restTemplate = new RestTemplate();
		final String content = restTemplate.getForObject(String.format(searchEndpoint, query), String.class);

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode rootNode = mapper.readTree(content);
		final JsonNode items = rootNode.path("items");
		final Iterator<JsonNode> nodes = items.elements();
		while (nodes.hasNext()) {
			JsonNode childNode = nodes.next();
			JsonNode name = childNode.path("name");
			JsonNode fullName = childNode.path("full_name");
			JsonNode htmlUrl = childNode.path("html_url");
			JsonNode desc = childNode.path("description");
			JsonNode owner = childNode.path("owner").path("login");
			System.out.println(">> ["+owner.asText()+"] <<"); 
			System.out.println(name.asText() + " = " + fullName.asText());
		}
	}

	@Test
	public void findRepoByOwnerAndName() throws JsonMappingException, JsonProcessingException {
		//GET /repos/:owner/:repo
		final String owner = "spring-projects";
		final String repo = "spring-boot";
		final String endpoint = "https://api.github.com/repos/%s/%s";
		final RestTemplate restTemplate = new RestTemplate();
		final String content = restTemplate.getForObject(String.format(endpoint, owner, repo), String.class);

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode rootNode = mapper.readTree(content);
		System.out.println(rootNode);
	}

	@Test
	public void findAllCommittersForACertainProject() throws JsonMappingException, JsonProcessingException {
		//GET /repos/:owner/:repo/contributors
		final String owner = "spring-projects";
		final String repo = "spring-boot";
		final String endpoint = "https://api.github.com/repos/%s/%s/contributors";
		final RestTemplate restTemplate = new RestTemplate();
		final String content = restTemplate.getForObject(String.format(endpoint, owner, repo), String.class);

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode rootNode = mapper.readTree(content);
		final Iterator<JsonNode> commiters = rootNode.iterator();
		while (commiters.hasNext()) {
			JsonNode commiter = commiters.next();
			JsonNode login = commiter.path("login");
			System.out.println(login.asText());
		}
		System.out.println(rootNode);
	}

	@Test
	public void findLatestCommitsForACertainProject() throws JsonMappingException, JsonProcessingException {
		//https://api.github.com/repos/spring-projects/spring-boot/commits?since=2020-04-25T09:33:07Z
		final String owner = "spring-projects";
		final String repo = "spring-boot";
		final Multimap<String, Commit> groupByCommitters = groupBy(owner, repo);
		groupByCommitters.keySet().forEach(key -> {
			Collection<Commit> items = groupByCommitters.get(key);
			System.out.println("["+key+"] "+items.size());
			items.forEach(c -> System.out.println(c));
		});
	}

	@Test
	public void generateDataTimelineBasedOn() {
		
	}
	
	private Multimap<String, Commit> groupBy(String owner, String repo) throws JsonMappingException, JsonProcessingException {
		final String endpoint = "https://api.github.com/repos/%s/%s/commits?per_page=100";
		final RestTemplate restTemplate = new RestTemplate();
		final String content = restTemplate.getForObject(String.format(endpoint, owner, repo), String.class);
		final Multimap<String, Commit> groupByCommitters = ArrayListMultimap.create();
		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode rootNode = mapper.readTree(content);
		final Iterator<JsonNode> commits = rootNode.iterator();
		while (commits.hasNext()) {
			final JsonNode node = commits.next();
			final JsonNode sha = node.path("sha");
			final JsonNode committer = node.path("author").path("login");
			final JsonNode date = node.path("commit").path("committer").path("date");
			final Commit commit = new Commit();
			commit.setDate(new Date(LocalDateTime
					.parse(date.asText(), DateTimeFormatter.ISO_DATE_TIME)
					.atZone(ZoneId.systemDefault())
					.toEpochSecond() * 1000));
			commit.setSha(sha.asText());
			groupByCommitters.put(committer.asText(), commit);
		}
		return groupByCommitters;
	}
}
