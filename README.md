# 📘 RestDocs-DSL for Kotlin

Kotlin 기반의 DSL(도메인 특화 언어)을 활용하여 **Spring REST Docs** 문서 작성을 더 간결하고 읽기 쉽게 만들어주는 라이브러리입니다.

토스 기술 블로그 해당 [Kotlin으로 DSL 만들기: 반복적이고 지루한 REST Docs 벗어나기](https://toss.tech/article/kotlin-dsl-restdocs) 글을 보고 만들었습니다.  
> ✨ 복잡한 설정 없이, 깔끔하고 선언적인 방식으로 API 문서를 작성해보세요!

---

## ✨ 주요 기능

- ✅ Spring REST Docs 기반 DSL 제공
- ✅ 요청/응답 필드 문서를 선언형으로 정의 가능
- ✅ 테스트 코드와 문서 생성을 자연스럽게 통합
- ✅ Enum, Object 타입에 대한 자동 문서화 지원

---

## 🛠 설치 방법

현재는 별도 배포 없이 로컬 모듈로 포함하여 사용 가능합니다.

### 1. 프로젝트에 모듈 추가

`settings.gradle.kts` 또는 `settings.gradle`에 다음을 추가합니다.

```kotlin
include(":restdocs-dsl")
```

### 2. 의존성 추가

```kotlin
// build.gradle.kts
dependencies {
    implementation(project(":restdocs-dsl"))
}
```

### 3. gradle.properties 정의

```properties
## openapi3 ##
openapi3ServerUrls=http://localhost:8080,https://dev.shower.me
openapi3Title=Sample-API
openapi3Description=Sample-API-Description
openapi3DocsVersion=0.1
openapi3OutDirectory=build/api-spec
openapi3IntoDirectory=src/main/resources/static/
openapi3JsonName=sample-docs

```

### 4. swagger.yml 추가

```yaml
springdoc:
  default-consumes-media-type: application/json;charset=UTF-8
  default-produces-media-type: application/json;charset=UTF-8
  swagger-ui:
    url: /static/sample-docs.yaml #gradle.properties openapi3JsonName
```

###

### 5. 📂 디렉토리 구조 예시

```swift
├── restdocs-dsl/
│   ├── src/main/kotlin/
│   │   └── dsl/ // DSL 구성 요소
│   └── build.gradle.kts
├── sample-api/
│   └── src/test/kotlin/
│       └── api/LoginApiTest.kt // DSL 사용 예시
```

### 6. 📄 RestDocs 코드 및 RestDocs-DSL 코드

### 📄 RestDocs Code

```kotlin
@DisplayName("로그인")
@Test
fun login() {
    every { service.login(any<Credentials>()) } returns Token("accessToken", "refreshToken")

    val response = given()
        .contentType(ContentType.JSON)
        .body(
            LoginRequest(
                loginId = "test@test.com",
                password = "test1234",
                socialType = "ME"
            )
        )
        .post("/api/v1/sample-login")
        .then()
        .status(HttpStatus.OK)
        .apply(
            document(
                "샘플 로그인",
                requestPreprocessor(),
                responsePreprocessor(),
                ResourceDocumentation.resource(
                    ResourceSnippetParameters.builder()
                        .tags(DocsTag.AUTH.tagName)
                        .summary("샘플 로그인")
                        .requestSchema(Schema.schema("LoginRequest"))
                        .requestFields(
                            fieldWithPath("loginId").description("로그인 아이디").type("String"),
                            fieldWithPath("password").description("패스워드").type("String"),
                            fieldWithPath("socialType").description("소셜 유형").type(SocialType::class),
                        )
                        .responseSchema(Schema.schema("TokenResponse"))
                        .responseFiedls(
                            fieldWithPath("result").description("응답").type(ResultType::class),
                            fieldWithPath("error").description("에러").optional(),
                            fieldWithPath("data.accessToken").description("accessToken").type("String"),
                            fieldWithPath("data.refreshToken").description("refreshToken").type("String"),
                        )
                        .build(),
                ),
            ),
        )
}
```

### 📄 RestDocs DSL Code

```kotlin
@DisplayName("로그인")
@Test
fun login() {
    every { service.login(any<Credentials>()) } returns Token("accessToken", "refreshToken")

    val response = given()
        .contentType(ContentType.JSON)
        .body(
            LoginRequest(
                loginId = "test@test.com",
                password = "test1234",
                socialType = "ME"
            )
        )
        .post("/api/v1/sample-login")
        .then()
        .status(HttpStatus.OK)

    response.makeDocument(
        identifier = "샘플 로그인",
        summary = "샘플 로그인",
        requestSchema = "LoginRequest",
        responseSchema = "TokenResponse",
        requestBody = requestBody(
            "loginId" type STRING means "로그인 아이디" example "test@test.com" isOptional false,
            "password" type STRING means "비밀번호" example "test1234",
            "socialType" type ENUM(SocialType::class) means "소셜 유형" example "ME",
        ),
        responseBody = responseBody(
            "result" type ENUM(ResultType::class) means "응답",
            "error" type OBJECT means "에러" isOptional true,
            "data.accessToken" type STRING means "accessToken",
            "data.refreshToken" type STRING means "refreshToken",
        ),
    )
}
```

### Run Shell Script
```shell
$ ./gradlew clean build tasks copyOasSwagger
```

### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui/index.html
