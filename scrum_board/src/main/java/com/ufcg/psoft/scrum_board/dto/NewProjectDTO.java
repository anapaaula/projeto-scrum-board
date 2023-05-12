package com.ufcg.psoft.scrum_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewProjectDTO {
    
    private String name;
    private String description;
    private String partnerInstitution;

}
