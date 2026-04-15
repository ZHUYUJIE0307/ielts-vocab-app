package com.ielts.vocab.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "associations",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["wordId"], unique = true)
    ]
)
data class AssociationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val roots: String = "",          // 词根词缀拆解
    val association: String = "",     // 谐音/情景联想
    val exampleSentence: String = ""  // 例句
)
