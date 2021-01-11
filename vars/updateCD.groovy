def call(Map configmap) {
    println configmap
    // git(
    //    url: 'https://github.com/sivarajp/tanzu-bank-cd.git',
    //    credentialsId: 'github-credentials',
    //    branch: "master"
    // )    

    dir("$HOME/tanzu-bank-cd") {
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'LocalBranch', localBranch: 'master']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'github-credentials', url: 'https://github.com/sivarajp/tanzu-bank-cd']]])        
        withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN',)]) {
        sh """
        git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
        git config --global user.name $GIT_USER
        git config --global user.password $GIT_TOKEN
        echo ${configmap.reponame}
        cd ${configmap.reponame}
        yq w ${configmap.reponame}.yaml "spec.containers[0].image.value" ${configmap.reponame}
        git add .
        git commit -m "pipeline commit"
        git push --set-upstream origin master
        git push
        """
        }

    }
    // withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN',)]) {
    //     sh('''
    //         printenv
    //         echo 
    //         echo $GIT_TOKEN
    //         git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
    //         git config --global user.name $GIT_USER
    //         git config --global user.password $GIT_TOKEN
    //         dir('/tmp') {
    //             git clone https://$GIT_USER:$GIT_TOKEN@github.com/$GIT_USER/tanzu-bank-cd
    //         }
    //         cd ${config.reponame}
    //         ls -lrt
    //     ''')
    // } 
}

//             git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
//             git config remote.origin.fetch '+refs/heads/*:refs/remotes/origin/*'
//             git fetch --all
//             git checkout -b temp #makes a new branch from current detached HEAD
//             git checkout master
//             git merge temp
//             git branch -d temp
//             git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
//             #git fetch --no-tags --force --progress -- $GIT_URL +refs/heads/*:refs/remotes/origin/*
//             git push


//