package com.navid.test.creditcard.cucumber.stepdefs

import com.navid.test.creditcard.CreditCardApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.reactive.server.WebTestClient

@WebAppConfiguration
@SpringBootTest(classes = [CreditCardApplication::class])
@ContextConfiguration(classes = [CreditCardApplication::class])
abstract class StepDefs {

    protected var actions: WebTestClient.ResponseSpec? = null

}
