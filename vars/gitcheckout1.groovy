def call() {
    utils.emptyDir()
    def commit = checkout scm
    if (commit.GIT_COMMIT != null) {
        env.GIT_COMMIT = commit.GIT_COMMIT.substring(0, 7)
    } else {
        env.GIT_COMMIT = "";
    }
}

