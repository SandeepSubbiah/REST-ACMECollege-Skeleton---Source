package acmecollege.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import static acmecollege.utility.MyConstants.*;

//@ApplicationPath(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
@ApplicationPath("/rest-acmecollege/api/v1")
@DeclareRoles({USER_ROLE, ADMIN_ROLE})
public class RestConfig extends Application {

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.jsonFeature", "JacksonFeature");
        return props;
    }
}
