def call(Map config) {
    withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN',)]) {
        sh('''
            echo $GIT_USER 
            echo $GIT_TOKEN
            git config remote.origin.fetch '+refs/heads/*:refs/remotes/origin/*'
            git fetch --all
            git config --local credential.helper "!f() { echo username=\\$GIT_USER; echo password=\\$GIT_TOKEN; }; f"
            git config --global user.email "sivathevan@gmail.com"
            git config --global user.name "Sivaraj"
            git checkout --orphan temp-test || exit
            modules=`git submodule | cut -d" " -f3`
            for module in $modules
            do
            git rm --cached $module
            mv $module/.git $module/.git.orig.$$
            echo git add $module
            git add $module
            done
            git rm .gitmodules
            git commit -m "$COMMITMSG"
            for module in $modules; do
            if [ $REMOVE -eq 1 ]; then
                echo rm -r $module/.git.orig.$$
                rm -r $module/.git.orig.$$
            else
                echo mv $module/.git.orig.$$ $module/.git
                mv $module/.git.orig.$$ $module/.git
            fi
            done
            git push --set-upstream origin temp-test
        ''')
    } 
}