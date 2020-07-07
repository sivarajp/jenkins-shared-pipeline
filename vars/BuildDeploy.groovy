
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
      skipDefaultCheckout() 
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
              config.doDockerBuild = "true"
              build(config)
          }
        }
      }

      stage ('Deploy to DEV kubernetes'){
         steps {
            echo 'Deploy to kubernetes' 
            deployKube(config)
         }
      }

      stage ('Deploy to Prod kubernetes'){
         steps {
            input (id: "Proceed", message: "Are you sure you wish to deploy this in production?")
            echo 'Deploy to kubernetes' 
            config.namespace = config.namespace + "-prod"
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




