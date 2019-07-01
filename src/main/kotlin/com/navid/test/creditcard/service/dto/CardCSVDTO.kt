package com.navid.test.creditcard.service.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
import com.navid.test.creditcard.domain.Card
import com.navid.test.creditcard.domain.User.Companion.USERNAME_MAX_LENGTH
import com.navid.test.creditcard.domain.User.Companion.USERNAME_MIN_LENGTH
import com.navid.test.creditcard.domain.util.CardType
import java.io.Serializable
import java.time.YearMonth
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Developed by Navid Ghahremani (ghahramani.navid@gmail.com)
 */
data class CardCSVDTO @JvmOverloads constructor(

    @NotEmpty
    var bank: String? = null,

    // When we want to work with it, we need to compare only year and month
    // so we have to reset all others ( Days, Hours, Minutes, ... )
    @NotNull
    @JsonFormat(pattern = Card.EXPIRY_DATE_FORMAT, with = [WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS])
    var expiryDate: YearMonth? = YearMonth.now(),

    @Size(min = 12, max = 19)
    private var _number: String? = null,

    var type: CardType? = null,

    @NotEmpty
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH)
    var username: String? = null

) : Serializable {

    var number: String?
        get() = _number
        set(value) {
            _number = value
            type = CardType.findCardType(value)
        }

    constructor(card: Card) : this(
        bank = card.bank.name,
        expiryDate = card.expiryDate,
        username = card.user.username,
        _number = card.number
    )

}
