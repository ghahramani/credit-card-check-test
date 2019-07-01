package com.navid.test.creditcard.service

import com.navid.test.creditcard.config.AppConstants.Security.Authority.Companion.ADMIN
import com.navid.test.creditcard.domain.Bank
import com.navid.test.creditcard.repository.mongo.BankRepository
import com.navid.test.creditcard.service.util.BaseMongoService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

/**
 * Developed by Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Service
class BankService(repository: BankRepository) : BaseMongoService<Bank, BankRepository>(repository) {

    fun findByName(name: String) = repository.findOneByName(name)

    @PreAuthorize("hasRole('$ADMIN')")
    override fun delete(id: String) = super.delete(id)

    @PreAuthorize("hasRole('$ADMIN')")
    override fun create(model: Bank) = super.create(model)
}
