package com.miiiin15.whereru.remote.utils

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

// FireStore 익스텐션
fun FirebaseFirestore.collectionRef(path: String): CollectionReference {
    return this.collection(path)
}

fun FirebaseFirestore.documentRef(collectionPath: String, documentId: String): DocumentReference {
    return this.collection(collectionPath).document(documentId)
}

// Collection 전체 가져오기
suspend inline fun <reified T> FirebaseFirestore.getCollection(
    collectionPath: String,
    errorLabel: String
): List<T> {
    return runCatching {
        this.collectionRef(collectionPath)
            .get()
            .await()
            .toObjects(T::class.java)
    }.getOrElse { throw Exception(FirebaseExceptionHandler.handle(it as Exception, errorLabel)) }
}

// Document 가져오기
suspend inline fun <reified T> FirebaseFirestore.getDocument(
    collectionPath: String,
    documentId: String,
    errorLabel: String
): T? {
    return runCatching {
        this.documentRef(collectionPath, documentId)
            .get()
            .await()
            .toObject(T::class.java)
    }.getOrElse { throw Exception(FirebaseExceptionHandler.handle(it as Exception, errorLabel)) }
}

// Document 범위 지정 해서 가져오기
suspend inline fun <reified T> FirebaseFirestore.getPaginatedDocuments(
    collectionPath: String?,
    orderByField: String,
    direction: Query.Direction = Query.Direction.DESCENDING,
    lastVisible: Any? = null,
    pageSize: Int,
    errorLabel: String
): List<T> {
    return runCatching {
        val query = this.collectionRef(collectionPath!!)
            .orderBy(orderByField, direction)

        val paginatedQuery = lastVisible?.let {
            query.startAfter(it)
        } ?: query

        paginatedQuery
            .limit(pageSize.toLong())
            .get()
            .await()
            .toObjects(T::class.java)
    }.getOrElse { throw Exception(FirebaseExceptionHandler.handle(it as Exception, errorLabel)) }
}

// Document 저장하기
suspend fun FirebaseFirestore.setDocument(
    collectionPath: String,
    documentId: String,
    data: Any,
    errorLabel: String
) {
    runCatching {
        this.documentRef(collectionPath, documentId)
            .set(data)
            .await()
    }.getOrElse { throw Exception(FirebaseExceptionHandler.handle(it as Exception, errorLabel)) }
}

// Document 업데이트
suspend fun FirebaseFirestore.updateDocument(
    collectionPath: String,
    documentId: String,
    updates: Map<String, Any>,
    errorLabel: String
) {
    runCatching {
        this.documentRef(collectionPath, documentId)
            .update(updates)
            .await()
    }.getOrElse { throw Exception(FirebaseExceptionHandler.handle(it as Exception, errorLabel)) }
}

// Document 삭제
suspend fun FirebaseFirestore.deleteDocument(
    collectionPath: String,
    documentId: String,
    errorLabel: String
) {
    runCatching {
        this.documentRef(collectionPath, documentId)
            .delete()
            .await()
    }.getOrElse { throw Exception(FirebaseExceptionHandler.handle(it as Exception, errorLabel)) }
}