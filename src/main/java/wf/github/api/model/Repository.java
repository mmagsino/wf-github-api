package wf.github.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Repository {
	private String name;
	private String fullName;
	private String url;
	private String description;
	private String owner;
}
