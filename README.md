# docker_briccs_label

To **build** the image :

  * sudo docker build -t lcbruit/briccs_label:v1.3 .
 
To **run** the docker container :
 
  * sudo docker run -d -p 8080:8080 lcbruit/briccs_label:v1.3
  
Ensure that the **version number** matches the actual redcap version you are upgrading to.

To **connect** to container :

  * sudo docker ps -a
  * sudo docker exec -i -t [CONTAINER ID] /bin/bash

The tomcat user and login details are in the docker logs

sudo docker logs [CONTAINER ID]


## To View

[http://uhlbriccsdev:8080/briccs-labels-webapp-1.05/]
