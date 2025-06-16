import io.mockk.every
import java.io.IOException
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import com.univcert.api.UnivCert
import org.junit.jupiter.api.Test
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import com.team573.gongguri.domain.member.service.UnivCertificationService
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import io.kotest.assertions.throwables.shouldThrow

class UnivCertificationServiceTests {

    private val service = UnivCertificationService()

    // 외부 정적 메서드 mocking
    @BeforeEach
    fun setup() {
        mockkStatic(UnivCert::class)
        val apiKeyField = UnivCertificationService::class.java.getDeclaredField("apiKey").apply { isAccessible = true }
        apiKeyField.set(service, "dummyApiKey")
    }

    // 테스트 후 메서드 mocking 해제
    @AfterEach
    fun teardown() {
        unmockkStatic(UnivCert::class)
    }

    @Test
    fun `verifyEmailCode는 성공 시 ture를 반환한다`() {
        // given
        val email = "test@test.com"
        val univName = "대학교"
        val code = "1234"

        every {
            UnivCert.certifyCode(any(), email, univName, code.toInt())
        } returns mapOf(
            "success" to true,
            "univName" to univName,
            "certified_email" to email
        )

        // when
        val result = service.verifyEmailCode(email, univName, code)

        // then
        result shouldBe true
    }

    @Test
    fun `verifyEmailCode는 인증 실패 시 CustomException을 던진다`() {
        // given
        val email = "test@test.com"
        val univName = "대학교"
        val code = "1234"

        every {
            UnivCert.certifyCode(any(), email, univName, code.toInt())
        } returns mapOf(
            "success" to false,
            "message" to "인증번호가 일치하지 않습니다.",
            "status" to 400
        )

        // when
        val exception = shouldThrow<CustomException> {
            service.verifyEmailCode(email, univName, code)
        }

        // then
        exception.getCustomErrorCode() shouldBe CustomErrorCode.VERIFICATION_CODE_MISMATCH
    }

    @Test
    fun `verifyEmailCode는 IOException 발생 시 CustomException을 던진다`() {
        // given
        val email = "test@test.com"
        val univName = "대학교"
        val code = "1234"

        every {
            UnivCert.certifyCode(any(), email, univName, code.toInt())
        } throws IOException("API Error")

        // when
        val exception = shouldThrow<CustomException> {
            service.verifyEmailCode(email, univName, code)
        }

        // then
        exception.getCustomErrorCode() shouldBe CustomErrorCode.VERIFICATION_SERVER_ERROR
    }
}
