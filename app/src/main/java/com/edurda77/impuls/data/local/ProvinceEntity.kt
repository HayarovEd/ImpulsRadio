package com.edurda77.impuls.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edurda77.impuls.domain.utils.PROVINCE_EXPANDED
import com.edurda77.impuls.domain.utils.PROVINCE_ID
import com.edurda77.impuls.domain.utils.PROVINCE_NAME
import com.edurda77.impuls.domain.utils.PROVINCE_TABLE

@Entity (tableName = PROVINCE_TABLE)
data class ProvinceEntity (
    @PrimaryKey
    @ColumnInfo(name = PROVINCE_ID)
    val id:Int,
    @ColumnInfo(name = PROVINCE_NAME)
    val name:String,
    @ColumnInfo(name = PROVINCE_EXPANDED)
    val isExpanded:Boolean = false,
)