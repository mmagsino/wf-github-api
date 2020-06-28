package wf.github.api.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Projection {
	@JsonProperty("contributor_latest_commits")
	List<Committer> contributorCommits;
	@JsonProperty("timeline_latest_commits")
	Map<String, Collection<Commit>> timelineCommits;
	public static final Projection empty() {
		Projection projection = new Projection();
		projection.setContributorCommits(Lists.newArrayList());
		projection.setTimelineCommits(Maps.newHashMap());
		return projection;
	}
}
