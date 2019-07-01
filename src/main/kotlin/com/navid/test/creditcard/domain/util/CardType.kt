package com.navid.test.creditcard.domain.util

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.apache.commons.lang3.RegExUtils
import org.apache.commons.lang3.StringUtils
import java.security.InvalidParameterException
import java.util.regex.Pattern

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = CardType.CardTypeDeserializer::class)
enum class CardType(
    override val key: String,
    val verify: Pattern,
    val type: Pattern,
    val luhn: Boolean,
    val format: IntArray
) : BaseEnum<String> {

    VISA(
        "card.type.visa",
        Constants.Verify.VISA,
        Constants.Types.VISA,
        true,
        4,
        4,
        4,
        4
    ),
    AMERICAN_EXPRESS(
        "card.type.american_express",
        Constants.Verify.AMERICAN_EXPRESS,
        Constants.Types.AMERICAN_EXPRESS,
        true,
        4,
        6,
        5
    ),
    MAESTRO(
        "card.type.maestro",
        Constants.Verify.MAESTRO,
        Constants.Types.MAESTRO,
        true,
        4,
        4,
        4,
        4
    ),
    DINERS_CLUB(
        "card.type.diners_club",
        Constants.Verify.DINERS_CLUB,
        Constants.Types.DINERS_CLUB,
        true,
        4,
        4,
        4,
        2
    ),
    CHINA_UNION(
        "card.type.china_union",
        Constants.Verify.CHINA_UNION_PAY,
        Constants.Types.CHINA_UNION_PAY,
        true,
        4,
        4,
        4,
        4
    ),
    DISCOVER(
        "card.type.discover",
        Constants.Verify.DISCOVER,
        Constants.Types.DISCOVER,
        true,
        4,
        4,
        4,
        4
    ),
    JCB(
        "card.type.jcb",
        Constants.Verify.JCB,
        Constants.Types.JCB,
        true,
        4,
        4,
        4,
        4
    ),
    MASTERCARD(
        "card.type.mastercard",
        Constants.Verify.MASTERCARD,
        Constants.Types.MASTERCARD,
        true,
        4,
        4,
        4,
        4
    ),
    UNKNOWN(
        "card.type.unknown",
        CardTypeUnknown.unknownVerify(),
        CardTypeUnknown.unknownType(),
        false,
        4,
        4,
        4,
        4
    );

    constructor(
        key: String,
        verify: String,
        type: String,
        luhn: Boolean,
        vararg format: Int
    ) : this(key, Pattern.compile(verify), Pattern.compile(type), luhn, format)

    fun validateWithLuhn(cardNumber: String): Boolean {
        var sum = 0
        var digit: Int
        var addend: Int
        var doubled = false
        for (i in cardNumber.length - 1 downTo 0) {
            digit = Integer.parseInt(cardNumber.substring(i, i + 1))
            if (doubled) {
                addend = digit * 2
                if (addend > 9) {
                    addend -= 9
                }
            } else {
                addend = digit
            }
            sum += addend
            doubled = !doubled
        }
        return sum % 10 == 0
    }

    companion object {
        private const val CARD_LENGTH_FOR_TYPE = 6

        /**
         * @param cardNumber the input to clean
         * @return a string containing only digits
         */
        fun clean(cardNumber: String) =
            StringUtils.trimToEmpty(cardNumber).replace("[^\\d]".toRegex(), "")

        /**
         * @param number the card number to find type of, can be partial
         * @return the instance of Card matching number, or null if no match
         */
        fun `is`(number: String, card: CardType) =
            with(clean(number)) {
                length >= CARD_LENGTH_FOR_TYPE
                    && card.type.matcher(substring(0, CARD_LENGTH_FOR_TYPE)).matches()
            }

        /**
         * @param number the card number to find type of, can be partial
         * @return the instance of Card matching number, or null if no match
         */
        fun findCardType(number: String?): CardType {
            if (number.isNullOrEmpty()) {
                return UNKNOWN
            }

            val cardNumber = clean(number)
            if (cardNumber.length >= CARD_LENGTH_FOR_TYPE) {
                for (card in values()) {
                    val identificationDigit = cardNumber.substring(0, CARD_LENGTH_FOR_TYPE)
                    if (card.type.matcher(identificationDigit).matches()) {
                        return card
                    }
                }
            }

            return UNKNOWN
        }

        /**
         * @param number    The input to clean and format
         * @param delimiter and character to put between numbers
         * @param mask      a character to replace with numbers except type number, if set null the original number will be placed
         * @return if cardNumber matches a Card, a formatted cardNumber, otherwise a cleaned version
         * of cardNumber
         */
        fun mask(number: String, delimiter: String, mask: String) = mask(number, delimiter, mask, findCardType(number))

        /**
         * @param number    the input to clean and format
         * @param card      the Card to format for, if possible
         * @param delimiter a character to put between numbers
         * @param mask      a character to replace with numbers except type number. Set null to place the original number
         * @return if number matches card, a formatted number, otherwise a cleaned version of number
         */
        fun mask(number: String?, delimiter: String, mask: String?, card: CardType?): String {
            // make sure the cc isn't null
            if (number == null) {
                return ""
            }

            // clean up the string
            val cleanedCardNumber = clean(number)

            // return the cleaned string if the card is null
            if (card == null) {
                return cleanedCardNumber
            }

            // make sure the format isn't null or empty
            var format: IntArray? = card.format
            if (format == null || format.isEmpty()) {
                format = intArrayOf(4, 4, 4, 4)
            }

            // sum the children and make sure it only contains numbers greater than zero
            for (i in format) {
                if (i <= 0) {
                    throw InvalidParameterException("the pattern must contain numbers greater than zero")
                }
            }

            // make sure the string is long enough
            val length = cleanedCardNumber.length
            return if (length <= 0) {
                cleanedCardNumber
            } else format(cleanedCardNumber, length, format, delimiter, mask)

        }

        /**
         * @param number The input to clean and format
         * @return if number matches a Card, a formatted number, otherwise a cleaned version of number
         */
        fun mask(number: String) = mask(number, " ", null, findCardType(number))

        /**
         * @param number the input to validate
         * @param type   the number type to validate against
         * @return true if number is a valid number for type
         */
        fun validate(number: String, type: CardType) = type.verify.matcher(number).matches()

        /**
         * @param number the input to validate
         * @return true if number is a valid number for this instance's number types
         */
        fun validate(number: String) = validate(number, findCardType(number))

        /**
         * @param cleanedNumber a clean credit card number to format
         * @param length        the length to limit
         * @param format        an array of groups lengths
         * @param delimiter     and character to put between numbers
         * @param mask          a character to replace with numbers except type number, if set null the original number will be placed
         * @return a formatted version of cleanedNumber
         */
        private fun format(
            cleanedNumber: String,
            length: Int,
            format: IntArray,
            delimiter: String,
            mask: String?
        ): String {
            val builder = StringBuilder()

            var start = 0
            var end = 0

            for (i in format.indices) {
                val p = format[i]
                end += p
                val isAtEnd = i == format.size - 1 && end < length || end >= length

                val group = cleanedNumber.substring(start, if (isAtEnd) length else end)
                if (mask == null || i == 0) {
                    builder.append(group)
                } else {
                    builder.append(RegExUtils.replaceAll(group, ".", mask))
                }
                if (!isAtEnd) {
                    builder.append(delimiter)
                    start += p
                } else {
                    break
                }
            }

            return builder.toString()
        }
    }

    private sealed class CardTypeUnknown {
        companion object {
            /**
             * The inverse type pattern of all patterns except unknown
             *
             * @return the opposite of all type patterns except unknown
             */
            fun unknownType() = "^((?!" + Constants.Types.all() + ").)*$"

            /**
             * The inverse varify pattern of all patterns except unknown
             *
             * @return the opposite of all varify patterns except unknown
             */
            fun unknownVerify() = "^((?!" + Constants.Verify.all() + ").)*$"
        }
    }

    class CardTypeDeserializer : BaseEnum.Deserializer<CardType>(CardType::class, values())

    private sealed class Constants {

        sealed class Types {
            companion object {
                const val AMERICAN_EXPRESS = "^3[47][0-9]{2}[0-9]{0,11}$"
                const val CHINA_UNION_PAY = "^62[0-5][0-9][0-9]{0,15}$"
                const val DINERS_CLUB = "^3(?:0[0-5]|[68][0-9])[0-9][0-9]{0,10}$"
                const val DISCOVER = "^6(?:011|5[0-9]{2})[0-9]{0,12}$"
                const val JCB = "^(?:2131|1800|35[0-9]{3})[0-9]{0,11}$"
                const val MAESTRO = "^(?:5[0678][0-9]{2}|6304|6390|67[0-9]{2})[0-9]{0,15}$"
                const val MASTERCARD = "^5[1-5][0-9]{2}[0-9]{0,12}$"
                const val VISA = "^4[0-9]{3}[0-9]{0,12}$"

                fun all(): String {
                    val list = arrayOf(
                        AMERICAN_EXPRESS,
                        CHINA_UNION_PAY,
                        DINERS_CLUB,
                        DISCOVER,
                        JCB,
                        MAESTRO,
                        MASTERCARD,
                        VISA
                    )
                    return StringUtils.join(list, "|")
                }
            }
        }

        sealed class Verify {
            companion object {
                const val AMERICAN_EXPRESS = "^3[47][0-9]{13}$"
                const val CHINA_UNION_PAY = "^62[0-5][0-9]{13,16}$"
                const val DINERS_CLUB = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$"
                const val DISCOVER = "^6(?:011|5[0-9]{2})[0-9]{12}$"
                const val JCB = "^(?:2131|1800|35[0-9]{3})[0-9]{11}$"
                const val MAESTRO = "^(?:5[0678][0-9]{2}|6304|6390|67[0-9]{2})[0-9]{8,15}"
                const val MASTERCARD = "^5[1-5][0-9]{14}$"
                const val VISA = "^4[0-9]{15}$"

                fun all(): String {
                    val list = arrayOf(
                        AMERICAN_EXPRESS,
                        CHINA_UNION_PAY,
                        DINERS_CLUB,
                        DISCOVER,
                        JCB,
                        MAESTRO,
                        MASTERCARD,
                        VISA
                    )
                    return StringUtils.join(list, "|")
                }
            }
        }
    }
}
