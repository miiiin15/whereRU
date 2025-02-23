package com.miiiin15.whereru.local.room

import androidx.room.TypeConverter
import com.miiiin15.whereru.common.extension.fromJson
import com.miiiin15.whereru.common.extension.toJson
import com.miiiin15.whereru.local.model.JoinedSessionLocal
import com.miiiin15.whereru.local.model.ProfileLocal
import java.util.Date

class DtoConverter {

    @TypeConverter
    fun fromTimestamp(value: Long): Date = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time

    @TypeConverter
    fun fromJoinedSession(session: JoinedSessionLocal) = session.toJson()

    @TypeConverter
    fun toJoinedSession(json: String) = json.fromJson<JoinedSessionLocal>()

    @TypeConverter
    fun fromProfiles(genre: List<ProfileLocal>) = genre.toJson()

    @TypeConverter
    fun toProfiles(json: String) = json.fromJson<List<ProfileLocal>>()

}
