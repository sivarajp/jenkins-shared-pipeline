def call(Map configmap) {

        def DOCKERIMG = sh(script: "echo ${configmap.dockerimage}  | sed 's#/#\\\\\\/#g'", returnStdout: true).trim()
        script {
            dir("$HOME/tanzu-bank-cd") {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'LocalBranch', localBranch: 'master']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'github-credentials', url: 'https://github.com/sivarajp/tanzu-bank-cd']]])        
                sh """
                    ls -lrt 
                    export tempvar=\$()
                    sed -i "/^\\([[:space:]]*image: \\).*/s//\\1$DOCKERIMG/"  ./${configmap.repoName}/${configmap.repoName}.yml
                """
                withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN',)]) {
                    sh """
                        git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
                        git config user.name $GIT_USER
                        git config user.password $GIT_TOKEN
                        echo `git commit -a -m "pipeline commit"`
                        git push --set-upstream origin master
                        echo `git push`
                    """
                }
        }
    }
    
}
