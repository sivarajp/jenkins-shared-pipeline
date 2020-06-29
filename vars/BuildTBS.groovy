import groovy.json.JsonSlurperClassic
def call(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  pipeline {
    agent any  
    options {
      buildDiscarder(logRotator(numToKeepStr:'5')) 
      skipStagesAfterUnstable()
      disableConcurrentBuilds()
    }
    stages {  
      stage ('Set Default values') { 
        steps {
            defaultProps(config)
        }
      }

      stage ('Build'){
        steps {
          script {
              config.doTBSBuild = "true"
              if (config.platform  == 'go') {
                gobuild(config)
              } else if  (config.platform  == 'java') {
                javabuild(config)
              } else if  (config.platform  == 'python') {
                pythonbuild(config)
              } else {
                genericbuild(config)
              }
          }
        }
      }
    }
    

    post {
        success {
            echo 'The build job finised successfully'
        }
        failure {
            echo 'The build job failed '
        }
        unstable {
            echo 'The build job is  unstable'
        }
    }

  }
 
}




