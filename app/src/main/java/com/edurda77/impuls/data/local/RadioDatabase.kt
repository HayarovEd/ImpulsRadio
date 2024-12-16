package com.edurda77.impuls.data.local


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        RadioEntity::class,
        ProvinceEntity::class,
        RadioProvinceEntity::class],
    version = 2
)
abstract class RadioDatabase : RoomDatabase() {
    abstract val radioDao: RadioDao
}