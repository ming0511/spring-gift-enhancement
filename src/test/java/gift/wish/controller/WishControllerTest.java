package gift.wish.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gift.member.security.JwtTokenProvider;
import gift.wish.dto.WishCreateRequestDto;
import gift.wish.dto.WishCreateResponseDto;
import gift.wish.dto.WishPageResponseDto;
import gift.wish.entity.Wish;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class WishControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient client = RestClient.builder().build();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    String userToken;
    String adminToken;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/wishes";
    }

    private <T> ResponseEntity<T> exchange(HttpMethod method,
        String url,
        String token,
        Object body,
        ParameterizedTypeReference<T> type) {

        var request = client.method(method)
            .uri(url);

        if (token != null) {
            request = request.headers(headers -> headers.setBearerAuth(token));
        }

        if (body != null) {
            request = request.body(body);
        }

        return request.retrieve()
            .toEntity(type);
    }

    private Wish queryWishById(int id) {
        return jdbcTemplate.queryForObject(
            "SELECT wishId, memberId, productId, createdDate FROM wishes WHERE wishId = ?",
            (rs, rowNum) -> new Wish(
                rs.getLong("wishId"),
                rs.getLong("memberId"),
                rs.getLong("productId"),
                rs.getTimestamp("createdDate").toLocalDateTime()),
            id
        );
    }

    Stream<String> tokenProvider() {
        return Stream.of(userToken, adminToken);
    }

    @BeforeAll
    void beforeAll() {
        jdbcTemplate.execute("DELETE FROM members");
        jdbcTemplate.execute("ALTER TABLE members ALTER COLUMN memberId RESTART WITH 1");

        String sql = "INSERT INTO members(email, password, name, role) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "user@email.com", "1234", "user", "ROLE_USER");
        jdbcTemplate.update(sql, "admin@email.com", "1234", "admin", "ROLE_ADMIN");

        userToken = jwtTokenProvider.generateToken(1L, "user@email.com", "ROLE_USER");
        adminToken = jwtTokenProvider.generateToken(2L, "admin@email.com", "ROLE_ADMIN");

        // product
        jdbcTemplate.execute("DELETE FROM products");
        jdbcTemplate.execute("ALTER TABLE products ALTER COLUMN productId RESTART WITH 1");

        String productSql = "INSERT INTO products(name, price, imageUrl, mdConfirmed) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(productSql, "one", "1", "https://1.img", "false");
        jdbcTemplate.update(productSql, "two", "2", "https://2.img", "false");
        jdbcTemplate.update(productSql, "three", "3", "https://3.img", "false");
    }

    @BeforeEach
    void setUp() {
        // wish
        jdbcTemplate.execute("DELETE FROM wishes");
        jdbcTemplate.execute("ALTER TABLE wishes ALTER COLUMN wishId RESTART WITH 1");

        String wishSql = "INSERT INTO wishes(memberId, productId) VALUES (?, ?)";
        jdbcTemplate.update(wishSql, "1", "1");
        jdbcTemplate.update(wishSql, "1", "2");
        jdbcTemplate.update(wishSql, "2", "1");
    }

    // POST
    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 위시상품추가_CREATED_성공(String token) {
        // given
        var request = new WishCreateRequestDto(3L);

        // when
        var response = exchange(HttpMethod.POST, baseUrl(), token, request,
            new ParameterizedTypeReference<WishCreateResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var actual = response.getBody();
        System.out.println(actual);
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 위시상품추가_BAD_REQUEST_유효성검사실패(String token) {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl(), token, null,
                    new ParameterizedTypeReference<WishCreateResponseDto>() {
                    })
            );
    }

    @Test
    void 위시상품추가_UNAUTHORIZED_토큰없음() {
        // given
        var request = new WishCreateRequestDto(3L);

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl(), null, request,
                    new ParameterizedTypeReference<WishCreateResponseDto>() {
                    })
            );
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
        // TODO: 이후 수량 변경에 활용할 수 있어서 따로 예외 처리 안함.
    void 위시상품추가_500_테스트(String token) {
        // given
        var request = new WishCreateRequestDto(1L);

        // when & then
        assertThatExceptionOfType(HttpServerErrorException.InternalServerError.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl(), token, request,
                    new ParameterizedTypeReference<WishCreateResponseDto>() {
                    })
            );
    }

    // GET
    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 위시상품조회_OK_성공(String token) {
        // given & when
        var response = exchange(HttpMethod.GET, baseUrl() + "?page=0&size=10&sort=createdDate,desc",
            token, null, new ParameterizedTypeReference<WishPageResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var actual = response.getBody();
        System.out.println(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "?page=0",
        "?size=10",
        "?sort=createdDate,desc",
        "?sort=createdDate",
        "?sort=createdDate,asc",
        "?page=10&size=5&sort=createdDate,asc"
    })
    void 위시상품조회_OK_유효성검사성공(String validUrl) {
        // given & when
        var response = exchange(HttpMethod.GET, baseUrl() + validUrl,
            userToken, null, new ParameterizedTypeReference<WishPageResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var actual = response.getBody();
        System.out.println(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "?size=0",
        "?sort=wishId,desc",
        "?sort=wishId",
        "?sort=wishId,asc"
    })
    void 위시상품조회_BAD_REQUEST_유효성검사실패(String validUrl) {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.GET, baseUrl() + validUrl,
                    userToken, null, new ParameterizedTypeReference<WishPageResponseDto>() {
                    })
            );
    }

    @Test
    void 위시상품조회_UNAUTHORIZED_토큰없음() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.GET, baseUrl() + "?page=0&size=10&createdDate,desc", null,
                    null,
                    new ParameterizedTypeReference<WishPageResponseDto>() {
                    })
            );
    }

    // DELETE
    @Test
    void 위시상품삭제_NO_CONTENT_성공() {
        // given & when
        var response = exchange(HttpMethod.DELETE, baseUrl() + "/1", userToken, null,
            new ParameterizedTypeReference<Void>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 위시상품삭제_NOT_FOUND_존재하지않은위시상품() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.NotFound.class)
            .isThrownBy(
                () -> exchange(HttpMethod.DELETE, baseUrl() + "/321", userToken,
                    null,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }

    @Test
    void 위시상품삭제_UNAUTHORIZED_토큰없음() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.DELETE, baseUrl() + "/1", null,
                    null,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }

    @Test
    void 위시상품삭제_FORBIDDEN_삭제권한없음() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.Forbidden.class)
            .isThrownBy(
                () -> exchange(HttpMethod.DELETE, baseUrl() + "/3", userToken,
                    null,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }


}