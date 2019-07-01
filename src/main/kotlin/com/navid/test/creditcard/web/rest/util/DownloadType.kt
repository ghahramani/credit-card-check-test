package com.navid.test.creditcard.web.rest.util

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.navid.test.creditcard.config.serdes.deserializer.BaseEnum
import com.navid.test.creditcard.web.rest.util.DownloadType.Deserializer

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 **/

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = Deserializer::class)
enum class DownloadType(override val key: String) : BaseEnum<String> {

    CSV("csv"),
    XLSX("xlsx"),
    PDF("pdf"),
    XML("xml");

    class Deserializer : BaseEnum.Deserializer<DownloadType>(DownloadType::class, values())

}
