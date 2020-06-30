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
              build(config)
          }
        }
      }
      stage ('Deploy to kubernetes'){
         steps {
            echo 'Deploy to kubernetes' 
            deployKube(config)
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




