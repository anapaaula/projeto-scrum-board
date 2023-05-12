package com.ufcg.psoft.scrum_board.dto;

import java.util.List;
import java.util.Map;

import com.ufcg.psoft.scrum_board.utils.Stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStoryReportDTO {
  private String projectId;
  private String projectName;
  private Map<String, Stats> stats;
  private List<SummarizedUserStoryDTO> userStories;
}
