pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Pulls the code from your Git repository
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                // Run JUnit tests
                bat 'mvn clean test' 
            }
        }
        
        stage('Build') {
            steps {
                // Package the application
                bat 'mvn clean package -DskipTests'
            }
        }
        
        stage('Docker Build') {
            steps {
                // Build the Docker image
                bat 'docker build -t registration-app:latest .'
            }
        }
    }
}
