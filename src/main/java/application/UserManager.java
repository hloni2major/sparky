/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import core.User;
import java.sql.SQLException;
import java.util.List;
import spark.Request;
import spark.Response;

/**
 *
 * @author lehlohonolo
 */
public final class UserManager {

    String databaseUrl = "jdbc:mysql://localhost/spark";
    ConnectionSource connectionSource;
    Dao<User, String> userDao;

    public UserManager() throws SQLException {
        connect();
    }

    public void connect() throws SQLException {

        this.connectionSource = new JdbcConnectionSource(databaseUrl);
        ((JdbcConnectionSource) connectionSource).setUsername("root");
        ((JdbcConnectionSource) connectionSource).setPassword("abc123");
        userDao = DaoManager.createDao(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
    }

    public String createUser(Request rqst, Response rspns) throws SQLException {
        String username = rqst.queryParams("username");
        String email = rqst.queryParams("email");
        String name = rqst.queryParams("name");
        String surname = rqst.queryParams("surname");
        String cellnumber = rqst.queryParams("cellnumber");
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setName(name);
        user.setSurname(surname);
        user.setCellnumber(cellnumber);
        List<User> checkUser = userDao.queryForMatching(user);
        
        
	Gson gson = new Gson();

	// convert java object to JSON format,
	// and returned as JSON formatted string
	//String json;
        HttpResponse response = new HttpResponse();
        
        if (checkUser.size() > 0) {
            
            response.setMessage("Error creating user. User already exists");
            response.setCode(409);
            rspns.status(409);
            
            return gson.toJson(response);
        } else {
            Dao.CreateOrUpdateStatus createOrUpdate;
            createOrUpdate = userDao.createOrUpdate(user);
            
            response.setMessage("User successfully created");
            response.setCode(201);
            response.setData(user);
            
            rspns.status(201); // 201 Created

            return gson.toJson(response);
        }
    }

    public String getUser(Request rqst, Response rspns) throws SQLException {
        // queryForSameId
        String id = rqst.params(":id");
        User user;
        user = userDao.queryForId(id);
        Gson gson = new Gson();
        HttpResponse response = new HttpResponse();
        
        if(user != null){
            response.setCode(200);
            response.setMessage("User successfully retrieved");
            response.setData(user);
        }else{
            response.setCode(404);
            response.setMessage("User with ID "+id+" does not exist");
        }
        
        return gson.toJson(user);
    }
}
