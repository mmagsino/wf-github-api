package wf.github.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Committer {
	private String name;
	@JsonProperty("num_of_commits")
	private Integer numOfCommits;
}
