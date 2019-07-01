package com.navid.test.creditcard.config.serdes.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

import java.io.IOException

class NumericBooleanDeserializer : JsonDeserializer<Boolean>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Boolean {
        return "0" != parser.text
    }
}
