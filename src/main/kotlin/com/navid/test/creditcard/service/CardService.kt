package com.navid.test.creditcard.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.navid.test.creditcard.domain.Card
import com.navid.test.creditcard.domain.util.CardType
import com.navid.test.creditcard.repository.mongo.CardRepository
import com.navid.test.creditcard.service.dto.CardCSVDTO
import com.navid.test.creditcard.service.util.BaseMongoService
import org.simpleflatmapper.csv.CsvMapperFactory
import org.simpleflatmapper.csv.CsvParser
import org.simpleflatmapper.util.TypeReference
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.toFlux
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Developed by Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Service
class CardService(
    repository: CardRepository,
    private val bankService: BankService,
    private val userService: UserService,
    private val objectMapper: ObjectMapper
) : BaseMongoService<Card, CardRepository>(repository) {

    private val formatter = DateTimeFormatter.ofPattern("MMM-yyyy").withZone(ZoneId.systemDefault())

    fun findAllByType(type: CardType, pageable: Pageable) = repository.findByType(type, pageable)

    fun findAllByUser(userId: String, pageable: Pageable) = repository.findByUserId(userId, pageable)

    fun findByNumber(number: String) = repository.findOneByNumber(number)

    fun saveFromCSV(username: String, filePart: MultipartFile) =
        // TODO: better to use coroutine
        userService.findByUsername(username)
            .flatMapMany { user ->
                filePart.inputStream.bufferedReader()
                    .use { reader ->
                        // Using skip(1) to skip the header in CSV
                        val mapper = generateCsvMapper()

                        CsvParser
                            .mapWith(mapper)
                            // Copying the whole CSV to memory is not efficent but it does the job for the test
                            .stream(reader.readText())
                            .toFlux()
                            .flatMap { items ->
                                items["expiryDate"] = YearMonth.parse(items["expiryDate"] as String, formatter)

                                val entity = objectMapper.convertValue<CardCSVDTO>(items)
                                entity.number = entity.number?.replace("-", "")

                                if (entity.bank.isNullOrEmpty()) {
                                    logger.warn("Bank name is empty, maybe the row is blank in CSV file")
                                    throw IllegalArgumentException("Bank name is empty")
                                }

                                if (entity.type == null) {
                                    logger.warn("Card type is empty, maybe the row is blank in CSV file")
                                    throw IllegalArgumentException("Card type is empty")
                                }

                                if (entity.expiryDate == null) {
                                    logger.warn("Card expiry date is empty, maybe the row is blank in CSV file")
                                    throw IllegalArgumentException("Card expiry date is empty")
                                }

                                if (entity.number.isNullOrEmpty()) {
                                    logger.warn("Card number is empty, maybe the row is blank in CSV file")
                                    throw IllegalArgumentException("Card number is empty")
                                }

                                bankService.findByName(entity.bank as String)
                                    .flatMap {
                                        create(
                                            Card(
                                                bank = it,
                                                type = entity.type as CardType,
                                                expiryDate = entity.expiryDate as YearMonth,
                                                number = entity.number as String,
                                                user = user
                                            )
                                        )
                                    }
                            }
                    }
            }

    private fun generateCsvMapper() =
        CsvMapperFactory
            .newInstance()
            .addAlias("Bank", "bank")
            .addAlias("Card number", "number")
            .addAlias("Expiry date", "expiryDate")
            .newMapper(object : TypeReference<HashMap<String, Any>>() {})

}
