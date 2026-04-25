package com.gaoacorp.microinternships.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gaoacorp.microinternships.data.local.entities.PublisherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PublisherDao {

    // ========== CREATE ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(publisher: PublisherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(publishers: List<PublisherEntity>)

    // ========== READ ==========
    @Query("SELECT * FROM publishers")
    fun observeAll(): Flow<List<PublisherEntity>>

    @Query("SELECT * FROM publishers WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PublisherEntity?

    @Query("SELECT cachedAt FROM publishers WHERE id = :id LIMIT 1")
    suspend fun getCacheTimeById(id: Int): Long?

    // ========== UPDATE ==========
    @Query("UPDATE publishers SET companyName = :name WHERE id = :id")
    suspend fun updateCompanyName(id: Int, name: String)

    // ========== DELETE ==========
    @Query("DELETE FROM publishers WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM publishers")
    suspend fun clearAll()
}
