package gift.member.controller;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.member.builder.MemberBuilder;
import gift.member.dto.TokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient client = RestClient.builder().build();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/members";
    }

    private <T> ResponseEntity<T> exchange(HttpMethod method,
        String url,
        Object body,
        ParameterizedTypeReference<T> type) {

        if (body == null) {
            return client.method(method)
                .uri(url)
                .retrieve()
                .toEntity(type);
        }

        return client.method(method)
            .uri(url)
            .body(body)
            .retrieve()
            .toEntity(type);
    }

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("DELETE FROM members");
        jdbcTemplate.execute("ALTER TABLE members ALTER COLUMN memberId RESTART WITH 1");

        String sql = "INSERT INTO members(email, password, name) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, "one@email.com", "1234", "user");
    }

    @Test
    void 로그인_OK_테스트() {
        // given
        var request = MemberBuilder.aMember()
            .withEmail("one@email.com")
            .withPassword("1234")
            .build();

        // when
        var response = exchange(HttpMethod.POST, baseUrl() + "/login", request,
            new ParameterizedTypeReference<TokenResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
        "invalid@email.com, 1234",
        "one@email.com, wrongpassword",
        "invalid@email.com, wrongpassword"
    })
    void 로그인_FORBIDDEN_로그인실패(String email, String password) {
        // given
        var request = MemberBuilder.aMember()
            .withEmail(email)
            .withPassword(password)
            .build();

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.Forbidden.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl() + "/login", request,
                    new ParameterizedTypeReference<TokenResponseDto>() {
                    })
            );
    }
}