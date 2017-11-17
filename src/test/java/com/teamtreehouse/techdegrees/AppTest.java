package com.teamtreehouse.techdegrees;

import com.google.gson.Gson;
import com.teamtreehouse.techdegrees.dao.Sql2oToDoDao;
import com.teamtreehouse.techdegrees.model.ToDo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;
import testing.ApiClient;
import testing.ApiResponse;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AppTest {
    public static final String PORT = "4568";
    public static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
    private Connection conn;
    private ApiClient client;
    private Gson gson;
    private Sql2oToDoDao toDoDao;

    @BeforeClass
    public static void startServer() {
        String[] args = {  PORT, TEST_DATASOURCE};
        App.main(args);
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Before
    public void setUp() throws Exception {
        Sql2o sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
        conn = sql2o.open();
        client = new ApiClient("http://localhost:" + PORT);
        gson = new Gson();
        toDoDao = new Sql2oToDoDao(sql2o);
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void returnAllTodos() throws Exception {
        toDoDao.create(new ToDo("test todo 1", false));
        toDoDao.create(new ToDo("test todo 2", false));

        ApiResponse response = client.request("GET", "/api/v1/todos");

        ToDo[] toDos = gson.fromJson(response.getBody(), ToDo[].class);

        assertEquals(2, toDos.length);
    }

    @Test
    public void addingTodoReturnsCreatedStatus() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Test");
        values.put("isCompleted", false);

        ApiResponse res = client.request("POST", "/api/v1/todos", gson.toJson(values));

        assertEquals(201, res.getStatus());
    }

    @Test
    public void updatingTodoReturns200Status() throws Exception {
        ToDo toDo = newTestToDo();
        toDoDao.create(toDo);
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Test");
        values.put("isCompleted", false);

        int id = toDo.getId();

        ApiResponse res = client.request("PUT", String.format("/api/v1/todos/%d",id), gson.toJson(values));

        assertEquals("Test", toDoDao.findById(id).getName());
    }

    @Test
    public void deletingTodoReturns204Status() throws Exception {
        ToDo toDo = newTestToDo();
        toDoDao.create(toDo);
        int id = toDo.getId();

        ApiResponse res = client.request("DELETE", String.format("/api/v1/todos/%d",id));

        assertEquals(204, res.getStatus());
    }

    public ToDo newTestToDo() {
        return new ToDo("test", false);
    }
}
