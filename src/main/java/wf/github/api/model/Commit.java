package wf.github.api.model;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Commit {
	private String sha;
	private Date date;
}
