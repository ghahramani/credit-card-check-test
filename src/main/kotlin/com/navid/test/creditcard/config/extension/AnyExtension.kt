package com.navid.test.creditcard.config.extension

import org.apache.commons.lang3.math.NumberUtils
import org.apache.commons.lang3.time.DateUtils
import java.text.ParseException

fun Any.toBoolean(): Boolean {
    val value = this.toString().toLowerCase().trim()
    return value == "true" || value == "yes" || value != "0"
}

fun Any.isNumber(): Boolean {
    val value = this.toString().toLowerCase().trim()
    return NumberUtils.isParsable(value)
}

fun Any.isTime(): Boolean {
    val value = this.toString().toLowerCase().trim()
    return try {
        DateUtils.parseDate(
            value,
            "HH:mm:ss",
            "hh",
            "hh:mm",
            "HH:mm:ss a",
            "HH:mm:ss a z"
        )
        true
    } catch (e: IllegalArgumentException) {
        false
    } catch (e: ParseException) {
        return false
    }
}

fun Any.isDate(): Boolean {
    val value = this.toString().toLowerCase().trim()
    return try {
        DateUtils.parseDate(
            value,
            "yy",
            "yyyy",
            "yyyy MM",
            "MM dd",
            "yyyy MM dd",
            "yyyy/MM",
            "MM/dd",
            "yyyy/MM/dd",
            "yyyy-MM",
            "yyyy-MM-dd",
            "MM-dd",
            "yyyy.MM",
            "yyyy.MM.dd",
            "MM.dd",
            "dd.MM.yy",
            "dd.MM.yyyy",
            "dd.MMM.yyyy",
            "dd.MMMM.yyyy",
            "dd MM yy",
            "dd MM yyyy",
            "dd MMM yyyy",
            "dd MMMM yyyy",
            "dd.MM.yyyy HH:mm:ss",
            "dd.M.yy",
            "dd.MM.yyyy hh:mm:ss",
            "dd.MM.yyyy HH:mm:ss a",
            "dd.MM.yyyy HH:mm:ss a z"
        )
        true
    } catch (e: IllegalArgumentException) {
        false
    } catch (e: ParseException) {
        return false
    }
}
