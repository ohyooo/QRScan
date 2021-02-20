package com.ohyooo.qrscan

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.createDataStore
import com.google.protobuf.InvalidProtocolBufferException
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
