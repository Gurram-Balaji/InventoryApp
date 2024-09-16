package com.App.fullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
public class ScatterLocationDataDTO {
    private List<ScatterDataDTO> scatterDataDTO ;
    private String locationName;
}
