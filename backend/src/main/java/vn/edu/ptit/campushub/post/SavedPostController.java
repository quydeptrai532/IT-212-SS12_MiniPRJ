package vn.edu.ptit.campushub.post;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.ptit.campushub.post.PostDtos.PostView;
import vn.edu.ptit.campushub.user.UserRepository;

@RestController
@RequestMapping("/api/saved-posts")
public class SavedPostController {
    private final SavedPostRepository savedPosts;
    private final UserRepository users;
    private final PostService posts;

    public SavedPostController(SavedPostRepository savedPosts, UserRepository users, PostService posts) {
        this.savedPosts = savedPosts;
        this.users = users;
        this.posts = posts;
    }

    @GetMapping
    @Transactional(readOnly = true)
    List<PostView> list(Principal principal) {
        Long userId = users.findByUsername(principal.getName()).orElseThrow().getId();
        return savedPosts.findByUserIdOrderBySavedAtDesc(userId).stream()
                .map(saved -> posts.viewForCurrentUser(saved.getPost(), principal, false))
                .filter(Objects::nonNull)
                .toList();
    }

    @PutMapping("/{postId}")
    Map<String, Boolean> save(@PathVariable Long postId, Principal principal) {
        posts.saveForCurrentUser(postId, principal, savedPosts);
        return Map.of("saved", true);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void remove(@PathVariable Long postId, Principal principal) {
        posts.removeSavedForCurrentUser(postId, principal, savedPosts);
    }
}
