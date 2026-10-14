package vn.edu.ptit.campushub.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import vn.edu.ptit.campushub.user.User;

@Entity
@Table(name = "saved_posts", uniqueConstraints = @UniqueConstraint(
        name = "uk_saved_post_user", columnNames = {"post_id", "user_id"}))
public class SavedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private Instant savedAt;

    protected SavedPost() { }

    public SavedPost(Post post, User user) {
        this.post = post;
        this.user = user;
        this.savedAt = Instant.now();
    }

    public Post getPost() { return post; }
}
