package com.dailydiscover;

import com.dailydiscover.common.response.ApiResponse;
import com.dailydiscover.recommendation.dto.TodayDiscoverResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DiscoverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetTodayDiscover() throws Exception {
        var result = mockMvc.perform(get("/api/v1/discover/today")
                        .header("X-Anonymous-Id", "test-anon-123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        // 简单校验返回结构
        assertThat(content).contains("code");
        // 解析为 ApiResponse
        // 这里不做完整解析，只检查成功码
        assertThat(content).contains("\"code\":0");
    }
}