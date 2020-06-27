package wf.github.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class UrlConfig {
	@Value("${wf.github.api}")
	private String githubApi;
	
	@Value("${wf.github.api.search-repo}")
	private String searchRepoEndpoint;

	public String githubApiRepoEndpoint() {
		return String.format("%s%s", githubApi, searchRepoEndpoint);
	}
}
