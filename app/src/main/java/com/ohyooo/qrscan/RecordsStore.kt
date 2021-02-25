package com.ohyooo.qrscan

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object RecordsSerializer : Serializer<Records> {
    override val defaultValue: Records = Records.getDefaultInstance()

    override fun readFrom(input: InputStream): Records {
        try {
            return Records.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: Records, output: OutputStream) = t.writeTo(output)
}

val recordsDataStore: DataStore<Records> = App.INSTANCE.createDataStore(
    fileName = "Records.pb",
    serializer = RecordsSerializer
)

private fun <T> Context.createDataStore(
    fileName: String,
    serializer: Serializer<T>,
    corruptionHandler: ReplaceFileCorruptionHandler<T>? = null,
    migrations: List<DataMigration<T>> = listOf(),
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
): DataStore<T> =
    DataStoreFactory.create(
        serializer = serializer,
        corruptionHandler = corruptionHandler,
        migrations = migrations,
        scope = scope
    ) { File(this.filesDir, "datastore/$fileName") }
