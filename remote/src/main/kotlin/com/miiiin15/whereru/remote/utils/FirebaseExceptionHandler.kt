package com.miiiin15.whereru.remote.utils

import com.google.firebase.firestore.FirebaseFirestoreException

object FirebaseExceptionHandler {
    fun handle(e: Exception): String {
        return when (e) {
            is FirebaseFirestoreException -> {
                when (e.code) {
                    FirebaseFirestoreException.Code.NOT_FOUND -> "데이터를 찾을 수 없습니다."
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> "권한이 없습니다."
                    FirebaseFirestoreException.Code.UNAVAILABLE -> "서버에 접근할 수 없습니다."
                    else -> "Firestore 오류: ${e.message}"
                }
            }

            is NullPointerException -> "문서가 null입니다. 데이터 형식이 맞는지 확인해주세요."
            is IllegalArgumentException -> "요청이 잘못되었습니다. ${e.message}"
            else -> "${e.message}"
        }
    }
}