package com.navid.test.creditcard.repository.mongo

import com.navid.test.creditcard.domain.Card
import com.navid.test.creditcard.domain.util.CardType
import com.navid.test.creditcard.repository.util.BaseMongoRepository
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Spring Data MongoDB repository for the Card entity.
 */
interface CardRepository : BaseMongoRepository<Card> {

    fun findByType(type: CardType, pageable: Pageable): Flux<Card>

    fun findByUserId(id: String, pageable: Pageable): Flux<Card>

    fun findOneByNumber(number: String): Mono<Card>

}
