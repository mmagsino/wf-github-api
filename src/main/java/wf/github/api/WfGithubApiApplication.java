package wf.github.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WfGithubApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WfGithubApiApplication.class, args);
	}

	@Autowired
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
