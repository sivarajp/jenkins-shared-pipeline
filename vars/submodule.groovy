def call(Map config) {
    withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN',)]) {
        sh('''
            echo $GIT_USER 
            echo $GIT_TOKEN
            git config remote.origin.fetch '+refs/heads/*:refs/remotes/origin/*'
            git fetch --all --recurse-submodules
            curl https://gist.githubusercontent.com/patdunlavey/dcc36b2085dddc22404f805978c0f11d/raw/903a4cbc23a798244f38ce1fb1414125647ff7ba/git-submodule-flatten.sh -o git-submodule-flatten.sh
            chmod +x git-submodule-flatten.sh 
            git add git-submodule-flatten.sh
            git commit -m "Jenkins sumodule commit"
            ./git-submodule-flatten.sh temp-test 
            git commit -m "Jenkins sumodule commit"
            git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
            git push --set-upstream origin temp-test
        ''')
    } 
}