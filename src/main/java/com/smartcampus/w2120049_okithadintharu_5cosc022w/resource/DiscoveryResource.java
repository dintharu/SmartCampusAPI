package com.smartcampus.w2120049_okithadintharu_5cosc022w.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover(@Context UriInfo uriInfo) {

        // Build absolute resource URLs that match the current deployment.
        String roomsUrl = uriInfo.getBaseUriBuilder().path("rooms").build().toString();
        String sensorsUrl = uriInfo.getBaseUriBuilder().path("sensors").build().toString();

        // LinkedHashMap preserves insertion order so the JSON keys appear in
        // a friendly, predictable sequence.
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("api", "Smart Campus — Sensor & Room Management API");
        body.put("version", "v1");
        body.put("module", "5COSC022W.2 Client-Server Architectures, 2025/26");
        body.put("author", "K.G. Okitha Dintharu (w2120049 / 20240578)");
        body.put("contact", "w2120049@iit.ac.lk");
        body.put("documentation", "https://github.com/dintharu/SmartCampusAPI");

        // The "_links" map is the HATEOAS payload — clients can navigate
        // the entire API from these URLs without prior knowledge.
        Map<String, String> links = new LinkedHashMap<>();
        links.put("self", uriInfo.getAbsolutePath().toString());
        links.put("rooms", roomsUrl);
        links.put("sensors", sensorsUrl);
        body.put("_links", links);

        return Response.ok(body).build();
    }
}
