pipeline {
    agent any

    tools {
        jdk 'temurin-17'
        nodejs 'node-20'
    }

    environment {
        PORT = '3000'
        BASE_URL = 'http://localhost:3000'
        HEADLESS = 'true'
    }

    stages {
        stage('Setup SUT') {
            steps {
                dir('demo-app') {
                    sh 'npm ci'
                    sh 'npm run reset'
                }
            }
        }

        stage('Parallel Automated Regression') {
            parallel {
                stage('Java UI, API & DB (Selenium, REST Assured, TestNG)') {
                    steps {
                        dir('demo-app') {
                            sh 'PORT=3000 npm start & sleep 3'
                        }
                        dir('java-tests') {
                            sh 'mvn clean test'
                        }
                    }
                    post {
                        always {
                            allure([
                                includeProperties: false,
                                jdk: '',
                                properties: [],
                                reportBuildPolicy: 'ALWAYS',
                                results: [[path: 'java-tests/target/allure-results']]
                            ])
                        }
                    }
                }

                stage('Python UI, API & DB (Playwright, Requests, PyTest)') {
                    steps {
                        dir('demo-app') {
                            sh 'PORT=3001 DB_PATH=../demo-app/data/autogate.db npm start & sleep 3'
                        }
                        dir('python-tests') {
                            sh '''
                                python3 -m venv .venv
                                . .venv/bin/activate
                                pip install --upgrade pip
                                pip install -r requirements.txt
                                playwright install chromium
                                AUTOGATE_BASE_URL=http://localhost:3001 pytest -n 4 --html=reports/report.html --self-contained-html --junitxml=reports/junit.xml
                            '''
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: 'python-tests/reports/junit.xml'
                            publishHTML([
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'python-tests/reports',
                                reportFiles: 'report.html',
                                reportName: 'PyTest HTML Report'
                            ])
                        }
                    }
                }

                stage('Newman REST API Suite') {
                    steps {
                        sh '''
                            npm ci
                            mkdir -p reports/postman
                            npm run test:postman
                        '''
                    }
                    post {
                        always {
                            publishHTML([
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'reports/postman',
                                reportFiles: 'report.html',
                                reportName: 'Newman API Report'
                            ])
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs notFailBuild: true
        }
    }
}
