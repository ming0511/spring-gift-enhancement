package gift.product.repository;

import gift.exception.product.ProductNotFoundException;
import gift.product.entity.Product;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long saveProduct(Product product) {

        String sql = "INSERT INTO products(name, price, imageUrl, mdConfirmed) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setString(3, product.getImageUrl());
            ps.setBoolean(4, product.getMdConfirmed());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return (key != null) ? key.longValue() : null;
    }

    @Override
    public List<Product> findAllProducts() {

        String sql = "SELECT productId, name, price, imageUrl, mdConfirmed FROM products";

        return jdbcTemplate.query(sql,
            (rs, rowNum) -> new Product(rs.getLong("productId"), rs.getString("name"),
                rs.getDouble("price"), rs.getString("imageUrl"), rs.getBoolean("mdConfirmed")));
    }

    @Override
    public Product findProductById(Long productId) {

        String sql = "SELECT productId, name, price, imageUrl, mdConfirmed FROM products WHERE productId = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new Product(rs.getLong("productId"), rs.getString("name"),
                    rs.getDouble("price"), rs.getString("imageUrl"), rs.getBoolean("mdConfirmed")),
                productId);
        } catch (EmptyResultDataAccessException e) {
            throw new ProductNotFoundException("상품이 존재하지 않습니다. productId = " + productId);
        }
    }

    @Override
    public void updateProduct(Product product) {

        String sql = "UPDATE products SET name = ?, price = ?, imageUrl = ?, mdConfirmed = ? WHERE productId = ?";

        jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getImageUrl(),
            product.getMdConfirmed(),
            product.getProductId());
    }


    @Override
    public void deleteProduct(Long productId) {

        String sql = "DELETE FROM products WHERE productId = ?";

        jdbcTemplate.update(sql, productId);
    }
}