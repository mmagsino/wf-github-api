package wf.github.api.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Commit {
	private String sha;
	private LocalDateTime date;
}
