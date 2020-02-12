package com.indigo24.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface intList {

    @Query("SELECT * FROM objList")
    List<objList> getAll();

    @Query("DELETE FROM objList")
    public  void deleteAll();

    @Query("SELECT * FROM objList WHERE id = :id OR fromID = :id")
    objList getById(long id);



    @Insert
    void insert(objList slider);

    @Update
    void update(objList slider);

    @Delete
    void delete(objList slider);





}