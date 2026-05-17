package com.abhinay.notesapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(@JsonProperty("access_token") String accessToken) {}
