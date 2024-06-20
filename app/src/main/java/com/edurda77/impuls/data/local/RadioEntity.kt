package com.edurda77.impuls.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edurda77.impuls.domain.utils.RADIO_TABLE
import com.edurda77.impuls.domain.utils.RADIO_TABLE_NAME
import com.edurda77.impuls.domain.utils.RADIO_TABLE_TIME
import com.edurda77.impuls.domain.utils.RADIO_TABLE_URL


@Entity(tableName = RADIO_TABLE)
data class RadioEntity(
    @PrimaryKey
    @ColumnInfo(name = RADIO_TABLE_NAME)
    val name:String,
    @ColumnInfo(name = RADIO_TABLE_URL)
    val url:String,
    @ColumnInfo(name = RADIO_TABLE_TIME)
    val time:Long,
)

