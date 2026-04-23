package com.smartcampus.w2120049_okithadintharu_5cosc022w;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class ApiApplication extends Application {
    // Intentionally empty — Jersey auto-discovers @Path and @Provider classes
    // via classpath scanning when the Application subclass declares no explicit
    // resource set.
}
