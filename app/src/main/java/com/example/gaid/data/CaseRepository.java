package com.example.gaid.data;

import com.example.gaid.model.get_case.GetCaseResponseDTO;

import retrofit2.Callback;

public interface CaseRepository {
    void getCaseResponseData(Callback<GetCaseResponseDTO> callback);
}
