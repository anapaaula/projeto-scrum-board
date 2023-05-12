package com.ufcg.psoft.scrum_board.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStoryReportForScrumMaster {
    private String projectId;
    private String projectName;
    private List<UserStoryReportForUserDTO> userStoryReportForUsers;
    private UserStoryReportDTO userStoryReportByState;
}
