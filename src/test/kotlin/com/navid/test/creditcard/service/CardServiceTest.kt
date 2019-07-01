package com.navid.test.creditcard.service

import com.navid.test.creditcard.CreditCardApplication
import com.navid.test.creditcard.domain.Bank
import com.navid.test.creditcard.domain.Card
import com.navid.test.creditcard.domain.util.CardType
import com.navid.test.creditcard.repository.mongo.BankRepository
import com.navid.test.creditcard.repository.mongo.CardRepository
import com.navid.test.creditcard.repository.mongo.UserRepository
import com.navid.test.creditcard.service.util.BaseServiceTest
import com.navid.test.creditcard.service.util.WithMockAdmin
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.test.test
import java.time.YearMonth
import javax.inject.Inject

/**
 * CardService Tester.
 *
 * @author Navid Ghahremani <ghahramani.navid></ghahramani.navid>@gmail.com>
 * @version 1.0
 * @since <pre>Mar 28, 2017</pre>
 */
@WithMockAdmin
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [CreditCardApplication::class])
class CardServiceTest : BaseServiceTest<Card, CardRepository, CardService>() {

    private lateinit var bank: Bank

    @Inject
    private lateinit var bankRepository: BankRepository

    @Inject
    private lateinit var userRepository: UserRepository

    @Before
    override fun setUp() {
        super.setUp()

        bank = bankRepository.save(Bank("Bank Test")).block()!!
        val user = userRepository.findOneByUsername("admin").block()!!

        entity = Card(
            bank,
            YearMonth.now(),
            "1111222233334444",
            CardType.UNKNOWN,
            user
        )
    }

    @After
    override fun tearDown() {
        super.tearDown()

        bankRepository.deleteAll().block()
    }

    @Test
    fun testFindAllByUser() {
        entity = repository.save(entity).block()!!

        service
            .findAllByUser(entity.user.id!!, Pageable.unpaged())
            .test()
            .expectNextCount(1)
            .verifyComplete()

        repository.deleteById(entity.id!!).block()

        service
            .findAllByUser(entity.user.id!!, Pageable.unpaged())
            .test()
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    @Throws(Exception::class)
    fun testFindByNumber() {
        entity = repository.save(entity).block()!!

        service
            .findByNumber(entity.number)
            .test()
            .expectNextMatches { item -> item.type == entity.type && item.id == entity.id }
            .verifyComplete()
    }

    @Test
    fun testSaveFromCSV() {
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

        bankRepository.saveAll(banks.map(::Bank)).collectList().block()

        val inputFile = javaClass.classLoader.getResourceAsStream("static/sample-test.csv") ?: throw Exception("Unable to open CSV file")
        val file = MockMultipartFile("file", "sample-test.csv", "multipart/form-data", inputFile)

        service
            .all(PageRequest.of(0, 10))
            .test()
            .expectNextCount(0)
            .verifyComplete()

        service
            .saveFromCSV("admin", file)
            .collectList()
            .test()
            .expectNextMatches { items -> items.size == 34 }
            .verifyComplete()

        val all = repository.findAll().toIterable()

        // Checking type of each card
        for (card in all) {
            assertEquals(CardType.findCardType(card.number), card.type)
        }

        val pageable = PageRequest.of(0, 100)

        Flux
            .mergeSequential(
                service.findAllByType(CardType.UNKNOWN, pageable).collectList(),
                service.findAllByType(CardType.AMERICAN_EXPRESS, pageable).collectList(),
                service.findAllByType(CardType.CHINA_UNION, pageable).collectList(),
                service.findAllByType(CardType.DISCOVER, pageable).collectList(),
                service.findAllByType(CardType.MASTERCARD, pageable).collectList(),
                service.findAllByType(CardType.JCB, pageable).collectList(),
                service.findAllByType(CardType.MAESTRO, pageable).collectList(),
                service.findAllByType(CardType.VISA, pageable).collectList(),
                service.findAllByType(CardType.DINERS_CLUB, pageable).collectList()
            )
            .test()
            .expectNextMatches { items -> items.size == 1 }
            .expectNextMatches { items -> items.size == 2 }
            .expectNextMatches { items -> items.size == 6 }
            .expectNextMatches { items -> items.size == 2 }
            .expectNextMatches { items -> items.size == 5 }
            .expectNextMatches { items -> items.size == 3 }
            .expectNextMatches { items -> items.size == 6 }
            .expectNextMatches { items -> items.size == 2 }
            .expectNextMatches { items -> items.size == 7 }
            .verifyComplete()
    }

}
