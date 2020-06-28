package wf.github.api.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

import wf.github.api.model.Commit;
import wf.github.api.model.Committer;
import wf.github.api.model.DataPoint;
import wf.github.api.model.OwnerRepo;
import wf.github.api.model.Projection;
import wf.github.api.model.TimeStampDatapoint;
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
		OwnerRepo ownerRepo = new OwnerRepo();
		ownerRepo.setOwner(owner);
		ownerRepo.setRepoName(name);
		model.addAttribute("q", q);
		model.addAttribute("ownerRepo", ownerRepo);
		model.addAttribute("contribs", service.contributors(ownerRepo));
		Projection projection = service.latestProjection(ownerRepo);
		List<DataPoint> dataPoints = projection.getContributorCommits()
			.stream()
			.map(contrib -> {
				DataPoint dp = new DataPoint();
				dp.setLabel(contrib.getName());
				dp.setY(contrib.getNumOfCommits());
				return dp;
			})
			.collect(Collectors.toList());
		model.addAttribute("committersActivity", dataPoints);
		Map<String, Collection<Commit>> commits = projection.getTimelineCommits();
		model.addAttribute("timestamps", projectionsDatapoint(commits));
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

	private List<TimeStampDatapoint> projectionsDatapoint(Map<String, Collection<Commit>> commits) {
		List<TimeStampDatapoint> timestamps = Lists.newLinkedList();
		Multimap<Date, String> freqs = TreeMultimap.create();
		Set<Map.Entry<String, Collection<Commit>>> entries = commits.entrySet();
		for (Map.Entry<String, Collection<Commit>> entry : entries) {
			entry.getValue()
				.forEach(c -> freqs.put(c.getDate(), c.getSha()));
		}
		freqs.asMap().forEach((k, v) -> {
			TimeStampDatapoint tsd = new TimeStampDatapoint();
			tsd.setX(k.getTime());
			tsd.setY(v.size());
			timestamps.add(tsd);
		});
		return timestamps;
	}
}
