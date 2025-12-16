package com.edurda77.impuls.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_NAME
import com.edurda77.impuls.domain.utils.RADIO_TABLE_PROVINCE
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_TABLE
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_TIME
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_URL


@Entity(tableName = RADIO_PROVINCE_TABLE)
data class RadioProvinceEntity(
    @PrimaryKey
    @ColumnInfo(name = RADIO_PROVINCE_NAME)
    val name:String,
    @ColumnInfo(name = RADIO_PROVINCE_URL)
    val url:String,
    @ColumnInfo(name = RADIO_PROVINCE_TIME)
    val time:Long,
    @ColumnInfo(name = RADIO_TABLE_PROVINCE)
    val provinceId:Int,
)

