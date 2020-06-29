def call(Map config) {
    podTemplate(
            label: 'kube-go-build-pod',
            cloud: 'kubernetes',
            inheritFrom: 'default',
            namespace: 'jenkins',
            containers: [
                    containerTemplate(name: 'go', image: 'golang:latest', ttyEnabled: true, command: 'cat'),
            ],
            serviceAccount: 'jenkins'
    ) 
    
    {
        node('kube-go-build-pod') {
            try {
                stage ('Checkout') {
                    checkout scm
                    config.commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    config.repoName = utils.getRepoName()
                    config.lastCommitterEmail = utils.getLastCommitterEmail()
                    config.lastCommit = utils.getLastCommit()
                    config.branchName = env.BRANCH_NAME
                }

                stage ('Build') {
                    container ('go') {
                        sh 'go version'
                    }
                }
            } finally {
               // cleanWs()
            }
        }
    }
}