package gift.wish.entity;

public class Page {

    private Integer page;
    private final Integer size;
    private final Integer offset;
    private final String sortField;
    private final String sortOrder;

    public Page(Integer size, Integer offset, String sortField, String sortOrder) {
        this.size = size;
        this.offset = offset;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public Page(Integer page, Integer size, Integer offset, String sortField,
        String sortOrder) {
        this.page = page;
        this.size = size;
        this.offset = offset;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getSortField() {
        return sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }
}
