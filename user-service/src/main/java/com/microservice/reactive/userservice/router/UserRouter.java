package com.microservice.reactive.userservice.router;

import com.microservice.reactive.userservice.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {
    public static final String USER_ROUTE = "/api/v1/user";
    public static final String USER_REGISTER = USER_ROUTE + "/register";
    public static final String USER_LOGIN = USER_ROUTE + "/login";
    public static final String USER_LIST = USER_ROUTE + "/list";
    public static final String USER_BY_ID = USER_ROUTE + "/list/{id}";
    public static final String USER_UPDATE = USER_ROUTE + "/update/{id}";
    public static final String USER_DELETE = USER_ROUTE + "/delete/{id}";

    private final UserHandler userHandler;

    public UserRouter(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes() {
        return route()
                .POST(USER_REGISTER, accept(APPLICATION_JSON), userHandler::handleRegister)
                .POST(USER_LOGIN, accept(APPLICATION_JSON), userHandler::handleLogin)
                .GET(USER_LIST, accept(APPLICATION_JSON), userHandler::handleGetAllUsers)
                .GET(USER_BY_ID, accept(APPLICATION_JSON), userHandler::handleGetUserById)
                .PUT(USER_UPDATE, accept(APPLICATION_JSON), userHandler::handleUpdateUser)
                .PUT(USER_DELETE, accept(APPLICATION_JSON), userHandler::handleDeleteUser)
                .build();
    }
}
