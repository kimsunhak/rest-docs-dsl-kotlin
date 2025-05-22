package me.shower.docs.support

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse
import me.shower.docs.support.RestDocsDSLSupport.pathVariablesToSwagger
import me.shower.docs.support.RestDocsDSLSupport.requestHeadersToSwagger
import me.shower.docs.support.RestDocsDSLSupport.requestParametersToSwagger
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Snippet

object MockMvcExtensions {
    /**
     * RestDocs DSL makeDocument
     *
     * @param identifier [String] Document identifier
     * @param docsTag [String] DocsTag String
     * @param pathVariables [pathVariables] PathVariables
     * @param requestHeader [requestHeader] Request Headers
     * @param requestParameters [requestParameters] Parameter Fields
     * @param requestSchema [String] Request Schema name
     * @param responseSchema [String] Response Schema name
     * @param requestBody [requestBody] Request Body Fields
     * @param responseBody [responseBody] Response Body Fields
     */

    fun ValidatableMockMvcResponse.makeDocument(
        identifier: String,
        summary: String,
        pathVariables: List<ParameterDescriptor> = emptyList(),
        requestHeader: List<HeaderDescriptor> = emptyList(),
        requestParameters: List<ParameterDescriptor> = emptyList(),
        requestSchema: String = "",
        responseSchema: String = "",
        requestBody: List<FieldDescriptor> = emptyList(),
        responseBody: List<FieldDescriptor> = emptyList(),
    ): ValidatableMockMvcResponse {
        val builder = ResourceSnippetParameters.builder()
            .tags(identifier)
            .summary(summary)
            .requestSchema(Schema.schema(requestSchema))
            .responseSchema(Schema.schema(responseSchema))

        val snippets = mutableListOf<Snippet>()

        if (pathVariables.isNotEmpty()) {
            builder.pathParameters(pathVariablesToSwagger(pathVariables))
            snippets.add(RequestDocumentation.pathParameters(pathVariables))
        }
        if (requestHeader.isNotEmpty()) {
            builder.requestHeaders(requestHeadersToSwagger(requestHeader))
            snippets.add(HeaderDocumentation.requestHeaders(requestHeader))
        }
        if (requestParameters.isNotEmpty()) {
            builder.queryParameters(requestParametersToSwagger(requestParameters))
            snippets.add(RequestDocumentation.queryParameters(requestParameters))
        }
        if (requestBody.isNotEmpty()) {
            builder.requestFields(requestBody)
            snippets.add(PayloadDocumentation.requestFields(requestBody))
        }

        if (responseBody.isNotEmpty()) {
            builder.responseFields(responseBody)
            snippets.add(PayloadDocumentation.responseFields(responseBody))
        }
        return this.apply(
            MockMvcRestDocumentationWrapper.document(
                identifier = identifier,
                requestPreprocessor = RestDocsDSLSupport.requestPreprocessor(),
                responsePreprocessor = RestDocsDSLSupport.responsePreprocessor(),
                resourceDetails = builder,
                snippets = snippets.toTypedArray(),
            ),
        )
    }
}