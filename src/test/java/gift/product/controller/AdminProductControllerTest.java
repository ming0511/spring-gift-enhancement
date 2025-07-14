package gift.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import gift.member.security.JwtTokenProvider;
import gift.product.dto.ProductCreateRequestDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import gift.product.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductGetResponseDto sampleProduct;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    String userToken;
    String adminToken;

    @BeforeAll
    void beforeAll() {
        jdbcTemplate.execute("DELETE FROM members");
        jdbcTemplate.execute("ALTER TABLE members ALTER COLUMN memberId RESTART WITH 1");

        String sql = "INSERT INTO members(email, password, name, role) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "user@email.com", "1234", "user", "ROLE_USER");
        jdbcTemplate.update(sql, "admin@email.com", "1234", "admin", "ROLE_ADMIN");

        userToken =
            "Bearer " + jwtTokenProvider.generateToken(1L, "user@email.com", "ROLE_USER");
        adminToken =
            "Bearer " + jwtTokenProvider.generateToken(2L, "admin@email.com", "ROLE_ADMIN");
    }

    @BeforeEach
    void setUp() {
        sampleProduct = new ProductGetResponseDto(
            1L,
            "테스트 상품",
            10000.0,
            "https://test.jpg",
            true
        );
    }

    // POST
    @Test
    void 단건상품등록_페이지접속() throws Exception {
        mockMvc.perform(get("/admin/products/create")
                .header("Authorization", adminToken))
            .andExpect(status().isOk())
            .andExpect(view().name("product/create-product"));
    }

    @Test
    void 단건상품등록_성공() throws Exception {
        mockMvc.perform(post("/admin/products/create")
                .param("name", "상품A")
                .param("price", "10000")
                .param("imageUrl", "https://1.img")
                .param("mdConfirmed", "false")
                .header("Authorization", adminToken))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(productService, times(1)).saveProduct(any(ProductCreateRequestDto.class));
    }

    @Test
    void 단건상품등록_유효성검사_실패() throws Exception {
        mockMvc.perform(post("/admin/products/create")
                .param("name", "12345 12345 12345") // 15자 초과 유효성 실패 유도
                .param("price", "10000")
                .param("imageUrl", "https://1.img")
                .param("mdConfirmed", "false")
                .header("Authorization", adminToken))
            .andExpect(status().isOk())
            .andExpect(view().name("product/create-product"))
            .andExpect(model().attributeExists("errors", "productCreateRequestDto"));
    }

    @Test
    void 단건상품등록_실패() throws Exception {
        doThrow(new RuntimeException("DB 오류")).when(productService).saveProduct(any());

        mockMvc.perform(post("/admin/products/create")
                .param("name", "상품A")
                .param("price", "10000")
                .param("imageUrl", "url.jpg")
                .param("mdConfirmed", "true")
                .header("Authorization", adminToken))
            .andExpect(status().isOk())
            .andExpect(view().name("product/create-product"))
            .andExpect(model().attributeExists("errorMessage"));

    }

    @Test
    void 단건상품등록_UNAUTHORIZED_인증없음() throws Exception {
        doThrow(new RuntimeException("DB 오류")).when(productService).saveProduct(any());

        mockMvc.perform(post("/admin/products/create")
                .param("name", "상품A")
                .param("price", "10000")
                .param("imageUrl", "url.jpg")
                .param("mdConfirmed", "true"))
            .andExpect(status().isUnauthorized());

    }

    @Test
    void 단건상품등록_FORBIDDEN_권한없음() throws Exception {
        doThrow(new RuntimeException("DB 오류")).when(productService).saveProduct(any());

        mockMvc.perform(post("/admin/products/create")
                .param("name", "상품A")
                .param("price", "10000")
                .param("imageUrl", "url.jpg")
                .param("mdConfirmed", "true")
                .header("Authorization", userToken))
            .andExpect(status().isForbidden());

    }

    // GET
    @Test
    void 상품조회_페이지접속() throws Exception {
        when(productService.findAllProducts()).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/admin/products")
                .header("Authorization", adminToken))
            .andExpect(status().isOk())
            .andExpect(view().name("product/products"))
            .andExpect(model().attributeExists("products"));
    }

    @Test
    void 상품조회_UNAUTHORIZED_인증없음() throws Exception {
        when(productService.findAllProducts()).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/admin/products"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void 상품조회_FORBIDDEN_권한없음() throws Exception {
        when(productService.findAllProducts()).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/admin/products")
                .header("Authorization", userToken))
            .andExpect(status().isForbidden());
    }

    // UPDATE
    @Test
    void 단건상품수정_성공() throws Exception {
        when(productService.findProductById(1L)).thenReturn(sampleProduct);

        mockMvc.perform(post("/admin/products/update/1")
                .param("name", "수정상품")
                .param("price", "15000")
                .param("imageUrl", "https://updated.img")
                .param("mdConfirmed", "true")
                .header("Authorization", adminToken))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(productService).updateProduct(eq(1L), any(ProductUpdateRequestDto.class));
    }

    @Test
    void 단건상품수정_유효성검사_실패() throws Exception {
        mockMvc.perform(post("/admin/products/update/1")
                .param("name", "")
                .param("price", "-1")      // 음수 유효성 실패
                .param("imageUrl", "img")
                .param("mdConfirmed", "true")
                .header("Authorization", adminToken))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products/update/1"))
            .andExpect(flash().attributeExists("errors", "productUpdateRequestDto"));
    }

    @Test
    void 단건상품수정_실패() throws Exception {
        doThrow(new RuntimeException("DB 오류"))
            .when(productService).updateProduct(eq(1L), any());

        mockMvc.perform(post("/admin/products/update/1")
                .param("name", "수정상품")
                .param("price", "15000")
                .param("imageUrl", "img")
                .param("mdConfirmed", "true")
                .header("Authorization", adminToken))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products/update/1"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void 단건상품수정_UNAUTHORIZED_인증없음() throws Exception {
        doThrow(new RuntimeException("DB 오류"))
            .when(productService).updateProduct(eq(1L), any());

        mockMvc.perform(post("/admin/products/update/1")
                .param("name", "수정상품")
                .param("price", "15000")
                .param("imageUrl", "img")
                .param("mdConfirmed", "true"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void 단건상품수정_FORBIDDEN_권한없음() throws Exception {
        doThrow(new RuntimeException("DB 오류"))
            .when(productService).updateProduct(eq(1L), any());

        mockMvc.perform(post("/admin/products/update/1")
                .param("name", "수정상품")
                .param("price", "15000")
                .param("imageUrl", "img")
                .param("mdConfirmed", "true")
                .header("Authorization", userToken))
            .andExpect(status().isForbidden());
    }


    // DELETE
    @Test
    void 단건상품삭제_성공() throws Exception {
        mockMvc.perform(post("/admin/products/delete/1")
                .header("Authorization", adminToken))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void 단건상품삭제_실패() throws Exception {
        doThrow(new RuntimeException()).when(productService).deleteProduct(1L);

        mockMvc.perform(post("/admin/products/delete/1")
                .header("Authorization", adminToken))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    void 단건상품삭제_UNAUTHORIZED_인증없음() throws Exception {
        doThrow(new RuntimeException()).when(productService).deleteProduct(1L);

        mockMvc.perform(post("/admin/products/delete/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void 단건상품삭제_FORBIDDEN_권한없음() throws Exception {
        doThrow(new RuntimeException()).when(productService).deleteProduct(1L);

        mockMvc.perform(post("/admin/products/delete/1")
                .header("Authorization", userToken))
            .andExpect(status().isForbidden());
    }


}