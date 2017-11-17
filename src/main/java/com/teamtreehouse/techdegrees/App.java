package com.teamtreehouse.techdegrees;


import com.google.gson.Gson;
import com.teamtreehouse.techdegrees.dao.Sql2oToDoDao;
import com.teamtreehouse.techdegrees.dao.ToDoDao;
import com.teamtreehouse.techdegrees.exception.ApiError;
import com.teamtreehouse.techdegrees.model.ToDo;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFileLocation;

public class App {

    public static void main(String[] args) {
        staticFileLocation("/public");

        String datasource = "jdbc:h2:~/todos.db";
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("java Api <port> <datasource>");
                System.exit(0);
            }
            port(Integer.parseInt(args[0]));
            datasource = args[1];
        }

        Sql2o sql2o = new Sql2o(
                String.format("%s;INIT=RUNSCRIPT from 'classpath:db/init.sql'",datasource),"","");
        ToDoDao toDoDao = new Sql2oToDoDao(sql2o);
        Gson gson = new Gson();

        get("/api/v1/todos", "application/json", (req, res) ->
            toDoDao.findAll(), gson::toJson);

        post("/api/v1/todos", "application/json", (req, res) -> {
            ToDo toDo = gson.fromJson(req.body(), ToDo.class);
            toDoDao.create(toDo);
            res.status(201);
            return toDo;
        }, gson::toJson);

        put("/api/v1/todos/:id", "application/json", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));

            if(toDoDao.findById(id) == null) {
                throw new  ApiError(404, "Could not find to do with id " + id);
            }
            ToDo toDo = gson.fromJson(req.body(), ToDo.class);

            toDo.setId(id);

            toDoDao.update(toDo);

            res.status(200);
            return toDo;
        }, gson::toJson);

        delete("/api/v1/todos/:id", "application/json", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));

            ToDo toDo = toDoDao.findById(id);
            if(toDo == null) {
                throw new  ApiError(404, "Could not find to do with id " + id);
            }

            toDoDao.delete(toDo);

            res.status(204);
            return null;
        }, gson::toJson);


        exception(ApiError.class, (exc, req, res) -> {
            ApiError err = (ApiError) exc;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatus());
            jsonMap.put("errorMessage", err.getMessage());
            res.type("application/json");
            res.status(err.getStatus());
            res.body(gson.toJson(jsonMap));
        });

        after((req, res) -> {
            res.type("application/json");
        });

    }

}
