package gift.wish.repository;

import gift.exception.wish.WishNotFoundException;
import gift.wish.entity.Page;
import gift.wish.entity.Wish;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class WishRepositoryImpl implements WishRepository {

    private final JdbcTemplate jdbcTemplate;

    public WishRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long addWish(Wish wish) {
        String sql = "INSERT INTO wishes(memberId, productId) VALUES(?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, wish.getMemberId());
            ps.setLong(2, wish.getProductId());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        Number wishId = (Number) keys.get("WISHID");

        return (wishId != null) ? wishId.longValue() : null;
    }

    @Override
    public List<Wish> getWishes(Long memberId, Page page) {
        String sql = String.format(
            "SELECT wishId, productId, createdDate FROM wishes WHERE memberId = ? ORDER BY %s %s LIMIT ? OFFSET ?",
            page.getSortField(), page.getSortOrder());
        return jdbcTemplate.query(sql,
            (rs, RowNum) -> new Wish(rs.getLong("wishId"), rs.getLong("productId"),
                rs.getTimestamp("createdDate").toLocalDateTime()), memberId,
            page.getSize(), page.getOffset());
    }

    @Override
    public void deleteWish(Long wishId) {
        String sql = "DELETE FROM wishes WHERE wishId = ?";
        jdbcTemplate.update(sql, wishId);
    }

    @Override
    public Wish findByWishId(Long wishId) {
        String sql = "SELECT wishId, memberId, productId, createdDate FROM wishes WHERE wishId = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new Wish(rs.getLong("wishId"), rs.getLong("memberId"),
                    rs.getLong("productId"),
                    rs.getTimestamp("createdDate").toLocalDateTime()), wishId);
        } catch (EmptyResultDataAccessException e) {
            throw new WishNotFoundException("위시 상품이 존재하지 않습니다. wishId = " + wishId);
        }
    }

    @Override
    public Long countWishesByMemberId(Long memberId) {
        String sql = "SELECT COUNT(wishId) FROM wishes WHERE memberId = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, memberId);
    }

    @Override
    public Boolean existsByMemberAndProduct(Long memberId, Long productId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM wishes WHERE memberId = ? AND productId = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, memberId, productId);
    }
}
