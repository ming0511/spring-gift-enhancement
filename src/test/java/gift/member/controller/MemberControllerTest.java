package gift.member.controller;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.member.builder.MemberBuilder;
import gift.member.dto.TokenResponseDto;
import gift.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class MemberControllerTest {

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

    private Member queryMemberById(int id) {
        return jdbcTemplate.queryForObject(
            "SELECT email, password, name, role FROM members WHERE memberId = ?",
            (rs, rowNum) -> new Member(
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("role")
            ),
            id
        );
    }

    private void assertThatMemberEquals(Member expected, Member actual) {
        Assertions.assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        Assertions.assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        Assertions.assertThat(actual.getName()).isEqualTo(expected.getName());
        Assertions.assertThat(actual.getRole()).isEqualTo(expected.getRole());
    }

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("DELETE FROM members");
        jdbcTemplate.execute("ALTER TABLE members ALTER COLUMN memberId RESTART WITH 1");

        String sql = "INSERT INTO members(email, password, name, role) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "one@email.com", "1234", "user", "ROLE_USER");
    }

    @Test
    void 회원가입_CREATED_테스트() {
        // given
        var request = MemberBuilder.aMember().build();

        // when
        var response = exchange(HttpMethod.POST, baseUrl() + "/register", request,
            new ParameterizedTypeReference<TokenResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        Member result = queryMemberById(2);
        assertThatMemberEquals(request, result);
    }

    @Test
    void 회원가입_BAD_REQUEST_이미존재하는이메일() {
        // given
        var request = MemberBuilder.aMember()
            .withEmail("one@email.com")
            .build();

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl() + "/register", request,
                    new ParameterizedTypeReference<TokenResponseDto>() {
                    })
            );
    }
}