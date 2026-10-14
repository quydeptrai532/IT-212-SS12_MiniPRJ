package vn.edu.ptit.campushub.post;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {
    Optional<SavedPost> findByPostIdAndUserId(Long postId, Long userId);
    List<SavedPost> findByUserIdOrderBySavedAtDesc(Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
}
