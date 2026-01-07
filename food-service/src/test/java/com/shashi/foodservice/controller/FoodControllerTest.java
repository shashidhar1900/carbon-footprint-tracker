package com.shashi.foodservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashi.foodservice.dto.FoodRequest;
import com.shashi.foodservice.dto.FoodResponse;
import com.shashi.foodservice.model.FoodUsage;
import com.shashi.foodservice.service.FoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodControllerTest {

    @Mock
    private FoodService foodService;

    @InjectMocks
    private FoodController foodController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(foodController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addFood_delegatesFoodService_andReturnsResponse() throws Exception {
        FoodRequest foodRequest = new FoodRequest();
        foodRequest.setQuantity(1232.45);
        foodRequest.setType("VEG");

        ResponseEntity<String> expected = ResponseEntity.ok("Food record added!");
        doReturn(expected).when(foodService).addFood(any(FoodRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/food/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Food record added!"));

        verify(foodService, times(1)).addFood(any(FoodRequest.class));
    }

    @Test
    void updateFood_delegatesFoodService_andReturnsResponse() throws Exception {
        FoodRequest foodRequest = new FoodRequest();
        foodRequest.setQuantity(10.0);
        foodRequest.setType("NON_VEG");

        ResponseEntity<String> expected = ResponseEntity.ok("Food record updated!");
        doReturn(expected).when(foodService).updateFood(any(FoodRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/food/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Food record updated!"));

        verify(foodService, times(1)).updateFood(any(FoodRequest.class));
    }

    @Test
    void deleteFood_delegatesFoodService_andReturnsResponse() throws Exception {
        String type = "VEG";
        ResponseEntity<String> expected = ResponseEntity.ok("Deleted");
        doReturn(expected).when(foodService).deleteFood(type);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/food/delete/{type}", type))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Deleted"));

        verify(foodService, times(1)).deleteFood(type);
    }

    @Test
    void getHistory_delegatesFoodService_andReturnsResponse() throws Exception {
        List<FoodUsage> list = Collections.singletonList(new FoodUsage());
        ResponseEntity<List<FoodUsage>> expected = ResponseEntity.ok(list);
        when(foodService.getHistory()).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/food/history"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(list)));

        verify(foodService, times(1)).getHistory();
    }

    @Test
    void getMonthlyFood_delegatesFoodService_andReturnsResponse() throws Exception {
        String username = "user1";
        int year = 2025;
        int month = 1;
        FoodResponse resp = new FoodResponse();
        ResponseEntity<FoodResponse> expected = ResponseEntity.ok(resp);
        when(foodService.getMonthlyFood(username, year, month)).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/food/history/{username}/{year}/{month}", username, year, month))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(resp)));

        verify(foodService, times(1)).getMonthlyFood(username, year, month);
    }

}
