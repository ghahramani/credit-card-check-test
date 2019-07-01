package com.navid.test.creditcard.config.database.nosql

import com.navid.test.creditcard.config.database.util.SearchCriteria
import com.navid.test.creditcard.config.database.util.SearchUtil
import org.bson.Document
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.IndexOperationsAdapter
import org.springframework.data.mongodb.core.index.MongoMappingEventPublisher
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexCreator
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 **/
class SimpleBaseGenericReactiveMongoRepository<ENTITY>(
    private val entityInformation: MappingMongoEntityInformation<ENTITY, String>,
    private val template: ReactiveMongoTemplate
) : SimpleReactiveMongoRepository<ENTITY, String>(entityInformation, template),
    BaseGenericReactiveMongoRepository<ENTITY> {

    private val eventPublisher: ApplicationEventPublisher?

    init {
        val context = template.converter.mappingContext as MongoMappingContext
        val indexCreator = MongoPersistentEntityIndexCreator(context) { collectionName ->
            IndexOperationsAdapter.blocking(template.indexOps(collectionName))
        }
        eventPublisher = MongoMappingEventPublisher(indexCreator)
    }

    override fun count(criteria: Set<SearchCriteria>): Mono<Long> {
        return template.count(SearchUtil.convertToMongoQuery(criteria), entityInformation.javaType)
    }

    override fun search(criteria: Set<SearchCriteria>, returnFields: Set<String>, pageable: Pageable): Flux<ENTITY> {
        var query = SearchUtil.convertToMongoQuery(criteria)

        for (field in returnFields) {
            query.fields().include(field)
        }

        query = query.with(pageable)
        return template.find(query, entityInformation.javaType)
    }

    override fun patch(id: String, fields: Map<String, Any>): Mono<ENTITY> {
        val collection = entityInformation.collectionName
        val query = Query(Criteria.where("_id").`is`(id))
        val document = Document()

        return findById(id)
            .flatMap { entity ->
                maybeEmitEvent(BeforeConvertEvent<ENTITY>(entity, collection))

                document.putAll(fields)

                val update = Update()

                fields
                    .filter { entry ->
                        !hashSetOf("_id", "createdAt", "createdBy", "modifiedAt", "modifiedBy").contains(entry.key)
                    }
                    .forEach { entry -> update.set(entry.key, entry.value) }

                maybeEmitEvent(BeforeSaveEvent<ENTITY>(entity, document, collection))

                template.updateFirst(query, update, collection)
            }
            .then(findById(id)).map { entity ->
                maybeEmitEvent(AfterSaveEvent<ENTITY>(entity, document, collection))
                entity
            }
    }

    private fun <T> maybeEmitEvent(event: MongoMappingEvent<T>) {
        eventPublisher?.publishEvent(event)
    }

}
