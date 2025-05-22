package me.shower.docs.support

import com.epages.restdocs.apispec.HeaderDescriptorWithType
import com.epages.restdocs.apispec.HeaderDescriptorWithType.Companion.fromHeaderDescriptor
import com.epages.restdocs.apispec.ParameterDescriptorWithType
import com.epages.restdocs.apispec.ParameterDescriptorWithType.Companion.fromParameterDescriptor
import me.shower.docs.data.Field
import me.shower.docs.data.Header
import me.shower.docs.data.Parameter
import me.shower.docs.data.RestDocsAttributeKeys.Companion.KEY_DEFAULT_VALUE
import me.shower.docs.data.RestDocsAttributeKeys.Companion.KEY_FORMAT
import me.shower.docs.data.RestDocsAttributeKeys.Companion.KEY_PARAM_TYPE
import me.shower.docs.data.RestDocsAttributeKeys.Companion.KEY_PART_TYPE
import me.shower.docs.data.RestDocsAttributeKeys.Companion.KEY_SAMPLE
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.snippet.Attributes

object RestDocsDSLSupport {
    fun requestPreprocessor(): OperationRequestPreprocessor {
        return Preprocessors.preprocessRequest(
            Preprocessors.modifyUris().scheme("http").host("dev.drunk.com").removePort(),
            Preprocessors.prettyPrint(),
        )
    }

    fun responsePreprocessor(): OperationResponsePreprocessor =
        Preprocessors.preprocessResponse(Preprocessors.prettyPrint())

    /**
     * RestDocs headers
     *
     * @param descriptors [Header] 헤더 설명
     */
    fun headers(vararg descriptors: Header): List<HeaderDescriptor> {
        return descriptors.map { it.descriptor }
    }

    fun requestHeadersToSwagger(
        headerDescriptors: List<HeaderDescriptor>,
    ): List<HeaderDescriptorWithType> {
        return headerDescriptors.map { fromHeaderDescriptor(it) }
    }

    /**
     * RestDocs pathVariables
     *
     * @param descriptors [Parameter] 파라미터 설명
     */
    fun pathVariables(vararg descriptors: Parameter): List<ParameterDescriptor> {
        return descriptors.map { it.descriptor }
    }

    fun pathVariablesToSwagger(
        parameterDescriptors: List<ParameterDescriptor>,
    ): List<ParameterDescriptorWithType> {
        return parameterDescriptors.map { fromParameterDescriptor(it) }
    }

    /**
     * RestDocs requestParameters
     *
     * @param descriptors [Parameter] 파라미터 설명
     */
    fun requestParameters(vararg descriptors: Parameter): List<ParameterDescriptor> {
        return descriptors.map { it.descriptor }
    }

    fun requestParametersToSwagger(
        parameterDescriptors: List<ParameterDescriptor>,
    ): List<ParameterDescriptorWithType> {
        return parameterDescriptors.map { fromParameterDescriptor(it) }
    }

    /**
     * RestDocs requestBody
     *
     * @param descriptors [Field] Request Field 설명
     */
    fun requestBody(vararg descriptors: Field): List<FieldDescriptor> {
        return descriptors.map { it.descriptor }
    }

    /**
     * RestDocs responseBody
     *
     * @param descriptors [Field] Response Field 설명
     */
    fun responseBody(vararg descriptors: Field): List<FieldDescriptor> {
        return descriptors.map { it.descriptor }
    }

    fun defaultValue(value: String) = Attributes.Attribute(KEY_DEFAULT_VALUE, value)

    fun customFormat(value: String) = Attributes.Attribute(KEY_FORMAT, value)

    fun customSample(value: String) = Attributes.Attribute(KEY_SAMPLE, value)

    fun customPartType(value: String) = Attributes.Attribute(KEY_PART_TYPE, value)

    fun customParamType(value: String) = Attributes.Attribute(KEY_PARAM_TYPE, value)

    fun emptyDefaultValue() = defaultValue("")

    fun emptyFormat() = customFormat("")

    fun emptySample() = customSample("")

    const val DATE_FORMAT = "yyyy-MM-dd"
    const val DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS"
}