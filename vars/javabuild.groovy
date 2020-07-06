def call(Map config) {
    podTemplate(
            label: 'kube-java-build-pod',
            cloud: 'kubernetes',
            inheritFrom: 'default',
            namespace: 'jenkins',
            containers: [
                    containerTemplate(name: 'maven', image: 'maven:3.5.4-jdk-8-alpine', runAsUser: 'jenkins', ttyEnabled: true, command: 'cat'),
                    containerTemplate(name: 'kaniko', image: 'mgit/base:kaniko-executor-debug-stable', ttyEnabled: true, command: 'cat'),

            ],
            volumes: [
                    secretVolume(mountPath: '/kaniko/.docker/', secretName: 'kaniko-secret'),
                    hostPathVolume(hostPath: '/root/.m2', mountPath: '/root/.m2'),
            ],
            serviceAccount: 'jenkins',
            runAsUser: 'jenkins'
    ) 
    
    {
        node('kube-java-build-pod') {
            try {
                stage ('Checkout') {
                    gitcheckout(config)
                }

                stage ('Test') {
                    container ('maven') {
                        sh 'mvn install'
                        sh 'chmod -R 777 $WORKSPACE/target'
                        stash includes: '**', name: 'source'
                        stash includes: '**/target/jacoco.exec', name: 'unitCodeCoverage'
                        stash includes: '**/target/surefire-reports/*.xml', name: 'surefire'
                    }
                }
                stage("JoCoCo Code Scan") {
                    sh 'mkdir $WORKSPACE/output'
                    parallel(
                        jacoco: {
                            dir('output') {
                                unstash 'source'
                                unstash 'unitCodeCoverage'
                                step([$class: 'JacocoPublisher', changeBuildStatus: true, 
                                        maximumBranchCoverage: '0', 
                                        maximumClassCoverage: '40', 
                                        maximumComplexityCoverage: '40', 
                                        maximumInstructionCoverage: '40', 
                                        maximumLineCoverage: '40', 
                                        maximumMethodCoverage: '40'
                                ])
                            }
                        },
                        junit:{
                             dir('output') {
                                unstash 'surefire'
                                junit '**/target/surefire-reports/*.xml'
                             }
                        }
                    )
                }

                if (config.doDockerBuild == 'true') {
                    stage ('Docker build and push')   {
                        container ('kaniko') {
                            sh "executor -f `pwd`/Dockerfile -c `pwd` --insecure --skip-tls-verify --cache=true --destination=sivarajp/${config.repoName}:${config.commitId}" 
                            config.dockerimage = "sivarajp/${config.repoName}:${config.commitId}"
                        }
                    }
                }

                if (config.doTBSBuild == 'true') {
                    stage ('TBS Docker build and push')   {
                        tbsbuild(config)
                    }
                }
            } finally {
               // cleanWs()
            }
        }
    }
}