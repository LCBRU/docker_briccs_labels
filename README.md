# docker_briccs_label

To **build** the image :

  * sudo docker build -t lcbruit/briccs_label:v1.1 .
 
To **run** the docker container :
 
  * sudo docker run -itd -p 8080:8080 lcbruit/briccs_label:v1.1
  
Ensure that the **version number** matches the actual redcap version you are upgrading to.

To **connect** to container :

  * sudo docker ps -a
  * sudo docker exec -i -t [CONTAINER ID] /bin/bash

admin:ldii0AylInTN

admin:9JPxu4bSxPrD
