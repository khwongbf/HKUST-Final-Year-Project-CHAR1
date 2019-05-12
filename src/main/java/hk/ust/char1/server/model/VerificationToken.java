package hk.ust.char1.server.model;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Entity
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    /**
     * Internal identifier for the database. Should not be made available for users to access.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The UUID token that was assigned by the system. Must be unique.
     */
    @Column(name= "TOKEN")
    private String token;

    /**
     * The {@link User User} that this token is referring to.
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "USERNAME", nullable = false, referencedColumnName = "username")
    private User user;

    /**
     * The {@link LocalDateTime timestamp} that the token was created.
     * <p>
     *     This field refers to the timestamp that the token was created.<br>
     *       The timestamp must be at the present or in the past.
     * </p>
     */
    @Column(name= "CREATED_DATE")
    @PastOrPresent
    private LocalDateTime createdDate;

    /**
     * The {@link LocalDateTime timestamp} that the token will be expired.
     */
    @Column(name= "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    public VerificationToken() {
    }

    public VerificationToken(String token, User user) {
        super();

        this.token = token;
        this.user = user;
        this.createdDate = LocalDateTime.now();
        this.expiryDate = calculateExpiryDate();
    }

    public static int getEXPIRATION() {
        return EXPIRATION;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    private LocalDateTime calculateExpiryDate(){
        var expiryTime = LocalDateTime.now();
        return expiryTime.plusMinutes(VerificationToken.EXPIRATION);
    }
}
