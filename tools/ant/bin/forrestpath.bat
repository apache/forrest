REM   Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
REM   reserved.

If %OS%'==Windows_NT' Set NTSwitch=/F "Tokens=*"
For %NTSwitch% %%V In (%1) Do set LOCALCLASSPATH=%LOCALCLASSPATH%;%%V
