package me.shower.docs.data

import me.shower.docs.support.EnumFormattingSupport
import me.shower.docs.support.RestDocsDSLSupport
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import kotlin.reflect.KClass

open class Field(
    val descriptor: FieldDescriptor,
) {
    val isIgnored: Boolean = descriptor.isIgnored
    val isOptional: Boolean = descriptor.isOptional

    protected open var default: String
        get() = descriptor.attributes.getOrDefault(RestDocsAttributeKeys.KEY_DEFAULT_VALUE, "") as String
        set(value) {
            descriptor.attributes(RestDocsDSLSupport.defaultValue(value))
        }

    open var format: String
        get() = descriptor.attributes.getOrDefault(RestDocsAttributeKeys.KEY_FORMAT, "") as String
        set(value) {
            descriptor.attributes(RestDocsDSLSupport.customFormat(value))
        }

    protected open var sample: String
        get() = descriptor.attributes.getOrDefault(RestDocsAttributeKeys.KEY_SAMPLE, "") as String
        set(value) {
            descriptor.attributes(RestDocsDSLSupport.customSample(value))
        }

    open infix fun means(value: String): Field {
        descriptor.description(value)
        return this
    }

    open infix fun attributes(block: Field.() -> Unit): Field {
        block()
        return this
    }

    open infix fun withDefaultValue(value: String): Field {
        this.default = value
        return this
    }

    open infix fun formattedAs(value: String): Field {
        this.format = value
        return this
    }

    open infix fun example(value: String): Field {
        this.sample = value
        return this
    }

    open infix fun isOptional(value: Boolean): Field {
        if (value) descriptor.optional()
        return this
    }

    open infix fun isIgnored(value: Boolean): Field {
        if (value) descriptor.ignored()
        return this
    }
}

data class ENUM<T : Enum<T>>(
    val enums: Collection<T>,
) : FieldType(JsonFieldType.STRING) {
    constructor(clazz: KClass<T>) : this(clazz.java.enumConstants.asList())
}

infix fun String.type(fieldType: FieldType): Field {
    val field = createField(this, fieldType.type, false)
    when (fieldType) {
        is DATE -> field formattedAs RestDocsDSLSupport.DATE_FORMAT
        is DATETIME -> field formattedAs RestDocsDSLSupport.DATETIME_FORMAT
        else -> {}
    }
    return field
}

infix fun <T : Enum<T>> String.type(enumFieldType: ENUM<T>): Field {
    val field = createField(this, JsonFieldType.STRING, false)
    field.format = EnumFormattingSupport.enumFormat(enumFieldType.enums)
    return field
}

private fun createField(value: String, type: JsonFieldType, optional: Boolean): Field {
    val descriptor = PayloadDocumentation.fieldWithPath(value)
        .type(type)
        .attributes(
            RestDocsDSLSupport.emptySample(),
            RestDocsDSLSupport.emptyFormat(),
            RestDocsDSLSupport.emptyDefaultValue()
        )
        .description("")

    if (optional) descriptor.optional()

    return Field(descriptor)
}
