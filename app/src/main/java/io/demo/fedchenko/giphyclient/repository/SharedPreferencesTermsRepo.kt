package io.demo.fedchenko.giphyclient.repository

import android.content.SharedPreferences

class SharedPreferencesTermsRepo(private val preferences: SharedPreferences) :TermsRepo{
    private val term = "term"

    override fun saveTerms(terms: List<String>) {
        val editor = preferences.edit()
        editor.putStringSet(term, terms.toMutableSet())
        editor.apply()
    }

    override fun getTerms(): List<String> = preferences.getStringSet(term, emptySet()).toList()
}