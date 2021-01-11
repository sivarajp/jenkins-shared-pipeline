def call(Map config) {
    withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN',)]) {
        sh('''
            printenv
            echo 
            echo $GIT_TOKEN
            git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
            git config --global user.name $GIT_USER
            git config --global user.password $GIT_TOKEN
            git clone https://github.com/$GIT_USER/tanzu-bank-cd
            cd ${config.reponame}
            ls -lrt
        ''')
    } 
}

// git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
//             git config remote.origin.fetch '+refs/heads/*:refs/remotes/origin/*'
//             git fetch --all
//             git checkout -b temp #makes a new branch from current detached HEAD
//             git checkout master
//             git merge temp
//             git branch -d temp
//             git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
//             #git fetch --no-tags --force --progress -- $GIT_URL +refs/heads/*:refs/remotes/origin/*
//             git push