package gift.member.entity;

public class Member {

    private Long memberId;
    private final String email;
    private final String password;
    private final String name;
    private final String role;

    public Member(String email, String password, String name, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public Member(Long memberId, String email, String password, String name, String role) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
