package com.toxiclens.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.pdfBrandingDataStore by preferencesDataStore(
    name = "pdf_branding_store"
)

data class PdfBranding(
    val logoUri: String = "",
    val companyName: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val address: String = ""
)

class PdfBrandingStore(
    private val context: Context
) {
    private val logoUriKey = stringPreferencesKey("logo_uri")
    private val companyNameKey = stringPreferencesKey("company_name")
    private val phoneKey = stringPreferencesKey("phone")
    private val emailKey = stringPreferencesKey("email")
    private val websiteKey = stringPreferencesKey("website")
    private val addressKey = stringPreferencesKey("address")

    suspend fun saveBranding(
        branding: PdfBranding
    ) {
        context.pdfBrandingDataStore.edit { preferences ->
            preferences[logoUriKey] = branding.logoUri
            preferences[companyNameKey] = branding.companyName
            preferences[phoneKey] = branding.phone
            preferences[emailKey] = branding.email
            preferences[websiteKey] = branding.website
            preferences[addressKey] = branding.address
        }
    }

    suspend fun getBranding(): PdfBranding {
        val preferences = context.pdfBrandingDataStore.data.first()

        return PdfBranding(
            logoUri = preferences[logoUriKey] ?: "",
            companyName = preferences[companyNameKey] ?: "",
            phone = preferences[phoneKey] ?: "",
            email = preferences[emailKey] ?: "",
            website = preferences[websiteKey] ?: "",
            address = preferences[addressKey] ?: ""
        )
    }
}