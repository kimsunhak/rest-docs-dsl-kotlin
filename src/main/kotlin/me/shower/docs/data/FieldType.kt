package me.shower.docs.data

import org.springframework.restdocs.payload.JsonFieldType

sealed class FieldType(
    val type: JsonFieldType,
)

/**
 * ARRAY : 정렬 타입
 */
data object ARRAY : FieldType(JsonFieldType.ARRAY)

/**
 * BOOLEAN : 부울 타입
 */
data object BOOLEAN : FieldType(JsonFieldType.BOOLEAN)

/**
 * OBJECT : 오브젝트 타입
 */
data object OBJECT : FieldType(JsonFieldType.OBJECT)

/**
 * NUMBER : 숫자 타입
 */
data object NUMBER : FieldType(JsonFieldType.NUMBER)

/**
 * NULL : NULL 타입
 */
data object NULL : FieldType(JsonFieldType.NULL)

/**
 * STRING : 문자열 타입
 */
data object STRING : FieldType(JsonFieldType.STRING)

/**
 * ANY : 다중 타입
 */
data object ANY : FieldType(JsonFieldType.VARIES)

/**
 * DATE : 날짜 타입
 */
data object DATE : FieldType(JsonFieldType.STRING)

/**
 * DATETIME : 날짜 시간 타입
 */
data object DATETIME : FieldType(JsonFieldType.STRING)
