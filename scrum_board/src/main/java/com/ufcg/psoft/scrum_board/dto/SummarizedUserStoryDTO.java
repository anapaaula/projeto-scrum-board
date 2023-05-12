package com.ufcg.psoft.scrum_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SummarizedUserStoryDTO {
  private String id;
  private String title;
  private String status;
}
