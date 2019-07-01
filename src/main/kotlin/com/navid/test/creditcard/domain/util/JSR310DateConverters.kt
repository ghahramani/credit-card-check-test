package com.navid.test.creditcard.domain.util

import org.springframework.core.convert.converter.Converter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

object JSR310DateConverters {

    enum class DateToLocalDateConverter : Converter<Date, LocalDate> {
        INSTANCE;

        override fun convert(source: Date): LocalDate? {
            return ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault())
                .toLocalDate()
        }
    }

    enum class DateToLocalDateTimeConverter : Converter<Date, LocalDateTime> {
        INSTANCE;

        override fun convert(source: Date): LocalDateTime {
            return LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault())
        }
    }

    enum class DateToYearMonthConverter : Converter<Date, YearMonth> {
        INSTANCE;

        override fun convert(source: Date): YearMonth {
            return YearMonth.from(source.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
        }
    }

    enum class DateToZonedDateTimeConverter : Converter<Date, ZonedDateTime> {
        INSTANCE;

        override fun convert(source: Date): ZonedDateTime {
            return ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault())
        }
    }

    enum class LocalDateTimeToDateConverter : Converter<LocalDateTime, Date> {
        INSTANCE;

        override fun convert(source: LocalDateTime): Date {
            return Date.from(source.atZone(ZoneId.systemDefault()).toInstant())
        }
    }

    enum class LocalDateToDateConverter : Converter<LocalDate, Date> {
        INSTANCE;

        override fun convert(source: LocalDate): Date {
            return Date.from(source.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }
    }

    enum class YearMonthToDateConverter : Converter<YearMonth, Date> {
        INSTANCE;

        override fun convert(source: YearMonth): Date {
            return Date.from(
                source.atEndOfMonth().atTime(LocalTime.MAX)
                    .atZone(ZoneId.systemDefault()).toInstant()
            )
        }
    }

    enum class ZonedDateTimeToDateConverter : Converter<ZonedDateTime, Date> {
        INSTANCE;

        override fun convert(source: ZonedDateTime): Date {
            return Date.from(source.toInstant())
        }
    }
}
