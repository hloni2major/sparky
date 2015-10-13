package restapi;


import application.UserManager;
import java.sql.SQLException;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        UserManager userManager = new UserManager();
        get("/users/:id", (req, res) -> userManager.getUser(req,res));
        
        post("/users/create", (Request rqst, Response rspns) -> userManager.createUser(rqst, rspns));
    }
}
