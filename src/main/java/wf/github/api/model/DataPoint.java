package wf.github.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataPoint {
	private String label;
	private Integer y;
}
