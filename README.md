
# GitHub Unforked Repository Fetcher

This project leverages the GitHub API to enumerate all non-forked repositories of a user, providing details of each repository's branches along with the SHA of their latest commits. It employs Reactor's asynchronous WebClient, ensuring the main thread remains unblocked even under a high volume of requests.
## Overview

The GitHub Unforked Repository Fetcher's main functionality is to provide users with a list of their non-forked repositories. This is achieved by sending a request to the endpoint `/github/getRepos` with the following payload:

```json
{
  "nickname": "username"
}
```

The response is structured as follows:

```json
{
  "repositoryName": "newRepo",
  "ownerLogin": "ravdes",
  "branchData": [
    {
      "name": "login-branch",
      "commit": {
        "sha": "xf123ca120417"
      }
    }
  ]
}
```

## Response Structure

- `repositoryName`: The name of the repository.
- `ownerLogin`: The GitHub username of the repository owner.
- `branchData`: An array with details about each branch, including:
    - `name`: The name of the branch.
    - `commit`: An object with details about the latest commit.
        - `sha`: The SHA hash of the latest commit in the branch.

## Error Handling

- **UserNotFoundException**: This exception is thrown if the specified user cannot be found.
- **InvalidHeaderException**: This exception is thrown if the "Accept" header is not set to `application/json` or is omitted.

Errors are formatted as follows:

```json
{
  "status": "httpStatusCode",
  "message": "Explanation of the error"
}
```

## Technologies Used

- Java 21
- Spring Boot
- Docker
- Wiremock

## How to Run

The application is containerized with Docker to ensure easy setup and a consistent environment across different systems.

1. Install and open Docker Desktop - [Download here](https://www.docker.com/products/docker-desktop/).

2. Clone this repository:

```bash
git clone https://github.com/ravdes/UnforkedRepositoryFetcher.git
```

3. Execute the following command in your terminal:

```yaml
docker-compose up
```

4. The application is now running and ready to receive requests.
