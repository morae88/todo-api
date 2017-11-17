package com.teamtreehouse.techdegrees.dao;

import com.teamtreehouse.techdegrees.model.ToDo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class Sql2oToDoDaoTest {

    private Sql2oToDoDao dao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        dao = new Sql2oToDoDao(sql2o);
        // Keep connection open through entire test
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingToDoSetsId() throws Exception {
        ToDo toDo = newTestToDo();
        int originalCourseId = toDo.getId();

        dao.create(toDo);

        assertNotEquals(originalCourseId, toDo.getId());
    }

    @Test
    public void deleteToDoRemovesFromDatabase() throws Exception {
        ToDo toDo = newTestToDo();
        dao.create(toDo);

        int id = toDo.getId();

        dao.delete(toDo);

        assertNull(dao.findById(id));


    }

    @Test
    public void toDoNameGetsUpdated() throws Exception {
        ToDo toDo = newTestToDo();
        dao.create(toDo);

        toDo.setName("updated todo");
        dao.update(toDo);

        ToDo updatedTodo = dao.findById(toDo.getId());

        assertEquals(toDo, updatedTodo);
    }

    @Test
    public void addedToDosAreReturnedFromFindAll() throws Exception {
        ToDo toDo = newTestToDo();

        dao.create(toDo);

        assertEquals(1, dao.findAll().size());
    }

    @Test
    public void noToDosReturnsEmptyList() throws Exception {
        assertEquals(0, dao.findAll().size());
    }

    @Test
    public void existingCoursesCanBeFoundById() throws Exception {
        ToDo toDo = newTestToDo();
        dao.create(toDo);

        ToDo foundToDo = dao.findById(toDo.getId());

        assertEquals(toDo, foundToDo);
    }

    private ToDo newTestToDo() {
        return new ToDo("test", false);
    }

}
