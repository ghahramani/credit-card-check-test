package com.navid.test.creditcard.web.rest

import com.navid.test.creditcard.config.security.jwt.AuthenticationToken
import com.navid.test.creditcard.domain.Card
import com.navid.test.creditcard.domain.util.CardType
import com.navid.test.creditcard.repository.mongo.CardRepository
import com.navid.test.creditcard.service.CardService
import com.navid.test.creditcard.web.rest.util.CrudResource
import io.micrometer.core.annotation.Timed
import org.springframework.data.domain.Pageable
import org.springframework.http.codec.multipart.Part
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux

@RestController
@RequestMapping("/api/cards")
class CardResource(service: CardService) : CrudResource<Card, CardRepository, CardService>(service) {

    fun allAuthorized(authentication: Mono<AuthenticationToken>, pageable: Pageable) =
        authentication.flatMapMany { token ->
            service.findAllByUser(token.principal as String, pageable)
        }

    @Timed
    @PostMapping("/csv")
    fun saveFromCsv(@RequestPart file: Mono<Part>, authentication: Mono<AuthenticationToken>) =
        authentication.flatMapMany { token ->
            file.flatMapMany { item -> service.saveFromCSV(token.principal as String, item) }
        }

    @Timed
    @GetMapping("/types")
    fun saveFromCsv(): Flux<CardType> = CardType.values().toFlux()

}
