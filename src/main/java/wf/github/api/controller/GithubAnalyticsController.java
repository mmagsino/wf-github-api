package wf.github.api.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import wf.github.api.service.GithubAnalyticsService;

@Controller
@RequestMapping("/")
public class GithubAnalyticsController {
	private final GithubAnalyticsService service;

	@Autowired
	public GithubAnalyticsController(final GithubAnalyticsService service) {
		this.service = service;
	}

	@GetMapping
	public String index(final Model model, 
		final HttpServletRequest request,
		@RequestParam(name = "q", required = false, defaultValue = "") final String q) 
	{
		if (Strings.isNullOrEmpty(q)) {
			model.addAttribute("repositories", Lists.newArrayList());
		} else {
			model.addAttribute("repositories", service.searchRepositories(q));
		}
		model.addAttribute("query", q);
		return "index";
	}

	@GetMapping("/repository")
	public String repository(final Model model, 
		final HttpServletRequest request,
		@RequestParam(name = "q", required = true) final String q,
		@RequestParam(name = "name", required = true) final String name,
		@RequestParam(name = "owner", required = true) final String owner) {
		model.addAttribute("q", q);
		model.addAttribute("name", name);
		model.addAttribute("owner", owner);
		return "repository";
	}

	@GetMapping(path = "suggest", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<String>> suggestion(@RequestParam(name = "q", required = true, defaultValue = "") final String q) {
		if (Strings.isNullOrEmpty(q)) {
			return ResponseEntity.ok(Sets.newHashSet());
		} else {
			return ResponseEntity.ok(service.suggests(q));
		}
		
	}
}
