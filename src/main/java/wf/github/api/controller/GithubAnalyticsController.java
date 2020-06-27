package wf.github.api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/")
public class GithubAnalyticsController {

	@GetMapping
	public String index() {
		return "index";
	}

	@GetMapping(path = "suggest", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<String>> suggestion(@RequestParam(name = "q", required = true, defaultValue = "") final String q) {
		log.info("Suggestion: ", q);
		if (!Strings.isNullOrEmpty(q)) {
			
		}
		return ResponseEntity.ok(Sets.newHashSet());
	}
}
