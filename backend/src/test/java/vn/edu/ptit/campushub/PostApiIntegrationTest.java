package vn.edu.ptit.campushub;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class PostApiIntegrationTest {
    private static final String STUDENT_PASSWORD = UUID.randomUUID().toString();
    private static final String ADMIN_PASSWORD = UUID.randomUUID().toString();

    @DynamicPropertySource
    static void configureDemoPasswords(DynamicPropertyRegistry registry) {
        registry.add("app.demo.student-password", () -> STUDENT_PASSWORD);
        registry.add("app.demo.admin-password", () -> ADMIN_PASSWORD);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.demo.student-password}")
    private String studentPassword;

    @Test
    void publicUserCanReadSeededPosts() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(2)));
    }

    @Test
    void anonymousUserCannotCreatePost() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Bài mới","content":"Nội dung hợp lệ","categoryId":1}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanCreatePost() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .with(httpBasic("student", studentPassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Tìm nhóm học DevOps","content":"Cần thêm hai thành viên cùng thực hành.","categoryId":6}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Tìm nhóm học DevOps"))
                .andExpect(jsonPath("$.author.username").value("student"));
    }

    @Test
    void invalidPostReturnsReadableMessage() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .with(httpBasic("student", studentPassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","content":"Nội dung","categoryId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void studentCanSaveListAndRemovePost() throws Exception {
        String pageJson = mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long postId = objectMapper.readTree(pageJson).path("content").get(0).path("id").asLong();

        mockMvc.perform(put("/api/saved-posts/{postId}", postId)
                        .with(httpBasic("student", studentPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saved").value(true));
        mockMvc.perform(put("/api/saved-posts/{postId}", postId)
                        .with(httpBasic("student", studentPassword)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/saved-posts").with(httpBasic("student", studentPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(postId))
                .andExpect(jsonPath("$[1]").doesNotExist());
        mockMvc.perform(delete("/api/saved-posts/{postId}", postId)
                        .with(httpBasic("student", studentPassword)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/saved-posts").with(httpBasic("student", studentPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void anonymousUserCannotReadSavedPosts() throws Exception {
        mockMvc.perform(get("/api/saved-posts"))
                .andExpect(status().isUnauthorized());
    }
}
