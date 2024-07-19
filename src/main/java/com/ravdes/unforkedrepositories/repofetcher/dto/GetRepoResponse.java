package com.ravdes.unforkedrepositories.repofetcher.dto;

import java.util.List;

public record GetRepoResponse(String repositoryName, String ownerLogin, List<BranchData> branchData) {
}
