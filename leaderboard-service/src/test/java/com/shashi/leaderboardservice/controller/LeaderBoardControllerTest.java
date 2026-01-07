package com.shashi.leaderboardservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashi.leaderboardservice.model.LeaderBoard;
import com.shashi.leaderboardservice.service.LeaderBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LeaderBoardControllerTest {

    @Mock
    LeaderBoardService leaderBoardService;

    @InjectMocks
    LeaderBoardController leaderBoardController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(leaderBoardController).build();
    }

    @Test
    void getGlobalLeaderboard_withMockMvc_returnsJsonFromService() throws Exception {
        int top = 3;
        List<LeaderBoard> payload = List.of(
                new LeaderBoard("user1", 10.0),
                new LeaderBoard("user2", 20.0)
        );
        when(leaderBoardService.getTopPerformers(top))
                .thenReturn(ResponseEntity.ok(payload));

        mockMvc.perform(get("/api/leaderboard/monthly/top/{top}", top))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(payload)));

        verify(leaderBoardService, times(1)).getTopPerformers(top);
        verifyNoMoreInteractions(leaderBoardService);
    }
}



