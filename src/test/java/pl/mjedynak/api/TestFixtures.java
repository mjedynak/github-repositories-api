package pl.mjedynak.api;

public class TestFixtures {

    public static final String FIRST_REPOSITORY =
            """
            [
              {
                "name": "Hello-World",
                "owner": {
                  "login": "mjedynak"
                },
                "fork": false
              }
            ]
            """;
    public static final String SECOND_REPOSITORY =
            """
            [
              {
                "name": "Hello-World2",
                "owner": {
                  "login": "mjedynak"
                },
                "fork": false
              }
            ]
            """;
    public static final String TWO_REPOSITORIES_WITH_ONE_FORK =
            """
            [
              {
                "name": "Hello-World",
                "owner": {
                  "login": "mjedynak"
                },
                "fork": false
              },
              {
                "name": "Hello-World2",
                "owner": {
                  "login": "mjedynak"
                },
                "fork": true
              }
            ]
            """;
    public static final String FIRST_BRANCH =
            """
            [
              {
            	"name": "master",
            	"commit": {
            	  "sha": "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"
            	}
              }
            ]
            """;
    public static final String SECOND_BRANCH =
            """
            [
              {
            	"name": "anotherBranch",
            	"commit": {
            	  "sha": "d01060d65ede31cc077d5a4445b91654740b86b5"
            	}
              }
            ]
            """;
    public static final String THIRD_BRANCH =
            """
            [
              {
            	"name": "master",
            	"commit": {
            	  "sha": "265303331d83bff373961a0e88f659a593d07ef8"
            	}
              }
            ]
            """;
}
