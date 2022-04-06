package com.pjtsearch.opencontroller.extensions

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.pjtsearch.opencontroller.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<Settings> {
    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return withContext(Dispatchers.IO) { Settings.parseFrom(input) }
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) =
        withContext(Dispatchers.IO) { t.writeTo(output) }

    override val defaultValue: Settings = Settings.getDefaultInstance()
}