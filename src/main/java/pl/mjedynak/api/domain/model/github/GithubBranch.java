package pl.mjedynak.api.domain.model.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubBranch {

    private String name;
    private String commitSha;

    @JsonProperty("commit")
    private void unpackNested(Map<String, Object> commit) {
        this.commitSha = (String) commit.get("sha");
    }
}
