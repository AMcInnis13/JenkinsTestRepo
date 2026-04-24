# JenkinsTestRepo
This is a test repo for jenkns 

The pipeline triggers after a push to github. 
when pushed, github sends a webhook post to ngrok url 
ngrok forwards to jenkins on localhost 
jenkins pulls the code and runs mvn clean test 
test results post in jenkins

# test
1
