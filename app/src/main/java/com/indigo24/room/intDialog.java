package com.indigo24.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface intDialog {


    @Query("SELECT * FROM objDialog")
    List<objDialog> getAllDialogs();


    @Query("SELECT * FROM objDialog WHERE fromID = :ids OR toID = :ids ORDER BY date DESC   ")

//    @Query("SELECT * FROM objDialog  INNER JOIN loan ON loan.book_id = book INNER JOIN user ON user.id = loan.user_id + WHERE toID = :ids OR fromID = :ids   ORDER BY date DESC")
    List<objDialog> loadAllById(int ids);


    @Query("DELETE FROM objDialog")
    public  void deleteAllDialogs();

    @Insert
    void insert(objDialog objDialog);

    @Update
    void update(objDialog objDialog);

    @Delete
    void delete(objDialog objDialog);
}