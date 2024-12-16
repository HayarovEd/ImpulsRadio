package com.edurda77.impuls.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.edurda77.impuls.domain.utils.PROVINCE_TABLE
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_TABLE
import com.edurda77.impuls.domain.utils.RADIO_TABLE
import com.edurda77.impuls.domain.utils.RADIO_TABLE_PROVINCE
import kotlinx.coroutines.flow.Flow


@Dao
interface RadioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadio(radioEntity: RadioEntity)

    @Query("SELECT * FROM $RADIO_TABLE")
    fun getLastData(): Flow<List<RadioEntity>>

    @Upsert
    suspend fun insertRadioProvince(radioProvinceEntity: RadioProvinceEntity)

    @Query("SELECT * FROM $RADIO_PROVINCE_TABLE WHERE $RADIO_TABLE_PROVINCE = :id")
    fun getRadiosByProvince(id: Int): List<RadioProvinceEntity>

    @Query("DELETE FROM $RADIO_PROVINCE_TABLE WHERE $RADIO_TABLE_PROVINCE = :id")
    suspend fun clearRadiosProvinceByProvince(id: Int)

    @Upsert
    suspend fun insertProvince(provinceEntity: ProvinceEntity)

    @Query("SELECT * FROM $PROVINCE_TABLE")
    fun getProvinces(): List<ProvinceEntity>

    @Query("DELETE FROM $PROVINCE_TABLE")
    suspend fun clearProvinces()

}