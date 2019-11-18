package io.demo.fedchenko.giphyclient.repository

interface TermsRepo {
    fun getTerms(): List<String>
    fun saveTerms(terms: List<String>)
}