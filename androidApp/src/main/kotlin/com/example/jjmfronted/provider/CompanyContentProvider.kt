package com.example.jjmfronted.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.jjmfronted.network.ApiClient
import kotlinx.coroutines.runBlocking

class CompanyContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.jjmfronted.provider"
        const val PATH_COMPANIES = "companies"
        const val PATH_VACANTES = "vacantes"

        val CONTENT_URI_COMPANIES = Uri.parse("content://$AUTHORITY/$PATH_COMPANIES")
        val CONTENT_URI_VACANTES = Uri.parse("content://$AUTHORITY/$PATH_VACANTES")

        private const val COMPANIES = 1
        private const val VACANTES = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH_COMPANIES, COMPANIES)
            addURI(AUTHORITY, PATH_VACANTES, VACANTES)
        }
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            COMPANIES -> queryCompanies()
            VACANTES -> queryVacantes()
            else -> null
        }
    }

    private fun queryCompanies(): Cursor {
        val columns = arrayOf("_id", "name", "industry", "address", "latitude", "longitude")
        val cursor = MatrixCursor(columns)

        runBlocking {
            val result = ApiClient.getCompaniesMap()
            result.fold(
                onSuccess = { companies ->
                    companies.forEach { company ->
                        cursor.addRow(
                            arrayOf<Any>(
                                company.id,
                                company.name ?: "",
                                company.industry ?: "",
                                company.address ?: "",
                                company.latitude ?: 0.0,
                                company.longitude ?: 0.0
                            )
                        )
                    }
                },
                onFailure = { }
            )
        }
        return cursor
    }

    private fun queryVacantes(): Cursor {
        val columns = arrayOf("_id", "title", "companyName", "description", "area", "location", "slots")
        val cursor = MatrixCursor(columns)

        runBlocking {
            val result = ApiClient.getVacantes()
            result.fold(
                onSuccess = { vacantes ->
                    vacantes.forEach { vacante ->
                        cursor.addRow(
                            arrayOf<Any>(
                                vacante.id,
                                vacante.title,
                                vacante.companyName,
                                vacante.description,
                                vacante.area ?: "",
                                vacante.location ?: "",
                                vacante.slots
                            )
                        )
                    }
                },
                onFailure = { }
            )
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            COMPANIES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.companies"
            VACANTES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.vacantes"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
}