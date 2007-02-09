# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


Running
*******

After installing StAX (see next section)...

Build
./build.sh

Run
./dispatch

StAX
****
To make the version compile you need to download the JSR 173 API from 
http://www.ibiblio.org/maven2/stax/stax-api/1.0/stax-api-1.0.jar 
and copy it to lib/api/. We cannot redistribute the API (it was once a
discussion on cocoon-dev when the jcr got introduced). The
specifications can be found http://www.jcp.org/en/jsr/detail?id=173 

To get kick started with StAX have a look at
http://today.java.net/pub/a/today/2006/07/20/introduction-to-stax.html

tutorials: http://www.devx.com/ibm/Door/20346

history: http://www.xmlpull.org/history/index.html

IBM Series
cursor vs. iterator: http://www-128.ibm.com/developerworks/xml/library/x-tipstx/
filtering: http://www-128.ibm.com/developerworks/xml/library/x-tipstx2/