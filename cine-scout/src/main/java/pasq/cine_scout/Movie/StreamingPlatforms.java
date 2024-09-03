package pasq.cine_scout.Movie;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StreamingPlatforms {
    private Integer provider_id;
    private String provider_name;
    private String logo_path;
    private Integer display_priority;
}
