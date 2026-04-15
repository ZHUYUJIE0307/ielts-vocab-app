package com.ielts.vocab.data.importer

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ielts.vocab.data.local.dao.AssociationDao
import com.ielts.vocab.data.local.dao.WordDao
import com.ielts.vocab.data.local.entity.AssociationEntity
import com.ielts.vocab.data.local.entity.WordEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class WordJson(
    val word: String,
    val phonetic: String,
    val pos: String,
    val definitions: String,
    val isKeyWord: Boolean = false,
    val wordListIndex: Int
)

data class AssociationJson(
    val word: String,
    val roots: String = "",
    val association: String = "",
    val exampleSentence: String = ""
)

@Singleton
class WordDataImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wordDao: WordDao,
    private val associationDao: AssociationDao
) {
    private val gson = Gson()

    suspend fun importIfNeeded(hasImported: Boolean) {
        if (hasImported) return

        // Import words
        val wordsJson = context.assets.open("ielts_words.json")
            .bufferedReader().use { it.readText() }
        val wordListType = object : TypeToken<List<WordJson>>() {}.type
        val words: List<WordJson> = gson.fromJson(wordsJson, wordListType)

        val wordEntities = words.map { w ->
            WordEntity(
                word = w.word,
                phonetic = w.phonetic,
                pos = w.pos,
                definitions = w.definitions,
                isKeyWord = w.isKeyWord,
                wordListIndex = w.wordListIndex
            )
        }
        val insertedIds = wordDao.insertAll(wordEntities)

        // Import associations
        val hasAssociations = try {
            context.assets.open("word_associations.json").bufferedReader().use { it.readText() }
            true
        } catch (_: Exception) {
            false
        }

        if (hasAssociations) {
            val assocJson = context.assets.open("word_associations.json")
                .bufferedReader().use { it.readText() }
            val assocListType = object : TypeToken<List<AssociationJson>>() {}.type
            val associations: List<AssociationJson> = gson.fromJson(assocJson, assocListType)

            val assocEntities = mutableListOf<AssociationEntity>()
            for (assoc in associations) {
                val wordEntity = wordDao.getByWord(assoc.word) ?: continue
                assocEntities.add(
                    AssociationEntity(
                        wordId = wordEntity.id,
                        roots = assoc.roots,
                        association = assoc.association,
                        exampleSentence = assoc.exampleSentence
                    )
                )
            }
            if (assocEntities.isNotEmpty()) {
                associationDao.insertAll(assocEntities)
            }
        }
    }
}
