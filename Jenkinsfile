//
// Copyright (c) 2019 Intel Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

loadGlobalLibrary()

pipeline {
    agent {
        label 'centos7-docker-4c-2g'
    }

    options {
        timestamps()
    }

    stages {
        stage('LF Prep') {
            steps {
                edgeXSetupEnvironment()
                edgeXSemver 'init'
                script {
                    def semverVersion = edgeXSemver()
                    env.setProperty('VERSION', semverVersion)
                    sh 'echo $VERSION > VERSION'
                    stash name: 'semver', includes: '.semver/**,VERSION', useDefaultExcludes: false
                }
            }
        }

        stage('Multi-Arch Build') {
            // fan out
            parallel {
                stage('Build amd64') {
                    agent {
                        label 'centos7-docker-4c-2g'
                    }
                    stages { 
                        stage('Phase 1') {
                            agent {
                                dockerfile {
                                    filename 'Dockerfile.build'
                                    label 'centos7-docker-4c-2g'
                                    args '-v /var/run/docker.sock:/var/run/docker.sock -v $PWD:/root -w /root -u 0:0 --privileged'
                                    reuseNode true
                                }
                            }
                            stages {
                                stage('Test') {
                                    steps {
                                        sh 'make test'
                                    }
                                }
                                stage('Maven Package') {
                                    when { expression { edgex.isReleaseStream() } }

                                    steps {
                                        sh 'make build'
                                    }
                                }
                                stage('Docker Build') {
                                    when { expression { edgex.isReleaseStream() } }

                                    steps {
                                        unstash 'semver'

                                        sh 'echo Currently Building version: `cat ./VERSION`'

                                        script {
                                            // This is the main docker image that will be pushed
                                            // BASE image = image from above
                                            image_amd64 = docker.build(
                                                'docker-support-rulesengine',
                                                "--label 'git_sha=${env.GIT_COMMIT}' ."
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // this should be back on the original node that has the tools required to run the login script
                        stage('Phase 2') {
                            stages {
                                stage('Docker Push') {
                                    when { expression { edgex.isReleaseStream() } }

                                    steps {
                                        script {
                                            edgeXDockerLogin(settingsFile: env.MVN_SETTINGS)

                                            docker.withRegistry("https://${env.DOCKER_REGISTRY}:10004") {
                                                image_amd64.push("${env.SEMVER_BRANCH}")
                                                image_amd64.push("${env.VERSION}")
                                                image_amd64.push("${env.GIT_COMMIT}-${env.VERSION}")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                stage('Build arm64') {
                    agent {
                        label 'ubuntu18.04-docker-arm64-4c-2g'
                    }
                    stages {
                        stage('Phase 1') {
                            agent {
                                dockerfile {
                                    filename 'Dockerfile.build'
                                    label 'ubuntu18.04-docker-arm64-4c-2g'
                                    args '-v /var/run/docker.sock:/var/run/docker.sock -v $PWD:/root -w /root -u 0:0 --privileged'
                                    reuseNode true
                                }
                            }
                            stages {
                                stage('Test') {
                                    steps {
                                        sh 'make test'
                                    }
                                }
                                stage('Maven Package') {
                                    when { expression { edgex.isReleaseStream() } }

                                    steps {
                                        sh 'make build'
                                    }
                                }
                                stage('Docker Build') {
                                    when { expression { edgex.isReleaseStream() } }

                                    steps {
                                        unstash 'semver'

                                        sh 'echo Currently Building version: `cat ./VERSION`'

                                        script {
                                            // This is the main docker image that will be pushed
                                            // BASE image = image from above
                                            image_arm64 = docker.build(
                                                'docker-support-rulesengine-arm64',
                                                "--label 'git_sha=${env.GIT_COMMIT}' ."
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        stage('Phase 2') {
                            stages {
                                stage('Docker Push') {
                                when { expression { edgex.isReleaseStream() } }

                                steps {
                                    script {
                                        edgeXDockerLogin(settingsFile: env.MVN_SETTINGS)

                                        docker.withRegistry("https://${env.DOCKER_REGISTRY}:10004") {
                                            image_arm64.push("${env.SEMVER_BRANCH}")
                                            image_arm64.push("${env.VERSION}")
                                            image_arm64.push("${env.GIT_COMMIT}-${env.VERSION}")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('SemVer Tag') {
            when { expression { edgex.isReleaseStream() } }
            steps {
                unstash 'semver'
                sh 'echo v${VERSION}'
                edgeXSemver('tag')
                edgeXInfraLFToolsSign(command: 'git-tag', version: 'v${VERSION}')
                edgeXSemver('push')
            }
        }

        stage('Semver Bump Pre-Release Version') {
            when { expression { edgex.isReleaseStream() } }
            steps {
                edgeXSemver('bump pre')
                edgeXSemver('push')
            }
        }
    }

    post {
        failure {
            script {
                currentBuild.result = "FAILED"
            }
        }
        always {
            edgeXInfraPublish()
        }
    }
}

def loadGlobalLibrary(branch = '*/master') {
    library(identifier: 'edgex-global-pipelines@master', 
        retriever: legacySCM([
            $class: 'GitSCM',
            userRemoteConfigs: [[url: 'https://github.com/edgexfoundry/edgex-global-pipelines.git']],
            branches: [[name: branch]],
            doGenerateSubmoduleConfigurations: false,
            extensions: [[
                $class: 'SubmoduleOption',
                recursiveSubmodules: true,
            ]]]
        )
    ) _
}