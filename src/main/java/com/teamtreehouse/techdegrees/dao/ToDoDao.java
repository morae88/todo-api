package com.teamtreehouse.techdegrees.dao;

import com.teamtreehouse.techdegrees.exception.DaoException;
import com.teamtreehouse.techdegrees.model.ToDo;

import java.util.List;

public interface ToDoDao {
    void create(ToDo toDo) throws DaoException;

    void update(ToDo toDo) throws DaoException;

    void delete(ToDo toDo) throws DaoException;

    ToDo findById(int id);

    List<ToDo> findAll();


}
