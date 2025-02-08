package com.miiiin15.whereru.common.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    /**
     * 현재 시간을 yyyy-MM-dd HH:mm:ss 형태의 문자열로 반환
     * ex) "2025-03-28 17:35:42"
     */
    fun nowAsFormattedString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * 현재 시간을 ISO 8601 포맷으로 반환
     * ex) "2025-03-28T08:35:42Z"
     */
    fun nowAsIsoString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    /**
     * 밀리초(Long) 값을 yyyy-MM-dd HH:mm:ss 형태로 변환
     * ex) 1743138428790 → "2025-04-27 23:47:08"
     */
    fun millisToFormattedString(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    /**
     * 밀리초(Long) 값을 사람이 이해하기 쉬운 상대 시간 문자열로 변환
     * ex) 지금 기준
     *  - 10초 전 → "방금 전"
     *  - 5분 전 → "5분 전"
     *  - 3시간 전 → "3시간 전"
     *  - 어제 → "어제"
     *  - 5일 전 → "5일 전"
     *  - 3주 전 → "3주 전"
     *  - 2달 전 → "2달 전"
     *  - 1년 전 → "1년 전"
     */
    fun Long.toRelativeTime(): String {
        val now = System.currentTimeMillis()
        val diff = now - this

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            seconds < 60 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            days == 1L -> "어제"
            days < 7 -> "${days}일 전"
            weeks < 5 -> "${weeks}주 전"
            months < 12 -> "${months}달 전"
            else -> "${years}년 전"
        }
    }
}