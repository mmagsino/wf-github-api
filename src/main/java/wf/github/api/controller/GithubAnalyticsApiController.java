package wf.github.api.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import wf.github.api.model.Commit;
import wf.github.api.model.Repository;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class GithubAnalyticsApiController {

	@GetMapping("/search")
	public ResponseEntity<List<Repository>> repositories() {
		return ResponseEntity.ok(Lists.newArrayList());
	}

	@GetMapping("/commits")
	public ResponseEntity<List<Commit>> commits() {
		return ResponseEntity.ok(Lists.newArrayList());
	}
}
