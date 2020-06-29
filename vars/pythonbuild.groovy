def call(Map config) {
    podTemplate(
            label: 'kube-generic-build-pod',
            cloud: 'kubernetes',
            inheritFrom: 'default',
            namespace: 'jenkins',
            serviceAccount: 'jenkins'
    ) 
    
    {
        node('kube-generic-build-pod') {
            try {
                stage ('Extract') {
                    checkout scm
                    config.commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    config.repoName = utils.getRepoName()
                    config.lastCommitterEmail = utils.getLastCommitterEmail()
                    config.lastCommit = utils.getLastCommit()
                    config.branchName = env.BRANCH_NAME
                }
                stage ('Test') {
                    
                }
            } finally {
               // cleanWs()
            }
        }
    }
}