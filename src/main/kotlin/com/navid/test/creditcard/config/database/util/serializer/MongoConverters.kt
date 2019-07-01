package com.navid.test.creditcard.config.database.util.serializer

import com.navid.test.creditcard.domain.Authority
import org.apache.commons.lang3.StringUtils
import org.springframework.core.convert.converter.Converter

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

object MongoConverters {

    enum class AuthorityConverter : Converter<String, Authority> {
        INSTANCE;

        override fun convert(source: String): Authority? {

            if (StringUtils.isEmpty(source)) {
                return null
            }

            return Authority(source)
        }
    }

}
