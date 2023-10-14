package com.tegar.istoriya.data.local.entity
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class StoryEntity(
    @field:PrimaryKey
    @field:ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "photoUrl")
    val photoUrl: String,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "description")
    val desc: String? = null,

    @field:ColumnInfo(name = "lon")
    val lon: Double? = null,

    @field:ColumnInfo(name = "lat")
    val lat: Double? = null,

    @field:ColumnInfo(name = "createdAt")
    val createdAt: String? = null,
) : Parcelable