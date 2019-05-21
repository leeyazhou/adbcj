#!/bin/bash
#
# Copyright © 2019 yazhou.li (lee_yazhou@163.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


docker rm -f asyncdb-test-mysql
# see mysqltestdb for building this image
docker run --name asyncdb-test-mysql -p 3306:3306 -d asyncdbmysql


docker run --name myadmin -d --link asyncdb-test-mysql:db -p 8082:80 --env MYSQL_ROOT_PASSWORD=asyncdbtck phpmyadmin/phpmyadmin

docker ps -a | awk '{print $1}' | xargs docker rm -f