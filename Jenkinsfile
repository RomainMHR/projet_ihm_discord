pipeline {
    agent any

    tools {
        // Cette ligne indique à Jenkins qu'il devra utiliser une installation de JDK (java) 21
        // (Il faudra configurer ce nom 'jdk21' dans l'administration de Jenkins)
        jdk 'jdk21'
    }

    environment {
        // Le token Sonar secret que nous avons généré et stocké dans Jenkins
        SONAR_TOKEN = credentials('sonar-token')
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                // Récupération de ton code source depuis Git
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                // Se déplacer dans le bon répertoire et nettoyer/compiler
                dir('IHM_M2-TIIL/MessageApp') {
                    // Si le système de Jenkins est Linux, on utilise ./gradlew, sinon gradlew.bat
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Lancer l'analyse Sonar
                dir('IHM_M2-TIIL/MessageApp') {
                    sh './gradlew sonar -Dsonar.token=$SONAR_TOKEN -Dsonar.host.url=$SONAR_HOST_URL'
                }
            }
        }

        stage('Generate Fat JAR') {
            steps {
                // Construire l'exécutable final
                dir('IHM_M2-TIIL/MessageApp') {
                    sh './gradlew shadowJar'
                }
            }
            post {
                success {
                    // Sauvegarder le .jar dans l'interface de Jenkins
                    archiveArtifacts artifacts: 'IHM_M2-TIIL/MessageApp/build/libs/*-all.jar', fingerprint: true
                }
            }
        }
    }
}
