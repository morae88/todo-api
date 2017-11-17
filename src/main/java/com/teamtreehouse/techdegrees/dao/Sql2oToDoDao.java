package com.teamtreehouse.techdegrees.dao;

import com.teamtreehouse.techdegrees.exception.DaoException;
import com.teamtreehouse.techdegrees.model.ToDo;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oToDoDao implements ToDoDao {

    private final Sql2o sql2o;

    public Sql2oToDoDao(Sql2o sql2o) { this.sql2o = sql2o; }

    @Override
    public void create(ToDo toDo) throws DaoException {
        String sql = "INSERT INTO todos(name, completed) VALUES(:name, :completed)";
        try (Connection connection = sql2o.open()) {
            int id = (int) connection.createQuery(sql)
                    .bind(toDo)
                    .executeUpdate()
                    .getKey();
            toDo.setId(id);
        } catch (Sql2oException ex) {
            throw new DaoException(ex, "Problem adding to do");
        }

    }

    @Override
    public void update(ToDo toDo) throws DaoException {
        String sql = "UPDATE todos SET name = :name, completed = :completed WHERE id = :id";
        try (Connection connection = sql2o.open()){
            connection.createQuery(sql)
                    .bind(toDo)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException(ex, "Problem updating to do");
        }

    }

    @Override
    public void delete(ToDo toDo) throws DaoException {
        String sql = "DELETE FROM todos WHERE id = :id";
        try (Connection connection = sql2o.open()) {
             connection.createQuery(sql)
                     .addParameter("id", toDo.getId())
                     .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException(ex, "Problem deleting to do");
        }
    }

    @Override
    public ToDo findById(int id) {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM todos WHERE id = :id")
                    .throwOnMappingFailure(false)
                    .addParameter("id", id)
                    .executeAndFetchFirst(ToDo.class);
        }
    }

    @Override
    public List<ToDo> findAll() {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM todos")
                    .throwOnMappingFailure(false)
                    .executeAndFetch(ToDo.class);
        }
    }
}
