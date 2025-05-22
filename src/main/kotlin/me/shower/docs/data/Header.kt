package me.shower.docs.data

import me.shower.docs.data.RestDocsAttributeKeys.Companion.KEY_DEFAULT_VALUE
import me.shower.docs.data.RestDocsAttributeKeys.Companion.KEY_FORMAT
import me.shower.docs.data.RestDocsAttributeKeys.Companion.KEY_PARAM_TYPE
import me.shower.docs.data.RestDocsAttributeKeys.Companion.KEY_SAMPLE
import me.shower.docs.support.RestDocsDSLSupport
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName

sealed class DocsHeaderType(
    val type: String,
)

data object Authorization : DocsHeaderType("Authorization")

open class Header(
    val descriptor: HeaderDescriptor,
) {
    val isOptional: Boolean = descriptor.isOptional
    protected open var default: String
        get() = descriptor.attributes.getOrDefault(KEY_DEFAULT_VALUE, "") as String
        set(value) {
            descriptor.attributes(RestDocsDSLSupport.defaultValue(value))
        }

    protected open var format: String
        get() = descriptor.attributes.getOrDefault(KEY_FORMAT, "") as String
        set(value) {
            descriptor.attributes(RestDocsDSLSupport.customFormat(value))
        }

    protected open var sample: String
        get() = descriptor.attributes.getOrDefault(KEY_SAMPLE, "") as String
        set(value) {
            descriptor.attributes(RestDocsDSLSupport.customSample(value))
        }

    protected open var type: String
        get() = descriptor.attributes.getOrDefault(KEY_PARAM_TYPE, "") as String
        set(value) {
            descriptor.attributes(RestDocsDSLSupport.customParamType(value))
        }

    open infix fun typedAs(value: String): Header {
        this.type = value
        return this
    }

    open infix fun attributes(block: Header.() -> Unit): Header {
        block()
        return this
    }

    open infix fun example(value: String): Header {
        this.sample = value
        return this
    }

    open infix fun isOptional(value: Boolean): Header {
        if (value) descriptor.optional()
        return this
    }
}

infix fun String.headerType(type: DocsHeaderType): Header {
    val header = createHeader(this, false)
    header typedAs type.type
    return header
}

private fun createHeader(value: String, optional: Boolean): Header {
    val descriptor = headerWithName(value).attributes(
        RestDocsDSLSupport.emptySample(),
        RestDocsDSLSupport.emptyFormat(),
        RestDocsDSLSupport.emptyDefaultValue(),
    ).description("")

    if (optional) descriptor.optional()

    return Header(descriptor)
}
