package com.infnet.resource;

import com.infnet.dto.UserRequest;
import com.infnet.entity.UserEntity;
import com.infnet.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    public Response criar(UserRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(userService.criarUsuario(request))
                .build();
    }

    @GET
    public List<UserEntity> listar() {
        return userService.listarUsuarios();
    }

    @GET
    @Path("/stress-persistencia/{quantidade}")
    public Response stress(@PathParam("quantidade") int quantidade) {
        int criados = userService.stressTest(quantidade);
        return Response.ok(
                "{\"usuariosCriados\":" + criados + "}"
        ).build();
    }

    @GET
    @Path("/stress-consulta/{quantidade}")
    public Response stressConsulta(@PathParam("quantidade") int quantidade) {
        int consultas = userService.stressConsulta(quantidade);
        return Response.ok(
                "{\"consultasExecutadas\":" + consultas + "}"
        ).build();
    }
}