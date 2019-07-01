package com.navid.test.creditcard.domain.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.navid.test.creditcard.config.error.exception.InvalidDeserializationException
import com.navid.test.creditcard.web.rest.error.EntityNotFoundException

import java.io.IOException
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * Developed by Navid Ghahremani (ghahramani.navid@gmail.com)
 */
interface BaseEnum<T : Serializable> {

    val key: T

    open class Deserializer<B : BaseEnum<*>>(
        clazz: KClass<*>,
        private val all: Array<B>
    ) : StdDeserializer<B>(clazz.java) {

        @Throws(IOException::class, InvalidDeserializationException::class)
        override fun deserialize(jp: JsonParser, dc: DeserializationContext): B? {
            val jsonNode = jp.readValueAsTree<JsonNode>()
            if (jsonNode == null || jsonNode.isNull) {
                return null
            }

            val key = (if (jsonNode.isObject) getKey(jsonNode.get("key")) else getKey(jsonNode)) ?: return null

            for (me in all) {
                if (me.key == key) {
                    return me
                }
            }

            throw InvalidDeserializationException("Cannot deserialize ${handledType().simpleName} from key $key")
        }

        private fun getKey(jsonNode: JsonNode): Serializable? {
            return when {
                jsonNode.isNull -> null
                jsonNode.isTextual -> jsonNode.asText()
                jsonNode.isInt -> jsonNode.asInt()
                jsonNode.isFloat || jsonNode.isDouble -> jsonNode.asDouble()
                jsonNode.isLong -> jsonNode.asLong()
                else -> jsonNode.asBoolean()
            }
        }
    }

    companion object {
        fun <E : BaseEnum<*>, T : Serializable> find(enumType: Class<E>, key: T): E {
            val objects = enumType.enumConstants
            for (item in objects) {
                if (key == item.key) {
                    return item
                }
            }
            throw EntityNotFoundException(enumType.simpleName)
        }
    }
}
