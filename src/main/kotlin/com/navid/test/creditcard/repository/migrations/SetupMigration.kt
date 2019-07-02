package com.navid.test.creditcard.repository.migrations

import com.github.mongobee.changeset.ChangeLog
import com.github.mongobee.changeset.ChangeSet
import com.navid.test.creditcard.config.AppConstants.Security.Authority.Companion.ADMIN
import com.navid.test.creditcard.config.AppConstants.Security.Authority.Companion.USER
import com.navid.test.creditcard.domain.Authority
import com.navid.test.creditcard.domain.Bank
import com.navid.test.creditcard.domain.User
import org.springframework.data.mongodb.core.MongoTemplate
import java.time.Instant

/**
 * Developed by Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@ChangeLog(order = "0001")
class SetupMigration {

    @ChangeSet(order = "01", author = "navid", id = "01-addAuthorities")
    fun addAuthorities(mongoTemplate: MongoTemplate) {
        val adminAuthority = Authority(ADMIN)
        val userAuthority = Authority(USER)
        mongoTemplate.save(adminAuthority)
        mongoTemplate.save(userAuthority)
    }

    @ChangeSet(order = "02", author = "navid", id = "02-addUsers")
    fun addUsers(mongoTemplate: MongoTemplate) {
        val adminAuthority = Authority(ADMIN)
        val userAuthority = Authority(USER)

        val systemUser = User(
            "system",
            "\$2a\$10\$Sasl6A0PcYFO5ubDJioOWOe7/ThZ1qMzm0vWGGQw9Vb7QxMK.TSU6",
            langKey = "en"
        )
        systemUser.id = "user-0"
        systemUser.createdBy = "system"
        systemUser.createdAt = Instant.now()
        mongoTemplate.save(systemUser)

        val adminUser = User(
            "admin",
            "\$2a\$10\$4HsckIHTKJOTMEmre6.Uo.9RVrE9xPFm7yox4mACyEnPlu3VRp/9G",
            langKey = "en"
        )
        adminUser.id = "user-1"
        adminUser.createdBy = "system"
        adminUser.createdAt = Instant.now()
        adminUser.authorities.add(adminAuthority)
        adminUser.authorities.add(userAuthority)
        mongoTemplate.save(adminUser)

        val userUser = User(
            "user",
            "\$2a\$10\$hUGvgS.EuutOqJRVd.ZZeOlU/ATjtuHJ90qt2wk.evTfxAma9JD6O",
            langKey = "en"
        )
        userUser.id = "user-2"
        userUser.createdBy = "admin"
        userUser.createdAt = Instant.now()
        userUser.authorities.add(userAuthority)
        mongoTemplate.save(userUser)
    }

    @ChangeSet(order = "03", author = "navid", id = "03-addBanks")
    fun addBanks(mongoTemplate: MongoTemplate) {
        val banks = arrayOf(
            "Visa",
            "Maestro",
            "MasterCard",
            "Discover",
            "Diners Club",
            "JBC",
            "Amex",
            "Unknown",
            "China UnionPay"
        )

        banks.map(::Bank).map(mongoTemplate::save)
    }
}
