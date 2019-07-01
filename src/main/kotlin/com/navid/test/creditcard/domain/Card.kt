package com.navid.test.creditcard.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.navid.test.creditcard.domain.util.BaseModelWithId
import com.navid.test.creditcard.domain.util.CardType
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.io.IOException
import java.time.YearMonth
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Developed by Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Document(collection = "cards")
@CompoundIndexes(
    CompoundIndex(
        name = "product_merchant_unique",
        unique = true,
        def = "{'expiryDate' : 1, 'number' : 1, 'bank.name': 1}"
    ),
    CompoundIndex(name = "created_at", def = "{'createdAt' : 1}"),
    CompoundIndex(name = "expiry_date", def = "{'expiryDate' : 1}")
)
data class Card(

    @DBRef
    @NotNull
    val bank: Bank,

    @NotNull
    @Transient
    @JsonFormat(pattern = EXPIRY_DATE_FORMAT, with = [WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS])
    val expiryDate: YearMonth,

    //Maestro card has range between 12 to 19, check the link out: http://bit.ly/2nIXO3f
    //The number will be saved pure digits without any non-numeric characters in db
    @Size(min = 12, max = 19)
    @JsonSerialize(using = CardNumberSerializer::class)
    @JsonDeserialize(using = CardNumberDeserializer::class)
    val number: String,

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val type: CardType,

    @DBRef
    val user: User

) : BaseModelWithId() {

    class CardNumberDeserializer : JsonDeserializer<String>() {

        @Throws(IOException::class)
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String {
            return CardType.clean(p.valueAsString)
        }

    }

    class CardNumberSerializer : JsonSerializer<String>() {

        @Throws(IOException::class)
        override fun serialize(value: String, gen: JsonGenerator, serializers: SerializerProvider) {
            gen.writeString(maskNumber(value))
        }

    }

    companion object {
        const val EXPIRY_DATE_FORMAT = "yyMM"
        private const val serialVersionUID = 1157970945878940541L

        private fun maskNumber(value: String) = CardType.mask(value, "-", "x")
    }
}
