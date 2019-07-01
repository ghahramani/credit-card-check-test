package com.navid.test.creditcard.web.rest

import com.navid.test.creditcard.domain.Bank
import com.navid.test.creditcard.repository.mongo.BankRepository
import com.navid.test.creditcard.service.BankService
import com.navid.test.creditcard.web.rest.util.CrudResource
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/banks")
class BankResource(service: BankService) : CrudResource<Bank, BankRepository, BankService>(service)
