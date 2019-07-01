package com.navid.test.creditcard.config.serdes.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

import java.io.IOException

class NumericBooleanSerializer : JsonSerializer<Boolean>() {

    @Throws(IOException::class)
    override fun serialize(bool: Boolean, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeString(if (bool) "1" else "0")
    }
}
