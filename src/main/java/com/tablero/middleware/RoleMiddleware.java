package com.tablero.middleware;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

public class RoleMiddleware {
    public static void requireAdmin(Context ctx){
        String role = ctx.attribute("role");

        if (role == null) {
            throw new UnauthorizedResponse("Unauthorized, Not Authenticated");
        }

        if (!"ADMIN".equals(role)) {  //ES MEJOR PORNERLO ASI PARA PREVENIR NULL POINTER EXCEPTION, AUQMUE ES AL PEDO PORQUE ARRIBA YA VALIDA
            throw new ForbiddenResponse("Denied Access");
        }
        
    }

     public static void requireUser(Context ctx){
        String role = ctx.attribute("role");

        if (role == null) {
            throw new UnauthorizedResponse("Unauthorized, Not Authenticated");
        }

        if (!role.equals("USER")) {
            throw new ForbiddenResponse("Denied Access");
        }
        
    }

    
}
