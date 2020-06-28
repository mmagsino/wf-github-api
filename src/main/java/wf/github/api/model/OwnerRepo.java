package wf.github.api.model;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OwnerRepo {
	@NotEmpty
	private String owner;
	@NotEmpty
	private String repoName;
}
