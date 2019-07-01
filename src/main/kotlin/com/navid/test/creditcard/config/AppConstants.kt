package com.navid.test.creditcard.config

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

sealed class AppConstants {

    sealed class Error {
        companion object {
            private const val BASE = "error"

            const val ERR_CONCURRENCY_FAILURE = "$BASE.concurrencyFailure"
            const val ERR_VALIDATION = "$BASE.validation"
            const val ERR_INVALID_TOKEN = "$BASE.invalid_token"
            const val ERR_DUPLICATED = "$BASE.duplicated"
            const val ERR_CREDENTIALS = "$BASE.credentials"
            const val ERR_NOT_FOUND = "$BASE.404"
            const val ERR_INTERNAL_ERROR = "$BASE.500"
            const val INVALID_PASSWORD = "$BASE.invalid_password"
            const val FIELD_REQUIRED_FOUND = "$BASE.field_required"
            const val FIELD_INVALID = "$BASE.field_invalid"

            sealed class Url {
                companion object {
                    private const val PROBLEM_BASE_URL = "/problem"

                    const val UI_TYPE: String = "$PROBLEM_BASE_URL/ui"
                    const val INVALID_TOKEN_TYPE: String = "$PROBLEM_BASE_URL/invalid-authorization-token"
                    const val CONSTRAINT_VIOLATION_TYPE: String = "$PROBLEM_BASE_URL/constraint-violation"
                    const val DUPLICATED_TYPE: String = "$PROBLEM_BASE_URL/duplicated"
                    const val FIELD_REQUIRED_TYPE: String = "$PROBLEM_BASE_URL/field-required"
                    const val INVALID_PASSWORD_TYPE: String = "$PROBLEM_BASE_URL/invalid-password"
                    const val USERNAME_NOT_FOUND_TYPE: String = "$PROBLEM_BASE_URL/username-not-found"
                    const val USER_NOT_FOUND_TYPE: String = "$PROBLEM_BASE_URL/user-not-found"
                    const val NEW_EMAIL_WITH_ID_TYPE: String = "$PROBLEM_BASE_URL/new-email-with-id"
                    const val NO_MATCHED_UPDATE_ID_TYPE: String = "$PROBLEM_BASE_URL/no-matched-update-id"
                    const val NO_ID_PROVIDED_TYPE: String = "$PROBLEM_BASE_URL/no-id-provided"
                    const val NOT_FOUND_TYPE: String = "$PROBLEM_BASE_URL/not-found"
                    const val NO_EMPTY_FIELD_PATCH_TYPE: String = "$PROBLEM_BASE_URL/no-empty-field-patch-allowed"
                    const val INVALID_CREDENTIAL_TYPE: String = "$PROBLEM_BASE_URL/bad-credential"
                    const val INTERNAL_TYPE: String = "$PROBLEM_BASE_URL/internal"
                    const val FORBIDDEN_TYPE: String = "$PROBLEM_BASE_URL/forbidden"
                    const val EXTERNAL_SOURCE_TYPE: String = "$PROBLEM_BASE_URL/external-source-error"
                    const val ACCESS_DENIED_TYPE: String = "$PROBLEM_BASE_URL/access-denied"
                    const val AUTHORIZATION_TYPE: String = "$PROBLEM_BASE_URL/authorization"
                    const val AUTHENTICATION_TYPE: String = "$PROBLEM_BASE_URL/authentication"
                }
            }
        }

        sealed class Crud {
            companion object {
                private const val BASE = "crud"
                private const val ERRORS = "$BASE.errors"

                const val CREATE_ID_EXISTS = "$ERRORS.create_id_exists"
                const val NO_EMPTY_PATCH_FIELDS_ALLOWED = "$ERRORS.no_empty_patch_fields_allowed"
                const val NOT_MATCHED_UPDATE_ID = "$ERRORS.no_matched_update_id"
                const val NO_ID_PROVIDED = "$ERRORS.no_id_provided"
            }
        }

        sealed class User {
            companion object {
                private const val BASE = "user"
                private const val ERRORS = "$BASE.errors"

                const val USER_NOT_FOUND = "$ERRORS.user_not_found"
                const val USERNAME_NOT_FOUND = "$ERRORS.username_not_found"
            }
        }

    }

    sealed class General {
        companion object {
            const val PROFILE_DEV = "dev"
            const val PROFILE_PROD = "prod"
        }
    }

    sealed class Security {
        companion object {
            const val USERNAME_PATTERN = "^[_.A-Za-z0-9-]*\$"
        }

        sealed class Jwt {
            companion object {
                const val TOKEN_PREFIX = "Bearer "
                const val HEADER_STRING = "Authorization"
                const val KEY_ID = "id"
                const val KEY_ROLES = "roles"
            }
        }

        sealed class Authority {
            companion object {
                const val PREFIX = "ROLE_"
                const val ACTUATOR = "ACTUATOR"
                const val ADMIN = "ADMIN"
                const val USER = "USER"
                const val ANONYMOUS = "ANONYMOUS"
            }
        }
    }
}
