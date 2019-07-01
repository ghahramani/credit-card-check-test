package com.navid.test.creditcard.web.rest.error

import java.io.Serializable

data class FieldErrorVM(val objectName: String, val field: String, val message: String) : Serializable
