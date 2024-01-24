package si.fri.rso.aichat.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.aichat.lib.ChatMetadata;
import si.fri.rso.aichat.services.beans.ChatMetadataBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;



@ApplicationScoped
@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatMetadataResource {

    private Logger log = Logger.getLogger(ChatMetadataResource.class.getName());

    @Inject
    private ChatMetadataBean chatMetadataBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all Chat metadata.", summary = "Get all metadata")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of chat metadata",
                    content = @Content(schema = @Schema(implementation = ChatMetadata.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getChatMetadata() {

        List<ChatMetadata> chatMetadata = chatMetadataBean.getChatMetadata();
        //List<ImageMetadata> imageMetadata = imageMetadataBean.getImageMetadataFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(chatMetadata).build();
    }




    @Operation(description = "Send question to ChatGPT", summary = "Send text")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Question successfully processed by ChatGPT."),
            @APIResponse(responseCode = "400", description = "Bad Request.")
    })
    @POST
    public Response createMetadataOrStringValue(@RequestBody(
            description = "DTO object with image metadata or a plain string.",
            required = true, content = @Content(
            schema = @Schema(implementation = ChatMetadata.class))) ChatMetadata chatMetadata) {

        if (chatMetadata.getUserCreated() == null || chatMetadata.getUserText() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid or empty request body").build();
        }


        chatMetadata = chatMetadataBean.createAiMetadata(chatMetadata);



        // If it's a valid JSON string, you can process it as needed
        // Example: ImageMetadata chatMetadata = objectMapper.readValue(requestBody, ImageMetadata.class);
        // Process chatMetadata...

        return Response.status(Response.Status.CREATED).entity(chatMetadata).build();
    }





}


