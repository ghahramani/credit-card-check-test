package com.navid.test.creditcard.service.mapper.util

import com.navid.test.creditcard.domain.Authority
import org.springframework.stereotype.Component
import javax.inject.Qualifier

@Component
class UserMappingUtil {

    @Qualifier
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class FromAuthorities

    @Qualifier
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class FromString

    @FromAuthorities
    fun fromAuthorities(value: HashSet<Authority>): Set<String> {
        return value.map(Authority::name).toSet()
    }

    @FromString
    fun toAuthorities(value: Set<String>): HashSet<Authority> {
        return value.map(::Authority).toHashSet()
    }

}
