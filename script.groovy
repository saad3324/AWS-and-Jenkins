def buildApp() {
    echo "building the application..."
    sh 'npm install'
    sh 'npm run build'

}


def testApp() {
    echo "Testing the app"
}

def deployApp() {
    echo "Deploying the application..."
    def dockerCMD = 'docker run -d -p 3000:3080 saad324/saad-docker:1.1.0'

    sshagent(['ec2-Docker-ssh-key']) {
    sh "ssh -o StrictHostKeyChecking=no ec2-user@18.133.197.170 ${dockerCMD}"
    }


}



return this

