package me.shower.docs.support

object EnumFormattingSupport {
    fun enumFormat(enums: Collection<Any>): String {
        return enums.joinToString(separator = "|")
    }
}
