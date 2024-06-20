package com.edurda77.impuls.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.edurda77.impuls.domain.utils.RADIO_TABLE
import kotlinx.coroutines.flow.Flow


@Dao
interface RadioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadio(radioEntity: RadioEntity)

    @Query("SELECT * FROM $RADIO_TABLE")
    fun getLastData(): Flow<List<RadioEntity>>


}