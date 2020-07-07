def call(Map config) {
    checkout scm
    config.commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
    config.repoName = utils.getRepoName()
    config.lastCommitterEmail = utils.getLastCommitterEmail()
    config.lastCommit = utils.getLastCommit()
    config.branchName = env.BRANCH_NAME
    //sh 'git submodule update --init --recursive'
    sh 'ls -R'
}

