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
            "\$2y\$10\$8mp9LLCI0QBh44XYhpSy/e96eKQgNlK3xp9nhBg9lb3rxe5GVTVjq",
            langKey = "en"
        )
        systemUser.id = "user-0"
        systemUser.createdBy = "system"
        systemUser.createdAt = Instant.now()
        mongoTemplate.save(systemUser)

        val adminUser = User(
            "admin",
            "\$2y\$10\$h7LxAx01hCLa.FJQUJbSvOrT92ttYUvm2V8TsSZaa9onTTC1I1JUy",
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
            "\$2y\$10\$jjYb80qw74rDKgu15rKxdOc/GHiPdZ7z7E.YXzGUm2wn2/4dJ9uUe",
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
        mongoTemplate.save(Bank("HSBC Canada"))
        mongoTemplate.save(Bank("Royal Bank of Canada"))
        mongoTemplate.save(Bank("American Express"))
    }
}
