package com.andikas.storyapp.utils.validator

interface Validator {
    fun validate(validatorResult: ValidatorResult)
    fun validationResult(): Boolean
}

interface ValidatorResult {
    fun validateResult()
}