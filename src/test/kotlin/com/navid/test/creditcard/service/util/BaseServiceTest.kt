package com.navid.test.creditcard.service.util

import com.navid.test.creditcard.domain.util.BaseModelWithId
import com.navid.test.creditcard.repository.util.BaseMongoRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import reactor.test.test
import javax.inject.Inject

@WithMockAdmin
abstract class BaseServiceTest<T : BaseModelWithId, R, S> where R : BaseMongoRepository<T>, S : BaseMongoService<T, R> {

    @Inject
    protected lateinit var repository: R

    @Inject
    protected lateinit var service: S

    protected lateinit var entity: T

    @Before
    open fun setUp() {
        repository.deleteAll().block()
    }

    @After
    open fun tearDown() {
        repository.deleteAll().block()
    }

    @Test
    fun testExistsTrue() {
        repository
            .save(entity)
            .flatMap { service.exists(it.id!!) }
            .test()
            .expectNext(true)
            .expectComplete()
            .verify()
    }

    @Test
    fun testExistsFalse() {
        service
            .exists("not-exists")
            .test()
            .expectNext(false)
            .expectComplete()
            .verify()
    }

    @Test
    fun testFindAllList() {
        repository
            .save(entity)
            .flatMapMany { service.all(Pageable.unpaged()) }
            .test()
            .expectNextCount(1)
            .expectComplete()
            .verify()
    }

    @Test
    fun testFindAllPage() {
        service
            .all(PageRequest.of(0, 1))
            .test()
            .expectNextCount(0)
            .expectComplete()
            .verify()
    }

    @Test
    fun testGet() {
        repository
            .save(entity)
            .flatMap { entity -> service.get(entity.id!!) }
            .test()
            .expectNextMatches { item -> item.id.isNullOrEmpty().not() }
            .expectComplete()
            .verify()
    }

    @Test
    fun testRemove() {
        repository
            .save(entity)
            .test()
            .expectNextCount(1)
            .expectComplete()
            .verify()

        repository
            .findAll()
            .test()
            .expectNextCount(1)
            .expectComplete()
            .verify()

        repository.findAll()
            .flatMap { entity -> service.delete(entity.id!!) }
            .test()
            .expectComplete()
            .verify()

        repository
            .findAll()
            .test()
            .expectNextCount(0)
            .expectComplete()
            .verify()
    }

    @Test
    fun testCreate() {
        service
            .create(entity)
            .test()
            .expectNextMatches { entity -> entity.id.isNullOrEmpty().not() }
            .expectComplete()
            .verify()
    }

}
