package com.navid.test.creditcard.service

import com.navid.test.creditcard.CreditCardApplication
import com.navid.test.creditcard.config.AppConstants.Security.Authority.Companion.ADMIN
import com.navid.test.creditcard.config.AppConstants.Security.Authority.Companion.USER
import com.navid.test.creditcard.domain.Bank
import com.navid.test.creditcard.repository.mongo.BankRepository
import com.navid.test.creditcard.service.util.BaseServiceTest
import com.navid.test.creditcard.service.util.WithMockAdmin
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.test
import org.springframework.security.access.AccessDeniedException as SecurityAccessAccessDeniedException

/**
 * BankService Tester.
 *
 * @author Navid Ghahremani <ghahramani.navid></ghahramani.navid>@gmail.com>
 * @version 1.0
 * @since <pre>Mar 28, 2017</pre>
 */

@WithMockAdmin
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [CreditCardApplication::class])
class BankServiceTest : BaseServiceTest<Bank, BankRepository, BankService>() {

    init {
        entity = Bank("Bank 1")
    }

    /**
     * Expected to throws exception because of authentication credential and user role
     * Method: delete(String id)
     */
    @Test
    @WithMockUser(roles = [USER])
    fun testRemoveUnauthorized() {
        service.delete("")
            .test()
            .expectError(SecurityAccessAccessDeniedException::class.java)
            .verify()
    }

    /**
     * Expected to throws exception because of authentication credential and user role
     * Method: create(Bank entity)
     */
    @Test
    @WithMockUser(roles = [USER])
    fun testSaveUnauthorized() {
        service.create(Bank("Bank 1"))
            .test()
            .expectError(SecurityAccessAccessDeniedException::class.java)
            .verify()
    }

    /**
     * Method: create(Bank entity)
     */
    @Test
    @WithMockUser(roles = [ADMIN])
    fun testSaveAuthorized() {
        service.create(Bank("Bank 1"))
            .test()
            .expectNextCount(1)
            .expectComplete()
            .verify()
    }

}
