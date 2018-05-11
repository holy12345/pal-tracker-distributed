package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;
    private final ConcurrentMap cache = new ConcurrentHashMap();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo result = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        this.cache.put(projectId, result);
        return result;
    }

    private ProjectInfo getProjectFromCache(long projectId) {
        if (this.cache.get(projectId) instanceof ProjectInfo) {
            return (ProjectInfo) this.cache.get(projectId);
        }
        return new ProjectInfo(false);
    }
}
