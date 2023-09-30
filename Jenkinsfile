#!/usr/bin/env groovy

library identifier: 'jenkins-shared-library@AWS-Practice', retriever: modernSCM(
    [
        $class: 'GitSCMSource',
        remote: 'https://gitlab.com/saad324/jenkins-shared-library.git',
        credentialsId: 'saad-git'
    ]
)

pipeline {
    agent any
    tools {
        maven 'Maven'
    }
   

    stages {
        stage('increment version') {
            steps {
                script {
                    echo 'incrementing app version...'
                    sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit'
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                }
            }
        }

        stage("build app") {
            steps {
                script {
                    buildApp()
                }
            }
        }

       stage("build and push image") {
            steps {
                 script {
                 imageName = "saad324/saad-docker:${IMAGE_NAME}"
                 buildImage(imageName)
                 dockerLogin()
                 dockerPush(imageName)
        }
    }
}


        stage("test") {
            steps {
                script {
                    echo "testing the app"
                }
            }
        }

        stage('deploy') {
            steps {
                script {
                    echo "Deploying the application..."
                     def shellCmd = "bash ./server-cmds.sh ${imageName}"
                   def ec2Instance = "ec2-user@18.133.197.170"

                   sshagent(['ec2-Docker-ssh-key']) {
                       sh "scp -o StrictHostKeyChecking=no server-cmds.sh ${ec2Instance}:/home/ec2-user"
                       sh "scp -o StrictHostKeyChecking=no docker-compose.yaml ${ec2Instance}:/home/ec2-user"
                       sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${shellCmd}"

                    }
                }
            }
        }

        stage('commit update') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'saad-git', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh 'git config --global user.email "saadi57214@gmail.com"'
                        sh 'git config --global user.name "saad324"'

                        sh 'git status'
                        sh 'git branch'
                        sh 'git config --list'

                        sh "git remote set-url origin https://${USERNAME}:${PASSWORD}@gitlab.com/saad324/aws-react-node.js.git"

                        sh 'git add .'
                        sh 'git commit -m "version update"'
                        sh 'git push origin HEAD:jenkins-ssh-agent'
                    }
                }
            }
        }
    }
}
